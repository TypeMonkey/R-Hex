package jg.rhex.compile.components.comparsers;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;

import jg.rhex.compile.ExpectedSet;
import jg.rhex.compile.components.errors.EmptyExprException;
import jg.rhex.compile.components.errors.ExpressionParseException;
import jg.rhex.compile.components.errors.FormationException;
import jg.rhex.compile.components.errors.InvalidPlacementException;
import jg.rhex.compile.components.errors.RepeatedStructureException;
import jg.rhex.compile.components.errors.RhexConstructionException;
import jg.rhex.compile.components.expr.ASTBuilder;
import jg.rhex.compile.components.expr.ExprParser;
import jg.rhex.compile.components.expr.GramPracConstants;
import jg.rhex.compile.components.expr.GramPracParser;
import jg.rhex.compile.components.expr.NewSeer;
import jg.rhex.compile.components.structs.ForBlock;
import jg.rhex.compile.components.structs.IfBlock;
import jg.rhex.compile.components.structs.RStateBlock;
import jg.rhex.compile.components.structs.RStatement;
import jg.rhex.compile.components.structs.RStatement.RStateDescriptor;
import jg.rhex.compile.components.structs.RVariable;
import jg.rhex.compile.components.structs.UseDeclaration;
import jg.rhex.compile.components.structs.WhileBlock;
import jg.rhex.compile.components.structs.RStateBlock.BlockType;
import jg.rhex.compile.components.tnodes.TExpr;
import jg.rhex.compile.components.tnodes.TNode;
import jg.rhex.compile.components.tnodes.atoms.TIden;
import jg.rhex.compile.components.tnodes.atoms.TType;
import net.percederberg.grammatica.parser.ParserCreationException;
import net.percederberg.grammatica.parser.ParserLogException;
import net.percederberg.grammatica.parser.Token;

/**
 * Parses a block of statements
 * @author Jose
 *
 */
