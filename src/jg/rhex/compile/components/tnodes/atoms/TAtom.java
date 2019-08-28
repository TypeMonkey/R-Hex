package jg.rhex.compile.components.tnodes.atoms;

import jg.rhex.compile.components.tnodes.TNode;

public abstract class TAtom<T> extends TNode{

  public TAtom(T value, int lineNumber, int colNumber) {
    super(value, lineNumber, colNumber);
  }
 
  public T getActValue(){
    return (T) getValue();
  }
  
}
