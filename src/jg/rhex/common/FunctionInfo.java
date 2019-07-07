package jg.rhex.common;

import java.util.Arrays;
import java.util.Objects;

import jg.rhex.compile.components.tnodes.atoms.TType;
import net.percederberg.grammatica.parser.Token;

public class FunctionInfo {
  
  private final TType [] parameterTypes;
  private final Token functionName;
  private final TType returnType;
    
  public FunctionInfo(Token functionName, TType returnType, TType ... paramTypes){
    this.functionName = functionName;
    this.parameterTypes = paramTypes;
    this.returnType = returnType;
  }
  
  public boolean equals(Object object){
    if (object instanceof FunctionInfo) {
      FunctionInfo otherKey = (FunctionInfo) object;
      
      if (otherKey.functionName.getImage().equals(functionName.getImage()) ) {
        if (parameterTypes.length == otherKey.parameterTypes.length) {
          for(int i = 0; i < parameterTypes.length; i++){
            if (!parameterTypes[i].equals(otherKey.parameterTypes[i])) {
              return false;
            }
          }
          
          return true;
        }
      }    
    }
    return false;
  }
  
  public int hashCode(){
    return Objects.hash(Arrays.hashCode(parameterTypes), functionName.getImage());
  }

  public TType[] getParameterTypes() {
    return parameterTypes;
  }

  public Token getFunctionName() {
    return functionName;
  }
  
  public TType getReturnType() {
    return returnType;
  }
}
