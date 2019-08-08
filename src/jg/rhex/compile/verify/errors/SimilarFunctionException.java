package jg.rhex.compile.verify.errors;

import jg.rhex.common.FunctionIdentity;
import jg.rhex.common.FunctionSignature;
import jg.rhex.compile.components.errors.RhexConstructionException;
import net.percederberg.grammatica.parser.Token;

public class SimilarFunctionException extends RhexConstructionException{

  public SimilarFunctionException(FunctionIdentity identity, Token location, String fileName) {
    super("The function declaration '"+identity+"' has been used before, at <ln: "+location.getStartLine()+"> , ", fileName);
  }
  
}
