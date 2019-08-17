package jg.rhex.compile.components.comparsers;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.ListIterator;

import jg.rhex.common.Descriptor;
import jg.rhex.compile.ExpectedSet;
import jg.rhex.compile.components.ExpectedConstants;
import jg.rhex.compile.components.errors.FormationException;
import jg.rhex.compile.components.errors.RepeatedStructureException;
import jg.rhex.compile.components.errors.RepeatedTParamException;
import jg.rhex.compile.components.expr.GramPracConstants;
import jg.rhex.compile.components.expr.GramPracTokenizer;
import jg.rhex.compile.components.structs.FunctionInfo;
import jg.rhex.compile.components.structs.RClass;
import jg.rhex.compile.components.structs.RFunc;
import jg.rhex.compile.components.structs.RVariable;
import jg.rhex.compile.components.structs.TypeParameter;
import jg.rhex.compile.components.tnodes.atoms.TType;
import net.percederberg.grammatica.parser.ParserCreationException;
import net.percederberg.grammatica.parser.ParserLogException;
import net.percederberg.grammatica.parser.Token;
import net.percederberg.grammatica.parser.Tokenizer;

public final class ClassParser {
  
  /**
   * Parses a class from a Token source
   * 
   * @param iterator - the ListIterator to consume Tokens from.
   * @return the RClass object representing the class
   */
  public static RClass parseClass(ListIterator<Token> iterator, String fileName) {
    RClass rClass = formClassHeader(iterator, fileName);
    parseClassBody(iterator, rClass, fileName);
    return rClass;
  }

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
  public static RClass formClassHeader(ListIterator<Token> iterator, String fileName){
    ExpectedSet expected = new ExpectedSet(GramPracConstants.TPARAM, GramPracConstants.CLASS, GramPracConstants.INTER);
    expected.addAll(ExpectedConstants.CLASS_DESC);
        
    boolean isInterface = false;
    
    Token name = null;
    HashSet<Descriptor> classDescriptors = new HashSet<>();
    ArrayList<TType> extensions = new ArrayList<>();
    LinkedHashSet<TypeParameter> typeParameters = new LinkedHashSet<>();
    
    while (iterator.hasNext()) {
      Token current = iterator.next();
      if (expected.noContainsThrow(current, "ClassHeader", fileName)) {
        if (current.getId() == GramPracConstants.TPARAM) {
          //first, roll back iterator
          iterator.previous();
          
          TypeParameter parameter = TypeParser.parseTParam(iterator, fileName);
          System.out.println("----TPARAM: "+parameter.getHandle().getActValue().getImage());
          if (!typeParameters.add(parameter)) {
            throw new RepeatedTParamException(parameter.getHandle().getActValue(), fileName);
          }
          
          expected.replace(GramPracConstants.TPARAM, GramPracConstants.CLASS, GramPracConstants.INTER);
          expected.addAll(ExpectedConstants.CLASS_DESC);
        }
        else if (ExpectedConstants.CLASS_DESC.contains(current.getId())) {
          if (!classDescriptors.add(Descriptor.getEnumEquivalent(current.getId()))) {
            throw FormationException.createException("ClassHeader", current, expected, fileName);
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
            
            extensions.add(TypeParser.parseType(iterator, fileName));
            expected.replace(GramPracConstants.COMMA, GramPracConstants.OP_CU_BRACK);
          }
        }
        else if (current.getId() == GramPracConstants.COLON) {         
          //parse first type name (Assume as super)
          extensions.add(TypeParser.parseType(iterator, fileName));
          
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
      RClass rClass = new RClass(name, classDescriptors, extensions, isInterface);
      rClass.setTypeParameters(typeParameters);
      return rClass;
    }
    throw FormationException.createException("ClassHeader", iterator.previous(), expected, fileName);
  } 
  
  /**
   * Parses the body of a class.
   * 
   * The class in this Token source should begin with an opening '{' and be terminated by '}'.
   * 
   * This method consumes the terminating '}'
   * 
   * @param source - the ListIterator to consume Tokens from
   * @param container - the RClass to add class components to
   */
  public static void parseClassBody(ListIterator<Token> source, RClass container, String fileName) {
    ExpectedSet expected = new ExpectedSet(GramPracConstants.OP_CU_BRACK);
    
    while (source.hasNext()) {
      Token current = source.next();
      System.out.println("CURRENT: "+current);
      if (expected.noContainsThrow(current, "ClassBody", fileName)) {
        if (current.getId() == GramPracConstants.OP_CU_BRACK) {
          //initial class opening curly brace
          expected.replace(GramPracConstants.TPARAM, GramPracConstants.CL_CU_BRACK);
          expected.add(GramPracConstants.NAME);
          expected.addAll(ExpectedConstants.VAR_FUNC_DESC);
          expected.addAll(ExpectedConstants.CLASS_DESC);
        }
        else if (current.getId() == GramPracConstants.TPARAM) {
          //Class functions/methods are the only things inside a class that
          //can have type parameter declarations
          RFunc rFunc = FunctionParser.parseFunction(true, source, fileName);
          container.addMethod(rFunc);
        }
        else if (ExpectedConstants.CLASS_DESC.contains(current.getId()) || 
                 ExpectedConstants.VAR_FUNC_DESC.contains(current.getId()) || 
                 current.getId() == GramPracConstants.NAME) {
          
          //decide if this is a function or variable
          
          ArrayList<Token> unknownSequence = new ArrayList<>();
          unknownSequence.add(current);
          
          boolean terminatorFound = false;
          boolean isSemincolonTerm = false;
          while (source.hasNext()) {
            Token uCurrent = source.next();
            unknownSequence.add(uCurrent);
            if (uCurrent.getId() == GramPracConstants.SEMICOLON) {
              terminatorFound = true;
              isSemincolonTerm = true;
              break;
            }
            else if (uCurrent.getId() == GramPracConstants.OP_CU_BRACK) {
              terminatorFound = true;
              isSemincolonTerm = false;
              break;
            }
          }
          
          if (!terminatorFound) {
            throw FormationException.createException("ClassBody", source.previous(),
                new ExpectedSet(GramPracConstants.SEMICOLON, GramPracConstants.OP_CU_BRACK),
                fileName);
          }
          
          if (isSemincolonTerm) {
            //parse as variable declaration
            System.out.println("---VAR: "+unknownSequence);
            RVariable variable = VarDecParsers.parseVariable(unknownSequence.listIterator(), GramPracConstants.SEMICOLON, fileName);
            if (!container.addClassVar(variable)) {
              throw new RepeatedStructureException(variable.getIdentifier().getActValue(), "Variable", fileName);
            }
          }
          else {
            //parse as function
            RFunc func = FunctionParser.parseFunctionHeader(true, unknownSequence.listIterator(), fileName);
            source.previous(); //roll back main source for body parsing
            StatementParser.parseBlock(func.getBody(), source, fileName);
            container.addMethod(func);
          }
          
          //We don't have to change the expected values. 
        }
        else if (current.getId() == GramPracConstants.CL_CU_BRACK) {
          expected.clear();
          break;
        }
      }
    }
    
    if (!(expected.isEmpty() || expected.contains(-1))) {
      throw FormationException.createException("ClassBody", source.previous(), expected, fileName);
    }
  }
  
  /*
  public static void main(String [] args) throws Exception{
    String classHeader = "tparam<T: funcName(String  , Map!(Int, String)) -> String; >"
                       + "tparam<Q: extends java.lang.Object, Integer; >"
                       + "public static final class hello{";
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
    
    for (TypeParameter tpar : rClass.getTypeParameters()) {
      System.out.println("tparam:  HANDLE -> "+tpar.getIdentifier().getImage());
      System.out.println("   **function constraint");
      for(FunctionInfo info : tpar.getExpectedFunctions()){
        System.out.println("      -> "+info.getFunctionName().getImage());
      }
    }
    
    System.out.println(iterator.next());
  }
  */
}
