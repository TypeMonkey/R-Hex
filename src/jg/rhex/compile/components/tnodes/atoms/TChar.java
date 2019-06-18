package jg.rhex.compile.components.tnodes.atoms;

public class TChar extends TAtom<Character>{

  public TChar(char value) {
    super(value);
  }

  @Override
  public String toString() {
    return "CHAR ~ "+getActValue();
  }
  
  
  
}
