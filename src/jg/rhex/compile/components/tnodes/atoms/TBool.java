package jg.rhex.compile.components.tnodes.atoms;

public class TBool extends TAtom<Boolean> {

  public TBool(Boolean value) {
    super(value);
  }

  @Override
  public String toString() {
    return "Bool ~ "+getActValue();
  }

}
