package jg.rhex.compile.components.tnodes.atoms;

public class TLong extends TNumber<Long>{

  public TLong(long value) {
    super(value);
  }

  @Override
  public String toString() {
   return "LONG ~ "+getActValue();
  }

}
