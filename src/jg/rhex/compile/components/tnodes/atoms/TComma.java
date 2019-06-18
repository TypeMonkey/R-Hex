package jg.rhex.compile.components.tnodes.atoms;

public class TComma extends TAtom<String>{

  public TComma() {
    super(",");
  }

  @Override
  public String toString() {
    return "COMMA";
  }

}
