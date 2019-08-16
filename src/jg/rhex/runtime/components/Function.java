package jg.rhex.runtime.components;

import jg.rhex.common.FunctionIdentity;
import jg.rhex.common.FunctionSignature;

/**
 * Represents callable functions (class, instance, and file functions) along with constructors
 * @author Jose
 *
 */
public abstract class Function {

  private final FunctionIdentity identity;
  private final boolean isConstructor;
  
  public Function(FunctionIdentity identity, boolean isConstructor) {
    this.identity = identity;
    this.isConstructor = isConstructor;
  }
  
  /**
   * Evaluates (or "executes") this function.
   * @param table - the SymbolTable to use when evaluating this function
   * @param parameters - the function parameters
   * @param instance - the instance on which this function is invoked on, or null if this function
   *                   isn't an instance function.
   * @return the function's output, or null if the function returns no output (void), or the function 
   *         intentionally returned null. 
   */
  public abstract Instance eval(SymbolTable table, Instance instance,  Instance ... parameters);
  
  public boolean equals(Object object){
    if (object instanceof Function) {
      Function function = (Function) object;
      return getSignature().equals(function.getSignature());
    }
    return false;
  }
  
  public int hashCode(){
    return identity.getFuncSig().hashCode();
  }
  
  public boolean isAConstructor(){
    return isConstructor;
  }

  public String getName(){
    return identity.getFuncSig().getName();
  }
  
  public FunctionSignature getSignature(){
    return identity.getFuncSig();
  }
  
  public FunctionIdentity getIdentity() {
    return identity;
  } 
  
  
}
