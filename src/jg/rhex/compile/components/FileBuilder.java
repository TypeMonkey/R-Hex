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
import jg.rhex.compile.components.comparsers.StatementParser;
import jg.rhex.compile.components.comparsers.ClassParser;
import jg.rhex.compile.components.comparsers.Escapist;
import jg.rhex.compile.components.comparsers.FunctionParser;
import jg.rhex.compile.components.comparsers.TypeParser;
import jg.rhex.compile.components.comparsers.VarDecParsers;
import jg.rhex.compile.components.errors.FormationException;
import jg.rhex.compile.components.errors.InvalidPlacementException;
import jg.rhex.compile.components.errors.RepeatedStructureException;
import jg.rhex.compile.components.errors.RepeatedTParamException;
import jg.rhex.compile.components.errors.RhexConstructionException;
import jg.rhex.compile.components.expr.GramPracConstants;
import jg.rhex.compile.components.expr.GramPracTokenizer;
import jg.rhex.compile.components.structs.Descriptor;
import jg.rhex.compile.components.structs.RClass;
import jg.rhex.compile.components.structs.RFunc;
import jg.rhex.compile.components.structs.RStateBlock;
import jg.rhex.compile.components.structs.RStateBlock.BlockType;
import jg.rhex.compile.components.structs.RVariable;
import jg.rhex.compile.components.structs.RhexFile;
import jg.rhex.compile.components.structs.TypeParameter;
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
    scanFileBody(rhexFile, tokenIterator, location.getName());
    
    rhexFile.seal();
    
    return rhexFile;
  }
  
  /**
   * Scans the file body for use statements, classes, and file functions/variables
   * @param rhexFile - the RhexFile to add constructs to
   * @param iterator - the token iterator to consume tokens from
   */
  private void scanFileBody(RhexFile rhexFile, ListIterator<Token> iterator, String fileName) {
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
            variable.seal();
            if (!rhexFile.addVariable(variable)) {
              throw new RepeatedStructureException(variable.getIdentifier().getActValue(), "Variable", fileName);
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
              func.seal();
            } catch (RhexConstructionException e) {
              //if function parsing fails, try to parse the header as a class declaration
              try {
                RClass rClass = ClassParser.formClassHeader(unknownComp.listIterator(), fileName);
                ClassParser.parseClassBody(iterator, rClass, fileName);
                if (!rhexFile.addClass(rClass)) {
                  throw new RepeatedStructureException(rClass.getName(), "Variable", fileName);
                }
                rClass.seal();
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

  /*
  public static void main(String [] args) {
    List<Token> tokens = TestUtils.tokenizeString("use hello, bye, what, hello from org;");
    
    System.out.println("--TOKENS--");
    for (Token token : tokens) {
      System.out.println(token);
    }
    System.out.println("--END TOKENS--");
    
    UseDeclaration declaration = formUseDeclaration(tokens.listIterator());
        
    System.out.println("-----INFOS:");
    System.out.println("--BASE: "+declaration.getBaseImport().getBaseString());
    for (TIden funcName : declaration.getImportedFuncs()) {
      System.out.println(funcName.getToken().getImage());
    }
  }
  */
  
}
