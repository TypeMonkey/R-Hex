package jg.rhex.compile.components.tnodes.atoms;

import jg.rhex.compile.components.tnodes.TOp;

public class TOParen extends TOp{

  public TOParen() {
    super("(");
  }

  @Override
  public String toString() {
    return "TOP"+value.toString();
  }

}
