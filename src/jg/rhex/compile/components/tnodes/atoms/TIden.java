package jg.rhex.compile.components.tnodes.atoms;

import net.percederberg.grammatica.parser.Token;

public class TIden extends TAtom<Token>{

  public TIden(Token name) {
    super(name);
  }

  @Override
  public String toString() {
    return "IDEN ~ "+getActValue().getImage();
  }

}
