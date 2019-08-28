package jg.rhex.compile.components.tnodes;

public abstract class TNode {

  protected Object value;
  protected int lineNumber, colNumber;

  public TNode(Object value, int lineNumber, int colNumber) {
    this.value = value;
    this.lineNumber = lineNumber;
    this.colNumber = colNumber;
  }

  public Object getValue() {
    return value;
  }
  
  public int getLineNumber(){
    return lineNumber;
  }
  
  public int getColNumber(){
    return colNumber;
  }

  public abstract String toString();

}
