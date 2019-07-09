package jg.rhex.compile.components.comparsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Stack;

import jg.rhex.compile.ExpectedSet;
import jg.rhex.compile.components.TestUtils;
import jg.rhex.compile.components.errors.FormationException;
import jg.rhex.compile.components.expr.GramPracConstants;
import net.percederberg.grammatica.parser.Token;

/**
 * Consumes a sequence of Tokens until a set terminator is encountered.
 * 
 * The difference with the parsing strategy of Escapist is that it allows one 
 * to configure the parser to escape certain closures.
 * 
 * Ex: If we wanted to consume Tokens until a comma - ',' - we can set the 
 * configuration of Escapist so that it will consume Tokens until a comma, but
 * if it encounters a '(', it will keep consuming Tokens until a closing ')'
 * is found (this works recursively too). 
 * 
 * @author Jose Guaro
 *
 */
public class Escapist {

  public enum EscapeClosure{
    PARENTHESES,
    CURLY_BRACES,
    SQUARE_BRACES,
    LESS_GREAT;
  }
  
  private Set<EscapeClosure> escapeClosures;
  private Set<Integer> terminators;
  private String context;
  
  public Escapist(Set<Integer> terminators, String context) {
    this(new HashSet<>(Arrays.asList(EscapeClosure.PARENTHESES, 
                                     EscapeClosure.CURLY_BRACES, 
                                     EscapeClosure.SQUARE_BRACES, 
                                     EscapeClosure.LESS_GREAT)), terminators, context);
  }
  
  public Escapist(int terminatorID, String context) {
    this(new HashSet<>(Arrays.asList(EscapeClosure.PARENTHESES, 
                                     EscapeClosure.CURLY_BRACES, 
                                     EscapeClosure.SQUARE_BRACES, 
                                     EscapeClosure.LESS_GREAT)), new HashSet<>(Arrays.asList(terminatorID)), context);
  }
  
  public Escapist(Set<EscapeClosure> escapeClosures, Set<Integer> terminators, String context) {
    this.escapeClosures = escapeClosures;
    this.terminators = terminators;
    this.context = context;
  }
  
  public void setTerminators(Set<Integer> terminators){
    this.terminators = terminators;
  }
  
  /**
   * Consumes Tokens from the provided Token source until 
   * the terminating symbol is encountered.
   * 
   * Note: the terminating symbol will also be consumed and included in the 
   * returned Token list
   * 
   * @param source - the ListIterator to consume Tokens from
   * @return the 
   */
  public List<Token> consume(ListIterator<Token> source){
    ArrayList<Token> tokens = new ArrayList<>();

    boolean terminatorFound = false;

    Stack<Token> closures = new Stack<>();

    while (source.hasNext()) {
      Token current = source.next();
      tokens.add(current);
      System.out.println("----CONSUMPTION: "+current);
      if (terminators.contains(current.getId()) && closures.isEmpty()) {
        terminatorFound = true;
        break;
      }
      else if (current.getId() == GramPracConstants.OP_PAREN || 
          current.getId() == GramPracConstants.OP_CU_BRACK ||
          current.getId() == GramPracConstants.OP_SQ_BRACK ||
          current.getId() == GramPracConstants.LESS) {
        //check for symbols needing escape
        if (escapeClosures.contains(EscapeClosure.PARENTHESES) || 
            escapeClosures.contains(EscapeClosure.CURLY_BRACES) ||
            escapeClosures.contains(EscapeClosure.SQUARE_BRACES) ||
            escapeClosures.contains(EscapeClosure.LESS_GREAT)) {
          closures.push(current);
        }
      }  
      else if (current.getId() == GramPracConstants.CL_PAREN || 
          current.getId() == GramPracConstants.CL_CU_BRACK ||
          current.getId() == GramPracConstants.CL_SQ_BRACK ||
          current.getId() == GramPracConstants.GREAT) {
        //check for symbols needing escape

        if (escapeClosures.contains(EscapeClosure.PARENTHESES)) {
          if (closures.isEmpty()) {
            throw FormationException.createException(context, current, new ExpectedSet(GramPracConstants.CL_PAREN));
          }
          Token token = closures.pop();
          if (token.getId() != GramPracConstants.OP_PAREN) {
            throw FormationException.createException(context, current, new ExpectedSet(GramPracConstants.CL_PAREN));
          }
        }
        else if (escapeClosures.contains(EscapeClosure.CURLY_BRACES)) {
          if (closures.isEmpty()) {
            throw FormationException.createException(context, current, new ExpectedSet(GramPracConstants.CL_CU_BRACK));
          }
          Token token = closures.pop();
          if (token.getId() != GramPracConstants.OP_CU_BRACK) {
            throw FormationException.createException(context, current, new ExpectedSet(GramPracConstants.CL_CU_BRACK));
          }
        }
        else if (escapeClosures.contains(EscapeClosure.SQUARE_BRACES)) {
          if (closures.isEmpty()) {
            throw FormationException.createException(context, current, new ExpectedSet(GramPracConstants.CL_SQ_BRACK));
          }
          Token token = closures.pop();
          if (token.getId() != GramPracConstants.OP_CU_BRACK) {
            throw FormationException.createException(context, current, new ExpectedSet(GramPracConstants.CL_SQ_BRACK));
          }
        }
        else if (escapeClosures.contains(EscapeClosure.LESS_GREAT)) {
          if (closures.isEmpty()) {
            throw FormationException.createException(context, current, new ExpectedSet(GramPracConstants.GREAT));
          }
          Token token = closures.pop();
          if (token.getId() != GramPracConstants.LESS) {
            throw FormationException.createException(context, current, new ExpectedSet(GramPracConstants.GREAT));
          }
        }
      }     
    }

    if (terminatorFound) {
      return tokens;
    }
    
    throw FormationException.createException(context, source.previous(), terminators);
  }
  
  public static void main(String [] args) {
    List<Token> tokens = TestUtils.tokenizeString("String yo = hello(10,20,12) , ){");
    TestUtils.printTokens(tokens);
    
    Escapist escapist = new Escapist(GramPracConstants.COMMA, "Test");
    
    ListIterator<Token> iterator = tokens.listIterator();
    List<Token> actualTokens = escapist.consume(iterator);
    
    System.out.println("---FIRST PARAM: "+iterator.next());
    TestUtils.printTokens(actualTokens);
  }
}
