package jg.rhex.runtime.components.java;

import java.lang.reflect.Method;

import jg.rhex.common.Descriptor;
import jg.rhex.common.FunctionIdentity;
import jg.rhex.runtime.components.Function;
import jg.rhex.runtime.components.Instance;
import jg.rhex.runtime.components.SymbolTable;

public class JavaMethod extends Function{

  public JavaMethod(FunctionIdentity identity, Method method) {
    super(identity, Descriptor.translateModifiers(method.getModifiers()));
  }

  @Override
  public Instance eval(SymbolTable table, Instance instance, Instance... parameters) {
    return null;
  }

}
