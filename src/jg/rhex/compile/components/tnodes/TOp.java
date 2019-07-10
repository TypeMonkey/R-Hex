package jg.rhex.compile.components.tnodes;

import jg.rhex.compile.components.tnodes.atoms.TAtom;
import net.percederberg.grammatica.parser.Token;

/**
 * Represents a binary, arithmetic operator (ex: + , - , * , / , %, =, new )
 * 
 * @author Jose
 *
 */
public class TOp extends TAtom<Token> {

  public TOp(Token op) {
    super(op);
  }

  public String getOpString() {
    return getActValue().getImage();
  }

  @Override
  public String toString() {
    return "OP ~ " + getActValue().getImage();
  }

}
