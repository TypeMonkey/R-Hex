package jg.rhex.compile.components.structs;

import net.percederberg.grammatica.parser.Token;

public class WhileBlock extends RStateBlock{
  
  public RStatement conditionalStatement;

  public WhileBlock(Token whileDescriptor) {
    super(whileDescriptor, BlockType.WHILE);
  }
  
  public void setConditional(RStatement conditional){
    if (isSealed) {
      throw new IllegalStateException("This structure has been sealed!");
    }
    this.conditionalStatement = conditional;
  }
  
  public RStatement getConditional(){
    return conditionalStatement;
  }
  
  public String toString() {
    String x = "WHILE ~ | COND ~ "+conditionalStatement;
    return x;
  }
}
