package jg.rhex.common;

import java.util.Objects;

public class ArrayType extends Type{

  private int dimensions;
  
  public ArrayType(int dimensions, Type baseType) {
    super(baseType.getSimpleName(), baseType.getFullName());
    if (dimensions <= 0) {
      throw new IllegalArgumentException("Array dimensions cannot be 0, or negative");
    }
    this.dimensions = dimensions;
  }
  
  public boolean equals(Object object){
    if (object instanceof ArrayType) {
      ArrayType type = (ArrayType) object;
      
      return super.equals(type) && dimensions == type.dimensions;  
    }
    return false;
  }
  
  public int hashCode() {
    return Objects.hash(super.hashCode(), dimensions);
  }
  
  public String toString() {
    return super.toString()+"[DIM:"+dimensions+"]";
  }

  public int getDimensions() {
    return dimensions;
  }
}
