package jg.rhex.compile.components.structs;

import net.percederberg.grammatica.parser.Token;

/**
 * Represents if and else-if blocks
 * @author Jose
 *
 */
public class IfBlock extends RStateBlock{
  
  private RStatement conditional;

  public IfBlock(Token descriptor, BlockType blockType) {
    super(descriptor, blockType);
  }
  
  public void setConditional(RStatement conditional){
    this.conditional = conditional;
  }
  
  public RStatement getConditional(){
    return conditional;
  }
  
  public String toString() {
    return getBlockType()+" ~ "+conditional;
  }
  
}
