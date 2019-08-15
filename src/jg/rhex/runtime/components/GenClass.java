package jg.rhex.runtime.components;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import jg.rhex.common.FunctionSignature;
import jg.rhex.common.Type;

public abstract class GenClass {

  private final Type typeInfo;
  private final Set<GenClass> parents;
  private final boolean isInterface;
  
  protected Map<FunctionSignature, Constructor> constructorMap;
  protected Map<FunctionSignature, Function> functionMap;
  protected Map<String, Variable> variableMap;
  
  public GenClass(Type typeInfo, Set<GenClass> parents, 
      Map<FunctionSignature, Function> funMap, 
      Map<FunctionSignature, Constructor> consMap, 
      Map<String, Variable> varMap,
      boolean isInterface){
    this.typeInfo = typeInfo;
    this.parents = parents;
    this.isInterface = isInterface;
    
    functionMap = funMap;
    constructorMap = consMap;
    variableMap = varMap;
  }
   
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof GenClass) {
      GenClass genClass = (GenClass) obj;
      return typeInfo.equals(genClass.typeInfo);
    }
    return false;
  }
  
  @Override
  public int hashCode() {
    return typeInfo.hashCode();
  }
  
  /**
   * Checks if this GenClass is a child of the provided type
   * @param type - the potential parent type
   * @return true if type is a paren of this GenClas, false if else
   */
  public boolean isAChildOf(Type type){
    return parents.contains(type);
  }
  
  public Constructor retrieveConstructor(FunctionSignature signature){
    return constructorMap.get(signature);
  } 

  public Function retrieveFunction(FunctionSignature signature){
    return functionMap.get(signature);
  }
  
  public Variable retrieveVariable(String name){
    return variableMap.get(name);
  }

  public Type getTypeInfo() {
    return typeInfo;
  }
  
  public abstract Map<String, Variable> cloneVariableMap();

  public Map<FunctionSignature, Function> getFunctionMap() {
    return functionMap;
  }

  public Map<String, Variable> getVariableMap() {
    return variableMap;
  } 
  
  public boolean isInterface() {
    return isInterface;
  }
  
  public Set<GenClass> getSuperTypes(){
    return parents;
  }  
}
