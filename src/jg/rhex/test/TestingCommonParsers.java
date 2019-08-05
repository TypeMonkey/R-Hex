package jg.rhex.test;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jg.rhex.compile.components.FileBuilder;
import jg.rhex.compile.components.comparsers.ClassParser;
import jg.rhex.compile.components.comparsers.FunctionParser;
import jg.rhex.compile.components.comparsers.StatementParser;
import jg.rhex.compile.components.comparsers.TypeParser;
import jg.rhex.compile.components.comparsers.VarDecParsers;
import jg.rhex.compile.components.errors.RhexConstructionException;
import jg.rhex.compile.components.expr.GramPracConstants;
import jg.rhex.compile.components.structs.Descriptor;
import jg.rhex.compile.components.structs.ForBlock;
import jg.rhex.compile.components.structs.IfBlock;
import jg.rhex.compile.components.structs.RClass;
import jg.rhex.compile.components.structs.RFunc;
import jg.rhex.compile.components.structs.RStateBlock;
import jg.rhex.compile.components.structs.RVariable;
import jg.rhex.compile.components.structs.RFile;
import jg.rhex.compile.components.structs.TypeParameter;
import jg.rhex.compile.components.structs.UseDeclaration;
import jg.rhex.compile.components.structs.RStateBlock.BlockType;
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

  private static final String FILE_NAME = "Test";
  
  /**
   * Tests the static method for parsing use declarations
   */
  private static void testUseDeclaration() {
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
  private static void testType() {
    String type = "org.hello.what!(GenericArg, Bye!(What))";
    List<Token> tokens = TestUtils.tokenizeString(type);
    TType tType = TypeParser.parseType(tokens, FILE_NAME);
    
    assert tType.getBaseString().equals("org.hello.what");
    assert tType.getGenericTypeArgs().size() == 2;
    assert tType.getArrayDimensions() == 0;
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
    
    
    type = "java.util.List!(String)[][][]";
    tokens = TestUtils.tokenizeString(type);    
    try {
      tType = TypeParser.parseType(tokens, FILE_NAME);
      
      assert tType.getBaseString().equals("java.util.List");
      assert tType.getGenericTypeArgs().size() == 1;
      assert tType.getGenericTypeArgs().get(0).getBaseString().equals("String");
      assert tType.getArrayDimensions() == 3;
    } catch (RhexConstructionException e) {
      e.printStackTrace();
      assert false;
    }
  }
  
  /**
   * Tests the parsing of variable declarations
   */
  private static void testVariableDeclaration() {
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
  
  private static void testFunctionParsing() {
    List<Token> tokens = null;
    RFunc func = null;
    try {
      tokens = TestUtils.tokenizeFile("testsrcs/funcSamples/TestFunction1.txt");
      TestUtils.printTokens(tokens);
      
      func = FunctionParser.parseFunction(false, tokens.listIterator(), FILE_NAME);
      assert func.getName().getImage().equals("main");
      assert func.getParameterAmount() == 2;
      assert func.getReturnType().getBaseString().equals("void");
      assert func.getBody().getStatements().get(0) instanceof RVariable;
      assert func.getBody().getStatements().get(1) instanceof RVariable;
      assert ((RVariable) func.getBody().getStatements().get(0)).getIdentifier().getActValue().getImage().equals("arg");
      assert ((RVariable) func.getBody().getStatements().get(1)).getIdentifier().getActValue().getImage().equals("size");
      assert func.getBody().getStatements().size() == 3;
    } catch (RhexConstructionException | ParseException e) {
      assert false;
    }  
    TestUtils.succ("----> PASSED FUNC 1");
    
    try {
      tokens = TestUtils.tokenizeFile("testsrcs/funcSamples/TestFunction2.txt");
      TestUtils.printTokens(tokens);
      
      func = FunctionParser.parseFunction(false, tokens.listIterator(), FILE_NAME);
      assert false;
    } catch (RhexConstructionException | ParseException e) {
      /*
       * Parsing should fail as the first parameter is missing a variable name
       */
      assert true;
      TestUtils.succ("-----> PASSED FUNC 2");
    }
    
    try {
      tokens = TestUtils.tokenizeFile("testsrcs/funcSamples/TestFunction3.txt");
      TestUtils.printTokens(tokens);
      
      func = FunctionParser.parseFunction(false, tokens.listIterator(), FILE_NAME);
      assert func.getName().getImage().equals("filter");
      assert func.getParameterAmount() == 2;
      assert func.getReturnType().getBaseString().equals("java.util.List");
      assert func.getBody().getStatements().get(0) instanceof RVariable;
      assert func.getBody().getStatements().get(1) instanceof RVariable;
      assert ((RVariable) func.getBody().getStatements().get(0)).getIdentifier().getActValue().getImage().equals("lst");
      assert ((RVariable) func.getBody().getStatements().get(1)).getIdentifier().getActValue().getImage().equals("filter");
      assert ((RVariable) func.getBody().getStatements().get(0)).getProvidedType().getBaseString().equals("java.util.List");
      assert ((RVariable) func.getBody().getStatements().get(1)).getProvidedType().getBaseString().equals("java.lang.String");
      assert func.getBody().getStatements().size() == 5;
      assert func.getBody().getStatements().get(2) instanceof RVariable;
      assert func.getBody().getStatements().get(3) instanceof RStateBlock;

      try {
        ForBlock forBlock = (ForBlock) func.getBody().getStatements().get(3);
        assert forBlock.getBlockType() == BlockType.FOR;
        assert forBlock.getIntialization() instanceof RVariable;
        assert ((RVariable) forBlock.getIntialization()).getIdentifier().getActValue().getImage().equals("i");
        assert forBlock.getIntialization() != null;
        assert forBlock.getChange() !=  null;
        assert forBlock.getStatements().size() == 1;
        assert forBlock.getStatements().get(0) instanceof IfBlock;
      } catch (ClassCastException e) {
        assert false;
      }
      
    } catch (RhexConstructionException | ParseException e) {
      e.printStackTrace();
      assert false;
    }
    TestUtils.succ("---> PASSED FUNC 3");
    
    try {
      tokens = TestUtils.tokenizeFile("testsrcs/funcSamples/TestFunction4.txt");
      TestUtils.printTokens(tokens);
      
      func = FunctionParser.parseFunction(false, tokens.listIterator(), FILE_NAME);
      assert func.getName().getImage().equals("mystery");
      assert func.getParameterAmount() == 1;
      assert func.getReturnType().getBaseString().equals("java.util.List");
      assert func.getReturnType().getArrayDimensions() == 0;
      assert func.getReturnType().getGenericTypeArgs().size() == 1;
      assert func.getTypeParameters().size() == 2;
      
      RStateBlock funcBody = func.getBody();
      assert funcBody.getStatements().size() == 1;
      assert ((RVariable) funcBody.getStatements().get(0)).getIdentifier().getActValue().getImage().equals("arg1");
      assert ((RVariable) funcBody.getStatements().get(0)).getProvidedType().getBaseString().equals("Q");
      
      Map<String, TypeParameter> quickMap = func.getTypeParameters();
      
      TypeParameter tParam = quickMap.get("T");      
      assert tParam != null;
      TypeParameter qParam = quickMap.get("Q");     
      assert qParam != null;
      
      assert tParam.getExpectedFunctions().size() == 3;
      assert qParam.getExpectedFunctions().size() == 1;
      
    } catch (RhexConstructionException | ParseException e) {
      e.printStackTrace();
      assert false;
    }
    TestUtils.succ("---> PASSED FUNC 4");
    
    try {
      //this test is parseFunction can parse constructors
      tokens = TestUtils.tokenizeFile("testsrcs/funcSamples/TestFunction5.txt");
      TestUtils.printTokens(tokens);
      
      func = FunctionParser.parseFunction(true, tokens.listIterator(), FILE_NAME);
      
      assert func.isConstructor();
      assert func.getReturnType() == null;
      assert func.getName().getImage().equals("Hello");
      assert func.getParameterAmount() == 1;
      
      RStateBlock funcBody = func.getBody();
      assert ((RVariable) funcBody.getStatements().get(0)).getIdentifier().getActValue().getImage().equals("x");
    } catch (RhexConstructionException | ParseException e) {
      e.printStackTrace();
      assert false;
    }
    TestUtils.succ("----> PASSED FUNC 5");
  }
  
  /**
   * Tests the parsing of classes
   */
  private static void testClassParsing() {
    List<Token> tokens = null;
    RClass rClass = null;
    try {
      tokens = TestUtils.tokenizeFile("testsrcs/classSamples/TestClass1.txt");
      rClass = ClassParser.parseClass(tokens.listIterator(), FILE_NAME);
      
      assert rClass.getName().getImage().equals("Object");
      assert rClass.getDescriptors().contains(Descriptor.PUBLIC);
      assert rClass.isAnInterface() == false;
      assert rClass.getClassVariables().size() == 0;
      assert rClass.getMethods().size() == 0;
      assert rClass.getTypeParameters().size() == 0;
      assert rClass.getClassVariables().size() == 0;
    } catch (RhexConstructionException | ParseException e) {
      TestUtils.fail("----> FAILED CLASS 1");
      assert false;
    }
    TestUtils.succ("----> PASSED CLASS 1");
    
    try {
      tokens = TestUtils.tokenizeFile("testsrcs/classSamples/TestClass2.txt");
      rClass = ClassParser.parseClass(tokens.listIterator(), FILE_NAME);

      assert false;
      TestUtils.fail("----> FAILED CLASS 2");
    } catch (RhexConstructionException | ParseException e) {
      /**
       * Should throw an error as top level classes can't be static
       */
      assert true;
      
    }
    TestUtils.succ("----> PASSED CLASS 2");

    try {
      tokens = TestUtils.tokenizeFile("testsrcs/classSamples/TestClass3.txt");
      rClass = ClassParser.parseClass(tokens.listIterator(), FILE_NAME);
      
      assert rClass.getName().getImage().equals("Map");
      assert rClass.getTypeParameters().size() == 2;
      assert rClass.getMethods().size() == 1;
    } catch (RhexConstructionException | ParseException e) {
      e.printStackTrace();
      TestUtils.fail("----> FAILED CLASS 3");
      assert false;
    }
    TestUtils.succ("----> PASSED CLASS 3");

  }
  
  private static void testSourceParsing() {
    // TODO Auto-generated method stub
    RFile rhexFile = null;
    try {
      File file = new File("testsrcs/sourceSamples/Source1.rhex");
      FileBuilder fileBuilder = new FileBuilder(file);
      rhexFile = fileBuilder.constructFile();
            
      //check component amounts
      assert rhexFile.getClasses().size() == 2;
      assert rhexFile.getUseDeclarations().size() == 1;
      assert rhexFile.getFunctions().size() == 1;
      assert rhexFile.getVariables().size() == 1;
      
      //check package declaration
      assert rhexFile.getPackDesignation() != null;
      
      
      assert rhexFile.getPackDesignation().equals("my.pack.okay");
      
      //check the use declarations
      assert rhexFile.getUseDeclarations().get(0).getBaseImport().getBaseString().equals("java.lang.Object");
      
      //check function
      assert rhexFile.getFunctions().get(0).getName().getImage().equals("hello");
      assert rhexFile.getFunctions().get(0).getTypeParameters().size() == 2;
      assert rhexFile.getFunctions().get(0).getReturnType().getBaseString().equals("void");
      assert rhexFile.getFunctions().get(0).getParameterAmount() == 2;
    } catch (RhexConstructionException  e) {
      e.printStackTrace();
     TestUtils.fail("---> FAILED SOURCE 1");
     assert false;
    }
    TestUtils.succ("----> PASSED SOURCE 1");
  }
  
  public static void main(String [] args) {
    testUseDeclaration();
    testType();
    testVariableDeclaration();
    testFunctionParsing();
    testClassParsing();
    testSourceParsing();
  }
  
}
