package jg.rhex.compile.components.tnodes.atoms;

public class TFloat extends TNumber<Float>{

  public TFloat(float value) {
    super(value);
    // TODO Auto-generated constructor stub
  }

  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return "FLOAT ~ "+getActValue();
  }
  
}
