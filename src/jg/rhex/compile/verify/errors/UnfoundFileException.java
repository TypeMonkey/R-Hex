package jg.rhex.compile.verify.errors;

import jg.rhex.compile.components.errors.RhexConstructionException;
import net.percederberg.grammatica.parser.Token;

public class UnfoundFileException extends RhexConstructionException{
  
  public UnfoundFileException(Token location, String binaryFileName, String filename){
    super("Cannot find the file '"+binaryFileName+"' at <ln: "+location.getStartLine()+">", filename);
  }
  
}
