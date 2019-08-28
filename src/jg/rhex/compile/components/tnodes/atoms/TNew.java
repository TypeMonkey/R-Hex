package jg.rhex.compile.components.tnodes.atoms;

public class TNew extends TAtom<TFuncCall>{

  private TType binaryName;
  
  public TNew(TType binaryName, TFuncCall value) {
    super(value, binaryName.getLineNumber(), binaryName.getColNumber());
    this.binaryName = binaryName;
  }
  
  public TType getFullBinaryName(){
    return binaryName;
  }

  @Override
  public String toString() {
    return "NEW ~ | "+binaryName+" | "+getActValue();
  }

}