public class StatementParser {
  
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
  public static UseDeclaration formUseDeclaration(ListIterator<Token> iterator, String fileName){
    ExpectedSet expected = new ExpectedSet(GramPracConstants.USE);
    
    HashSet<TIden> importedFunctions = new HashSet<>();
    ArrayList<TIden> baseImport = new ArrayList<>();
    Token useToken = null;
    
    boolean nameIsForFunction = false;
        
    while (iterator.hasNext()) {
      Token current = iterator.next();
      if (expected.noContainsThrow(current, "UseStatement", fileName)) {
        if (current.getId() == GramPracConstants.USE) {
          useToken = current;
          expected.replace(GramPracConstants.NAME);
        }
        else if (current.getId() == GramPracConstants.NAME) {
          if (nameIsForFunction) {
            if (!importedFunctions.add(new TIden(current))) {
              throw new RepeatedStructureException(current, "UseStatement", fileName);
            }
            
            expected.replace(GramPracConstants.COMMA, GramPracConstants.FROM);
          }
          else {
            //lookahead and see if the next token is a comma, dot, or semicolon
            //if it's a dot, then this name is the beginning of a binary name
            //if it's a comma, then this use statement follows a "from" syntax
            //if it's a semicolon, then this use statement is simple

            if (iterator.hasNext()) {
              Token next = iterator.next();
              if (next.getId() == GramPracConstants.DOT) {
                
                ExpectedSet dotExpected = new ExpectedSet(GramPracConstants.NAME);

                baseImport.add(new TIden(current));
                
                boolean terminatorFound = false;
                while (iterator.hasNext()) {
                  Token dotCurrent = iterator.next();
                  if (dotExpected.noContainsThrow(dotCurrent, "UseStatement", fileName)) {
                    if (dotCurrent.getId() == GramPracConstants.NAME) {
                      baseImport.add(new TIden(dotCurrent));
                      dotExpected.replace(GramPracConstants.DOT, GramPracConstants.SEMICOLON);
                    }
                    else if (dotCurrent.getId() == GramPracConstants.DOT) {
                      dotExpected.replace(GramPracConstants.NAME);
                    }
                    else if (dotCurrent.getId() == GramPracConstants.SEMICOLON) {
                      terminatorFound = true;
                      break;
                    }
                  }
                }
                
                if (terminatorFound) {
                  expected.replace(GramPracConstants.SEMICOLON);
                }
                else {
                  throw FormationException.createException("UseStatement", iterator.previous(), new ExpectedSet(GramPracConstants.SEMICOLON), fileName);
                }
              }
              else if (next.getId() == GramPracConstants.COMMA) {
                nameIsForFunction = true;
                if (!importedFunctions.add(new TIden(current))) {
                  throw new RepeatedStructureException(current, "UseStatement", fileName);
                }                
                expected.replace(GramPracConstants.COMMA);
              }
              else if (next.getId() == GramPracConstants.SEMICOLON) {
                baseImport.add(new TIden(current));
                expected.replace(GramPracConstants.SEMICOLON);
              }
              else {
                throw new InvalidPlacementException(next, fileName);
              }

              iterator.previous(); //roll back iterator
            }
            else {
              continue;
            }
          }
        }
        else if (current.getId() == GramPracConstants.COMMA) {
          expected.replace(GramPracConstants.NAME);
        }
        else if (current.getId() == GramPracConstants.FROM) {
          ExpectedSet dotExpected = new ExpectedSet(GramPracConstants.NAME);
          
          boolean terminatorFound = false;
          while (iterator.hasNext()) {
            Token dotCurrent = iterator.next();
            System.out.println("from current: "+dotCurrent);
            if (dotExpected.noContainsThrow(dotCurrent, "UseStatement", fileName)) {
              if (dotCurrent.getId() == GramPracConstants.NAME) {
                baseImport.add(new TIden(dotCurrent));
                dotExpected.replace(GramPracConstants.DOT, GramPracConstants.SEMICOLON);
              }
              else if (dotCurrent.getId() == GramPracConstants.DOT) {
                dotExpected.replace(GramPracConstants.NAME);
              }
              else if (dotCurrent.getId() == GramPracConstants.SEMICOLON) {
                terminatorFound = true;
                break;
              }
            }
          }
          
          if (terminatorFound) {
            expected.replace(GramPracConstants.SEMICOLON);
            iterator.previous();
          }
          else {
            throw FormationException.createException("UseStatement", iterator.previous(), new ExpectedSet(GramPracConstants.SEMICOLON), fileName);
          }
        }
        else if (current.getId() == GramPracConstants.SEMICOLON) {
          expected.clear();
          break;
        }
      }
    }
    
    if (expected.isEmpty() || expected.contains(-1)) {
      return new UseDeclaration(useToken, new TType(baseImport), importedFunctions);
    }
    throw FormationException.createException("UseStatement", iterator.previous(), expected, fileName);
  }

  
  /**
   * Parses a statement from a Token source
   * 
   * A Statement is a series of Tokens terminated by a semicolon, this includes Variable declarations
   * 
   * NOTE: the semicolon is also consumed by this method
   * 
   * @param iterator - the iterator to consume Tokens from
   * @return an RStatement representing the statement
   */
  public static RStatement parseStatement(ListIterator<Token> iterator, String fileName) throws ParserLogException{
    ArrayList<Token> tokens = new ArrayList<>();
    
    if (iterator.hasNext()) {
      Token firstToken = iterator.next();
      RStateDescriptor descriptor = null;

      switch (firstToken.getId()) {
      case GramPracConstants.THROW:
        descriptor = RStateDescriptor.THROW;
        break;
      case GramPracConstants.RETURN:
        descriptor = RStateDescriptor.RETURN;
        break;
      case GramPracConstants.BREAK:
        descriptor = RStateDescriptor.BREAK;
        break;
      case GramPracConstants.CONT:
        descriptor = RStateDescriptor.CONTINUE;
        break;
      default:
        descriptor = RStateDescriptor.REGULAR;
        tokens.add(firstToken);
        break;
      }

      while (iterator.hasNext()) {
        Token current = iterator.next();
        tokens.add(current);
        if (current.getId() == GramPracConstants.SEMICOLON) {
          break;
        }
      }      

      if (descriptor == RStateDescriptor.REGULAR) {
        //attempt to parse it as a variable declaration

        try {
          RVariable variable = VarDecParsers.parseVariable(tokens.listIterator(), GramPracConstants.SEMICOLON, fileName);
          variable.seal();
          return variable;
        } catch (RhexConstructionException e) {
          //if not variable declaration, continue on and parse as regular statement
        }
      }

      //remove ending semicolon as to not get a parse error from parser
      tokens.remove(tokens.size()-1);
           
      List<TNode> exprNodes = ExprParser.getUniversalParser().parseExpression(tokens);

      if (exprNodes.size() == 1) {
        RStatement statement = new RStatement(descriptor, firstToken, exprNodes.get(0));
        statement.seal();
        return statement;
      }
      else {
        RStatement statement = new RStatement(descriptor, firstToken, new TExpr(exprNodes));
        statement.seal();
        return statement;
      }
    }
    else {
      throw new EmptyExprException(iterator.previous(), "Statement", fileName);
    }
  }
  
