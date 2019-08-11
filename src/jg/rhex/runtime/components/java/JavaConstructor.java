package jg.rhex.runtime.components.java;

import jg.rhex.common.FunctionSignature;
import jg.rhex.runtime.components.Constructor;
import jg.rhex.runtime.components.GenClass;
import jg.rhex.runtime.components.Instance;
import jg.rhex.runtime.components.SymbolTable;

public class JavaConstructor extends Constructor{

  public JavaConstructor(GenClass hostClass, FunctionSignature signature) {
    super(hostClass, signature);
    // TODO Auto-generated constructor stub
  }

  @Override
  public Instance eval(SymbolTable table, Instance instance, Instance... parameters) {
    // TODO Auto-generated method stub
    return null;
  }

}
