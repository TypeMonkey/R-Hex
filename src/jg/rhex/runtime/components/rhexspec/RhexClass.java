package jg.rhex.runtime.components.rhexspec;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jg.rhex.common.Descriptor;
import jg.rhex.common.Type;
import jg.rhex.runtime.components.GenClass;
import jg.rhex.runtime.components.Instance;
import jg.rhex.runtime.components.Variable;

public class RhexClass extends GenClass{

  public RhexClass(Type typeInfo, Set<Type> parents) {
    super(typeInfo, parents);
  }

  @Override
  public Map<String, Variable> cloneVariableMap() {   
    Map<String, Variable> varMap = new HashMap<>();
    
    for(Variable variable : variableMap.values()){
      if (!variable.containsDescriptor(Descriptor.STATIC)) {
        varMap.put(variable.getName(), variable.clone());
      }
    }
    
    return varMap;
  }
}
