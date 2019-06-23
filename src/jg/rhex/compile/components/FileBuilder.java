package jg.rhex.compile.components;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import jg.rhex.compile.ExpectedSet;
import jg.rhex.compile.components.comparsers.BlockParser;
import jg.rhex.compile.components.comparsers.TypeNameParser;
import jg.rhex.compile.components.comparsers.VarDecParsers;
import jg.rhex.compile.components.errors.FormationException;
import jg.rhex.compile.components.structs.Descriptor;
import jg.rhex.compile.components.structs.RFunc;
import jg.rhex.compile.components.structs.RStateBlock;
import jg.rhex.compile.components.structs.RStateBlock.BlockType;
import jg.rhex.compile.components.structs.RVariable;
import jg.rhex.compile.components.structs.RhexFile;
import jg.rhex.compile.components.structs.UseDeclaration;
import jg.rhex.compile.components.tnodes.atoms.TIden;
import jg.rhex.compile.components.tnodes.atoms.TType;
import net.percederberg.grammatica.parser.ParseException;
import net.percederberg.grammatica.parser.ParserCreationException;
import net.percederberg.grammatica.parser.Token;
import net.percederberg.grammatica.parser.Tokenizer;

public class FileBuilder {

  /**
   * Location of the source file
   */
  private File location;
  
  /**
   * Contains the list of all tokens that was formed from the file
   */
  private List<Token> tokens;
  
  public FileBuilder(File location){
    this.location = location;
    tokens = new ArrayList<Token>();
  }
  
  public RhexFile constructFile(){
    RhexFile rhexFile = new RhexFile(FilenameUtils.getBaseName(location.getAbsolutePath()));
    
    try {
      absorbTokens();
    } catch (IOException e) {
      throw new RuntimeException("Error encountered reading '"+rhexFile.getFileName()+".rhex'");
    } catch (ParseException e) {
      throw new RuntimeException("Parsing Error: "+e.getMessage());
    } catch (ParserCreationException e) {
      throw new RuntimeException("Couldn't parse '"+rhexFile.getFileName()+".rhex'");
    }
    
    if (tokens.isEmpty()) {
      throw new IllegalArgumentException("The file '"+rhexFile.getFileName()+".rhex' is empty");
    }
    
    ListIterator<Token> tokenIterator = tokens.listIterator();
    scanFileBody(rhexFile, tokenIterator);
    
    return rhexFile;
  }
  
  /**
   * Scans the file body for use statements, classes, and file functions/variables
   * @param rhexFile - the RhexFile to add constructs to
   * @param iterator - the token iterator to consume tokens from
   */
  private void scanFileBody(RhexFile rhexFile, ListIterator<Token> iterator) {
    ExpectedSet expected = new ExpectedSet(GramPracConstants.USE, GramPracConstants.FUNC);
    expected.addAll(ExpectedConstants.VAR_FUNC_DESC);
    expected.addAll(ExpectedConstants.VISIBILITY);
    expected.addAll(ExpectedConstants.CLASS_DESC);
    
    while (iterator.hasNext()) {
      Token current = iterator.next();
      
      if (expected.noContainsThrow(current, null)) {
        if (current.getId() == GramPracConstants.USE) {
          rhexFile.addUseDec(formUseDeclaration(current, iterator));
        }
        else {
          //TODO: make helper methods to distinguish between classes, functions and variables
        }
      }
    }
  }
  
