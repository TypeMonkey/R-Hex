package jg.rhex.common;

import java.util.Objects;

/**
 * Represents a date type in a purely nominal (by name) form.
 * 
 * Types are distinguished by their full, binary name. (ex: "java.lang.Object")
 * Types can also be referred by their simple name. (ex: "Object")
 * 
 * 
 * @author Jose Guaro
 *
 */
public class Type {

  private final String simpleName;
  private final String fullName;
  
  public Type(String simpleName, String fullName){
    this.simpleName = simpleName;
    this.fullName = fullName;
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
  
  public String toString() {
    return fullName;
  }

  public String getSimpleName() {
    return simpleName;
  }

  public String getFullName() {
    return fullName;
  }
}
