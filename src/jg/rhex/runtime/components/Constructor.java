package jg.rhex.runtime.components;

import jg.rhex.common.FunctionIdentity;
import jg.rhex.common.FunctionSignature;
import jg.rhex.common.Type;

public abstract class Constructor extends Function{
  
  public Constructor(Type hostType, FunctionSignature signature) {
    super(new FunctionIdentity(signature, hostType), true);
  }
  
  public abstract Instance createInstance(SymbolTable table, Instance ... parameters);
  
  @Override
  public final Instance eval(SymbolTable table, Instance instance,  Instance ... parameters) {
    return createInstance(table, parameters);  
  }
  
}
