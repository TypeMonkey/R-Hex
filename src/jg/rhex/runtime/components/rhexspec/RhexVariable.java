package jg.rhex.runtime.components.rhexspec;

import jg.rhex.compile.components.structs.RVariable;
import jg.rhex.runtime.components.Instance;
import jg.rhex.runtime.components.Variable;

public class RhexVariable extends Variable{

  private final RVariable original;
  
  private Instance value;
  
  public RhexVariable(RVariable original) {
    this(original, null);
  }
  
  public RhexVariable(RVariable original, Instance initialValue) {
    super(original.getDescriptors(), 
          original.getProvidedType().getAttachedType(), 
          original.getIdentifier().getToken().getImage());
    this.original = original;
    this.value = initialValue;
  }
  
  public Variable clone(){
    return new RhexVariable(original);
  }

  @Override
  public Instance getValue() {
    return value;
  }

  @Override
  public void setValue(Instance instance) {
    // TODO Auto-generated method stub
    
  }
  
  public RVariable getOriginal(){
    return original;
  }
}
