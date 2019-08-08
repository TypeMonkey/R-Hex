package jg.rhex.compile.verify.errors;

import java.util.Arrays;

import jg.rhex.compile.components.errors.RhexConstructionException;
import net.percederberg.grammatica.parser.Token;

public class UnfoundTypeException extends RhexConstructionException{

  public UnfoundTypeException(Token target, String desiredTypeName, String filename){
    super("Type Error! Cannot find the type '"+desiredTypeName+"' , at <ln:"+target.getStartLine()+">", filename);
  }
}
