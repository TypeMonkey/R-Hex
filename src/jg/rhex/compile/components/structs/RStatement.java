package jg.rhex.compile.components.structs;

import jg.rhex.compile.components.tnodes.TNode;
import net.percederberg.grammatica.parser.Token;

/**
 * Represents a statement.
 * @author Jose
 *
 */
public class RStatement {
  
  public enum RStateDescriptor{
    RETURN,    //a return statement - to return value from functions / methods
    THROW,     //a throw statement - to "throw" errors within functions / methods
    BREAK,     //a "break" statement
    CONTINUE,  //a loop "continue" statement
    REGULAR,   //a regular statement 
    VAR_DEC,   //a variable declaration statement
    BLOCK;     //this statement is actually a "block" of statements
  }
  
  private TNode statement;
  
  private RStateDescriptor descriptor;
  
  private Token descriptorToken;
  
  /**
   * Constructs an RStatement meant for single-descriptor statements
   * (i.e: break, continue, or return - assuming a no value return)
   * @param descriptor
   * @param descToken
   */
  public RStatement(RStateDescriptor descriptor , Token descToken){
    this(descriptor, descToken, null);
  }
  
  /**
   * Constructs an RStatement meant for regular statements
   * @param statement
   */
  public RStatement(TNode statement){
    this(RStateDescriptor.REGULAR, null, statement);
  }
  
  /**
   * Constructs an RStatement
   * @param descriptor - the statement descriptor
   * @param descToken - the descriptor token of this statement
   * @param statement - the actual TNode that contains this statement
   */
  public RStatement(RStateDescriptor descriptor , Token descToken, TNode statement){
    this.statement = statement;
    this.descriptor = descriptor;
    this.descriptorToken = descToken;
  }

  public TNode getStatement() {
    return statement;
  }

  public RStateDescriptor getDescriptor() {
    return descriptor;
  }

  public Token getDescriptorToken() {
    return descriptorToken;
  }
}
