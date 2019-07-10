package jg.rhex.compile.components.tnodes.atoms;

import net.percederberg.grammatica.parser.Token;

public class TString extends TAtom<Token>{

  public TString(Token value) {
    super(value);
  }

  @Override
  public String toString() {
    return "STR ~ "+getActValue();
  }

  public String getUnquotedString(){
    String init = getActValue().getImage();
    init = init.substring(1);
    init = init.substring(0, init.length());
    return init;
  }
}
