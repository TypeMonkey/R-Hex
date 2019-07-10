package jg.rhex.compile.components.tnodes.atoms;

import net.percederberg.grammatica.parser.Token;

public class TLong extends TNumber<Long>{

  public TLong(Token token) {
    super(token);
  }

  @Override
  public String toString() {
   return "LONG ~ "+getActValue();
  }

  @Override
  public Long getNumber() {
    String actualLong = getActValue().getImage();
    actualLong = actualLong.substring(0, actualLong.length());
    return Long.parseLong(actualLong);
  }

}
