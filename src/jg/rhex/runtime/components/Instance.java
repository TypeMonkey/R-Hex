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
    Variable variable = variableScope.get(name);
    if (variable == null) {
      variable = variableScope.get("super");
      variable = variable.getValue().getVariable(name);
    }
    return variable;
  }
  
  public GenClass getBackingClass() {
    return backingType;
  }
}
