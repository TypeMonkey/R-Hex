package jg.rhex.runtime.components;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import jg.rhex.common.Descriptor;
import jg.rhex.common.FunctionIdentity;
import jg.rhex.common.FunctionSignature;
import jg.rhex.common.Type;
import jg.rhex.runtime.components.java.JavaClass;

public abstract class GenClass {

  private final Type typeInfo;
  private final boolean isInterface;
  private final Set<Descriptor> descriptors;
  
  protected GenClass parent;
  protected Set<GenClass> interfaces;
  
  protected Map<FunctionSignature, Constructor> constructorMap;
  
  protected Map<FunctionIdentity, Function> functionIdenMap;
  protected Map<FunctionSignature, Function> functionMap;
  protected Map<String, Set<FunctionIdentity>> funcsByName;
  
  protected Map<String, Variable> variableMap;
  
  public GenClass(Type typeInfo, GenClass parent, Set<GenClass> interfaces, 
      Map<FunctionSignature, Function> funMap, 
      Map<FunctionSignature, Constructor> consMap, 
      Map<String, Variable> varMap,
      Set<Descriptor> descriptors,
      boolean isInterface){
    this.typeInfo = typeInfo;
    this.parent = parent;
    this.interfaces = interfaces;
    this.isInterface = isInterface;
    this.descriptors = descriptors;
    
    functionMap = funMap;
    constructorMap = consMap;
    variableMap = varMap;
    
    functionIdenMap = funMap.values().stream().collect(
          Collectors.toMap(Function::getIdentity, x -> x)
        );
    
    funcsByName = new HashMap<>();
    for(FunctionIdentity identity : functionIdenMap.keySet()){
      if (funcsByName.containsKey(identity.getFuncSig().getName())) {
        funcsByName.get(identity.getFuncSig().getName()).add(identity);
      }
      else {
        HashSet<FunctionIdentity> identities = new HashSet<>();
        identities.add(identity);
        funcsByName.put(identity.getFuncSig().getName(), identities);
      }
    }
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
   **/
  public boolean decendsFrom(GenClass ancestor){
    System.out.println("for: "+this+" || vs "+ancestor);
    if (equals(ancestor) || ((parent != null) && (parent.equals(ancestor) || parent.decendsFrom(ancestor)))) {
      return true;
    }
    
    if (interfaces.size() > 0) {
      for(GenClass inter : interfaces) {
        if (!inter.decendsFrom(ancestor)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  public Constructor retrieveConstructor(FunctionSignature signature){
    return constructorMap.get(signature);
  } 

  public Function retrieveFunction(FunctionSignature signature, boolean recurseSearchParent){
    Function function = functionMap.get(signature);
    if (function == null && recurseSearchParent) {
      if (parent != null) {
        function = parent.retrieveFunction(signature, recurseSearchParent);
      }
    }
    return function;
  }
  
  public Function retrieveFunction(FunctionIdentity identity){
    Function function = functionIdenMap.get(identity);
    return function;
  }
  
  public Set<FunctionIdentity> retrieveFuncIdens(String funcName){
    return funcsByName.get(funcName);
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
  
  public Map<FunctionSignature, Constructor> getConstructors(){
    return constructorMap;
  }

  public Map<String, Variable> getVariableMap() {
    return variableMap;
  } 
  
  public boolean isInterface() {
    return isInterface;
  }
  
  public Set<GenClass> getInterfaces(){
    return interfaces;
  }
  
  public Set<Descriptor> getDescriptors(){
    return descriptors;
  }
  
  public GenClass getParent(){
    return parent;
  }  
  
  public String toString() {
    return "Class: "+typeInfo;
  }
  
  
}
