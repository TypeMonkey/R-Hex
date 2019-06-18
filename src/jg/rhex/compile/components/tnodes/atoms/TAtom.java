package jg.rhex.compile.components.tnodes.atoms;

import jg.rhex.compile.components.tnodes.TNode;

public abstract class TAtom<T> extends TNode{

  public TAtom(T value) {
    super(value);
  }

  public T getActValue(){
    return (T) getValue();
  }
  
}
