package jg.rhex.common;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public class FunctionSignature {
  
  private final String name;
  private final Type [] paramTypes;
  private final Set<Descriptor> descriptors;
  
  public FunctionSignature(String funcName, Set<Descriptor> descriptors) {
    this(funcName, new Type[0], descriptors);
  }
  
  public FunctionSignature(String funcName, Type [] parameterTypes, Set<Descriptor> descriptors) {
    this.name = funcName;
    this.paramTypes = parameterTypes;
    this.descriptors = descriptors;
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

  public String toString() {
    String strFormat = name+"( ";
    
    for(int i = 0 ; i < paramTypes.length; i++) {
      if (i < paramTypes.length - 1) {
        strFormat += paramTypes[i] +" , ";
      }
      else {
        strFormat += paramTypes[i];
      }
    }
    
    return strFormat+" )";
  }
  
  public Set<Descriptor> getDescriptors(){
    return descriptors;
  }
  
  public String getName() {
    return name;
  }

  public Type[] getParamTypes() {
    return paramTypes;
  }
}
