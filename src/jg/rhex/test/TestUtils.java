package jg.rhex.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
  
  public static List<Token> tokenizeFile(String file) throws ParseException{
    File actFile = new File(file);
    if (!actFile.exists()) {
      return null;
    }
    
    try {
      GramPracTokenizer tokenizer = new GramPracTokenizer(new FileReader(actFile));
      
      ArrayList<Token> tokens = new ArrayList<>();
      Token token = null;
      while ((token = tokenizer.next()) != null) {
        tokens.add(token);
      }
      
      return tokens;
      
    } catch (FileNotFoundException e) {
      throw new Error("Unexpected IO Error....");
    } catch (ParserCreationException e) {
      throw new Error("Unexpected error encountered at parser creation....");
    } 
  }
  
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
  
  public static void printTokens(List<Token> tokens) {
    System.out.println("------->TOKENS<-------");
    for (Token token : tokens) {
      System.out.println(token);
    }
    System.out.println("------->END TOKENS<-------");
  }

  public static void fail(String mess) {
    System.err.println(mess);
    System.exit(-1);
  }
  
  public static void succ(String mess) {
    System.out.println(mess);
  }
}
