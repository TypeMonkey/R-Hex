package jg.rhex.compile.components.tnodes.atoms;

public class TInt extends TNumber<Integer>{

  public TInt(int value) {
    super(value);
  }

  @Override
  public String toString() {
    return "INT ~ "+getActValue();
  }

}
