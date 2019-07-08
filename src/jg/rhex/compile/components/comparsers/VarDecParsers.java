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
import jg.rhex.compile.components.expr.ASTBuilder;
import jg.rhex.compile.components.expr.ExprParser;
import jg.rhex.compile.components.expr.GramPracConstants;
import jg.rhex.compile.components.expr.GramPracParser;
import jg.rhex.compile.components.expr.NewSeer;
import jg.rhex.compile.components.structs.Descriptor;
import jg.rhex.compile.components.structs.RVariable;
import jg.rhex.compile.components.tnodes.TExpr;
import jg.rhex.compile.components.tnodes.TNode;
import jg.rhex.compile.components.tnodes.atoms.TIden;
import jg.rhex.compile.components.tnodes.atoms.TType;
import net.percederberg.grammatica.parser.ParserCreationException;
import net.percederberg.grammatica.parser.ParserLogException;
import net.percederberg.grammatica.parser.Token;

public final class VarDecParsers {
  
  /**
   * Parses a Token source (ListIterator) for a variable declaration
   * 
   * A variable declaration in R-Hex has the following syntax:
   * 
   *  (static | volatile | final | public | private) <Type | "infer"> name;
   *  (static | volatile | final | public | private) <Type | "infer"> name = Expr;
   *  
   *  The semicolon is also parsed and consumed!
   * 
   * @param iterator - a ListIterator to consume tokens from
   * @return RVariable that was parsed from the Token source
   */
  public static RVariable parseVariable(ListIterator<Token> iterator){
    ExpectedSet expected = new ExpectedSet(GramPracConstants.NAME, 
                                           GramPracConstants.STATIC,
                                           GramPracConstants.VOLATILE,
                                           GramPracConstants.FINAL,
                                           GramPracConstants.INFER);
    
    
    HashSet<Descriptor> variableDescriptors = new HashSet<>();   
    TType varType = null;
    Token varName = null;
    TNode value = null;
    boolean varToBeInferred = false;
    
    while (iterator.hasNext()) {
      Token current = iterator.next();
      if (expected.noContainsThrow(current, "VariableDeclaration")) {
        if (current.getId() == GramPracConstants.STATIC || 
            current.getId() == GramPracConstants.VOLATILE ||
            current.getId() == GramPracConstants.FINAL) {
          Descriptor descriptor = Descriptor.getEnumEquivalent(current.getId());
          //reports illegal syntax (descriptor duplication)
          if (!variableDescriptors.add(descriptor)) {
            throw FormationException.createException("VariabelDeclaration", current, expected);
          }

          expected.replace(GramPracConstants.NAME, 
              GramPracConstants.STATIC,
              GramPracConstants.VOLATILE,
              GramPracConstants.FINAL,
              GramPracConstants.INFER);
        }
        else if (current.getId() == GramPracConstants.INFER) {
          varToBeInferred = true;
          
          expected.replace(GramPracConstants.NAME);
        }
        else if (current.getId() == GramPracConstants.NAME) {
          if (varType == null && !varToBeInferred) {
            //then this name is the start of the variable's type
            iterator.previous(); //backtrack iterator
            varType = TypeParser.parseType(iterator);
            
            System.out.println("---GOT TYPE: "+varType.getBaseType());
            
            expected.replace(GramPracConstants.NAME);
          }
          else {
            //then this name is the variable's name
            varName = current;
            
            System.out.println("---GOT NAME: "+varName);
            
            if (iterator.hasNext()) {
              if (iterator.next().getId() == GramPracConstants.EQUAL) {
                expected.replace(GramPracConstants.EQUAL);
                iterator.previous();
              }
              else {
                iterator.previous();
                expected.clear();
                break;
              }
            }
            else {
              expected.clear();
            }
            
          }
        }
        else if (current.getId() == GramPracConstants.EQUAL) {
          List<Token> valueContent = new ArrayList<Token>();
          
          boolean semiColonEncountered = false;
          
          while (iterator.hasNext()) {
            Token cur = iterator.next();
            if (cur.getId() == GramPracConstants.SEMICOLON) {
              semiColonEncountered = true;
              break;
            }
            else {
              valueContent.add(cur);
            }
          }
          
          if (valueContent.isEmpty()) {
            throw new EmptyExprException(current, "VariableDeclaration");
          }
          
          if (!semiColonEncountered) {
            throw FormationException.createException("VariableDeclaration", current, new ExpectedSet(GramPracConstants.SEMICOLON));
          }
          else {
            iterator.previous(); //backtrack iterator for callee method
          }
          
          try {         
            List<TNode> exprNodes = ExprParser.getUniversalParser().parseExpression(valueContent);
            
            value = new TExpr(new ArrayList<>(exprNodes));
            expected.clear();
            break;
          } catch (ParserLogException e) {
            throw new ExpressionParseException(e);
          }
        }
        
      }
    }
    
    if (expected.isEmpty() || expected.contains(-1)) {
      return new RVariable(varType, new TIden(varName), value, variableDescriptors);
    }
    
    throw FormationException.createException("VariableDeclaration", iterator.previous(), expected);
  }
  
  /*
  public static void main(String [] args) throws Exception{
    Tokenizer tokenizer = new GramPracTokenizer(new StringReader("java.lang.String<T<W>, wow.lmao.ca<O>> wow = 10+2*3;"));
    List<Token> tokens = new ArrayList<>();
        
    Token token = null;
    while ((token = tokenizer.next()) != null) {
      tokens.add(token);
    }
    
    System.out.println("RAW: "+tokens);
    
    ListIterator<Token> iterator = tokens.listIterator();
    
    RVariable variable = parseVariable(iterator);
    
    System.out.println(" PARSED: "+variable.getIdentifier()+" | Value: "+variable.getValue()+"LATEST: "+iterator.next());
    System.out.println(" TYPE: "+variable.getProvidedType());
    System.out.println(" INFER? "+variable.toBeInferred());
  }
  */
}
