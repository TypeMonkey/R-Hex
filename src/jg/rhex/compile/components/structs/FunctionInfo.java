package jg.rhex.compile.components.structs;

import jg.rhex.compile.components.tnodes.atoms.TType;
import net.percederberg.grammatica.parser.Token;

public class FunctionInfo {

  private final TType [] parameterTypes;
  private final Token functionName;
  private final TType returnType;

  public FunctionInfo(Token functionName, 
      TType returnType, 
      TType [] parameterTypes){
    this.functionName = functionName;
    this.parameterTypes = parameterTypes;
    this.returnType = returnType;
  }
  
  public boolean equals(Object object){
    if (object instanceof FunctionInfo) {
      FunctionInfo functionInfo = (FunctionInfo) object;
      
      if (functionInfo.getFunctionName().getImage().equals(functionName.getImage()) &&
          functionInfo.getParameterTypes().length == parameterTypes.length &&
          functionInfo.getReturnType().getBaseString().equals(returnType.getBaseString())) {
        for(int i = 0; i < parameterTypes.length; i++){
          if (!functionInfo.parameterTypes[i].equals(parameterTypes)) {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }

  public TType [] getParameterTypes() {
    return parameterTypes;
  }

  public Token getFunctionName() {
    return functionName;
  }

  public TType getReturnType() {
    return returnType;
  }

}
