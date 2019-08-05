package jg.rhex.compile.verify.errors;

import net.percederberg.grammatica.parser.Token;

public class UnfoundFileException extends RuntimeException{
  
  public UnfoundFileException(Token location, String binaryFileName){
    super("Cannot find the file '"+binaryFileName+"' at <ln: "+location.getStartLine()+">");
  }
  
}
