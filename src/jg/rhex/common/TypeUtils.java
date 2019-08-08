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
}
