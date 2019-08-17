package jg.rhex.runtime.components;

import java.util.Set;

import jg.rhex.common.Descriptor;
import jg.rhex.common.FunctionIdentity;
import jg.rhex.common.FunctionSignature;
import jg.rhex.common.Type;

public abstract class Constructor extends Function{
  
  private final GenClass hostClass;
  
  public Constructor(GenClass hostClass, FunctionSignature signature, Set<Descriptor> descriptors) {
    super(new FunctionIdentity(signature, hostClass.getTypeInfo()), descriptors);
    this.hostClass = hostClass;
  }
  
  public abstract Instance createInstance(SymbolTable table, Instance ... parameters);
  
  @Override
  public final Instance eval(SymbolTable table, Instance instance,  Instance ... parameters) {
    return createInstance(table, parameters);  
  }
  
  public GenClass getHostClass() {
    return hostClass;
  }
}
