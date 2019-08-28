package jg.rhex.compile.components.tnodes.atoms;

import jg.rhex.compile.components.tnodes.TNode;
import jg.rhex.compile.components.tnodes.TOp;

public class TUnary extends TAtom<TNode>{

  private TOp unaryOp;
  
  public TUnary(TNode value, TOp unary) {
    super(value, unary.getLineNumber(), unary.getColNumber());
    this.unaryOp = unary;
  }
  
  public TOp getUnaryOp(){
    return unaryOp;
  }

  @Override
  public String toString() {
    return "UNARY ~ [OP: "+unaryOp+"]  --  "+getActValue();
  }

}
