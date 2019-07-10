package jg.rhex.compile.components.tnodes.atoms;

import net.percederberg.grammatica.parser.Token;

public class TFloat extends TNumber<Float>{

  public TFloat(Token token) {
    super(token);
  }

  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return "FLOAT ~ "+getActValue();
  }

  @Override
  public Float getNumber() {
    return Float.parseFloat(getActValue().getImage());
  }
  
}
