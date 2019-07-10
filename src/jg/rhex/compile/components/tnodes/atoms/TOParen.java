package jg.rhex.compile.components.tnodes.atoms;

import jg.rhex.compile.components.tnodes.TOp;
import net.percederberg.grammatica.parser.Token;

public class TOParen extends TOp{

  public TOParen(Token opParen) {
    super(opParen);
  }

  @Override
  public String toString() {
    return "TOP ~ "+getOpString();
  }

}
