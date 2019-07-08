package jg.rhex.compile.components.expr;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import jg.rhex.compile.components.tnodes.TNode;
import net.percederberg.grammatica.parser.ParserCreationException;
import net.percederberg.grammatica.parser.ParserLogException;
import net.percederberg.grammatica.parser.Token;

public final class ExprParser {
  
  private static ExprParser universalExprParser;
  private static boolean isInitialized;
  
  static{
    //DEVELOPMENT CODE. TODO: Delete later
    try {
      initUniversalParser();
    } catch (ParserCreationException e) {
      e.printStackTrace();
    }
  }
  
  private GramPracParser parser;
  private NewSeer seer;
  private ASTBuilder astBuilder;
    
  protected ExprParser(){}
  
  public List<TNode> parseExpression(List<Token> tokens) throws ParserLogException{
    seer.reset();
    
    try {
      parser.parseFromTokenList(tokens);
      Deque<TNode> tNodes = astBuilder.build(seer.getStackNodes());
      
      return new ArrayList<>(tNodes);
    } catch (ParserCreationException e) {
      throw new IllegalStateException("Unexpected exception! Failed to create expression parser.");
    } 
  }

  public static ExprParser initUniversalParser() throws ParserCreationException{
    if (!isInitialized) {
      ExprParser exprParser = new ExprParser();
      
      exprParser.astBuilder = new ASTBuilder();
      exprParser.seer = new NewSeer();
      exprParser.parser = new GramPracParser(null, exprParser.seer);
      
      
      universalExprParser = exprParser;
      isInitialized = true;
      
    }
    return universalExprParser;
  }
  
  public static ExprParser getUniversalParser(){
    return universalExprParser;
  }
  
  public static boolean isInitialize(){
    return isInitialized;
  }
  
}
