package jg.rhex.compile.components.tnodes.atoms;

import java.util.Objects;

import net.percederberg.grammatica.parser.Token;

public class TIden extends TAtom<Token>{

  public TIden(Token name) {
    super(name);
  }
  
  public boolean equals(Object object){
    if (object instanceof TIden) {
      TIden iden = (TIden) object;
      
      return iden.getActValue().getImage().equals(getActValue().getImage());
    }
    return false;
  }
  
  public Token getToken(){
    return getActValue();
  }
  
  public int hashCode() {
    return Objects.hash(getActValue().getImage());
  }

  @Override
  public String toString() {
    return "IDEN ~ "+getActValue().getImage();
  }

}
