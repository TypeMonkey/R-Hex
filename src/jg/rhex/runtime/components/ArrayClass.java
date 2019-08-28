package jg.rhex.runtime.components;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import jg.rhex.common.ArrayType;
import jg.rhex.common.Type;
import jg.rhex.runtime.components.java.JavaClass;

public class ArrayClass extends GenClass{

  private final int arrayDimensions;
  private final GenClass baseType;
  
  public ArrayClass(GenClass baseType, int arrayDimensions) {
    super(new ArrayType(arrayDimensions, baseType.getTypeInfo()), 
        JavaClass.getJavaClassRep(Object.class), 
        new HashSet<>(), 
        new HashMap<>(), 
        new HashMap<>(), 
        new HashMap<>(), new HashSet<>(), false);
    
    this.arrayDimensions = arrayDimensions;
    this.baseType = baseType;
  }
  
  public int getArrayDimensions() {
    return arrayDimensions;
  }
  
  public GenClass getBaseType() {
    return baseType;
  }
  
  public String toString() {
    return "ACLASS "+super.toString();
  }

  @Override
  public Map<String, Variable> cloneVariableMap() {
    return null;
  }

}
