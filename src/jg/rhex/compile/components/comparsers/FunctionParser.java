package jg.rhex.compile.components.comparsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import jg.rhex.compile.ExpectedSet;
import jg.rhex.compile.components.errors.FormationException;
import jg.rhex.compile.components.errors.RepeatedTParamException;
import jg.rhex.compile.components.errors.RhexConstructionException;
import jg.rhex.compile.components.expr.GramPracConstants;
import jg.rhex.compile.components.structs.Descriptor;
import jg.rhex.compile.components.structs.RFunc;
import jg.rhex.compile.components.structs.RStatement;
import jg.rhex.compile.components.structs.RVariable;
import jg.rhex.compile.components.structs.TypeParameter;
import jg.rhex.compile.components.tnodes.atoms.TIden;
import jg.rhex.compile.components.tnodes.atoms.TType;
import jg.rhex.test.TestUtils;
import net.percederberg.grammatica.parser.Token;

public final class FunctionParser {
  
  /**
   * Parses a function from a Token source
   * @param isClassFunc - whether this function was declared inside a class
   * @param iterator - the ListIterator to consume Tokens from
   * @return the RFunc Object representing the parsed function
   */
  public static RFunc parseFunction(boolean isClassFunc, ListIterator<Token> iterator, String fileName) {
    RFunc rFunc = parseFunctionHeader(isClassFunc, iterator, fileName);
    StatementParser.parseBlock(rFunc.getBody(), iterator, fileName);
    return rFunc;
  }

