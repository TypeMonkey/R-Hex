package jg.rhex.common;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class TypeRequirements {

  //key is function name , value is corresponding functions with the same name
  private final Map<String, Set<FunctionReturn>> reqMap;
  
  private final Map<String, TypeRequirements> nestedRequirements;
  
  private final Set<Type> superTypes;
  
  public TypeRequirements(Map<String, Set<FunctionReturn>> reqs, Map<String, 
      TypeRequirements> nestedReqs,
      Set<Type> superTypes){
    this.reqMap = reqs;
    this.superTypes = superTypes;
    this.nestedRequirements = nestedReqs;
  }
  
  public Set<FunctionReturn> getFunctionReq(String functionName){
    return reqMap.get(functionName);
  }
  
  public TypeRequirements getNestedTypeRequirement(String handle) {
    return nestedRequirements.get(handle);
  }
  
  public Set<Type> getSuperTypes(){
    return superTypes;
  }
  
  public Map<String, Set<FunctionReturn>> getAllRequiredFunctions(){
    return reqMap;
  }
  
  public static class FunctionReturn{
    private final FunctionKey funcSig;
    private final Type returnType;
    
    public FunctionReturn(FunctionKey funcSig, Type returnType) {
      this.funcSig = funcSig;
      this.returnType = returnType;
    }
    
    public boolean equals(Object object){
      if (object instanceof FunctionReturn) {
        FunctionReturn functionReturn = (FunctionReturn) object;       
        return functionReturn.funcSig.equals(funcSig) && functionReturn.returnType.equals(returnType);
      }
      return false;
    }
    
    public int hashCode(){
      return Objects.hash(funcSig, returnType);
    }

    public FunctionKey getFuncSig() {
      return funcSig;
    }

    public Type getReturnType() {
      return returnType;
    }
  }
  
}