  /**
   * Parses a statement block's body
   * @param stateBlock - the RStateBlock representing an already formed header.
   * @param iterator - the Iterator to consume Tokens from
   */
  public static void parseBlock(RStateBlock stateBlock, ListIterator<Token> iterator, String fileName){
    ExpectedSet expected = new ExpectedSet(GramPracConstants.OP_CU_BRACK);
    
    if (!iterator.hasNext()) {
      throw FormationException.createException("StatementBlock", iterator.previous(), expected, fileName);
    }
    
    expected.noContainsThrow(iterator.next(), "StatementBlock", fileName);
    
    
    while (iterator.hasNext()) {
      Token current = iterator.next();
      System.out.println("CURRENT: "+current);
      if (current.getId() == GramPracConstants.CL_CU_BRACK) {
        break;
      }
      else {
        ArrayList<Token> tempStatement = new ArrayList<>();
        tempStatement.add(current);
        
        boolean terminatorFound = false;
        
        if (tempStatement.get(0).getId() == GramPracConstants.FOR) {
          //consume Tokens from the iterator until a '{' is found.
          while (iterator.hasNext()) {
            Token tempCur = iterator.next();
            System.out.println("---STATE FOR CUR: "+tempCur);
            tempStatement.add(tempCur);
            if (tempCur.getId() == GramPracConstants.OP_CU_BRACK) {
              terminatorFound = true;
              break;
            }
          }
        }
        else {
          //consume Tokens from the iterator until a semicolon or '{' is found.
          while (iterator.hasNext()) {
            Token tempCur = iterator.next();
            System.out.println("---STATE CUR: "+tempCur);
            tempStatement.add(tempCur);
            if (tempCur.getId() == GramPracConstants.SEMICOLON || 
                tempCur.getId() == GramPracConstants.OP_CU_BRACK) {
              terminatorFound = true;
              break;
            }
          }
        }
        
        //if token stream runs out of Tokens before a semicolon or '{' is found, then there's an error
        if (!terminatorFound) {
          throw FormationException.createException("Statement", iterator.previous(), 
              new ExpectedSet(GramPracConstants.SEMICOLON, GramPracConstants.CL_CU_BRACK),
              fileName);
        }
                
        System.out.println("CANDITDATE: "+tempStatement);
        if (tempStatement.get(tempStatement.size() - 1).getId() == GramPracConstants.SEMICOLON) {
          try {
            RStatement statement = parseStatement(tempStatement.listIterator(), fileName);
            stateBlock.addStatement(statement);
          } catch (ParserLogException e) {
            e.printStackTrace();
          } 
        }
        else {
          //if this is a block header
          RStateBlock nestedHeader = parseBlockHeader(tempStatement.listIterator(), fileName);
          iterator.previous(); //roll back iterator
          parseBlock(nestedHeader, iterator, fileName);
          
          stateBlock.addStatement(nestedHeader);
        }
      }
    }
    
  }
  
