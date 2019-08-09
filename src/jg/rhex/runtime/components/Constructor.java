package jg.rhex.runtime.components;

import jg.rhex.common.FunctionIdentity;
import jg.rhex.common.FunctionSignature;

public abstract class Constructor extends Function{

  public Constructor(GenClass hostClass, FunctionSignature signature) {
    super(new FunctionIdentity(signature, hostClass.getTypeInfo()));
  }

  
}
