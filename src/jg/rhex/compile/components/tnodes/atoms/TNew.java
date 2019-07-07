package jg.rhex.compile.components.tnodes.atoms;

import java.util.List;

import jg.rhex.compile.components.tnodes.TFuncCall;

public class TNew extends TAtom<TFuncCall>{

  private List<TIden> binaryName;
  
  public TNew(List<TIden> binaryName, TFuncCall value) {
    super(value);
    this.binaryName = binaryName;
  }
  
  public List<TIden> getFullBinaryName(){
    return binaryName;
  }

  @Override
  public String toString() {
    return "NEW ~ | "+binaryName+" | "+getActValue();
  }

}