  /**
   * Parses a Token stream for block headers.
   * 
   * Once this method completes, the next call to next() on 
   * the iterator should return the opening '{'
   * 
   * @param iterator - source to consume Tokens from
   * @return
   */
  public static RStateBlock parseBlockHeader(ListIterator<Token> iterator, String fileName){
    ExpectedSet expected = new ExpectedSet(GramPracConstants.OP_CU_BRACK, 
                                           GramPracConstants.FOR,
                                           GramPracConstants.WHILE,
                                           GramPracConstants.IF, 
                                           GramPracConstants.ELSE);
    
    if (!iterator.hasNext()) {
      throw FormationException.createException("BlockHeader", iterator.previous(), expected, fileName);
    }
    
    Token headerDesciptor = iterator.next();
    expected.noContainsThrow(headerDesciptor, "BlockHeader", fileName);
    
    ArrayList<Token> headerList = new ArrayList<>();
    
    boolean terminatorFound = false;
    while (iterator.hasNext()) {
      Token current = iterator.next();
      headerList.add(current);
      if (current.getId() == GramPracConstants.OP_CU_BRACK) {
        terminatorFound = true;
        break;
      }
    }
    
    if (!terminatorFound) {
      throw FormationException.createException("BlockHeader", 
          iterator.previous(), 
          new ExpectedSet(GramPracConstants.OP_CU_BRACK), fileName);
    }
    
    iterator.previous(); //roll back
    
    switch (headerDesciptor.getId()) {
    case GramPracConstants.FOR:
      return parseForHeader(headerDesciptor, headerList.listIterator(), fileName);
    case GramPracConstants.WHILE:
      return parseWhileHeader(headerDesciptor, headerList.listIterator(), "WhileLoopBlock", fileName);
    case GramPracConstants.IF:
      return parseIfHeader(headerDesciptor, headerList.listIterator(), fileName);
    case GramPracConstants.ELSE:
      return parseElseHeader(headerDesciptor, headerList.listIterator(), fileName);
    default:
      return new RStateBlock(headerDesciptor, BlockType.GENERAL);
    }   
  }

  /** The following "parseXHeader" methods accepts an Iterator in which the first call to next()
   *  returns the token AFTER the block descriptor 
   **/
  
  public static ForBlock parseForHeader(Token descritor, ListIterator<Token> iterator, String fileName){
    ExpectedSet expected = new ExpectedSet(GramPracConstants.OP_PAREN);
    
    if (!iterator.hasNext()) {
      throw FormationException.createException("ForLoopBlock", iterator.previous(), expected, fileName);
    }
    
    expected.noContainsThrow(iterator.next(), "ForLoopBlock", fileName);
    
    //Parse the initialization statement
    ArrayList<Token> initState = new ArrayList<>();
    boolean initTermFound = false;
    while (iterator.hasNext()) {
      Token current = iterator.next();
      initState.add(current);
      if (current.getId() == GramPracConstants.SEMICOLON) {
        initTermFound = true;
        break;
      }
    }
    
    System.out.println("----> FOR INIT STATEMENTS: "+initState);
    
    RStatement initStatement = null;
    if (initTermFound) {      
      if (!initState.isEmpty()) {
        try {
          initStatement = parseStatement(initState.listIterator(), fileName);
        } catch (ParserLogException e1) {
          throw new ExpressionParseException(e1, fileName);
        }
      }
      
    }
    else {
      throw FormationException.createException("ForLoopBlock", iterator.previous(), new ExpectedSet(GramPracConstants.SEMICOLON), fileName);
    }
    
    //Parse the condition statement
    ArrayList<Token> conditionState = new ArrayList<>();
    boolean condTermFound = false;
    while (iterator.hasNext()) {
      Token current = iterator.next();
      conditionState.add(current);
      if (current.getId() == GramPracConstants.SEMICOLON) {
        condTermFound = true;
        break;
      }
    }
    
    RStatement condStatement = null;
    if (condTermFound) {
      try {
        condStatement = parseStatement(conditionState.listIterator(), fileName);
      } catch (ParserLogException e) {
        throw new ExpressionParseException(e, fileName);
      } 
    }
    else {
      throw FormationException.createException("ForLoopBlock", iterator.previous(), new ExpectedSet(GramPracConstants.SEMICOLON), fileName);
    }
    
    //parse the change statement
    ArrayList<Token> changeState = new ArrayList<>();
    boolean changeTermFound = false;
    while (iterator.hasNext()) {
      Token current = iterator.next();
      changeState.add(current);
      if (current.getId() == GramPracConstants.OP_CU_BRACK) {
        changeTermFound = true;
        changeState.remove(changeState.size() - 1); //remove opening '{'
        changeState.remove(changeState.size() - 1); //remove closing ')'
        break;
      }
    }
    
    RStatement changeStatement = null;
    if (changeTermFound) {      
      if (!changeState.isEmpty()) {
        try {
          List<TNode> postFix = ExprParser.getUniversalParser().parseExpression(changeState);
          
          if (postFix.size() == 1) {
            changeStatement = new RStatement(postFix.get(0));
          }
          else {
            changeStatement = new RStatement(new TExpr(postFix));
          }
          
        } catch (ParserLogException e1) {
          System.out.println(changeState);
          throw new ExpressionParseException(e1, fileName);
        }
      }
    }
    else {
      throw FormationException.createException("ForLoopBlock", iterator.previous(), new ExpectedSet(GramPracConstants.SEMICOLON), fileName);
    }
    
    ForBlock forBlock = new ForBlock(descritor);
    forBlock.setInitStatement(initStatement);
    forBlock.setConditional(condStatement);
    forBlock.setChange(changeStatement);
    
    return forBlock;
  }

