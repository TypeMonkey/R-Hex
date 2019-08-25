package jg.rhex.compile.verify;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import jg.rhex.common.FunctionIdentity;
import jg.rhex.common.FunctionSignature;
import jg.rhex.common.Type;
import jg.rhex.compile.RhexCompiler;
import jg.rhex.compile.components.structs.CatchBlock;
import jg.rhex.compile.components.structs.ForBlock;
import jg.rhex.compile.components.structs.IfBlock;
import jg.rhex.compile.components.structs.RFile;
import jg.rhex.compile.components.structs.RFunc;
import jg.rhex.compile.components.structs.RStateBlock;
import jg.rhex.compile.components.structs.RStateBlock.BlockType;
import jg.rhex.compile.components.structs.RStatement;
import jg.rhex.compile.components.structs.RVariable;
import jg.rhex.compile.components.structs.WhileBlock;
import jg.rhex.compile.components.structs.RStatement.RStateDescriptor;
import jg.rhex.compile.components.tnodes.TNode;
import jg.rhex.compile.components.tnodes.atoms.TCast;
import jg.rhex.compile.components.tnodes.atoms.TExpr;
import jg.rhex.compile.components.tnodes.atoms.TNew;
import jg.rhex.compile.components.tnodes.atoms.TNewArray;
import jg.rhex.compile.components.tnodes.atoms.TType;
import jg.rhex.compile.verify.errors.SimilarFunctionException;
import jg.rhex.compile.verify.errors.UnfoundTypeException;
import jg.rhex.runtime.components.Function;
import jg.rhex.runtime.components.GenClass;
import jg.rhex.runtime.components.Variable;
import jg.rhex.runtime.components.java.JavaClass;
import jg.rhex.runtime.components.rhexspec.RhexClass;
import jg.rhex.runtime.components.rhexspec.RhexFile;
import jg.rhex.runtime.components.rhexspec.RhexFunction;
import jg.rhex.runtime.components.rhexspec.RhexVariable;

public class TypeAttacher {
  private final Map<Type, RhexClass> rhexTypes;
  private final RhexCompiler compiler;
  
  public TypeAttacher(Map<Type, RhexClass> rhexTypes, RhexCompiler compiler){
    this.rhexTypes = rhexTypes;
    this.compiler = compiler;
  }
  
  public void extract(RhexFile file){
    final RFile original = file.getOriginal();
    final NameResolver nameResolver = new NameResolver(original, compiler);
    
    /*
     * Type attachment needs to happen to the following:
     * 
     * TNew (the TType for it)
     * TNewArr (the TType for it)
     * TCast (the TType desired type)
     * TType (for variable declarations) 
     */
    for(RVariable variable : original.getVariables()){
      Type varType = retrieveType(variable.getProvidedType(), nameResolver, file);
      variable.getProvidedType().attachType(varType);
      
      file.placeVariable(new RhexVariable(variable));
      
      walkExpression(variable.getStatement(), nameResolver, file);
    }
    
    for(RFunc func : original.getFunctions()){
      HashSet<Type> exceptions = new HashSet<>();
      for (TType type : func.getDeclaredExceptions()) {
        Type actual = retrieveType(type, nameResolver, file);
        type.attachType(actual);
        if (!exceptions.add(actual)) {
          throw new RuntimeException("The function: "+func.getName().getImage()+
              " repeats the exception '"+actual+"' , at <ln:"+
              type.getBaseType().get(0).getToken().getStartLine()+">");
        }
      }
      FunctionIdentity identity = formIdentity(func, nameResolver, file);
      if (!file.placeFunction(new RhexFunction(identity, func, exceptions))) {
        throw new SimilarFunctionException(identity, func.getName(), original.getFileName());
      }
      
      walkBlock(func.getBody().getStatements(), nameResolver, file);
    }
    
    for(GenClass rClass : file.getFileClasses().values()) {
      for(Variable variable : rClass.getVariableMap().values()) {
        RhexVariable actualVar = (RhexVariable) variable;
        retrieveType(actualVar.getOriginal().getProvidedType(), nameResolver, file);
        walkExpression(actualVar.getOriginal().getStatement(), nameResolver, file);
      }
      
      for(Function function : rClass.getFunctionMap().values()) {
        RhexFunction actualFunc = (RhexFunction) function;
        formIdentity(actualFunc.getOriginal(), nameResolver, file);
        
        walkBlock(actualFunc.getOriginal().getBody().getStatements(), nameResolver, file);
      }
    }
  }
  
