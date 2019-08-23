package jg.rhex.compile.components.structs;

import java.util.ArrayList;
import java.util.List;

import jg.rhex.compile.components.tnodes.atoms.TIden;
import jg.rhex.compile.components.tnodes.atoms.TType;
import net.percederberg.grammatica.parser.Token;

public class CatchBlock extends RStateBlock{

  private List<TType> exceptionTypes;
  private TIden tIden;
  
  public CatchBlock(Token descriptor) {
    super(descriptor, BlockType.CATCH);
    exceptionTypes = new ArrayList<>();
  }

  public void setExceptionIdentifier(TIden identifier){
    this.tIden = identifier;
  }
  
  public void addException(TType exception){
    exceptionTypes.add(exception);
  }
  
  public List<TType> getExceptionTypes() {
    return exceptionTypes;
  }
  
  public TIden getExceptionIdentifier(){
    return tIden;
  }
}
