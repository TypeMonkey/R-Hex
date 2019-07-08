package jg.rhex.compile.components;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import jg.rhex.compile.components.expr.GramPracTokenizer;
import net.percederberg.grammatica.parser.ParseException;
import net.percederberg.grammatica.parser.ParserCreationException;
import net.percederberg.grammatica.parser.Token;
import net.percederberg.grammatica.parser.Tokenizer;

/**
 * Static methods useful for testing self-contained parsing methods
 * @author Jose
 *
 */
public class TestUtils {
  
  public static List<Token> tokenizeString(String target){
    ArrayList<Token> tokens = new ArrayList<>();

    try {
      Tokenizer tokenizer = new GramPracTokenizer(new StringReader(target));
      Token token = null;
      while ((token = tokenizer.next()) != null) {
        tokens.add(token);
      }
    } catch (ParserCreationException | ParseException e) {
      e.printStackTrace();
    }

    return tokens;
  }

}
