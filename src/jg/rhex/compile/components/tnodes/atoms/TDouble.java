package jg.rhex.compile.components.tnodes.atoms;

import net.percederberg.grammatica.parser.Token;

public class TDouble extends TNumber<Double>{

  public TDouble(Token token) {
    super(token);
  }

  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return "DOUBLE ~ "+getActValue();
  }

  @Override
  public Double getNumber() {
    return Double.parseDouble(getActValue().getImage());
  }

}
