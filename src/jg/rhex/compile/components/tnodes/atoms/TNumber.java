package jg.rhex.compile.components.tnodes.atoms;

public abstract class TNumber<T extends Number> extends TAtom<T>{

  public TNumber(T value) {
    super(value);
  }

}
