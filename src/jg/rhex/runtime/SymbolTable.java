package jg.rhex.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jg.rhex.common.FunctionSignature;
import jg.rhex.common.Type;
import jg.rhex.common.TypeUtils;
import jg.rhex.runtime.components.Function;
import jg.rhex.runtime.components.GenClass;
import jg.rhex.runtime.components.Variable;
import jg.rhex.runtime.components.java.JavaClass;
import jg.rhex.runtime.components.rhexspec.RhexFile;

public class SymbolTable {
  
  private ClassLoader classLoader;

  private Map<String, RhexFile> rhexFileMap;
  
  private Map<Type, GenClass> classMap;
  
  private List<Map<FunctionSignature, Function>> funcMaps;
  
  private List<Map<String, Variable>> varMaps;
  
  public SymbolTable(ClassLoader classLoader,
      Map<String, RhexFile> fileMap, 
      Map<Type, GenClass> classMap, 
      Map<String, Variable> localVars, 
      Map<FunctionSignature, Function> localFunctions) {   
    this.classLoader = classLoader;
    rhexFileMap = new HashMap<>(fileMap);
    this.classMap = classMap;
    varMaps = new ArrayList<>();
    funcMaps = new ArrayList<>();
    
    varMaps.add(localVars);
    funcMaps.add(localFunctions);
  }
  
  public boolean addClass(GenClass genClass) {
    if (classMap.containsKey(genClass.getTypeInfo())) {
      return false;
    }
    return classMap.put(genClass.getTypeInfo(),  genClass) == null;
  }
  
  public void addFunctionMap(Map<FunctionSignature, Function> fMap) {
    funcMaps.add(fMap);
  }
  
  public boolean addLocalFunction(Function function) {
    if (funcMaps.get(0).containsKey(function.getSignature())) {
      return false;
    }
    return funcMaps.get(0).put(function.getSignature(), function) == null;
  }
  
  public void setLocalFuncMap(Map<FunctionSignature, Function> funcMap) {
    funcMaps.set(0, funcMap);
  }
  
  public void addVariableMap(Map<String, Variable> varMap) {
    varMaps.add(varMap);
  }
  
  public boolean addLocalVariable(Variable variable) {
    if (varMaps.get(0).containsKey(variable.getName())) {
      return false;
    }
    return varMaps.get(0).put(variable.getName(), variable) == null;
  }
  
  public void setLocalVarMap(Map<String, Variable> varMap) {
    varMaps.set(0, varMap);
  }
  
  public RhexFile findFile(String fullName) {
    return rhexFileMap.get(fullName);
  }
  
  public GenClass findClass(Type type) {
    if (TypeUtils.isPrimitive(type)) {
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
  
  public Function findFunction(FunctionSignature signature) {
    for (Map<FunctionSignature, ? super Function> map : funcMaps) {
      Function found = (Function) map.get(signature);
      if (found != null) {
        return found;
      }
    }
    return null;
  }
  
  public Variable findVariable(String varName) {
    for (Map<String, ? super Variable> map : varMaps) {
      Variable found = (Variable) map.get(varName);
      if (found != null) {
        return found;
      }
    }
    return null;
  }
}
