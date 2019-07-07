package jg.rhex.compile.components.tnodes.atoms;

import jg.rhex.compile.components.tnodes.TNode;

public class TBool extends TAtom<Boolean> {

  public TBool(Boolean value) {
    super(value);
  }
  
  public boolean equals(Object obj) {
    if (obj instanceof TNode) {
      return ((TNode) obj).getValue().equals(getValue());
    }
    return false;
  }

  @Override
  public String toString() {
    return "Bool ~ "+getActValue();
  }

}
