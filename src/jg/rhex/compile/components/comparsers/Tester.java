package jg.rhex.compile.components.comparsers;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import jg.rhex.compile.components.expr.GramPracTokenizer;
import jg.rhex.compile.components.structs.RStateBlock;
import jg.rhex.compile.components.structs.RStatement;
import jg.rhex.compile.components.structs.RVariable;
import jg.rhex.compile.components.structs.WhileBlock;
import jg.rhex.test.TestUtils;
import net.percederberg.grammatica.parser.Token;
import net.percederberg.grammatica.parser.Tokenizer;

public class Tester {

  
  
  public static void main(String[] args) throws Exception{
    String test = "while( i < 10*h+2.5 ) {"
                 
                 + "  String<T> wow = 10+2*3;"
                 + "  for(int i = 0; i < 10; i = i +1){  "
                 + "       if(i % 2 == 0){ prinln(i+10); }"
                 +"   }"
                 + "   while(j < 10) {"
                 +"        yo();"
                 +"        if(j < 5){"
                 +"           print(i*j+2-5);"
                 +"        }"
                 +"    }"
                 +"  if( i == 10) { println(10); }"
                 +"  else if (i % 3){ println(20); "
                 +"                   println('c'); }"
                 +"  else{println(5);}"
                 + "} ";
    
    List<Token> tokens = TestUtils.tokenizeString(test);
    
    System.out.println("RAW: ------");
    for(Token x: tokens){
      System.out.println(x);
    }
    System.out.println("RAW DONE ------");

    
    ListIterator<Token> iterator = tokens.listIterator();
    
    System.out.println("----PARSING WHILE HEADER-----");
    WhileBlock block = (WhileBlock) StatementParser.parseBlockHeader(iterator, "Test");
    System.out.println(block.getConditional().getStatement());
    
    System.out.println("----PARSING BLOCK----");
    StatementParser.parseBlock(block, iterator, "Test");
    
    System.out.println("**************STATEMENT***************");
    for(RStatement statement : block.getStatements()){
      printStatement(statement); 
    }
    
    block.seal();
    
    System.out.println(" -----> LAST "+(iterator.hasNext() ? iterator.next().toString() : "NONE"));
    
    //System.out.println(block.getBlockType());
    
    //BlockParser.parseBlock(block, iterator);
    
    //System.out.println("---------------");
  }
  
  public static void printStatement(RStatement statement){
    if (statement instanceof RStateBlock) {
      RStateBlock block = (RStateBlock) statement;
      System.out.println("----BLOCK: "+block);
      for(RStatement state : block.getStatements()){
        printStatement(state);
      }
      System.out.println("----END----- ");
    }
    else {
      System.out.println(statement+"       "+statement.getClass());
    }
  }

}
