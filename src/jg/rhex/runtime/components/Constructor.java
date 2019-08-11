package jg.rhex.runtime.components;

import jg.rhex.common.FunctionSignature;

public abstract class Constructor extends Function{

  private final GenClass hostClass;
  
  public Constructor(GenClass hostClass, FunctionSignature signature) {
    super(signature, hostClass);
    this.hostClass = hostClass;
  }

  public GenClass getHostClass() {
    return hostClass;
  }
  
}