  public static WhileBlock parseWhileHeader(Token headerDesciptor, ListIterator<Token> iterator, String actualContext, String fileName){
    ExpectedSet expected = new ExpectedSet(GramPracConstants.OP_PAREN);

    if (!iterator.hasNext()) {
      throw FormationException.createException(actualContext, iterator.previous(), expected, fileName);
    }
    
    expected.noContainsThrow(iterator.next(), actualContext, fileName);
    
    ArrayList<Token> conditionState = new ArrayList<>();
    boolean changeTermFound = false;
    while (iterator.hasNext()) {
      Token current = iterator.next();
      conditionState.add(current);
      if (current.getId() == GramPracConstants.OP_CU_BRACK) {
        changeTermFound = true;
        conditionState.remove(conditionState.size() - 1); //removes opening '{'
        break;
      }
    }
    
    RStatement conditionStatement = null;
    if (changeTermFound) {
      conditionState.remove(conditionState.size() - 1);
      
      if (!conditionState.isEmpty()) {
        try {
          List<TNode> postFix = ExprParser.getUniversalParser().parseExpression(conditionState);
          
          if (postFix.size() == 1) {
            conditionStatement = new RStatement(postFix.get(0));
          }
          else {
            conditionStatement = new RStatement(new TExpr(postFix));
          }
          
        } catch (ParserLogException e1) {
          throw new ExpressionParseException(e1, fileName);
        }
      }
    }
    else {
      throw FormationException.createException(actualContext, iterator.previous(), new ExpectedSet(GramPracConstants.SEMICOLON), fileName);
    }
    
    WhileBlock whileBlock = new WhileBlock(headerDesciptor);
    whileBlock.setConditional(conditionStatement);
    
    return whileBlock;
  }

  public static IfBlock parseIfHeader(Token headerDesciptor, ListIterator<Token> iterator, String fileName){
    WhileBlock whileBlock = (WhileBlock) parseWhileHeader(headerDesciptor, iterator, "IfBlock", fileName);
    IfBlock ifBlock = new IfBlock(headerDesciptor, BlockType.IF);
    ifBlock.setConditional(whileBlock.getConditional());
    return ifBlock;
  }

  public static RStateBlock parseElseHeader(Token headerDesciptor, ListIterator<Token> iterator, String fileName){
    ExpectedSet expected = new ExpectedSet(GramPracConstants.OP_CU_BRACK, GramPracConstants.IF);

    if (!iterator.hasNext()) {
      throw FormationException.createException("ElseBlock", iterator.previous(), expected, fileName);
    }
    
    Token upcoming = iterator.next();
    expected.noContainsThrow(upcoming, "ElseBlock", fileName);
    
    iterator.previous(); //roll back parser
    if (upcoming.getId() == GramPracConstants.OP_CU_BRACK) {
      RStateBlock stateBlock = new RStateBlock(upcoming, BlockType.ELSE);
      parseBlock(stateBlock, iterator, fileName);
      
      return stateBlock;
    }
    else {
      IfBlock header = (IfBlock) parseBlockHeader(iterator, fileName);
      RStatement condtion = header.getConditional();
      header = new IfBlock(header.getDescriptorToken(), BlockType.ELSE_IF);
      header.setConditional(condtion);
      
      parseBlock(header, iterator, fileName);
      
      return header;
    }
  }
}
