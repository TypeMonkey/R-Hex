package jg.rhex.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class TypeUtils {

  public static final Map<String, Type> PRIMITIVE_TYPES;
  
  public static final Map<String, Type> WRAPPER_TYPES;

  static{   
    HashMap<String, Type> temp = new HashMap<>();
    temp.put(Type.INT.getSimpleName(), Type.INT);
    temp.put(Type.LONG.getSimpleName(), Type.LONG);
    temp.put(Type.DOUBLE.getSimpleName(), Type.DOUBLE);
    temp.put(Type.FLOAT.getSimpleName(), Type.FLOAT);
    temp.put(Type.CHAR.getSimpleName(), Type.CHAR);
    temp.put(Type.BYTE.getSimpleName(), Type.BYTE);
    temp.put(Type.SHORT.getSimpleName(), Type.SHORT);
    temp.put(Type.BOOL.getSimpleName(), Type.BOOL);

    PRIMITIVE_TYPES = Collections.unmodifiableMap(temp);
    
    temp = new HashMap<>();
    temp.put("java.lang.Integer", new Type("Integer", "java.lang.Integer"));
    temp.put("java.lang.Long", new Type("Long", "java.lang.Long"));
    temp.put("java.lang.Double", new Type("Double", "java.lang.Double"));
    temp.put("java.lang.Float", new Type("Float", "java.lang.Float"));
    temp.put("java.lang.Character", new Type("Character", "java.lang.Character"));
    temp.put("java.lang.Byte", new Type("Byte", "java.lang.Byte"));
    temp.put("java.lang.Short", new Type("Short", "java.lang.Short"));
    temp.put("java.lang.Boolean", new Type("Boolean", "java.lang.Boolean"));
    WRAPPER_TYPES = Collections.unmodifiableMap(temp);
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
  
  public static boolean isNumericalPrimitive(String type) {
    if (isPrimitive(type) && !Type.BOOL.getSimpleName().equals(type)) {
      return true;
    }
    return false;
  }
  
  public static boolean isNumerical(String type) {
    return isNumericalPrimitive(type) || (WRAPPER_TYPES.containsKey(type) && !type.equals("java.lang.Boolean"));
  }
  
  public static boolean isNumerical(Type type) {
    return isNumerical(type.getFullName());
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
