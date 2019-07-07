package jg.rhex.compile.components.comparsers;

import java.util.ArrayList;
import java.util.Deque;
import java.util.ListIterator;

import jg.rhex.compile.ExpectedSet;
import jg.rhex.compile.components.ASTBuilder;
import jg.rhex.compile.components.GramPracConstants;
import jg.rhex.compile.components.GramPracParser;
import jg.rhex.compile.components.NewSeer;
import jg.rhex.compile.components.errors.EmptyExprException;
import jg.rhex.compile.components.errors.FormationException;
import jg.rhex.compile.components.structs.ForBlock;
import jg.rhex.compile.components.structs.IfBlock;
import jg.rhex.compile.components.structs.RStateBlock;
import jg.rhex.compile.components.structs.RStatement;
import jg.rhex.compile.components.structs.RStatement.RStateDescriptor;
import jg.rhex.compile.components.structs.RVariable;
import jg.rhex.compile.components.structs.WhileBlock;
import jg.rhex.compile.components.structs.RStateBlock.BlockType;
import jg.rhex.compile.components.tnodes.TExpr;
import jg.rhex.compile.components.tnodes.TNode;
import net.percederberg.grammatica.parser.ParserCreationException;
import net.percederberg.grammatica.parser.ParserLogException;
import net.percederberg.grammatica.parser.Token;

/**
 * Parses a block of statements
 * @author Jose
 *
 */
