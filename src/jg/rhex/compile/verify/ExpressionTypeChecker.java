package jg.rhex.compile.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import jg.rhex.common.FunctionSignature;
import jg.rhex.common.Type;
import jg.rhex.common.TypeUtils;
import jg.rhex.compile.RhexCompiler;
import jg.rhex.compile.components.structs.RStatement;
import jg.rhex.compile.components.tnodes.TNode;
import jg.rhex.compile.components.tnodes.TOp;
import jg.rhex.compile.components.tnodes.atoms.TBool;
import jg.rhex.compile.components.tnodes.atoms.TCast;
import jg.rhex.compile.components.tnodes.atoms.TChar;
import jg.rhex.compile.components.tnodes.atoms.TDouble;
import jg.rhex.compile.components.tnodes.atoms.TExpr;
import jg.rhex.compile.components.tnodes.atoms.TFloat;
import jg.rhex.compile.components.tnodes.atoms.TFuncCall;
import jg.rhex.compile.components.tnodes.atoms.TIden;
import jg.rhex.compile.components.tnodes.atoms.TInt;
import jg.rhex.compile.components.tnodes.atoms.TLong;
import jg.rhex.compile.components.tnodes.atoms.TMemberInvoke;
import jg.rhex.compile.components.tnodes.atoms.TString;
import jg.rhex.compile.components.tnodes.atoms.TUnary;
import jg.rhex.compile.verify.errors.UnfoundVariableException;
import jg.rhex.runtime.SymbolTable;
import jg.rhex.runtime.components.Function;
import jg.rhex.runtime.components.GenClass;
import jg.rhex.runtime.components.Variable;
import jg.rhex.runtime.components.java.JavaClass;
import jg.rhex.runtime.components.rhexspec.RhexFile;;

/**
 * Class that verifies that the provided function is correctly structured
 * i.e: all variable referrals refers to variables that are actually visible,
 *      type casts are actually bound to existing types,
 *      the function returns something in all control paths (if not void),
 *      type checks expressions and assignments.
 */
public class ExpressionTypeChecker {
  
