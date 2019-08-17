package jg.rhex.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class TypeUtils {

  public static final Map<String, Type> PRIMITIVE_TYPES;

  static{   
    HashMap<String, Type> temp = new HashMap<>();
    temp.put(Type.INT.getSimpleName(), Type.INT);
    temp.put(Type.LONG.getSimpleName(), Type.LONG);
    temp.put(Type.DOUBLE.getSimpleName(), Type.DOUBLE);
    temp.put(Type.FLOAT.getSimpleName(), Type.FLOAT);
    temp.put(Type.CHAR.getSimpleName(), Type.CHAR);
    temp.put(Type.BYTE.getSimpleName(), Type.BYTE);
    temp.put(Type.SHORT.getSimpleName(), Type.SHORT);

    PRIMITIVE_TYPES = Collections.unmodifiableMap(temp);
  }

  public static boolean isPrimitive(String type){
    return PRIMITIVE_TYPES.containsKey(type);
  }

  public static boolean isPrimitive(Type type){
    return PRIMITIVE_TYPES.containsKey(type.getSimpleName());
  }

  public static boolean isVoid(String type){
    return Type.VOID_TYPE.getSimpleName().equals(type);
  }

  public static boolean isVoid(Type type){
    return Type.VOID_TYPE.equals(type);
  }
  
  public static String getHostFileName(Type type) {
    String [] split = type.getFullName().split("\\.");
    return split[split.length - 2];
  }
  
  public static Type formType(Class<?> javaClass){
    String [] totalName = javaClass.getCanonicalName().split("\\[]",-1);
    String [] baseTypeSplit = totalName[0].split("\\.");
    if (totalName.length - 1 >= 1) {
      Type base = PRIMITIVE_TYPES.get(baseTypeSplit[baseTypeSplit.length - 1]);
      if (base == null) {
        return new ArrayType(totalName.length - 1, new Type(baseTypeSplit[baseTypeSplit.length - 1], totalName[0]));
      }
      else {
        return new ArrayType(totalName.length - 1, base);
      }
    }
    return new Type(javaClass.getSimpleName(), javaClass.getName());
  }
}
