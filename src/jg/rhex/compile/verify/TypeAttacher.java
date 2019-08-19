package jg.rhex.compile.verify;

import java.util.Map;

import jg.rhex.common.FunctionIdentity;
import jg.rhex.common.FunctionSignature;
import jg.rhex.common.Type;
import jg.rhex.compile.RhexCompiler;
import jg.rhex.compile.components.structs.RFile;
import jg.rhex.compile.components.structs.RFunc;
import jg.rhex.compile.components.structs.RVariable;
import jg.rhex.compile.components.tnodes.atoms.TCast;
import jg.rhex.compile.components.tnodes.atoms.TType;
import jg.rhex.compile.verify.errors.SimilarFunctionException;
import jg.rhex.compile.verify.errors.UnfoundTypeException;
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
    
    for(RVariable variable : original.getVariables()){
      Type varType = retrieveType(variable.getProvidedType(), nameResolver, file);
      variable.getProvidedType().attachType(varType);
      
      file.placeVariable(new RhexVariable(variable));
    }
    
    for(RFunc func : original.getFunctions()){
      FunctionIdentity identity = formIdentity(func, nameResolver, file);
      if (!file.placeFunction(new RhexFunction(identity, func))) {
        throw new SimilarFunctionException(identity, func.getName(), original.getFileName());
      }
    }
  }
  
  private void attachTypeToCast(TCast cast, NameResolver resolver, RhexFile current){
    Type actual = retrieveType(cast.getDesiredType(), resolver, current);
    cast.getDesiredType().attachType(actual);
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