  /**
   * Type checks the expression and returns the type of the expression
   * @param node - the TNode to type check
   * @param table - the SymbolTable to use
   */
  public static GenClass typeCheckExpression(TNode expr, RhexFile file, SymbolTable table){    
    //Note that with respect to arithmetic operators, expressions are organized in postfix style
    ArrayList<TNode> exprBody = new ArrayList<>();
    if (expr instanceof TExpr) {
      exprBody.addAll(((TExpr) expr).getActValue());
    }
    else {
      exprBody.add(expr);
    }
    
    Stack<GenClass> valueTypes = new Stack<>();
    
    /**
     * Expressions are organized in post-fix pattern
     */
    for (TNode node : exprBody) {
      if (node instanceof TOp) {   
        GenClass left = valueTypes.pop();
        GenClass right = valueTypes.pop();
        TOp op = (TOp) node;
        
        //check if operator is numerical
        HashSet<String> arithOps = new HashSet<>(Arrays.asList("+","-","/","*","%"));
        HashSet<String> numCompOps = new HashSet<>(Arrays.asList("<","<=",">",">="));
        HashSet<String> uniCompOps = new HashSet<>(Arrays.asList("==", "!="));
        HashSet<String> boolOps = new HashSet<>(Arrays.asList("&&", "||"));
        if (arithOps.contains(op.getOpString())) {
          if (op.getOpString().equals("+")) {
            if (left.getTypeInfo().getFullName().equals("java.lang.String") || 
                right.getTypeInfo().getFullName().equals("java.lang.String")) {
              valueTypes.push(table.findClass(new Type("String", "java.lang.String")));
              continue;
            }
          }
          Type result = getResultingArithmeticType(left.getTypeInfo(), right.getTypeInfo());
          valueTypes.push(table.findClass(result));
        }
        else if (boolOps.contains(op.getOpString())) {
          if ( (left.getTypeInfo().getFullName().equals("java.lang.Boolean") || 
                left.getTypeInfo().getFullName().equals("boolean")) && 
               (right.getTypeInfo().getFullName().equals("java.lang.Boolean") || 
                right.getTypeInfo().getFullName().equals("boolean"))) {
            valueTypes.push(table.findClass(Type.BOOL));
          }
          else {
            throw new RuntimeException("'"+op.getOpString()+"' isn't define for non-boolean types, at <ln:"+
                                          op.getOperatorToken().getStartLine()+"> , at file: "+file.getName());
          }
        }
        else if (op.getOpString().equals("=")) {
          if (right.decendsFrom(left)) {
            valueTypes.push(right);
          }
          else {
            throw new RuntimeException("Cannot assign instance of '"+right.getTypeInfo()+"' to variable "+
                "of type '"+left.getTypeInfo()+"'"+
                " , at <ln:"+op.getOperatorToken().getStartLine()+">, file: "+file.getName());
          }
        }
        else if(numCompOps.contains(op.getOpString())){
          if (TypeUtils.isNumerical(left.getTypeInfo()) && TypeUtils.isNumerical(right.getTypeInfo())) {
            valueTypes.push(table.findClass(Type.BOOL));
          }
          else {
            throw new RuntimeException("Not both operands are numbers, op: "+op.getOpString()+
                " , at <ln:"+op.getOperatorToken().getStartLine()+"> , file: "+file.getName());
          }
        }
        else if(uniCompOps.contains(op.getOpString())){
          valueTypes.push(table.findClass(Type.BOOL));
        }
      }
      else if (node instanceof TFuncCall) {
        TFuncCall funcCall = (TFuncCall) node;
        
        Type [] argTypes = new Type[funcCall.getArgList().size()];
        for (int i = 0; i < argTypes.length; i++) {
          argTypes[i] = typeCheckExpression(funcCall.getArgList().get(i), file, table).getTypeInfo();
        }
        
        FunctionSignature toLookFor = new FunctionSignature(funcCall.getFuncName().getToken().getImage(), 
            argTypes, null);
        Function actualFunction = table.findFunction(toLookFor);
        if (actualFunction == null) {
          throw new RuntimeException("Cannot find file function "+toLookFor+" from '"+file.getName()+"' "+
              " , at <ln:"+funcCall.getFuncName().getToken().getStartLine()+">");
        }
        valueTypes.push(table.findClass(actualFunction.getReturnType()));
      }
      else if (node instanceof TMemberInvoke) {
        TMemberInvoke memberInvoke = (TMemberInvoke) node;
        
        TNode firstMember = memberInvoke.getSequence().get(0);
        System.out.println("  R_CH: "+firstMember);
        GenClass firstType = typeCheckExpression(firstMember, file, table);
        
        for(int i = 1; i < memberInvoke.getSequence().size(); i++){
          TNode current = memberInvoke.getSequence().get(i);
          if (current instanceof TIden) {
            TIden iden = (TIden) current;
            Variable variable = firstType.retrieveVariable(iden.getToken().getImage());
            if (variable == null) {
              throw new RuntimeException("Cannot find class variable "+iden.getToken().getImage()+
                                         " from class '"+firstType.getTypeInfo()+"' "+
                                         " , at <ln:"+iden.getToken().getStartLine()+"> , at "+file.getName());
            }
            firstType = table.findClass(variable.getType());
          }
          else if (current instanceof TFuncCall) {
            TFuncCall funcCall = (TFuncCall) current;
            System.out.println(" * MEM FUNC CALL: "+funcCall);
            
            Type [] argTypes = new Type[funcCall.getArgList().size()];
            for (int j = 0; j < argTypes.length; j++) {
              argTypes[j] = typeCheckExpression(funcCall.getArgList().get(j), file, table).getTypeInfo();
            }
            
            FunctionSignature toLookFor = new FunctionSignature(funcCall.getFuncName().getToken().getImage(), 
                argTypes, null);
            Function actualFunction = firstType.retrieveFunction(toLookFor, true);
            if (actualFunction == null) {
              throw new RuntimeException("Cannot find class function "+toLookFor+" from class '"+firstType.getTypeInfo()+"' "+
                  " , at <ln:"+funcCall.getFuncName().getToken().getStartLine()+"> , at "+file.getName());
            }              
            firstType = table.findClass(actualFunction.getReturnType());
            System.out.println("----> MEM FUNC CALL RETURN: "+actualFunction.getReturnType());
          }
        }
        
        System.out.println(" LATEST MEMBER: "+firstType);
        valueTypes.push(firstType);
      }
      else if (node instanceof TCast) {
        TCast cast = (TCast) node;
        GenClass desiredType = table.findClass(cast.getDesiredType().getAttachedType());      
        GenClass targetType = typeCheckExpression(cast.getTarget(), file, table);
        
        //numerical types can be converted between each other
        if ( !(TypeUtils.isNumerical(desiredType.getTypeInfo()) && TypeUtils.isNumerical(targetType.getTypeInfo())) &&
             (!desiredType.decendsFrom(targetType) && !targetType.decendsFrom(desiredType)) ) {
          //desired type and target type have no common ancestry. Cast isn't possible
          throw new RuntimeException("Cast type '"+desiredType.getTypeInfo()+"' has no common ancestry with"+
                     " target '"+targetType.getTypeInfo()+
                     "' , at <ln:"+cast.getDesiredType().getBaseType().get(0).getToken().getStartLine()+"> , at "+file.getName());
        }
        
        valueTypes.push(desiredType);
      }
      else if (node instanceof TUnary) {
        TUnary unary = (TUnary) node;
        GenClass targetType = typeCheckExpression(unary.getActValue(), file, table);
        switch (unary.getUnaryOp().getOpString()) {
        case "-":
          if (!TypeUtils.isNumerical(targetType.getTypeInfo())) {
            throw new RuntimeException("Unary op '-' isn't defined for class "+targetType.getTypeInfo()+
                " , at <ln:"+unary.getUnaryOp().getOperatorToken().getStartLine()+"> , at "+file.getName());
          }
          break;
        case "!":
          if (!targetType.getTypeInfo().getFullName().equals("java.lang.Boolean") && 
              !targetType.getTypeInfo().equals(Type.BOOL)) {
            throw new RuntimeException("Unary op '-' isn't defined for class "+targetType.getTypeInfo()+
                " , at <ln:"+unary.getUnaryOp().getOperatorToken().getStartLine()+"> , at "+file.getName());
          }
          break;
        default:
          //not possible. Enforced by language grammar
        }
        
        valueTypes.push(targetType);
      }
      else if (node instanceof TExpr) {
        valueTypes.push(typeCheckExpression(node, file, table));
      }
      else if (node instanceof TIden) {
        TIden iden = (TIden) node;
        
        Variable targetVariable = table.findVariable(iden.getActValue().getImage());
        if (targetVariable == null) {
          throw new UnfoundVariableException(iden.getActValue(), file.getName());
        }
        else {
          valueTypes.push(table.findClass(targetVariable.getType()));
        }
      }
      else if (node instanceof TInt) {
        valueTypes.push(table.findClass(Type.INT));
      }
      else if (node instanceof TDouble) {
        valueTypes.push(table.findClass(Type.DOUBLE));
      }
      else if (node instanceof TFloat) {
        valueTypes.push(table.findClass(Type.FLOAT));
      }
      else if (node instanceof TLong) {
        valueTypes.push(table.findClass(Type.LONG));
      }
      else if (node instanceof TString) {
        valueTypes.push(table.findClass(new Type("String", "java.lang.String")));
      }
      else if (node instanceof TChar) {
        valueTypes.push(table.findClass(Type.CHAR));
      }
      else if (node instanceof TBool) {
        valueTypes.push(table.findClass(Type.BOOL));
      }
    }
    
    System.out.println("---RETURNING!!! "+valueTypes.peek());
    return valueTypes.pop();
  }
  
  public static Type getResultingArithmeticType(Type left, Type right){
    if (TypeUtils.isNumerical(right) && TypeUtils.isNumerical(right)) {
      if (left.equals(right)) {
        //if the same type, their addition should be the same
        return left;
      }
      else if (TypeUtils.isFloatingNumType(left) && TypeUtils.isFloatingNumType(right)) {
        return Type.DOUBLE;
      }
      else if (TypeUtils.isIntegralNumType(left) && TypeUtils.isIntegralNumType(right)) {
        if ((left.getSimpleName().equals("Long") || left.equals(Type.LONG)) || 
            (right.getSimpleName().equals("Long") || right.equals(Type.LONG))) {
          return Type.LONG;
        }
        else{
          return Type.INT;
        }
      }
      else if ( (TypeUtils.isFloatingNumType(left) && TypeUtils.isNumerical(right)) || 
                (TypeUtils.isIntegralNumType(left) && TypeUtils.isFloatingNumType(right)) ) {
        return Type.DOUBLE;
      }
    }
    return null;
  }
}
