package jg.rhex.compile.components.comparsers;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;

import jg.rhex.compile.ExpectedSet;
import jg.rhex.compile.components.errors.FormationException;
import jg.rhex.compile.components.errors.RepeatedTParamException;
import jg.rhex.compile.components.expr.GramPracConstants;
import jg.rhex.compile.components.expr.GramPracTokenizer;
import jg.rhex.compile.components.structs.FunctionInfo;
import jg.rhex.compile.components.structs.TypeParameter;
import jg.rhex.compile.components.tnodes.TNode;
import jg.rhex.compile.components.tnodes.atoms.TIden;
import jg.rhex.compile.components.tnodes.atoms.TType;
import jg.rhex.test.TestUtils;
import net.percederberg.grammatica.parser.Token;
import net.percederberg.grammatica.parser.Tokenizer;

public final class TypeParser {

  public static TType parseType(List<Token> tokenList, String fileName){
    return parseType(tokenList.listIterator(), fileName);
  }
  
  /**
   * Parses a type declaration/annotation.
   * 
   * R-Hex type annotations have the following grammar: 
   *  typeAnno = "void" | (Identifier (. Identifier)* [ Generic ] [ArrayDimensions])
   *  Generic = '!(' typeAnno (, typeAnno)* ')'
   *  
   * 
   *  
   * Note: The next call to next() on the iterator should be an Identifier Token
   * 
   * Once parseType() finishes, the next call to next() on ListIterator should
   * be the Token after the latest Identifier - if type has no generic arguments. Or ')' - if type had generic arguments
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
  public static TType parseType(ListIterator<Token> source, String fileName){
    ExpectedSet expected = new ExpectedSet(GramPracConstants.NAME, GramPracConstants.VOID);
    
    ArrayList<TIden> fullTypeName = new ArrayList<>();
    TType baseType = null;
    int arrayDimensions = 0;
           
    //Stack<Integer> lastingGreats = new Stack<Integer>(); //for keeping track of '< >' pairs for generics
        
    while (source.hasNext()) {
      Token current = source.next();
      System.out.println("--T->CURRENT: "+current);
      if (expected.noContainsThrow(current, "TypeAnnotation", fileName)) {
        if (current.getId() == GramPracConstants.VOID) {
          fullTypeName.add(new TIden(current));
          expected.clear();
          break;
        }
        else if (current.getId() == GramPracConstants.OP_PAREN) {
          if (baseType == null) {
            baseType = new TType(fullTypeName);
          }
          baseType.addGenericArgType(parseType(source, fileName));
          System.out.println("--- RETURNED FOR: "+fullTypeName.get(0));
          expected.replace(GramPracConstants.CL_PAREN, GramPracConstants.COMMA);
          
          System.out.println("NEW EXPECTED: CL_PAREN and COMMA");
          //lastingGreats.push(GramPracConstants.GREAT);
        }
        else if (current.getId() == GramPracConstants.DOT) {
          expected.replace(GramPracConstants.NAME);
        }
        else if (current.getId() == GramPracConstants.CL_PAREN) {
          System.out.println("CL PAREN! Context: "+fullTypeName.get(0));
          
          if (source.hasNext()) {
            System.out.println("---CHECKING ARR BOUND");
            Token next = source.next();
            source.previous();  //roll back iterator
            if (next.getId() == GramPracConstants.OP_SQ_BRACK) {
              expected.replace(GramPracConstants.OP_SQ_BRACK);
              continue;
            }
          }
          
          expected.clear();
          break;
        }
        else if (current.getId() == GramPracConstants.COMMA) {
          System.out.println("COMMA ENCOUNTERED! Context: "+fullTypeName.get(0)+" | "+source.next().getImage());
          source.previous();
          expected.replace(GramPracConstants.NAME);
        }
        else if (current.getId() == GramPracConstants.BANG) {
          expected.replace(GramPracConstants.OP_PAREN);
        }
        else if (current.getId() == GramPracConstants.OP_SQ_BRACK) {
          expected.replace(GramPracConstants.CL_SQ_BRACK);
        }
        else if (current.getId() == GramPracConstants.CL_SQ_BRACK) {
          arrayDimensions++;
          
          if (source.hasNext()) {
            System.out.println("---CHECKING ARR SEQ BOUND");
            Token next = source.next();
            source.previous();  //roll back iterator
            if (next.getId() == GramPracConstants.OP_SQ_BRACK) {
              expected.replace(GramPracConstants.OP_SQ_BRACK);
              continue;
            }
          }
          
          expected.clear();
          break;          
        }
        else if (current.getId() == GramPracConstants.NAME) {
          if (baseType == null) {
            fullTypeName.add(new TIden(current));
            if (source.hasNext()) {
              Token potential = source.next();
              source.previous(); //roll back iterator
              if (potential.getId() != GramPracConstants.BANG && 
                  potential.getId() != GramPracConstants.DOT && 
                  potential.getId() != GramPracConstants.OP_SQ_BRACK) {
                expected.clear();
                break;
              }
              
            }
            expected.replace(GramPracConstants.DOT, GramPracConstants.BANG, GramPracConstants.OP_SQ_BRACK, -1);
          }
          else {
            source.previous();
            baseType.addGenericArgType(parseType(source, fileName));
            expected.replace(GramPracConstants.COMMA, GramPracConstants.CL_PAREN);
          }
          
          System.out.println("NAME Encountered: "+current.getImage()+"| Context: "+fullTypeName.get(0));
        }
      }
    }
    
    if (expected.isEmpty() || expected.contains(-1)) {
      if (baseType == null) {
        baseType = new TType(fullTypeName);
      }
      baseType.setArrayDimensions(arrayDimensions);
      return baseType;
    }
    
    throw FormationException.createException(source.previous(), expected, fileName);  
  }
  
  /**
   * Parses a type parameter declaration ("tparam").
   *
   * Syntax for tparam is:
   * 
   * tparam '<' TypeHandle ("," TypeHandle)* ':' [typeName] ("," typeName)*  '>'
   * 
   * The terminating '>' is consumed by this method
   * 
   * @param source
   * @return
   */
  public static TypeParameter parseTParam(ListIterator<Token> source, String fileName){   
    TypeParameter typeParameter = null;
    ExpectedSet expected = new ExpectedSet(GramPracConstants.TPARAM);
    
    HashSet<TIden> handles = new HashSet<>();
    
    while (source.hasNext()) {
      Token current = source.next();
      if (expected.noContainsThrow(current, "TParam", fileName)) {
        if (current.getId() == GramPracConstants.TPARAM) {
          if (typeParameter == null) {
            //then this is the initial tparam check
            expected.replace(GramPracConstants.LESS);
          }
        }
        else if (current.getId() == GramPracConstants.LESS) {
          expected.replace(GramPracConstants.NAME);
        }
        else if (current.getId() == GramPracConstants.NAME) {
          if (typeParameter == null) {
            //this means that the name is the type parameter's variable handle
            if(!handles.add(new TIden(current))){
              throw new RepeatedTParamException(current, fileName);
            }
            expected.replace(GramPracConstants.COLON);
          }
          else {
            //this is for type extension
            source.previous(); //roll back iterator first

            TType reqExtended = parseType(source, fileName);

            System.out.println("---CURRENT: "+reqExtended);

            if (!typeParameter.addReqClass(reqExtended)) {
              throw new RepeatedTParamException(reqExtended, fileName);
            }
            expected.replace(GramPracConstants.NAME, 
                GramPracConstants.TPARAM,
                GramPracConstants.COMMA,
                GramPracConstants.GREAT);
          }
        }
        else if (current.getId() == GramPracConstants.EXTNDS) {
          
          TType extendedType = parseType(source, fileName);
          if (!typeParameter.addReqClass(extendedType)) {
            throw new RepeatedTParamException(extendedType, fileName);
          }
          
          ExpectedSet extExpected = new ExpectedSet(GramPracConstants.COMMA, GramPracConstants.SEMICOLON);
          boolean terminatorFound = false;
          
          while (source.hasNext()) {
            Token extCurrent = source.next();
            if (extExpected.noContainsThrow(extCurrent, "TParam", fileName)) {
              if (extCurrent.getId() == GramPracConstants.COMMA) {
                extExpected.replace(GramPracConstants.NAME);
              }
              else if (extCurrent.getId() == GramPracConstants.NAME) {
                source.previous(); //rollback iterator
                TType seqType = parseType(source, fileName);
                if (!typeParameter.addReqClass(seqType)) {
                  throw new RepeatedTParamException(seqType, fileName);
                }
                
                extExpected.replace(GramPracConstants.SEMICOLON);
              }
              else if (extCurrent.getId() == GramPracConstants.SEMICOLON) {
                terminatorFound = true;
                break;
              }
            }
          }
          
          if (!terminatorFound) {
            throw FormationException.createException("TParam", source.previous(),
                new ExpectedSet(GramPracConstants.SEMICOLON), fileName);
          }
          else {
            expected.replace(GramPracConstants.EXTNDS, GramPracConstants.NAME, GramPracConstants.GREAT,
                             GramPracConstants.TPARAM);
          }
        }
        else if (current.getId() == GramPracConstants.GREAT) {
          expected.clear();
          break;
        }
        else if (current.getId() == GramPracConstants.COMMA) {
          expected.replace(GramPracConstants.NAME);
        }
        else if (current.getId() == GramPracConstants.COLON) {
          typeParameter = new TypeParameter(handles);
          expected.replace(GramPracConstants.NAME, GramPracConstants.EXTNDS, GramPracConstants.TPARAM, GramPracConstants.GREAT);
        }
      }
    }
    
    if (expected.isEmpty() || expected.contains(-1)) {
      return typeParameter;
    }
    throw FormationException.createException("TParam", source.previous(), expected, fileName);
  }
  
  
  /**
   * Parses a function constraint (for tparams).
   * 
   * The function constraint should be terminated by a semicolon. This method will
   * consume such semicolon.
   * 
   * Syntax for function constraint:
   * 
   * functionName '(' [type_name (',' type_name)*] ')' '->' type_name ';'
   * 
   * @param iterator - the ListIterator to consume Tokens from
   * @return the FunctionInfo representing the required functions
   */
  /*
  public static FunctionInfo parseFunctionConstraint(ListIterator<Token> iterator, String fileName){
    ExpectedSet expected = new ExpectedSet(GramPracConstants.NAME);
    
    Token functionName = null;
    TType returnType = null;
    
    ArrayList<TType> parameters = new ArrayList<>();
    
    while (iterator.hasNext()) {
      Token current = iterator.next();
      System.out.println("----> CONSTRAINT: "+current);
      if (expected.noContainsThrow(current, "FunctionConstraint", fileName)) {
        if (current.getId() == GramPracConstants.NAME) {
          functionName = current;
          expected.replace(GramPracConstants.OP_PAREN);
        }
        else if (current.getId() == GramPracConstants.OP_PAREN) {
          //peek - if possible - at the next token if it's a closing parenthesis
          if (iterator.hasNext()) {
            Token next = iterator.next();
            iterator.previous(); //roll back iterator
            if (next.getId() == GramPracConstants.CL_PAREN) {
              //no parameter for this function constraint
              expected.replace(GramPracConstants.CL_PAREN);
              continue;
            }
          }
          else {
            continue;
          }
          
          //start parsing required types
          TType paramType = TypeParser.parseType(iterator, fileName);
          parameters.add(paramType);
          
          boolean closingParenFound = false;
          
          while (iterator.hasNext()) {
            Token paramCurrent = iterator.next();
            if (paramCurrent.getId() == GramPracConstants.COMMA) {
              paramType = TypeParser.parseType(iterator, fileName);
              parameters.add(paramType);
            }
            else if (paramCurrent.getId() == GramPracConstants.CL_PAREN) {
              closingParenFound = true;
              break;
            }
            else {
              throw FormationException.createException("FuncConstraints", paramCurrent, 
                  new ExpectedSet(GramPracConstants.COMMA, GramPracConstants.CL_PAREN), fileName);
            }
          }
          
          if (closingParenFound) {
            iterator.previous(); //roll back iterator
            expected.replace(GramPracConstants.CL_PAREN);
          }
          
        }
        else if (current.getId() == GramPracConstants.CL_PAREN) {
          expected.replace(GramPracConstants.ARROW);
        }
        else if (current.getId() == GramPracConstants.ARROW) {
          returnType = parseType(iterator, fileName);
          
          expected.replace(GramPracConstants.SEMICOLON);
        }
        else if (current.getId() == GramPracConstants.SEMICOLON) {
          expected.clear();
          break;
        }
      }
    }
    
    if (expected.isEmpty() || expected.contains(-1)) {
      return new FunctionInfo(functionName, returnType, parameters.toArray(new TType[parameters.size()]));
    }
    throw FormationException.createException("FuncConstraints", iterator.previous(), expected, fileName);
  }
  */
  
