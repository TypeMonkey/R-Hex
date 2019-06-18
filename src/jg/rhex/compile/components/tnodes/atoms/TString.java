package jg.rhex.compile.components.tnodes.atoms;

public class TString extends TAtom<String>{

  public TString(String value) {
    super(value);
  }

  @Override
  public String toString() {
    return "STR ~ "+getActValue();
  }

}
