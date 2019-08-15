package jg.rhex.runtime.components.rhexspec;

import jg.rhex.common.FunctionSignature;
import jg.rhex.compile.components.structs.RFunc;
import jg.rhex.runtime.components.Constructor;
import jg.rhex.runtime.components.GenClass;
import jg.rhex.runtime.components.Instance;
import jg.rhex.runtime.components.SymbolTable;

public class RhexConstructor extends Constructor{

  private final RFunc original;
  
  public RhexConstructor(RhexClass hostClass, FunctionSignature signature, RFunc original) {
    super(hostClass, signature);
    this.original = original;
  }

 

  public RFunc getOriginal(){
    return original;
  }



  @Override
  public Instance createInstance(SymbolTable table, Instance... parameters) {
    // TODO Auto-generated method stub
    return null;
  }
}
