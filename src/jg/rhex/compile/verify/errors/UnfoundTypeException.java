package jg.rhex.compile.verify.errors;

import java.util.Arrays;

import net.percederberg.grammatica.parser.Token;

public class UnfoundTypeException extends RuntimeException{

  public UnfoundTypeException(Token target, String desiredTypeName){
    super("Type Error! Cannot find the type '"+desiredTypeName+"' , at <ln:"+target.getStartLine()+">");
  }
}
