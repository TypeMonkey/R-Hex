package jg.rhex.compile.components.tnodes.atoms;

import jg.rhex.compile.components.tnodes.TNode;
import net.percederberg.grammatica.parser.Token;

public class TBool extends TAtom<Token> {

  public TBool(Token value) {
    super(value, value.getStartLine(), value.getStartColumn());
  }
  
  public boolean equals(Object obj) {
    if (obj instanceof TNode) {
      return ((TNode) obj).getValue().equals(getValue());
    }
    return false;
  }

  @Override
  public String toString() {
    return "Bool ~ "+getActValue();
  }

}
