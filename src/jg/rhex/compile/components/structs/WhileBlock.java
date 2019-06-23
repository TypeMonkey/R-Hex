package jg.rhex.compile.components.structs;

import net.percederberg.grammatica.parser.Token;

public class WhileBlock extends RStateBlock{
  
  public RStatement conditionalStatement;

  public WhileBlock(Token whileDescriptor) {
    super(whileDescriptor, BlockType.WHILE);
  }
  
  public void setConditional(RStatement conditional){
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
