package jg.rhex.runtime.components;

import jg.rhex.common.FunctionSignature;

public abstract class Constructor extends Function{

  private final GenClass hostClass;
  
  public Constructor(GenClass hostClass, FunctionSignature signature) {
    super(signature, hostClass);
    this.hostClass = hostClass;
  }
  
  public abstract Instance createInstance(SymbolTable table, Instance ... parameters);
  
  @Override
  public Instance eval(SymbolTable table, Instance instance,  Instance ... parameters) {
    return createInstance(table, parameters);  
  }

  public GenClass getHostClass() {
    return hostClass;
  }
  
}
