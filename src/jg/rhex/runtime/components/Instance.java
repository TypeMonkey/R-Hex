package jg.rhex.runtime.components;

import java.util.Map;

import jg.rhex.common.Type;

public abstract class Instance {

  private final GenClass backingType;
  
  private Map<String, Variable> variableScope;
  
  public Instance(GenClass backingType, Map<String, Variable> variableScope){
    this.backingType = backingType;
    this.variableScope = variableScope;
  }

  public Variable getVariable(String name){
    return variableScope.get(name);
  }
  
  public GenClass getBackingClass() {
    return backingType;
  }
}
