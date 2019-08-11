package jg.rhex.common;

import java.util.Objects;

public class FunctionIdentity{
  private final FunctionSignature funcSig;
  private final Type returnType;
  
  public FunctionIdentity(FunctionSignature funcSig) {
    this(funcSig, Type.VOID_TYPE);
  }
  
  public FunctionIdentity(FunctionSignature funcSig, Type returnType) {
    this.funcSig = funcSig;
    this.returnType = returnType;
  }
  
  public boolean equals(Object object){
    if (object instanceof FunctionIdentity) {
      FunctionIdentity functionReturn = (FunctionIdentity) object;       
      return functionReturn.funcSig.equals(funcSig) && functionReturn.returnType.equals(returnType);
    }
    return false;
  }
  
  public int hashCode(){
    return Objects.hash(funcSig, returnType);
  }
  
  public String toString() {
    return returnType+" "+funcSig;
  }

  public FunctionSignature getFuncSig() {
    return funcSig;
  }

  public Type getReturnType() {
    return returnType;
  }
}
