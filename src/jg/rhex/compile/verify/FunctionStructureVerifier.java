package jg.rhex.compile.verify;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import jg.rhex.common.FunctionSignature;
import jg.rhex.common.Type;
import jg.rhex.compile.RhexCompiler;
import jg.rhex.compile.components.structs.RStatement;
import jg.rhex.compile.components.tnodes.TNode;
import jg.rhex.compile.components.tnodes.TOp;
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
import jg.rhex.compile.components.tnodes.atoms.TNumber;
import jg.rhex.compile.components.tnodes.atoms.TString;
import jg.rhex.compile.verify.errors.UnfoundFunctionException;
import jg.rhex.compile.verify.errors.UnfoundTypeException;
import jg.rhex.compile.verify.errors.UnfoundVariableException;
import jg.rhex.runtime.components.Function;
import jg.rhex.runtime.components.GenClass;
import jg.rhex.runtime.components.Variable;
import jg.rhex.runtime.components.rhexspec.RhexFile;
import jg.rhex.runtime.components.rhexspec.RhexFunction;
import jg.rhex.runtime.components.rhexspec.RhexVariable;

/**
 * Class that verifies that the provided function is correctly structured
 * i.e: all variable referrals refers to variables that are actually visible,
 *      type casts are actually bound to existing types,
 *      the function returns something in all control paths (if not void),
 *      type checks expressions and assignments.
 */
public class FunctionStructureVerifier {
  
  private RhexCompiler compiler;
  private RhexFile hostFile;
  private NameResolver store;
  private RhexFunction function;
  
  public FunctionStructureVerifier(NameResolver store, RhexCompiler compiler,  RhexFile hostFile, RhexFunction function) {
    this.compiler = compiler;
    this.hostFile = hostFile;
    this.store = store;
    this.function = function;
  }

  /**
   * Verifies that the provided function is correctly structured
   * i.e: all variable referrals refers to variables that are actually visible,
   *      type casts are actually bound to existing types,
   *      the function returns something in all control paths (if not void),
   *      type checks expressions and assignments.
   * @param store - the TypeStore to use to lookup type names
   * @param compiler - the RhexCompiler to use 
   * @param hostFile - the host RhexFile of the function
   * @param function - the function to inspect
   */
  public void verfiyFunctionStructure(){    
    List<RStatement> statements = function.getOriginal().getBody().getStatements();
    
    for(int i = 0; i < statements.size(); i++){
      RStatement current = statements.get(i);
      
    }
  }
  
  private Type checkExpression(Map<String, RhexVariable> scopeTExpr, TExpr expr){
    return checkExpression(scopeTExpr, expr.getActValue().toArray(new TNode[expr.getActValue().size()]));
  }
  
  private Type checkExpression(Map<String, RhexVariable> scope, TNode ... exprBody){
    Stack<Type> valueTypes = new Stack<>();
       
    /**
     * Expressions are organized in post-fix pattern
     */
    for (TNode node : exprBody) {
      if (node instanceof TOp) {
        
      }
      else if (node instanceof TFuncCall) {
        valueTypes.push(retrieveFunctionReturn(null, scope, (TFuncCall) node));
      }
      else if (node instanceof TMemberInvoke) {
        TMemberInvoke memberInvoke = (TMemberInvoke) node;
        
        Type memType = null;
        for(int i = 0; i < memberInvoke.getSequence().size(); i++){
          if (i == 0) {
            if (memberInvoke.getSequence().get(i) instanceof TFuncCall) {
              memType = retrieveFunctionReturn(null, scope, (TFuncCall) memberInvoke.getSequence().get(i));
            }
            else {
              //other atoms
              memType = checkExpression(scope, memberInvoke.getSequence().get(i));
            }
          }
          else {
            memType = checkExpression(scope, memberInvoke.getSequence().get(i));
          }
        }
        
        valueTypes.push(memType);
      }
      else if (node instanceof TCast) {
        TCast cast = (TCast) node;
        Type type = store.retrieveType(cast.getDesiredType());
        if (type == null) {
          throw new UnfoundTypeException(cast.getDesiredType().getBaseType().get(0).getToken(), 
              cast.getDesiredType().getBaseString(), hostFile.getName());
        }
        
        Type targetType = checkExpression(scope, cast.getTarget());
      }
      else if (node instanceof TExpr) {
        valueTypes.push(checkExpression(scope, (TExpr) node));
      }
      else if (node instanceof TIden) {
        TIden iden = (TIden) node;
        
        RhexVariable targetVariable = scope.get(iden.getActValue().getImage());
        if (targetVariable == null) {
          throw new UnfoundVariableException(iden.getActValue(), hostFile.getName());
        }
        else {
          valueTypes.push(targetVariable.getType());
        }
      }
      else if (node instanceof TInt) {
        valueTypes.push(Type.INT);
      }
      else if (node instanceof TDouble) {
        valueTypes.push(Type.DOUBLE);
      }
      else if (node instanceof TFloat) {
        valueTypes.push(Type.FLOAT);
      }
      else if (node instanceof TLong) {
        valueTypes.push(Type.FLOAT);
      }
      else if (node instanceof TString) {
        valueTypes.push(Type.FLOAT);
      }
      else if (node instanceof TChar) {
        valueTypes.push(Type.FLOAT);
      }
    }
    
    return valueTypes.pop();
  }
  
  private Type retrieveFunctionReturn(GenClass targetClass, Map<String, RhexVariable> scope, TFuncCall original){   
    Type [] argTypes = new Type[original.getArgList().size()];
    for(int i = 0; i < argTypes.length; i++){
      argTypes[i] = checkExpression(scope, original.getArgList().get(i));
    }
    
    FunctionSignature signature = new FunctionSignature(original.getFuncName().getToken().getImage(), argTypes);
    
    if (targetClass == null) {
      return hostFile.getFunction(signature).getIdentity().getReturnType();
    }
    else {
      Function genClass = targetClass.retrieveFunction(signature);
      if (genClass == null) {
        throw new UnfoundFunctionException(signature, original, hostFile.getName());
      }
      
      return genClass.getIdentity().getReturnType();
    }
  }
}
