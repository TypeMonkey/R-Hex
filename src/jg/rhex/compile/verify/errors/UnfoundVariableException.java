package jg.rhex.compile.verify.errors;

import jg.rhex.compile.components.errors.RhexConstructionException;
import net.percederberg.grammatica.parser.Token;

public class UnfoundVariableException extends RhexConstructionException {

  public UnfoundVariableException(Token referral, String fileName) {
    super("Cannot find variable '"+referral.getImage()+"' at <ln:"+referral.getStartLine()+">", fileName);
  }

}