public class BlockParser {
  
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
  public static RStatement parseStatement(ListIterator<Token> iterator) throws ParserCreationException, ParserLogException{
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
          RVariable variable = VarDecParsers.parseVariable(tokens.listIterator());
          return variable;
        } catch (FormationException e) {
          //if not variable declaration, continue on and parse as regular statement
        }
      }

      //remove ending semicolon as to not get a parse error from parser
      tokens.remove(tokens.size()-1);
      
      NewSeer seer = new NewSeer();
      GramPracParser parser = new GramPracParser(null, seer);
      parser.parseFromTokenList(tokens);

      if (seer.getStackNodes().size() == 1) {
        return new RStatement(descriptor, firstToken, seer.getStackNodes().pop());
      }
      else {
        return new RStatement(descriptor, firstToken, new TExpr(new ArrayList<>(seer.getStackNodes())));
      }
    }
    else {
      throw new EmptyExprException(iterator.previous(), "Statment");
    }
  }
  
  /**
   * Parses a statement block's body
   * @param stateBlock - the RStateBlock representing an already formed header.
   * @param iterator - the Iterator to consume Tokens from
   */
  public static void parseBlock(RStateBlock stateBlock, ListIterator<Token> iterator){
    ExpectedSet expected = new ExpectedSet(GramPracConstants.OP_CU_BRACK);
    
    if (!iterator.hasNext()) {
      throw FormationException.createException("StatementBlock", iterator.previous(), expected);
    }
    
    expected.noContainsThrow(iterator.next(), "StatementBlock");
    
    
    while (iterator.hasNext()) {
      Token current = iterator.next();
      System.out.println("CURRENT: "+current);
      if (current.getId() == GramPracConstants.CL_CU_BRACK) {
        break;
      }
      else if (current.getId() == GramPracConstants.OP_CU_BRACK) {
        //TODO: First, make a method that can parse statement block headers (for, while, if, else if , else blocks)
        iterator.previous();
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
              new ExpectedSet(GramPracConstants.SEMICOLON, GramPracConstants.CL_CU_BRACK));
        }
                
        System.out.println("CANDITDATE: "+tempStatement);
        if (tempStatement.get(tempStatement.size() - 1).getId() == GramPracConstants.SEMICOLON) {
          try {
            RStatement statement = parseStatement(tempStatement.listIterator());
            stateBlock.addStatement(statement);
          } catch (ParserCreationException | ParserLogException e) {
            e.printStackTrace();
          } 
        }
        else {
          //if this is a block header
          RStateBlock nestedHeader = parseBlockHeader(tempStatement.listIterator());
          iterator.previous(); //roll back iterator
          parseBlock(nestedHeader, iterator);
          
          stateBlock.addStatement(nestedHeader);
        }
      }
    }
    
  }
  
  /**
   * Parses a Token stream for block headers.
   * @param iterator - source to consume Tokens from
   * @return
   */
  public static RStateBlock parseBlockHeader(ListIterator<Token> iterator){
    ExpectedSet expected = new ExpectedSet(GramPracConstants.OP_CU_BRACK, 
                                           GramPracConstants.FOR,
                                           GramPracConstants.WHILE,
                                           GramPracConstants.IF, 
                                           GramPracConstants.ELSE);
    
    if (!iterator.hasNext()) {
      throw FormationException.createException("BlockHeader", iterator.next(), expected);
    }
    
    Token headerDesciptor = iterator.next();
    expected.noContainsThrow(headerDesciptor, "BlockHeader");
    
    switch (headerDesciptor.getId()) {
    case GramPracConstants.FOR:
      return parseForHeader(headerDesciptor, iterator);
    case GramPracConstants.WHILE:
      return parseWhileHeader(headerDesciptor, iterator, "WhileLoopBlock");
    case GramPracConstants.IF:
      return parseIfHeader(headerDesciptor, iterator);
    case GramPracConstants.ELSE:
      return parseElseHeader(headerDesciptor, iterator);
    default:
      return new RStateBlock(headerDesciptor, BlockType.GENERAL);
    }   
  }

  /** The following "parseXHeader" methods accepts an Iterator in which the first call to next()
   *  returns the token AFTER the block descriptor 
   **/
  
  public static ForBlock parseForHeader(Token descritor, ListIterator<Token> iterator){
    ExpectedSet expected = new ExpectedSet(GramPracConstants.OP_PAREN);
    
    if (!iterator.hasNext()) {
      throw FormationException.createException("ForLoopBlock", iterator.previous(), expected);
    }
    
    expected.noContainsThrow(iterator.next(), "ForLoopBlock");
    
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
          initStatement = parseStatement(initState.listIterator());
        } catch (ParserCreationException | ParserLogException e1) {
          e1.printStackTrace();
        }
      }
      
    }
    else {
      throw FormationException.createException("ForLoopBlock", iterator.previous(), new ExpectedSet(GramPracConstants.SEMICOLON));
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
        condStatement = parseStatement(conditionState.listIterator());
      } catch (ParserCreationException | ParserLogException e) {
        e.printStackTrace();
      } 
    }
    else {
      throw FormationException.createException("ForLoopBlock", iterator.previous(), new ExpectedSet(GramPracConstants.SEMICOLON));
    }
    
    //parse the change statement
    ArrayList<Token> changeState = new ArrayList<>();
    boolean changeTermFound = false;
    while (iterator.hasNext()) {
      Token current = iterator.next();
      changeState.add(current);
      if (current.getId() == GramPracConstants.CL_PAREN) {
        changeTermFound = true;
        break;
      }
    }
    
    RStatement changeStatement = null;
    if (changeTermFound) {      
      if (!changeState.isEmpty()) {
        try {
          NewSeer seer = new NewSeer();
          GramPracParser parser = new GramPracParser(null, seer);
          parser.parseFromTokenList(changeState);
          
          ASTBuilder postFixer = new ASTBuilder();
          Deque<TNode> postFix = postFixer.build(seer.getStackNodes());
          
          if (postFix.size() == 1) {
            changeStatement = new RStatement(postFix.pollFirst());
          }
          else {
            changeStatement = new RStatement(new TExpr(new ArrayList<>(postFix)));
          }
          
        } catch (ParserCreationException | ParserLogException e1) {
          e1.printStackTrace();
        }
      }
    }
    else {
      throw FormationException.createException("ForLoopBlock", iterator.previous(), new ExpectedSet(GramPracConstants.SEMICOLON));
    }
    
    ForBlock forBlock = new ForBlock(descritor);
    forBlock.setInitStatement(initStatement);
    forBlock.setConditional(condStatement);
    forBlock.setChange(changeStatement);
    
    return forBlock;
  }

  public static WhileBlock parseWhileHeader(Token headerDesciptor, ListIterator<Token> iterator, String actualContext){
    ExpectedSet expected = new ExpectedSet(GramPracConstants.OP_PAREN);

    if (!iterator.hasNext()) {
      throw FormationException.createException(actualContext, iterator.previous(), expected);
    }
    
    expected.noContainsThrow(iterator.next(), actualContext);
    
    ArrayList<Token> conditionState = new ArrayList<>();
    boolean changeTermFound = false;
    while (iterator.hasNext()) {
      Token current = iterator.next();
      conditionState.add(current);
      if (current.getId() == GramPracConstants.CL_PAREN) {
        changeTermFound = true;
        break;
      }
    }
    
    RStatement conditionStatement = null;
    if (changeTermFound) {
      conditionState.remove(conditionState.size() - 1);
      
      if (!conditionState.isEmpty()) {
        try {
          NewSeer seer = new NewSeer();
          GramPracParser parser = new GramPracParser(null, seer);
          parser.parseFromTokenList(conditionState);
          
          ASTBuilder postFixer = new ASTBuilder();
          Deque<TNode> postFix = postFixer.build(seer.getStackNodes());
          
          if (postFix.size() == 1) {
            conditionStatement = new RStatement(postFix.pollFirst());
          }
          else {
            conditionStatement = new RStatement(new TExpr(new ArrayList<>(postFix)));
          }
          
        } catch (ParserCreationException | ParserLogException e1) {
          e1.printStackTrace();
        }
      }
    }
    else {
      throw FormationException.createException(actualContext, iterator.previous(), new ExpectedSet(GramPracConstants.SEMICOLON));
    }
    
    WhileBlock whileBlock = new WhileBlock(headerDesciptor);
    whileBlock.setConditional(conditionStatement);
    
    return whileBlock;
  }

  public static IfBlock parseIfHeader(Token headerDesciptor, ListIterator<Token> iterator){
    WhileBlock whileBlock = (WhileBlock) parseWhileHeader(headerDesciptor, iterator, "IfBlock");
    IfBlock ifBlock = new IfBlock(headerDesciptor, BlockType.IF);
    ifBlock.setConditional(whileBlock.getConditional());
    return ifBlock;
  }

  public static RStateBlock parseElseHeader(Token headerDesciptor, ListIterator<Token> iterator){
    ExpectedSet expected = new ExpectedSet(GramPracConstants.OP_CU_BRACK, GramPracConstants.IF);

    if (!iterator.hasNext()) {
      throw FormationException.createException("ElseBlock", iterator.previous(), expected);
    }
    
    Token upcoming = iterator.next();
    expected.noContainsThrow(upcoming, "ElseBlock");
    
    iterator.previous(); //roll back parser
    if (upcoming.getId() == GramPracConstants.OP_CU_BRACK) {
      RStateBlock stateBlock = new RStateBlock(upcoming, BlockType.ELSE);
      parseBlock(stateBlock, iterator);
      
      return stateBlock;
    }
    else {
      IfBlock header = (IfBlock) parseBlockHeader(iterator);
      RStatement condtion = header.getConditional();
      header = new IfBlock(header.getDescriptorToken(), BlockType.ELSE_IF);
      header.setConditional(condtion);
      
      parseBlock(header, iterator);
      
      return header;
    }
  }
}
