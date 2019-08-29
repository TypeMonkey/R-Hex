package jg.rhex.runtime.components.rhexspec;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import jg.rhex.common.Descriptor;
import jg.rhex.common.FunctionIdentity;
import jg.rhex.common.Type;
import jg.rhex.compile.components.structs.RClass;
import jg.rhex.runtime.components.GenClass;
import jg.rhex.runtime.components.Variable;

public class RhexClass extends GenClass{
  
  private final RClass original;

  public RhexClass(Type typeInfo, RClass original) {
    super(typeInfo, null, new HashSet<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), original.getDescriptors(), original.isAnInterface());
    this.original = original;
  }
  
  public void setParent(GenClass parent) {
    this.parent = parent;
  }
  
  public boolean addInterface(GenClass inter) {
    return interfaces.add(inter);
  }
  
  public void placeConstructor(RhexConstructor constructor){
    constructorMap.put(constructor.getSignature(), constructor);
  }
  
  public void placeFunction(RhexFunction function){
    if (functionMap.put(function.getSignature(), function) == null) {
      functionIdenMap.put(function.getIdentity(), function);
      if (funcsByName.containsKey(function.getName())) {
        funcsByName.get(function.getName()).add(function.getIdentity());
      }
      else {
        HashSet<FunctionIdentity> identities = new HashSet<>();
        identities.add(function.getIdentity());
        funcsByName.put(function.getName(), identities);
      }
    }
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
