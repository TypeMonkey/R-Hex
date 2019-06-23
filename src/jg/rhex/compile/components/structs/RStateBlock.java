package jg.rhex.compile.components.structs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.percederberg.grammatica.parser.Token;

/**
 * Represents a "block" of statements used to represent the body of code blocks
 * @author Jose
 *
 */
public class RStateBlock extends RStatement {

  public enum BlockType{
    FOR,
    WHILE,
    IF,
    ELSE,
    ELSE_IF,
    TRY,
    CATCH,
    GENERAL; //For blocks that are just in a separate scope
  }
  
  private List<RStatement> statements;
  private BlockType blockType;
  
  /**
   * Constructs an empty RStateBlock
   * @param descirptor - the Token that describes this block
   * @param blockType - the corresponding BlockType of this block's descriptor
   */
  public RStateBlock(Token descriptor, BlockType blockType){
    this(new ArrayList<>(), descriptor, blockType);
  }
  
  /**
   * Constructs an RStateBlock
   * @param statements - statements in this block
   * @param descirptor - the Token that describes this block
   * @param blockType - the corresponding BlockType of this block's descriptor
   */
  public RStateBlock(List<RStatement> statements, Token descriptor, BlockType blockType) {
    super(RStateDescriptor.BLOCK, descriptor);
    this.statements = statements;
    this.blockType = blockType;
  }
  
  public void addStatements(Collection<RStatement> statements){
    this.statements.addAll(statements);
  }
  
  public void addStatement(RStatement statement){
    statements.add(statement);
  }

  public List<RStatement> getStatements() {
    return statements;
  }

  public BlockType getBlockType() {
    return blockType;
  }  
  
  public String toString() {
    return "BLOCK ~ "+getBlockType()+" | STATEMENT COUNT: "+statements.size();
  }
}
