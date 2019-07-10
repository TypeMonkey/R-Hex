package jg.rhex.compile.components.tnodes.atoms;

import jg.rhex.compile.components.tnodes.TOp;
import net.percederberg.grammatica.parser.Token;

public class TCParen extends TOp{

  public TCParen(Token clParen) {
    super(clParen);
  }

  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return getOpString()+" ~ TCP";
  }

}
