package jg.rhex.compile.components.tnodes.atoms;

import java.util.List;

import jg.rhex.compile.components.tnodes.TNode;


public class TNewArray extends TAtom<TType>{

  private List<TNode> dimSizes;
  
  public TNewArray(TType type, List<TNode> dimSizes) {
    super(type, type.getLineNumber(), type.getColNumber());
    this.dimSizes = dimSizes;
  }
  
  public List<TNode> getDimensionSizes(){
    return dimSizes;
  }
  
  public TType getArrayBaseType() {
    return getActValue();
  }

  @Override
  public String toString() {
    return "NEW ARR ~ "+getArrayBaseType()+" , SIZES: "+dimSizes;
  }

  
  
}
