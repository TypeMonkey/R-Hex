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
    this.intialization = init;
  }
  
  public void setConditional(RStatement conditional){
    this.conditional = conditional;
  }
  
  public void setChange(RStatement change){
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
