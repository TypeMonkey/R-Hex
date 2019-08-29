package jg.rhex.runtime.components.java;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jg.rhex.common.Descriptor;
import jg.rhex.common.FunctionIdentity;
import jg.rhex.common.FunctionSignature;
import jg.rhex.common.Type;
import jg.rhex.common.TypeUtils;
import jg.rhex.compile.RhexCompiler;
import jg.rhex.runtime.components.Constructor;
import jg.rhex.runtime.components.Function;
import jg.rhex.runtime.components.GenClass;
import jg.rhex.runtime.components.Variable;

public class JavaClass extends GenClass{
  
  private static final Map<Class<?>, JavaClass> loadedClasses = new HashMap<>();
  static{
    loadedClasses.put(Void.class, new JavaClass(new Type("Void", "java.lang.Void"), 
        null, 
        new HashSet<>(), 
        new HashMap<>(), 
        new HashMap<>(), 
        new HashMap<>(), 
        new HashSet<>(Arrays.asList(Descriptor.FINAL)),
        false));
    
    loadedClasses.put(void.class, new JavaClass(Type.VOID_TYPE, 
        null, 
        new HashSet<>(), 
        new HashMap<>(), 
        new HashMap<>(), 
        new HashMap<>(), 
        new HashSet<>(Arrays.asList(Descriptor.FINAL)),
        false));
  }

  protected JavaClass(Type typeInfo, JavaClass parent, Set<GenClass> interfaces, 
      Map<FunctionSignature, Function> funcMap, 
      Map<FunctionSignature, Constructor> constructors, 
      Map<String, Variable> varMap,
      Set<Descriptor> descriptors,
      boolean isInterface) {
    super(typeInfo, parent, interfaces, funcMap, constructors, varMap, descriptors, isInterface);
  }

  @Override
  public Map<String, Variable> cloneVariableMap() {
    return null;
  }
  
  public static JavaClass getJavaClassRep(String fullName){
    if (TypeUtils.isPrimitive(fullName) || TypeUtils.isVoid(fullName)) {
      if (fullName.equals(Type.INT.getFullName())) {
         return getJavaClassRep(int.class);
      }
      else if (fullName.equals(Type.DOUBLE.getFullName())) {
        return getJavaClassRep(double.class);
      }
      else if (fullName.equals(Type.LONG.getFullName())) {
        return getJavaClassRep(long.class);
      }
      else if (fullName.equals(Type.FLOAT.getFullName())) {
        return getJavaClassRep(float.class);
      }
      else if (fullName.equals(Type.BYTE.getFullName())) {
        return getJavaClassRep(byte.class);
      }
      else if (fullName.equals(Type.SHORT.getFullName())) {
        return getJavaClassRep(short.class);
      }
      else if (fullName.equals(Type.BYTE.getFullName())) {
        return getJavaClassRep(byte.class);
      }
      else if (fullName.equals(Type.BOOL.getFullName())) {
        return getJavaClassRep(boolean.class);
      }
      else if (fullName.equals(Type.VOID_TYPE.getFullName())) {
        return getJavaClassRep(Void.class);
      }
      else{
        return getJavaClassRep(void.class);
      }     
    }
    else {
      try {
        Class<?> target = Class.forName(fullName);
        return getJavaClassRep(target);
      } catch (ClassNotFoundException e) {
        return null;
      }
    }
  }

  public static JavaClass getJavaClassRep(Class<?> target){
    if (loadedClasses.containsKey(target)) {
      return loadedClasses.get(target);
    }
    else {
      Type targetType = TypeUtils.formType(target);   
      
      JavaClass parent = null;
      HashSet<GenClass> interfaces = new HashSet<>();
      
      HashMap<FunctionSignature, Function> methods = new HashMap<>();
      HashMap<FunctionSignature, Constructor> constructors = new HashMap<>();
      HashMap<String, Variable> varMap = new HashMap<>();
      
      
      //add super type (start with parent)
      if (target.getSuperclass() != null) {
        parent = getJavaClassRep(target.getSuperclass());
      }
      
      //add interface types
      for(Class<?> inters : target.getInterfaces()) {
        interfaces.add(getJavaClassRep(inters));
      }
      
      JavaClass javaClass = new JavaClass(targetType, parent, interfaces, 
          methods, constructors, varMap, Descriptor.translateModifiers(target.getModifiers()), target.isInterface());
      
      //add class variables
      for (Field field : target.getFields()) {
        if (Modifier.isStatic(field.getModifiers())) {
          JavaStaticVariable variable = new JavaStaticVariable(Descriptor.translateModifiers(field.getModifiers()), 
              field);
          
          varMap.put(variable.getName(), variable);
        }       
      }
      
      //add constructors
      for (java.lang.reflect.Constructor<?> constructor : target.getConstructors()) {
        Type [] params = new Type[constructor.getParameterCount()];
        for(int i = 0; i < params.length; i++) {
          params[i] = TypeUtils.formType(constructor.getParameterTypes()[i]);
        }
        
        FunctionSignature signature = new FunctionSignature(targetType.getSimpleName(),
            params, 
            Descriptor.translateModifiers(constructor.getModifiers()));
        
        HashSet<Type> declaredExceptions = new HashSet<>();
        for(Class<?> ex : constructor.getExceptionTypes()){
          declaredExceptions.add(TypeUtils.formType(ex));
        }

        JavaConstructor javaConstructor = new JavaConstructor(javaClass, signature, constructor, declaredExceptions);
        
        constructors.put(signature, javaConstructor); 
      }
      
      //add methods
      for (Method method : target.getMethods()) {
        Type [] params = new Type[method.getParameterCount()];
        for(int i = 0; i < params.length; i++) {
          params[i] = TypeUtils.formType(method.getParameterTypes()[i]);
        }
        
        HashSet<Type> declaredExceptions = new HashSet<>();
        for(Class<?> ex : method.getExceptionTypes()){
          declaredExceptions.add(TypeUtils.formType(ex));
        }
        
        FunctionSignature methodSig = new FunctionSignature(method.getName(), params, Descriptor.translateModifiers(method.getModifiers()));
        FunctionIdentity methodIdentity = new FunctionIdentity(methodSig, TypeUtils.formType(method.getReturnType()));
        
        methods.put(methodSig, new JavaMethod(methodIdentity, method, declaredExceptions));
      }
      
      loadedClasses.put(target, javaClass);
      
      return javaClass;
    }
  }
}
