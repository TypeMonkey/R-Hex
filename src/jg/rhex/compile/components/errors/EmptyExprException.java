package jg.rhex.compile.components.errors;

import net.percederberg.grammatica.parser.Token;

public class EmptyExprException extends RhexConstructionException{
  
  public EmptyExprException(Token latesToken, String context, String fileName){
    super("Empty expression encountered when forming '"+context+"' at <ln:"+latesToken.getEndLine()+">",fileName);
  }
  
}
