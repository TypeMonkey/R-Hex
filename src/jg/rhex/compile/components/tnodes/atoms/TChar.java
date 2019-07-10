package jg.rhex.compile.components.tnodes.atoms;

import net.percederberg.grammatica.parser.Token;

public class TChar extends TAtom<Token>{

  public TChar(Token value) {
    super(value);
  }

  @Override
  public String toString() {
    return "CHAR ~ "+getActValue();
  }
  
  public String getUnquotedString(){
    String init = getActValue().getImage();
    init = init.substring(1);
    init = init.substring(0, init.length());
    return init;
  }
  
}
