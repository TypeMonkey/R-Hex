package jg.rhex.common;

import java.util.Objects;

public class Type {

  private final String simpleName;
  private final String fullName;
  private final TypeRequirements typeRequirements;
  
  public Type(String parameterName, TypeRequirements requirements) {
    this(parameterName, parameterName, requirements);
  }
  
  public Type(String simpleName, String fullName){
    this(simpleName, fullName, null);
  }
  
  private Type(String simpleName, String fullName, TypeRequirements typeRequirements){
    this.simpleName = simpleName;
    this.fullName = fullName;
    this.typeRequirements = typeRequirements;
  }
  
  public boolean equals(Object object){
    if (object instanceof Type) {
      Type type = (Type) object;
      
      return simpleName.equals(type.simpleName) && 
          fullName.equals(type.fullName);  
    }
    return false;
  }
  
  public int hashCode() {
    return Objects.hash(simpleName, fullName);
  }

  public String getSimpleName() {
    return simpleName;
  }

  public String getFullName() {
    return fullName;
  }
  
  public boolean isParameterizedType(){
    return typeRequirements != null;
  }
}
