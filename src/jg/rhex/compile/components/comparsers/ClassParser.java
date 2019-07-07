package jg.rhex.compile.components.comparsers;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ListIterator;

import jg.rhex.compile.ExpectedSet;
import jg.rhex.compile.components.ExpectedConstants;
import jg.rhex.compile.components.GramPracConstants;
import jg.rhex.compile.components.GramPracTokenizer;
import jg.rhex.compile.components.errors.FormationException;
import jg.rhex.compile.components.errors.RepeatedTParamException;
import jg.rhex.compile.components.structs.Descriptor;
import jg.rhex.compile.components.structs.RClass;
import jg.rhex.compile.components.structs.TypeParameter;
import jg.rhex.compile.components.tnodes.atoms.TType;
import net.percederberg.grammatica.parser.Token;
import net.percederberg.grammatica.parser.Tokenizer;

public class ClassParser {

  /**
   * Parses a class' header from a Token source.
   * 
   * The Token source should have the terminating '{' at the end of the class header.
   * Once this method is done parsing, the iterator it was provided should provide 
   * the Token '{' in the next call to next(). 
   * 
   * @param iterator - the ListIterator to consume Tokens from.
   * @return the RClass object representing the class header
   */
  public static RClass formClassHeader(ListIterator<Token> iterator){
    ExpectedSet expected = new ExpectedSet(GramPracConstants.TPARAM, GramPracConstants.CLASS, GramPracConstants.INTER);
    expected.addAll(ExpectedConstants.CLASS_DESC);
    
    boolean isInterface = false;
    
    Token name = null;
    HashSet<Descriptor> classDescriptors = new HashSet<>();
    ArrayList<TType> extensions = new ArrayList<>();
    HashSet<TypeParameter> typeParameters = new HashSet<>();
    TType parent = null;
    
    while (iterator.hasNext()) {
      Token current = iterator.next();
      if (expected.noContainsThrow(current, "ClassHeader")) {
        if (current.getId() == GramPracConstants.TPARAM) {
          //first, roll back iterator
          iterator.previous();
          
          TypeParameter parameter = TypeParser.parseTParam(iterator);
          if (!typeParameters.add(parameter)) {
            throw new RepeatedTParamException(parameter.getIdentifier());
          }
          
          expected.replace(GramPracConstants.TPARAM, GramPracConstants.CLASS, GramPracConstants.INTER);
        }
        else if (ExpectedConstants.CLASS_DESC.contains(current.getId())) {
          if (!classDescriptors.add(Descriptor.getEnumEquivalent(current.getId()))) {
            throw FormationException.createException("ClassHeader", current, expected);
          }
          expected.replace(GramPracConstants.CLASS, GramPracConstants.INTER);
          expected.addAll(ExpectedConstants.CLASS_DESC);
        }
        else if (current.getId() == GramPracConstants.CLASS) {
          expected.replace(GramPracConstants.NAME);
        }
        else if (current.getId() == GramPracConstants.INTER) {
          expected.replace(GramPracConstants.NAME);
          isInterface = true;
        }
        else if (current.getId() == GramPracConstants.NAME) {
          if (name == null) {
            //this is for the class name
            name = current;
            expected.replace(GramPracConstants.COLON, GramPracConstants.OP_CU_BRACK);
          }
          else {
            //this is for type name (for parent classes)
            iterator.previous(); //roll back iterator
            
            extensions.add(TypeParser.parseType(iterator));
            expected.replace(GramPracConstants.COMMA, GramPracConstants.OP_CU_BRACK);
          }
        }
        else if (current.getId() == GramPracConstants.COLON) {         
          //parse first type name (Assume as super)
          parent = TypeParser.parseType(iterator);
          
          expected.replace(GramPracConstants.COMMA, GramPracConstants.OP_CU_BRACK);
        }
        else if (current.getId() == GramPracConstants.COMMA) {
          expected.replace(GramPracConstants.NAME);
        }
        else if (current.getId() == GramPracConstants.OP_CU_BRACK) {
          iterator.previous(); //roll back iterator
          expected.clear();
          break;
        }
      }
    }
    
    if (expected.isEmpty() || expected.contains(-1)) {
      RClass rClass = new RClass(name, classDescriptors, parent, extensions, isInterface);
      rClass.setTypeParameters(typeParameters);
      return rClass;
    }
    throw FormationException.createException("ClassHeader", iterator.previous(), expected);
  } 
  
  public static TypeParameter parseTypeParameter(ListIterator<Token> iterator){
    return null;
  }
  
  public static void main(String [] args) throws Exception{
    String classHeader = "public static final class hello{";
    Tokenizer tokenizer = new GramPracTokenizer(new StringReader(classHeader));
    
    ArrayList<Token> tokens = new ArrayList<>();
        
    Token token = null;
    while ((token = tokenizer.next()) != null) {
      tokens.add(token);
    }
    
    System.out.println("----TOKENS-----");
    System.out.println(tokens);
    
    
    ListIterator<Token> iterator = tokens.listIterator();
    RClass rClass = formClassHeader(iterator);
    
    System.out.println(iterator.next());
  }
}
