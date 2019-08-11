package jg.rhex.runtime.components.java;

import java.util.Map;
import java.util.Set;

import jg.rhex.common.FunctionSignature;
import jg.rhex.common.Type;
import jg.rhex.runtime.components.Constructor;
import jg.rhex.runtime.components.Function;
import jg.rhex.runtime.components.GenClass;
import jg.rhex.runtime.components.Variable;

public class JavaClass extends GenClass{

  private JavaClass(Type typeInfo, Set<Type> parents, 
      Map<FunctionSignature, Function> funcMap, 
      Map<FunctionSignature, Constructor> constructors, 
      Map<String, Variable> varMap) {
    super(typeInfo, parents, funcMap, constructors, varMap);
  }

  @Override
  public Map<String, Variable> cloneVariableMap() {
    
    return null;
  }

  public static JavaClass getJavaClassRep(Class<?> target){
    
  }
}
