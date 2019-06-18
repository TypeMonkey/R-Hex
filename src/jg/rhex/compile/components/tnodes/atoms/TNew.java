package jg.rhex.compile.components.tnodes.atoms;

import jg.rhex.compile.components.tnodes.TFuncCall;

public class TNew extends TAtom<TFuncCall>{

  public TNew(TFuncCall value) {
    super(value);
  }

  @Override
  public String toString() {
    return "NEW ~ WITH "+getActValue();
  }

}
