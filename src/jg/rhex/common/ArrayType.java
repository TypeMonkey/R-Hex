package jg.rhex.common;

public class ArrayType extends Type{

  private int dimensions;
  
  public ArrayType(int dimensions, Type baseType) {
    super(baseType.getSimpleName(), baseType.getFullName());
    this.dimensions = dimensions;
  }
  
  public String toString() {
    return super.toString()+"[DIM:"+dimensions+"]";
  }

  public int getDimensions() {
    return dimensions;
  }
}
