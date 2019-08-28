package jg.rhex.compile.components.tnodes.atoms;

import net.percederberg.grammatica.parser.Token;

public class TComma extends TAtom<String>{

  public TComma(Token comma) {
    super(comma.getImage(), comma.getStartLine(), comma.getStartColumn());
  }

  @Override
  public String toString() {
    return "COMMA";
  }

}
