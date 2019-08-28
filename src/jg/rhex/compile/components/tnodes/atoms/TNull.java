package jg.rhex.compile.components.tnodes.atoms;

import net.percederberg.grammatica.parser.Token;

public class TNull extends TAtom<Token>{

  public TNull(Token nullToken) {
    super(nullToken, nullToken.getStartLine(), nullToken.getStartColumn());
  }

  @Override
  public String toString() {
    return "NULL ~ "+getActValue().getImage();
  }

}
