package jg.rhex.test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jg.rhex.compile.components.comparsers.StatementParser;
import jg.rhex.compile.components.comparsers.TypeParser;
import jg.rhex.compile.components.comparsers.VarDecParsers;
import jg.rhex.compile.components.errors.RhexConstructionException;
import jg.rhex.compile.components.expr.GramPracConstants;
import jg.rhex.compile.components.structs.RVariable;
import jg.rhex.compile.components.structs.UseDeclaration;
import jg.rhex.compile.components.tnodes.TExpr;
import jg.rhex.compile.components.tnodes.atoms.TIden;
import jg.rhex.compile.components.tnodes.atoms.TInt;
import jg.rhex.compile.components.tnodes.atoms.TNew;
import jg.rhex.compile.components.tnodes.atoms.TType;
import net.percederberg.grammatica.parser.ParseException;
import net.percederberg.grammatica.parser.Token;

/**
 * Tests on the correctness of the common parsing methods
 * @author Jose Guaro
 *
 */
public class TestingCommonParsers {

  public static final String FILE_NAME = "Test";
  
  /**
   * Tests the static method for parsing use declarations
   */
  public static void testUseDeclaration() {
    String useDeclaration = "use org.hello.bye;";
    List<Token> tokens = TestUtils.tokenizeString(useDeclaration);
    UseDeclaration actualDec = StatementParser.formUseDeclaration(tokens.listIterator(), FILE_NAME);
    
    assert actualDec.getBaseImport().getBaseString().equals("org.hello.bye");
    TestUtils.succ("--->PASSED USE DEC 1");
    
    
    
    useDeclaration = "use hello, bye, what from java.lang.Object;";
    tokens = TestUtils.tokenizeString(useDeclaration);
    actualDec = StatementParser.formUseDeclaration(tokens.listIterator(), FILE_NAME);
    assert actualDec.getBaseImport().getBaseString().equals("java.lang.Object");
    assert actualDec.getImportedFuncs().size() == 3;
    
    Set<String> funcNames = actualDec.getImportedFuncs().stream()
                                .map(s -> s.getActValue().getImage())
                                .collect(Collectors.toSet());
    
    assert funcNames.size() == 3;
    assert funcNames.containsAll(new HashSet<>(Arrays.asList("hello", "bye", "what")));
    
    TestUtils.succ("--->PASSED USE DEC 2");
    
    useDeclaration = "use";
    tokens = TestUtils.tokenizeString(useDeclaration);
    try {
      actualDec = StatementParser.formUseDeclaration(tokens.listIterator(), FILE_NAME);
      TestUtils.fail("----! FAILED USE DEC 3");
    } catch (RhexConstructionException e) {
      assert true;
    }
    TestUtils.succ("--->PASSED USE DEC 3");
    
    
    useDeclaration = "use java.lang.func from obj;";
    tokens = TestUtils.tokenizeString(useDeclaration);
    try {
      actualDec = StatementParser.formUseDeclaration(tokens.listIterator(), FILE_NAME);
      TestUtils.fail("----! FAILED USE DEC 4");
    } catch (RhexConstructionException e) {
      assert true;
    }
    TestUtils.succ("--->PASSED USE DEC 4");
    
    useDeclaration = "from here.what.Class;";
    tokens = TestUtils.tokenizeString(useDeclaration);
    try {
      actualDec = StatementParser.formUseDeclaration(tokens.listIterator(), FILE_NAME);
      TestUtils.fail("----! FAILED USE DEC 5");
    } catch (RhexConstructionException e) {
      assert true;
    }
    TestUtils.succ("--->PASSED USE DEC 5");
  }
  
  /**
   * Tests the parsing of type notations
   */
  public static void testType() {
    String type = "org.hello.what!(GenericArg, Bye!(What))";
    List<Token> tokens = TestUtils.tokenizeString(type);
    TType tType = TypeParser.parseType(tokens, FILE_NAME);
    
    assert tType.getBaseString().equals("org.hello.what");
    assert tType.getGenericTypeArgs().size() == 2;
    assert tType.getGenericTypeArgs().get(0).getBaseString().equals("GenericArg");
    assert tType.getGenericTypeArgs().get(1).getBaseString().equals("Bye");
    assert tType.getGenericTypeArgs().get(1).getGenericTypeArgs().size() == 1;
    assert tType.getGenericTypeArgs().get(1).getGenericTypeArgs().get(0).getBaseString().equals("What");
    TestUtils.succ("--->PASSED TYPE 1");
    
    
    type = "What!()";
    tokens = TestUtils.tokenizeString(type);
    
    try {
      tType = TypeParser.parseType(tokens, FILE_NAME);
      TestUtils.fail("----! FAILED TYPE 1");
    } catch (RhexConstructionException e) {
      assert true;
    }
    TestUtils.succ("---->PASSED TYPE 2");
  }
  
  /**
   * Tests the parsing of variable declarations
   */
  public static void testVariableDeclaration() {
    String varDec = "String var = hello;";
    List<Token> tokens = TestUtils.tokenizeString(varDec);
    RVariable var = VarDecParsers.parseVariable(tokens.listIterator(), GramPracConstants.SEMICOLON, FILE_NAME);
    
    assert var.getIdentifier().getActValue().getImage().equals("var");
    assert var.getProvidedType().getBaseString().equals("String");
    assert ((TExpr) var.getValue()).getActValue().size() == 1;
    assert ((TExpr) var.getValue()).getActValue().get(0) instanceof TIden;
    assert ((TIden)((TExpr) var.getValue()).getActValue().get(0)).getActValue().getImage().equals("hello");
    TestUtils.succ("---> PASSED VAR DEC 1");
    
    
    varDec = "ArrayList!(String) gene = new ArrayList();";
    tokens = TestUtils.tokenizeString(varDec);
    var = VarDecParsers.parseVariable(tokens.listIterator(), GramPracConstants.SEMICOLON, FILE_NAME);
    
    assert var.getProvidedType().getBaseString().equals("ArrayList");
    assert var.getProvidedType().getGenericTypeArgs().get(0).getBaseString().equals("String");
    assert ((TExpr) var.getValue()).getActValue().get(0) instanceof TNew;
    TestUtils.succ("---> PASSED VAR DEC 2");
    
    
    varDec = "infer var = 10;";
    tokens = TestUtils.tokenizeString(varDec);
    var = VarDecParsers.parseVariable(tokens.listIterator(), GramPracConstants.SEMICOLON, FILE_NAME);
    
    assert var.getProvidedType() == null;
    assert var.toBeInferred();
    assert ((TExpr) var.getValue()).getActValue().get(0) instanceof TInt;
    TestUtils.succ("---> PASSED VAR DEC 3");
  }
  
  public static void testFunctionParsing() {
    try {
      List<Token> tokens = TestUtils.tokenizeFile("TestFunction1.txt");
      TestUtils.printTokens(tokens);
      assert true;
    } catch (ParseException e) {
      assert false;
    }
  }
  
  public static void main(String [] args) {
    testUseDeclaration();
    testType();
    testVariableDeclaration();
    testFunctionParsing();
  }
  
}
