package jg.rhex.common;

import java.util.Arrays;
import java.util.Objects;

public class FunctionSignature {
  
  private final String name;
  private final Type [] paramTypes;
  
  public FunctionSignature(String funcName, Type [] parameterTypes) {
    this.name = funcName;
    this.paramTypes = parameterTypes;
  }
  
  public boolean equals(Object object) {
    if (object instanceof FunctionSignature) {
      FunctionSignature functionKey = (FunctionSignature) object;
      if (functionKey.name.equals(name) && functionKey.paramTypes.length == paramTypes.length) {
        for (int i = 0; i < paramTypes.length; i++) {
          if (!paramTypes[i].equals(functionKey.paramTypes[i])) {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(name, Arrays.hashCode(paramTypes));
  }

  public String getName() {
    return name;
  }

  public Type[] getParamTypes() {
    return paramTypes;
  }
}