  /**
   * Parses the header of an RHex function (pure functions, static methods, instance methods)
   * 
   * Function headers should be terminated by a '{'.
   * This method only consumes tokens upto '{'
   * 
   * @param iterator - the ListIterator to consume Tokens from
   * @return the RFunc object representing the header of the function
   */
  public static RFunc parseFunctionHeader(boolean isClassFunc, ListIterator<Token> iterator, String fileName){ 
    ExpectedSet expected = new ExpectedSet(GramPracConstants.TPARAM, 
                                           GramPracConstants.PUBL,
                                           GramPracConstants.PRIV,
                                           GramPracConstants.VOID,
                                           GramPracConstants.NAME);
    
    if (isClassFunc) {
      expected.addAll(GramPracConstants.ABSTRACT, GramPracConstants.FINAL);
    }
    
    
    RFunc function = new RFunc();    
        
    while (iterator.hasNext()) {
      Token current = iterator.next();
      System.out.println("--FUNCTION CURRENT: "+current);
      if (expected.noContainsThrow(current, "Function", fileName)) {
        if (current.getId() == GramPracConstants.TPARAM) {
          iterator.previous(); //roll back iterator
          TypeParameter parameter = TypeParser.parseTParam(iterator, fileName);
          if (!function.addTypeParameter(parameter)) {
            throw new RepeatedTParamException(parameter.getHandle().getActValue(), fileName);
          }
          
          expected.replace(GramPracConstants.TPARAM, 
                           GramPracConstants.PUBL,
                           GramPracConstants.PRIV,
                           GramPracConstants.VOID,
                           GramPracConstants.NAME,
                           GramPracConstants.ABSTRACT,
                           GramPracConstants.FINAL);
        }
        else if (current.getId() == GramPracConstants.PUBL || current.getId() == GramPracConstants.PRIV) {
          if (!function.addDescriptor(Descriptor.getEnumEquivalent(current.getId()))) {
            throw FormationException.createException("Function", current, expected, fileName);
          }
          expected.replace(GramPracConstants.VOID, GramPracConstants.NAME);
        }
        else if (current.getId() == GramPracConstants.ABSTRACT || current.getId() == GramPracConstants.FINAL) {
          //should only be valid for class function
          if (function.addDescriptor(Descriptor.getEnumEquivalent(current.getId()))) {
            throw FormationException.createException("Function", current, expected, fileName);
          }
          expected.replace(GramPracConstants.VOID, GramPracConstants.NAME);
        }
        else if (current.getId() == GramPracConstants.VOID) {
          TIden typeNameToken = new TIden(current);
          TType returnType = new TType(new ArrayList<>(Arrays.asList(typeNameToken)));
          function.setReturnType(returnType);
          
          expected.replace(GramPracConstants.NAME);
        }
        else if (current.getId() == GramPracConstants.NAME) {
          if (function.getReturnType() == null) {
            if (isClassFunc) {
              //this Function can be a constructor. Do a lookahead
              
              if (iterator.hasNext()) {
                Token next = iterator.next();
                iterator.previous(); //rollback parser
                if (next.getId() == GramPracConstants.OP_PAREN) {
                  //this is a constructor...
                  function.setName(current);
                  function.setAsConstructor(true);
                  expected.replace(GramPracConstants.OP_PAREN);
                  continue;
                }
              }
            }
            //then this is the return type
            System.out.println("* FUNCTION RETURN: "+current);
            iterator.previous(); //roll back iterator
            TType returnType = TypeParser.parseType(iterator, fileName);
            function.setReturnType(returnType);
            expected.replace(GramPracConstants.NAME);
            System.out.println("NEXT INDER: "+iterator.nextIndex());
          }
          else {
            //the this is the function's name
            function.setName(current);
            expected.replace(GramPracConstants.OP_PAREN);
          }
        }
        else if (current.getId() == GramPracConstants.OP_PAREN) {
          //function parameters begin
          
          if (iterator.hasNext()) {
            Token next = iterator.next();
            iterator.previous();
            if (next.getId() == GramPracConstants.CL_PAREN) {
              //function has no parameters
              expected.clear();
              break;
            }
            else {
              //function has parameters
              Escapist escapist = new Escapist("FunctionParameter", GramPracConstants.COMMA, GramPracConstants.CL_PAREN);
              
              List<Token> paramTokens = escapist.consume(iterator, fileName);
              iterator.previous(); //roll back parser
              
              RVariable param = VarDecParsers.parseVariable(paramTokens.listIterator(), GramPracConstants.COMMA, fileName); //parse first parameter
              int paramAmnt = 1;
              if (param.toBeInferred()) {
                throw new RhexConstructionException("The parameter '"+
                                    param.getIdentifier().getToken().getImage()+
                                    "' cannot be inferred, at <ln:"+param.getIdentifier().getToken().getStartLine()+">", fileName);
              }
              function.addStatement(param);
              
              
              boolean terminatorFound = false;
              
              while (iterator.hasNext()) {
                Token pCurrent = iterator.next();
                if (pCurrent.getId() == GramPracConstants.COMMA) {
                  paramTokens = escapist.consume(iterator, fileName);
                  RVariable variable = VarDecParsers.parseVariable(paramTokens.listIterator(), iterator.previous().getId(), fileName);
                  
                  function.addStatement(variable);
                  
                  paramAmnt++;
                }
                else if (pCurrent.getId() == GramPracConstants.CL_PAREN) {
                  terminatorFound = true;
                  iterator.previous(); //roll back iterator so main loop can get cl_paren
                  break;
                }
              }
              
              if (terminatorFound) {
                function.setParamAmnt(paramAmnt);
                expected.replace(GramPracConstants.CL_PAREN);
                continue;
              }
              throw FormationException.createException("Function", iterator.previous(), new ExpectedSet(GramPracConstants.CL_PAREN), fileName);
            }
          }
          
        }
        else if (current.getId() == GramPracConstants.CL_PAREN) {
          expected.replace(GramPracConstants.THROWS, GramPracConstants.OP_CU_BRACK);
        }
        else if (current.getId() == GramPracConstants.OP_CU_BRACK) {
          iterator.previous();
          expected.clear();
          break;
        }
        else if (current.getId() == GramPracConstants.THROWS) {
          TType firstException = TypeParser.parseType(iterator, fileName);
          function.addDeclaredException(firstException);
          
          ExpectedSet throwExpected = new ExpectedSet(GramPracConstants.COMMA, 
                                                      GramPracConstants.OP_CU_BRACK);
          
          while (iterator.hasNext()) {
            Token nextToken = iterator.next();
            throwExpected.noContainsThrow(nextToken, "Function", fileName);

            if (nextToken.getId() == GramPracConstants.OP_CU_BRACK) {
              iterator.previous();  //rollback iterator
              throwExpected.clear();
              break; 
            }
            else if (nextToken.getId() == GramPracConstants.COMMA) {
              TType exception = TypeParser.parseType(iterator, fileName);
              function.addDeclaredException(exception);
            }
          }
          
          if (throwExpected.isEmpty() || throwExpected.contains(-1)) {
            expected.replace(GramPracConstants.OP_CU_BRACK);
            continue;
          }
          throw FormationException.createException("Function", current, throwExpected, fileName);
       
        }
      }
    }
    
    //want to make sure that all expected tokens are met
    if (!(expected.isEmpty() || expected.contains(-1))) {
      throw FormationException.createException("Function", iterator.previous(), expected, fileName);
    }
    
    //at this point, the very next token from iterator should be an opening curly brace   
    return function;
  }
  
  public static void main(String [] args) {
    String target = "List!(String) hello(int val = 10, String x) throws java.lang.What, List!(What){"+
                    "    what();      "+
                    "    if(i < 10){ bye();}                "+
                    "                }";
    List<Token> tokens = TestUtils.tokenizeString(target);
    TestUtils.printTokens(tokens);
    
    RFunc rFunc = parseFunction(false, tokens.listIterator(), "Test");
    
    System.out.println("--------------------------");
    System.out.println(rFunc.getReturnType());
    
    System.out.println("-----PARAMS: "+rFunc.getParameterAmount());
    
    System.out.println("---EXCEPTIONS---");
    for (TType exception : rFunc.getDeclaredExceptions()) {
      System.out.println(exception.getBaseString());
    }
    System.out.println("---EXCEPTIONS DONE---");
    
    for(RStatement statement : rFunc.getBody().getStatements()) {
      System.out.println(statement);
    }
  }
  
}
