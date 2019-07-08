package jg.rhex.compile.components.comparsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

import jg.rhex.compile.ExpectedSet;
import jg.rhex.compile.components.errors.FormationException;
import jg.rhex.compile.components.errors.RepeatedTParamException;
import jg.rhex.compile.components.expr.GramPracConstants;
import jg.rhex.compile.components.structs.Descriptor;
import jg.rhex.compile.components.structs.RFunc;
import jg.rhex.compile.components.structs.RVariable;
import jg.rhex.compile.components.structs.TypeParameter;
import jg.rhex.compile.components.tnodes.atoms.TIden;
import jg.rhex.compile.components.tnodes.atoms.TType;
import net.percederberg.grammatica.parser.Token;

public final class FunctionParser {
  
  /**
   * Parses a function from a Token source
   * @param isClassFunc - whether this function was declared inside a class
   * @param iterator - the ListIterator to consume Tokens from
   * @return the RFunc Object representing the parsed function
   */
  public static RFunc parseFunction(boolean isClassFunc, ListIterator<Token> iterator) {
    RFunc rFunc = parseFunctionHeader(isClassFunc, iterator);
    StatementParser.parseBlock(rFunc.getBody(), iterator);
    rFunc.seal();
    return rFunc;
  }

  /**
   * Parses the header of an RHex function (pure functions, static methods, instance methods)
   * 
   * Function headers should be terminated by a closing ')' prior to '{'.
   * This method only consumes tokens upto ')'
   * 
   * @param iterator - the ListIterator to consume Tokens from
   * @return the RFunc object representing the header of the function
   */
  public static RFunc parseFunctionHeader(boolean isClassFunc, ListIterator<Token> iterator){ 
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
      if (expected.noContainsThrow(current, "Function")) {
        if (current.getId() == GramPracConstants.TPARAM) {
          TypeParameter parameter = TypeParser.parseTParam(iterator);
          if (!function.addTypeParameter(parameter)) {
            throw new RepeatedTParamException(parameter.getIdentifier());
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
          if (function.addDescriptor(Descriptor.getEnumEquivalent(current.getId()))) {
            throw FormationException.createException("Function", current, expected);
          }
          expected.replace(GramPracConstants.VOID, GramPracConstants.NAME);
        }
        else if (current.getId() == GramPracConstants.ABSTRACT || current.getId() == GramPracConstants.FINAL) {
          //should only be valid for class function
          if (function.addDescriptor(Descriptor.getEnumEquivalent(current.getId()))) {
            throw FormationException.createException("Function", current, expected);
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
          if (function.getName() == null) {
            //then this is the return type
            TType returnType = TypeParser.parseType(iterator);
            function.setReturnType(returnType);
            expected.replace(GramPracConstants.NAME);
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
            if (iterator.next().getId() == GramPracConstants.CL_PAREN) {
              //function has no parameters
              expected.clear();
              break;
            }
            else {
              //function has parameters
              iterator.previous(); //roll back iterator 
                            
              RVariable param = VarDecParsers.parseVariable(iterator); //parse first parameter
              int paramAmnt = 1;
              function.addStatement(param);
              
              ExpectedSet paramExpected = new ExpectedSet(GramPracConstants.COMMA, GramPracConstants.CL_PAREN);
              while (iterator.hasNext()) {
                Token curGeneric = iterator.next();
                if (paramExpected.noContainsThrow(curGeneric, "Function")) {
                  if (curGeneric.getId() == GramPracConstants.CL_PAREN) {
                    paramExpected.clear();
                    expected.replace(GramPracConstants.CL_PAREN);
                    iterator.previous();
                    break;
                  }
                  else if (curGeneric.getId() == GramPracConstants.COMMA) {
                    function.addStatement(VarDecParsers.parseVariable(iterator));
                    paramAmnt++;
                    paramExpected.replace(GramPracConstants.COMMA, GramPracConstants.CL_PAREN);
                  }
                }
              }
              
              if (paramExpected.isEmpty() || paramExpected.contains(-1)) {
                function.setParamAmnt(paramAmnt);
                continue;
              }
              throw FormationException.createException("Function", iterator.previous(), paramExpected);
            }
          }
          
        }
        else if (current.getId() == GramPracConstants.CL_PAREN) {
          expected.replace(GramPracConstants.THROWS, -1);
        }
        else if (current.getId() == GramPracConstants.THROWS) {
          TType firstException = TypeParser.parseType(iterator);
          function.addDeclaredException(firstException);
          
          ExpectedSet throwExpected = new ExpectedSet(GramPracConstants.COMMA, 
                                                      GramPracConstants.OP_CU_BRACK);
          
          while (iterator.hasNext()) {
            Token nextToken = iterator.next();
            throwExpected.noContainsThrow(nextToken, "Function");

            if (nextToken.getId() == GramPracConstants.OP_CU_BRACK) {
              iterator.previous();  //rollback iterator
              throwExpected.clear();
              break; 
            }
            else if (nextToken.getId() == GramPracConstants.COMMA) {
              TType exception = TypeParser.parseType(iterator);
              function.addDeclaredException(exception);
            }
          }
          
          if (throwExpected.isEmpty() || throwExpected.contains(-1)) {
            expected.clear();
            continue;
          }
          throw FormationException.createException("Function", current, throwExpected);
       
        }
      }
    }
    
    //want to make sure that all expected tokens are met
    if (!(expected.isEmpty() || expected.contains(-1))) {
      throw FormationException.createException("Function", iterator.previous(), expected);
    }
    
    //at this point, the very next token from iterator should be an opening curly brace   
    return function;
  }
  
}
