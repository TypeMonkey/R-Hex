package jg.rhex.compile.components.tnodes.atoms;

public class TCast extends TAtom<TType> {

  private TExpr target;

  public TCast(TExpr target, TType desiredType) {
    super(desiredType, desiredType.getLineNumber(), desiredType.getColNumber());
    this.target = target;
  }

  public void setTarget(TExpr target) {
    this.target = target;
  }

  public TExpr getTarget() {
    return target;
  }

  public TType getDesiredType() {
    return getActValue();
  }

  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return "CAST " + target + " AS | " + getDesiredType() + " |";
  }

}
