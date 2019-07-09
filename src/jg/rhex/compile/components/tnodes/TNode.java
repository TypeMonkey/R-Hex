package jg.rhex.compile.components.tnodes;

public abstract class TNode {

  protected Object value;

  public TNode(Object value) {
    this.value = value;
  }

  public Object getValue() {
    return value;
  }

  public abstract String toString();

}
