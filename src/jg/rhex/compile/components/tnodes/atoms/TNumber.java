package jg.rhex.compile.components.tnodes.atoms;

import net.percederberg.grammatica.parser.Token;

public abstract class TNumber<T extends Number> extends TAtom<Token>{

  public TNumber(Token token) {
    super(token);
  }

  public abstract T getNumber();
}
