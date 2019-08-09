package jg.rhex.compile.components;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import com.google.common.io.Files;

import jg.rhex.common.Descriptor;
import jg.rhex.compile.ExpectedSet;
import jg.rhex.compile.components.comparsers.StatementParser;
import jg.rhex.compile.components.comparsers.ClassParser;
import jg.rhex.compile.components.comparsers.Escapist;
import jg.rhex.compile.components.comparsers.FunctionParser;
import jg.rhex.compile.components.comparsers.TypeParser;
import jg.rhex.compile.components.comparsers.VarDecParsers;
import jg.rhex.compile.components.errors.EmptyExprException;
import jg.rhex.compile.components.errors.FormationException;
import jg.rhex.compile.components.errors.InvalidPlacementException;
import jg.rhex.compile.components.errors.RepeatedStructureException;
import jg.rhex.compile.components.errors.RepeatedTParamException;
import jg.rhex.compile.components.errors.RhexConstructionException;
import jg.rhex.compile.components.expr.GramPracConstants;
import jg.rhex.compile.components.expr.GramPracTokenizer;
import jg.rhex.compile.components.structs.RClass;
import jg.rhex.compile.components.structs.RFunc;
import jg.rhex.compile.components.structs.RStateBlock;
import jg.rhex.compile.components.structs.RStateBlock.BlockType;
import jg.rhex.compile.components.structs.RVariable;
import jg.rhex.compile.components.structs.RFile;
import jg.rhex.compile.components.structs.TypeParameter;
import jg.rhex.compile.components.structs.UseDeclaration;
import jg.rhex.compile.components.tnodes.atoms.TIden;
import jg.rhex.compile.components.tnodes.atoms.TType;
import jg.rhex.runtime.components.rhexspec.RhexFile;
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
  
  public RFile constructFile(){
    RFile rhexFile = new RFile(location);
    
    try {
      System.out.println("--------------------------------------------------------------------------");
      absorbTokens();
      System.out.println("--------------------------------------------------------------------------");
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
    scanFileBody(rhexFile, tokenIterator, location.getName());
        
    return rhexFile;
  }
  
  /**
   * Scans the file body for use statements, classes, and file functions/variables
   * @param rhexFile - the RhexFile to add constructs to
   * @param iterator - the token iterator to consume tokens from
   */
  private void scanFileBody(RFile rhexFile, ListIterator<Token> iterator, String fileName) {
    
    /*
     * Peek the first token and check if it's a package designation. 
     */
    if (iterator.hasNext()) {
      Token next = iterator.next();
      iterator.previous();
      if (next.getId() == GramPracConstants.PACK) {
        parsePackDesignation(rhexFile, iterator, fileName);
      }
    }
    
    ExpectedSet expected = new ExpectedSet(GramPracConstants.USE, 
        GramPracConstants.TPARAM, 
        GramPracConstants.CLASS, 
        GramPracConstants.NAME, 
        GramPracConstants.VOID);
    expected.addAll(ExpectedConstants.VAR_FUNC_DESC);
    expected.addAll(ExpectedConstants.VISIBILITY);
    expected.addAll(ExpectedConstants.CLASS_DESC);
    
    while (iterator.hasNext()) {
      Token current = iterator.next();
      
      if (expected.noContainsThrow(current, null, fileName)) {
        if (current.getId() == GramPracConstants.USE) {
          iterator.previous(); //roll back iterator
          rhexFile.addUseDec(StatementParser.formUseDeclaration(iterator, fileName));
        }
        else {
          //TODO: make helper methods to distinguish between classes, functions and variables
          //Gather Tokens until a ';' or '{'
          Escapist escapist = new Escapist("FileComponent", GramPracConstants.SEMICOLON, GramPracConstants.OP_CU_BRACK);
          List<Token> unknownComp = escapist.consume(iterator, fileName);
          unknownComp.add(0, current);
          
          System.out.println("ATTEMPT: "+unknownComp);
          
          //now, attempt to parse
          if (unknownComp.get(unknownComp.size()-1).getId() == GramPracConstants.SEMICOLON) {
            //then this is a statement. All external statements (outside functions and classes)
            //must be variable declarations
            RVariable variable = VarDecParsers.parseVariable(unknownComp.listIterator(), GramPracConstants.SEMICOLON, fileName);
            if(variable.getValue() == null){
              //File variables must be initialized
              throw new EmptyExprException(variable.getDescriptorToken(), "FileVariable", fileName);
            }
            
            if (!rhexFile.addVariable(variable)) {
              throw new RepeatedStructureException(variable.getIdentifier().getActValue(), "Variable", fileName);
            }
            
            if (variable.getProvidedType().getBaseString().equals("infer")) {
              String mess = "Only local variables can be inferred! at <ln:"+variable.getIdentifier().getToken().getStartLine()+">";
              throw new RhexConstructionException(mess, fileName);
            }
          }
          else {
            //then this is either a function declaration or a class declaration
            //first, attempt to parse as a function
            iterator.previous();
            try {
              RFunc func = FunctionParser.parseFunctionHeader(false, unknownComp.listIterator(), fileName);
              StatementParser.parseBlock(func.getBody(), iterator, fileName);
              rhexFile.addFunction(func);
            } catch (RhexConstructionException e) {
              //if function parsing fails, try to parse the header as a class declaration
              try {
                RClass rClass = ClassParser.formClassHeader(unknownComp.listIterator(), fileName);
                ClassParser.parseClassBody(iterator, rClass, fileName);
                if (!rhexFile.addClass(rClass)) {
                  throw new RepeatedStructureException(rClass.getName(), "Variable", fileName);
                }
              } catch (RhexConstructionException e2) {
                // If an error is thrown this far, then what ever this construct is...
                //it doesn't belong here.
                e2.printStackTrace();
                throw new InvalidPlacementException(current, fileName);
              }
            }
          }
        }
      }
    }
  }
  
  
  private void parsePackDesignation(RFile rhexFile, ListIterator<Token> iterator, String fileName) {
    ArrayList<TIden> packDesignation = new ArrayList<>();
    
    ExpectedSet expected = new ExpectedSet(GramPracConstants.PACK);
    
    while (iterator.hasNext()) {
      Token current = iterator.next();
      if (expected.noContainsThrow(current, "Package", fileName)) {
        if (current.getId() == GramPracConstants.PACK) {
          expected.replace(GramPracConstants.NAME);
        }
        else if (current.getId() == GramPracConstants.NAME) {
          packDesignation.add(new TIden(current));
          expected.replace(GramPracConstants.DOT, GramPracConstants.SEMICOLON);
        }
        else if (current.getId() == GramPracConstants.DOT) {
          expected.replace(GramPracConstants.NAME);
        }
        else if (current.getId() == GramPracConstants.SEMICOLON) {
          expected.clear();
          break;
        }
      }
    }
    
    rhexFile.setPackDesignation(packDesignation);
    
    if (! (expected.isEmpty() || expected.contains(-1))) {
      throw FormationException.createException("Package", iterator.previous(), expected, fileName);
    }
  }
  
  
  /**
   * Reads and tokenizes the contents of this source file.
   * 
   * Note: This method will filter lines that are commented (starts with "//") 
   * from tokenization and therefore, will not be part of the compilation process
   * 
   * @throws IOException
   * @throws ParseException
   * @throws ParserCreationException
   */
  private void absorbTokens() throws IOException, ParseException, ParserCreationException{
    BufferedReader fileReader = new BufferedReader(new FileReader(location));
    
    String wholeFile = "";
    String temp = null;
    while ((temp = fileReader.readLine()) != null) {
      if (!temp.trim().startsWith("//")) {
        System.out.println(" ADDING LINE: "+temp);
        wholeFile += temp+System.lineSeparator();
      }
    }
    
    fileReader.close();

    Tokenizer tokenizer = new GramPracTokenizer(new StringReader(wholeFile));
    Token current = null;

    while ((current = tokenizer.next()) != null) {
      tokens.add(current);
    }


    fileReader.close();
  }
  
}
