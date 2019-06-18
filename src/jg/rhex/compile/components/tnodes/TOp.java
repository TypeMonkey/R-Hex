package jg.rhex.compile.components.tnodes;

import jg.rhex.compile.components.tnodes.atoms.TAtom;

/**
 * Represents a binary, arithmetic operator (ex: + , - , * , / , %, =, new )
 * 
 * @author Jose
 *
 */
public class TOp extends TAtom<String> {

  public TOp(String op) {
    super(op);
  }

  public String getOpString() {
    return value.toString();
  }

  @Override
  public String toString() {
    return "OP ~ " + getActValue();
  }

}