  /**
   * 
   * @param useToken
   * @param iterator
   * @return
   */
  private RFunc formFunction(Token useToken, ListIterator<Token> iterator){ 
    ExpectedSet expected = new ExpectedSet(GramPracConstants.PUBL,
                                           GramPracConstants.PRIV,
                                           GramPracConstants.VOID,
                                           GramPracConstants.NAME);
    
    RFunc function = new RFunc();    
        
    while (iterator.hasNext()) {
      Token current = iterator.next();
      if (expected.noContainsThrow(current, "Function")) {
        if (current.getId() == GramPracConstants.PUBL || current.getId() == GramPracConstants.PRIV) {
          function.addDescriptor(Descriptor.getEnumEquivalent(current.getId()));
          expected.replace(GramPracConstants.VOID, GramPracConstants.NAME, GramPracConstants.LESS);
        }
        else if (current.getId() == GramPracConstants.VOID) {
          TIden typeNameToken = new TIden(current);
          TType returnType = new TType(new ArrayList<>(Arrays.asList(typeNameToken)));
          function.setReturnType(returnType);
          
          expected.replace(GramPracConstants.NAME, GramPracConstants.LESS);
        }
        else if (current.getId() == GramPracConstants.NAME) {
          if (function.getName() == null) {
            //then this is the return type
            TType returnType = TypeNameParser.parseType(iterator);
            function.setReturnType(returnType);
            expected.replace(GramPracConstants.NAME, GramPracConstants.LESS);
          }
          else {
            //the this is the function's name
            function.setName(current);
            expected.replace(GramPracConstants.OP_PAREN);
          }
        }
        else if (current.getId() == GramPracConstants.LESS) {
          //function generic argument begins
          function.addGenericTypeArg(TypeNameParser.parseType(iterator));
          
          ExpectedSet genExpected = new ExpectedSet(GramPracConstants.GREAT, GramPracConstants.COMMA);
          
          while (iterator.hasNext()) {
            Token curGeneric = iterator.next();
            if (genExpected.noContainsThrow(curGeneric, "Function")) {
              if (curGeneric.getId() == GramPracConstants.GREAT) {
                genExpected.clear();
                break;
              }
              else if (curGeneric.getId() == GramPracConstants.COMMA) {
                function.addGenericTypeArg(TypeNameParser.parseType(iterator));
                genExpected.replace(GramPracConstants.GREAT, GramPracConstants.COMMA);
              }
            }
          }
          
          if (genExpected.isEmpty() || genExpected.contains(-1)) {
            expected.replace(GramPracConstants.NAME);
            continue;
          }
          throw FormationException.createException("Function", iterator.previous(), genExpected);
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
          expected.clear();
          expected.add(GramPracConstants.THROWS);
          break;
        }
        else if (current.getId() == GramPracConstants.THROWS) {
          TType firstException = TypeNameParser.parseType(iterator);
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
              TType exception = TypeNameParser.parseType(iterator);
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
      throw FormationException.createException("Function", useToken, expected);
    }
    
    //at this point, the very next token from iterator should be an opening curly brace
    
    BlockParser.parseBlock(function.getBody(), iterator);
    return function;
  }
  
  /**
   * Forms the upcoming sequence of tokens in iterator
   * as a UseDeclaration. 
   * 
   * Once this method finishes, the next token to be consumed from the provided
   * iterator will be the Token AFTER the concluding semicolon of this statement. 
   * So, it's possible that all tokens may be consumed in this iterator
   * 
   * @param useToken - the Token that has the "use" keyword
   * @param iterator - the Iterator to consume Tokens from
   * @return a UseDeclaration 
   */
  private UseDeclaration formUseDeclaration(Token useToken, Iterator<Token> iterator){ 
    HashSet<Integer> expected = new HashSet<>(Arrays.asList(GramPracConstants.STRING));
    
    Token fileToBeUsed = null;
    
    while (iterator.hasNext()) {
      Token current = iterator.next();

      if (expected.contains(current.getId())) {
        if (current.getId() == GramPracConstants.STRING) {
          fileToBeUsed = current;
          expected.clear();
          expected.add(GramPracConstants.SEMICOLON);
        }
        else if (current.getId() == GramPracConstants.SEMICOLON) {
          expected.clear();
          break;
        }
      }
      else {
        throw FormationException.createException(current, expected);
      }
    }
    
    if (expected.isEmpty() || expected.contains(-1)) {
      String actualFile = fileToBeUsed.getImage().substring(1);
      actualFile = actualFile.substring(0, actualFile.length()-1);
      if (actualFile.isEmpty()) {
        throw new IllegalArgumentException("Empty use-statement file at ln:"+useToken.getEndLine());
      }
      return new UseDeclaration(useToken, fileToBeUsed.getImage());
    }
    
    throw FormationException.createException("use-statement", useToken, expected);
  }

  /**
   * Reads and tokenizes the contents of this source file
   * @throws IOException
   * @throws ParseException
   * @throws ParserCreationException
   */
  private void absorbTokens() throws IOException, ParseException, ParserCreationException{
    FileReader fileReader = new FileReader(location);

    Tokenizer tokenizer = new GramPracTokenizer(fileReader);
    Token current = null;

    while ((current = tokenizer.next()) != null) {
      tokens.add(current);
    }


    fileReader.close();
  }
  
}
