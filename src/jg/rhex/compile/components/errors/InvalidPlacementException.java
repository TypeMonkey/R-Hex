package jg.rhex.compile.components.errors;

import net.percederberg.grammatica.parser.Token;

public class InvalidPlacementException extends RhexConstructionException{

  public InvalidPlacementException(Token token) {
    super("Invalid placemt of '"+token.getImage()+"' at <ln:"+token.getStartLine()+">");
  }

}
