package jg.rhex.compile.components.errors;

import net.percederberg.grammatica.parser.ParserLogException;

public class ExpressionParseException extends RhexConstructionException{

  private ParserLogException actualParseException;
  
  public ExpressionParseException(ParserLogException exception) {
    super(exception.getMessage());
  }
  
  public ParserLogException getActualException() {
    return actualParseException;
  }
}
