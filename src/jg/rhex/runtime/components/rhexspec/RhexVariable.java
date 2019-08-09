package jg.rhex.runtime.components.rhexspec;

import jg.rhex.compile.components.structs.RVariable;
import jg.rhex.runtime.components.Instance;
import jg.rhex.runtime.components.Variable;

public class RhexVariable extends Variable{

  private RVariable original;
  
  public RhexVariable(RVariable original) {
    super(original.getDescriptors(), 
          original.getProvidedType().getAttachedType(), 
          original.getIdentifier().getToken().getImage());
    this.original = original;
  }
  
  public Variable clone(){
    return new RhexVariable(original);
  }

  @Override
  public Instance getValue() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setValue(Instance instance) {
    // TODO Auto-generated method stub
    
  }
}
