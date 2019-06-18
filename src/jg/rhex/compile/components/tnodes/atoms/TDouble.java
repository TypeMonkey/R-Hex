package jg.rhex.compile.components.tnodes.atoms;

public class TDouble extends TNumber<Double>{

  public TDouble(double value) {
    super(value);
  }

  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return "DOUBLE ~ "+getActValue();
  }

}
