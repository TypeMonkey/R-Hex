package jg.rhex.runtime.components.java;

import java.lang.reflect.Method;
import java.util.Set;

import jg.rhex.common.Descriptor;
import jg.rhex.common.FunctionIdentity;
import jg.rhex.common.Type;
import jg.rhex.runtime.SymbolTable;
import jg.rhex.runtime.components.Function;
import jg.rhex.runtime.components.Instance;

public class JavaMethod extends Function{

  public JavaMethod(FunctionIdentity identity, Method method, Set<Type> exceptions) {
    super(identity, Descriptor.translateModifiers(method.getModifiers()), exceptions);
  }

  @Override
  public Instance eval(SymbolTable table, Instance instance, Instance... parameters) {
    return null;
  }

}
