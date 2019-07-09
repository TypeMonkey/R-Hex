package jg.rhex.compile.components.comparsers;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import jg.rhex.compile.ExpectedSet;
import jg.rhex.compile.components.errors.FormationException;
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
  private int terminatorID;
  private String context;
  
  public Escapist(Set<EscapeClosure> escapeClosures, int terminatorID, String context) {
    this.escapeClosures = escapeClosures;
    this.terminatorID = terminatorID;
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
    while (source.hasNext()) {
      Token current = source.next();
      tokens.add(current);
      if (current.getId() == terminatorID) {
        break;
      }
      else {
        //check for symbols needing escape
        if (escapeClosures.contains(EscapeClosure.PARENTHESES)) {
          
        }
        else if (escapeClosures.contains(EscapeClosure.CURLY_BRACES)) {

        }
        else if (escapeClosures.contains(EscapeClosure.SQUARE_BRACES)) {

        }
        else if (escapeClosures.contains(EscapeClosure.LESS_GREAT)) {

        }
      }
    }
    
    if (terminatorFound) {
      return tokens;
    }
    throw FormationException.createException(context, source.previous(), new ExpectedSet(terminatorID));
  }
  
}
