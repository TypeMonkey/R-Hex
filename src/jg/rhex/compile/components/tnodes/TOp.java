package jg.rhex.compile.components.tnodes;

import net.percederberg.grammatica.parser.Token;

/**
 * Represents a binary, arithmetic operator (ex: + , - , * , / , %, =, ==, !=, ||, <, <=, >, >=, &&, | , &)
 * 
 * @author Jose
 *
 */
public class TOp extends TNode {
  
  public TOp(Token op) {
    super(op);
  }
  
  public Token getOperatorToken(){
    return (Token) getValue();
  }

  public String getOpString() {
    return ((Token) getValue()).getImage();
  }

  @Override
  public String toString() {
    return "OP ~ " + getOpString();
  }

}
