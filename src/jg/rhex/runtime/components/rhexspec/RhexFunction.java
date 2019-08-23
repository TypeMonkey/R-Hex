package jg.rhex.runtime.components.rhexspec;

import java.util.Set;

import jg.rhex.common.FunctionIdentity;
import jg.rhex.common.Type;
import jg.rhex.compile.components.structs.RFunc;
import jg.rhex.runtime.components.Function;
import jg.rhex.runtime.components.Instance;
import jg.rhex.runtime.components.SymbolTable;

public class RhexFunction extends Function{

  private final RFunc original;
  
  public RhexFunction(FunctionIdentity identity, RFunc original, Set<Type> exceptions) {
    super(identity, original.getDescriptors(), exceptions);
    this.original = original;
  }
  
  @Override
  public Instance eval(SymbolTable table, Instance instance, Instance... parameters) {
    // TODO Auto-generated method stub
    return null;
  }
  
  public RFunc getOriginal(){
    return original;
  }
}
