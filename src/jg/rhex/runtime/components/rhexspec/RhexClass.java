package jg.rhex.runtime.components.rhexspec;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import jg.rhex.common.Descriptor;
import jg.rhex.common.Type;
import jg.rhex.compile.components.structs.RClass;
import jg.rhex.runtime.components.GenClass;
import jg.rhex.runtime.components.Variable;

public class RhexClass extends GenClass{
  
  private final RClass original;

  public RhexClass(Type typeInfo, Set<GenClass> parents, RClass original) {
    super(typeInfo, parents, new HashMap<>(), new HashMap<>(), new LinkedHashMap<>(), original.isAnInterface());
    this.original = original;
  }
  
  public void placeConstructor(RhexConstructor constructor){
    constructorMap.put(constructor.getSignature(), constructor);
  }
  
  public void placeFunction(RhexFunction function){
    functionMap.put(function.getSignature(), function);
  }
  
  public void placeVariable(RhexVariable variable){
    variableMap.put(variable.getName(), variable);
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
  
  public RClass getOriginal(){
    return original;
  }
}
