package jg.rhex.compile.components.structs;

import net.percederberg.grammatica.parser.Token;

public class ForBlock extends RStateBlock{
  
  private RStatement intialization;
  private RStatement conditional;
  private RStatement change;

  public ForBlock(Token descriptor) {
    super(descriptor, BlockType.FOR);
  }

  public void setInitStatement(RStatement init){
    if (isSealed) {
      throw new IllegalStateException("This structure has been sealed!");
    }
    this.intialization = init;
  }
  
  public void setConditional(RStatement conditional){
    if (isSealed) {
      throw new IllegalStateException("This structure has been sealed!");
    }
    this.conditional = conditional;
  }
  
  public void setChange(RStatement change){
    if (isSealed) {
      throw new IllegalStateException("This structure has been sealed!");
    }
    this.change = change;
  }

  public RStatement getIntialization() {
    return intialization;
  }

  public RStatement getConditional() {
    return conditional;
  }

  public RStatement getChange() {
    return change;
  }
  
  public String toString() {
    String x = "FOR ~ | INIT ~ "+intialization+" | COND ~ "+conditional+" | CHANGE ~ "+change;
    return x;
  }
}
