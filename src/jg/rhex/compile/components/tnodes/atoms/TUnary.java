package jg.rhex.compile.components.tnodes.atoms;

import jg.rhex.compile.components.tnodes.TNode;
import jg.rhex.compile.components.tnodes.TOp;

public class TUnary<T> extends TAtom<TNode>{

  private TOp unaryOp;
  
  public TUnary(TNode value, TOp unary) {
    super(value);
    this.unaryOp = unary;
  }

  @Override
  public String toString() {
    return "UNARY ~ [OP: "+unaryOp+"]  --  "+getActValue();
  }

}