  //public static void main(String [] args) {
    /*FUNCTION CONSTRAINT TESTER
    List<Token> tokens = TestUtils.tokenizeString("funcName(String  , Map!(Int, String)) -> String; -");
    
    System.out.println("-----TOKENS-----");
    for (Token token : tokens) {
      System.out.println(token);
    }
    System.out.println("-----END TOKENS-----");

    ListIterator<Token> iterator = tokens.listIterator();
    FunctionInfo functionInfo = parseFunctionConstraint(iterator);
    
    System.out.println("REQ FUN INFOS:");
    System.out.println("NAME: "+functionInfo.getFunctionName());
    System.out.println("RETURN: "+functionInfo.getReturnType());
    
    System.out.println("----ARGS: ");
    for (TType par : functionInfo.getParameterTypes()) {
      System.out.println(par);
    }
    
    System.out.println("---LATEEST: "+iterator.next());
    */
    
    /* TPARAM TESTER
    List<Token> tokens = TestUtils.tokenizeString("tparam<T:"
        + " hello(java.lang.String  , Map!(Int, String)) -> String; "
        + "bye(java.lang.String  , Map!(Int, String)) -> String; "
        + "extends java.lang.Object;"
        + "  tparam<Q: what()->Object; >"
        + "   yo(List!(Q)) -> Double;"
        + ">  class");
    
    System.out.println("-----TOKENS-----");
    for (Token token : tokens) {
      System.out.println(token);
    }
    System.out.println("-----END TOKENS-----");

    ListIterator<Token> iterator = tokens.listIterator();
    TypeParameter functionInfo = parseTParam(iterator);
    
    System.out.println("TypeParameter FUN INFOS:");
    System.out.println("NAME: "+functionInfo.getIdentifier().getImage());
    
    System.out.println("---EXPECTED FUNCTIONS: ");
    for (FunctionInfo info : functionInfo.getExpectedFunctions()) {
      System.out.println("--- "+info.getFunctionName().getImage()+"() :");
      System.out.println("        -> "+Arrays.toString(info.getParameterTypes()));
      System.out.println("        -> RETURN: "+info.getReturnType());
    }
    
    for(TType ext : functionInfo.getRequiredClasses()){
      System.out.println(" --- MANDATED: "+ext.getBaseString());
    }
    
    
    System.out.println("---LATEST: "+iterator.next());
    */
    
    /* FUNCTION CONSTRAINT EQUALITY TESTER
    String target = "hello(java.lang.String  , Map!(Int, String)) -> String;";
    FunctionInfo info1 = parseFunctionConstraint(TestUtils.tokenizeString(target).listIterator());
    FunctionInfo info2 = parseFunctionConstraint(TestUtils.tokenizeString(target).listIterator());
    
    HashSet<FunctionInfo> infos = new HashSet<>();
    infos.add(info1);
    System.out.println(infos.add(info2));
    */
    
    /* SIMPLE TYPE PARSE
    String target = "boolean hello()";
    ListIterator<Token> listIterator = TestUtils.tokenizeString(target).listIterator();
    TestUtils.printTokens(TestUtils.tokenizeString(target));
    TType info1 = parseType(listIterator);
    
    System.out.println(info1.getBaseString());
    System.out.println(listIterator.nextIndex());
    */
  //}
  
}
