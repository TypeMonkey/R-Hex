package jg.rhex.compile.components.comparsers;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import jg.rhex.compile.ExpectedSet;
import jg.rhex.compile.components.GramPracConstants;
import jg.rhex.compile.components.GramPracTokenizer;
import jg.rhex.compile.components.errors.FormationException;
import jg.rhex.compile.components.tnodes.atoms.TIden;
import jg.rhex.compile.components.tnodes.atoms.TType;
import net.percederberg.grammatica.parser.Token;
import net.percederberg.grammatica.parser.Tokenizer;

public class TypeNameParser {

  public static TType parseType(List<Token> tokenList){
    return parseType(tokenList.listIterator());
  }
  
  /**
   * Parses a type declaration/annotation.
   * 
   * R-Hex type annotations have the following grammar: 
   *  typeAnno = Identifier [ Generic ]
   *  Generic = '<' typeAnno (, typeAnno)* '>'
   *  
   * Note: The next call to next() on the iterator should be an Identifier Token
   * 
   * Once parseType() finishes, the next call to next() on ListIterator should
   * be the Token after the latest Identifier - if type has no generic arguments, or '>' - if type had generic arguments
   * 
   * Example:  Provided Token sequence in ListIterator: "Type1"
   *           After parseType(), a call to next() should throw a "NoSuchElementException" as there's no token
   *           after "Type1" - the latest Identifier
   *           
   *           Provided Token sequence in ListIterator: "Type1 varName"
   *           After parseType(), a call to next() should return "varName" as it's the Token
   *           after "Type1" - the latest Identifier
   *           
   *           Provided Token sequence in ListIterator: "Type1<String>"
   *           After parseType(), a call to next() should throw a "NoSuchElementException" as there's no token
   *           after ">" - the outermost generic argument bound
   *           
   *           Provided Token sequence in ListIterator: "Type1<List<Integer>, Set<Tears>, Map<Integher, String>> varName"
   *           After parseType(), a call to next() should return "varName" as it's the Token
   *           after ">" - the outermost generic argument bound
   * 
   * 
   * @param source - a ListIterator to consume tokens from
   * @return the TType parsed from this iterator
   */
  public static TType parseType(ListIterator<Token> source){
    ExpectedSet expected = new ExpectedSet(GramPracConstants.NAME);
    
    Token nameToken = source.next();
    
    TType baseType = null;
    if (expected.noContainsThrow(nameToken, "TypeAnnotation")) {
      baseType = new TType(new ArrayList<>(Arrays.asList(new TIden(nameToken))));
      baseType.formalizeRawTypeBody();
    }
    
    System.out.println("BASE: "+nameToken.getImage());
    
    if (source.next().getId() != GramPracConstants.LESS) {
      source.previous();
      System.out.println("---NOT GENERIC! ");
      return baseType;
    }
    source.previous();
    
    System.out.println("----GENERIC!");
    
    expected.replace(GramPracConstants.LESS, -1);
    
    //Stack<Integer> lastingGreats = new Stack<Integer>(); //for keeping track of '< >' pairs for generics
    
    
    while (source.hasNext()) {
      Token current = source.next();
      if (expected.noContainsThrow(current, "TypeAnnotation")) {
        if (current.getId() == GramPracConstants.LESS) {
          baseType.addGenericArgType(parseType(source));
          System.out.println("--- RETURNED FOR: "+nameToken.getImage());
          expected.replace(GramPracConstants.GREAT, GramPracConstants.COMMA);
          
          System.out.println("NEW EXPECTED: GREAT and COMMA");
          //lastingGreats.push(GramPracConstants.GREAT);
        }
        else if (current.getId() == GramPracConstants.GREAT) {
          System.out.println("GREAT ENCOUNTERED! Context: "+nameToken.getImage());
          expected.replace(-1);
          break;
        }
        else if (current.getId() == GramPracConstants.COMMA) {
          System.out.println("COMMA ENCOUNTERED! Context: "+nameToken.getImage()+" | "+source.next().getImage());
          source.previous();
          expected.replace(GramPracConstants.NAME);
        }
        else if (current.getId() == GramPracConstants.NAME) {
          System.out.println("NAME Encountered: "+current.getImage()+"| Context: "+nameToken.getImage());
          source.previous();
          baseType.addGenericArgType(parseType(source));
          expected.replace(GramPracConstants.COMMA, GramPracConstants.GREAT);
        }
      }
    }
    
    
    if (expected.isEmpty() || expected.contains(-1)) {
      return baseType;
    }
    
    throw FormationException.createException(source.previous(), expected);  
  }
  
  
  /*
  public static void main(String [] args) throws Exception{
    Tokenizer tokenizer = new GramPracTokenizer(new StringReader("Hello<T<W>>;"));
    List<Token> tokens = new ArrayList<>();
        
    Token token = null;
    while ((token = tokenizer.next()) != null) {
      tokens.add(token);
    }
    
    System.out.println("RAW: "+tokens);
    
    ListIterator<Token> iterator = tokens.listIterator();
    
    TType tType = parseType(iterator);
    
    System.out.println(" PARSED: "+tType+" ||||| LATEST: "+iterator.next());
  }
  */
  
  
}