  private void walkBlock(List<RStatement> statements, NameResolver resolver, RhexFile file) {
    for (RStatement rStatement : statements) {
      System.out.println("---ATTACHING FOR "+rStatement.getDescriptor()+" | "+rStatement.getStatement());
      if (rStatement.getDescriptor() == RStateDescriptor.BLOCK) {
        RStateBlock block = (RStateBlock) rStatement;
        if (block.getBlockType() == BlockType.ELSE_IF || block.getBlockType() == BlockType.IF) {
          IfBlock ifBlock = (IfBlock) block;
          walkExpression(ifBlock.getConditional().getStatement(), resolver, file);
        }
        else if (block.getBlockType() == BlockType.WHILE) {
          WhileBlock whileBlock = (WhileBlock) block;
          walkExpression(whileBlock.getConditional().getStatement(), resolver, file);
        }
        else if (block.getBlockType() == BlockType.FOR) {
          ForBlock forBlock = (ForBlock) block;
          if (forBlock.getIntialization() != null) {
            walkExpression(forBlock.getIntialization().getStatement(), resolver, file);
          }
          if (forBlock.getConditional() != null) {
            walkExpression(forBlock.getConditional().getStatement(), resolver, file);
          }
          if (forBlock.getChange() != null) {
            walkExpression(forBlock.getChange().getStatement(), resolver, file);
          }
        }
        else if (block.getBlockType() == BlockType.CATCH) {
          CatchBlock catchBlock = (CatchBlock) block;
          for (TType type : catchBlock.getExceptionTypes()) {
            Type actual = retrieveType(type, resolver, file);
            type.attachType(actual);
          }
        }
        walkBlock(block.getStatements(), resolver, file);
      }
      else if (rStatement.getDescriptor() == RStateDescriptor.VAR_DEC) {
        RVariable variable = (RVariable) rStatement;
        Type varType = retrieveType(variable.getProvidedType(), resolver, file);
        variable.getProvidedType().attachType(varType);
                
        walkExpression(variable.getStatement(), resolver, file);
      }
      else {
        walkExpression(rStatement.getStatement(), resolver, file);
      }
    }
  }
  
  private void walkExpression(TNode node, NameResolver resolver, RhexFile file) {
    walkExpression(Arrays.asList(node), resolver, file);
  }
  
  private void walkExpression(List<TNode> nodeList, NameResolver resolver, RhexFile file) {
    for(TNode node : nodeList) {
      if (node instanceof TExpr) {
        TExpr expr = (TExpr) node;
        walkExpression(expr.getActValue(), resolver, file);
      }
      else if (node instanceof TNew) {
        TNew newCreation = (TNew) node;
        Type actual = retrieveType(newCreation.getFullBinaryName(), resolver, file);
        newCreation.getFullBinaryName().attachType(actual);
      }
      else if (node instanceof TNewArray) {
        TNewArray newCreation = (TNewArray) node;
        Type actual = retrieveType(newCreation.getArrayBaseType(), resolver, file);
        newCreation.getArrayBaseType().attachType(actual);
      }
      else if (node instanceof TCast) {
        TCast cast = (TCast) node;
        Type actual = retrieveType(cast.getDesiredType(), resolver, file);
        cast.getDesiredType().attachType(actual);
      }
    }
  }
  
  private FunctionIdentity formIdentity(RFunc rFunc, NameResolver resolver, RhexFile current) {   
    System.out.println("----FUNC: "+rFunc.getName().getImage());
    
    //resolve the types of the parameters
    Type [] paramTypes = new Type[rFunc.getParameterAmount()];
    
    for(int i = 0; i < rFunc.getParameterAmount(); i++) {
      RVariable variable = (RVariable) rFunc.getBody().getStatements().get(i);
      //we are guaranteed during the parsing stage that no parameter has an inferred type
      TType proType = variable.getProvidedType();
      Type concreteType = retrieveType(proType, resolver, current);
      paramTypes[i] = concreteType;
    }
    
    //then, resolve the return type first
    TType returnType = rFunc.getReturnType();
    if (returnType.getBaseString().equals("void")) {
      return new FunctionIdentity(new FunctionSignature(rFunc.getName().getImage(), paramTypes, rFunc.getDescriptors()));
    }
    else {
      Type actualType = retrieveType(returnType, resolver, current);
      return new FunctionIdentity(new FunctionSignature(rFunc.getName().getImage(), paramTypes, rFunc.getDescriptors()), actualType);
    }   
  }
  
  private Type retrieveType(TType proType, NameResolver resolver, RhexFile current){
    Type concreteType = resolver.retrieveType(proType);
    
    if (concreteType == null) {
      throw new UnfoundTypeException(proType.getBaseType().get(0).getToken(), proType.getBaseString(), current.getName());
    }
    else {
      if (!rhexTypes.containsKey(concreteType) && JavaClass.getJavaClassRep(concreteType.getFullName()) == null) {
        throw new UnfoundTypeException(proType.getBaseType().get(0).getToken(), proType.getBaseString(), current.getName());
      }
      return concreteType;
    }
  }
}
