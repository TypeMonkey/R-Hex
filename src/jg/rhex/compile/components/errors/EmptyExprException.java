package jg.rhex.compile.components.errors;

import net.percederberg.grammatica.parser.Token;

public class EmptyExprException extends RuntimeException{
  
  public EmptyExprException(Token latesToken, String context){
    super("Empty expression encountered when forming '"+context+"' at <ln:"+latesToken.getEndLine()+">");
  }
  
}
