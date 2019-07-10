package jg.rhex.compile.components.tnodes.atoms;

import net.percederberg.grammatica.parser.Token;

public class TInt extends TNumber<Integer>{

  private final boolean isCompilationInt; 
  private final int value;
    
  /**
   * Constructs a TInt.
   * 
   * This TInt shouldn't be used to represent a Token integer. 
   * This constructor should only be used within NewSeer for tracking purposes
   * 
   * @param value - the int value to set this TInt as
   */
  public TInt(int value) {
    super(null);
    this.isCompilationInt = true;
    this.value = value;
  }
  
  /**
   * Constructs a TInt that represents a Token integer.
   * 
   * @param intToken - the Token that's an integer
   */
  public TInt(Token intToken) {
    super(intToken);
    isCompilationInt = false;
    value = -1;
  }
  
  public boolean isCompInt() {
    return isCompilationInt;
  }

  @Override
  public String toString() {
    return "INT ~ "+getActValue()+" >> "+isCompilationInt;
  }

  @Override
  public Integer getNumber() {
    if (isCompilationInt) {
      return value;
    }
    return Integer.parseInt(getActValue().getImage());
  }

}
