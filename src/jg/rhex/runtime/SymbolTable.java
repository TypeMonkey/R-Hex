package jg.rhex.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jg.rhex.common.ArrayType;
import jg.rhex.common.FunctionIdentity;
import jg.rhex.common.FunctionSignature;
import jg.rhex.common.Type;
import jg.rhex.common.TypeUtils;
import jg.rhex.runtime.components.ArrayClass;
import jg.rhex.runtime.components.Function;
import jg.rhex.runtime.components.GenClass;
import jg.rhex.runtime.components.Variable;
import jg.rhex.runtime.components.java.JavaClass;
import jg.rhex.runtime.components.rhexspec.RhexFile;

public class SymbolTable {
  
  private ClassLoader classLoader;

  private Map<String, RhexFile> rhexFileMap;
  
  private Map<Type, GenClass> classMap;
  
  public SymbolTable(ClassLoader classLoader,
      Map<String, RhexFile> fileMap, 
      Map<Type, GenClass> classMap) {   
    this.classLoader = classLoader;
    this.rhexFileMap = fileMap;
    this.classMap = classMap;
  }
  
  public RhexFile findFile(String fullName) {
    return rhexFileMap.get(fullName);
  }
  
  public GenClass findClass(Type type) {
    if (type instanceof ArrayType) {
      return findClass((ArrayType) type);
    }
    else if (TypeUtils.isPrimitive(type)) {
      return JavaClass.getJavaClassRep(type.getFullName());
    }
    GenClass genClass = classMap.get(type);
    if (genClass == null) {
      try {
        Class<?> compClass = classLoader.loadClass(type.getFullName());
        genClass = JavaClass.getJavaClassRep(compClass);
      } catch (ClassNotFoundException e) {
        return null;
      }
    }
    return genClass;
  }
  
  public ArrayClass findClass(ArrayType type) {
    System.out.println("---- F CLASS - arr type");
    GenClass baseType = findClass(new Type(type.getSimpleName(), type.getFullName()));
    if (baseType == null) {
      return null;
    }
    else {
      return new ArrayClass(baseType, type.getDimensions());
    }
  }
}
