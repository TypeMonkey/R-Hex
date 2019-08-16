package jg.rhex.runtime.components.java;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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

  protected JavaClass(Type typeInfo, JavaClass parent, Set<JavaClass> interfaces, 
      Map<FunctionSignature, Function> funcMap, 
      Map<FunctionSignature, Constructor> constructors, 
      Map<String, Variable> varMap,
      boolean isInterface) {
    super(typeInfo, parent,  interfaces, funcMap, constructors, varMap, isInterface);
  }

  @Override
  public Map<String, Variable> cloneVariableMap() {
    
    return null;
  }

  public static JavaClass getJavaClassRep(Class<?> target){
    if (loadedClasses.containsKey(target)) {
      return loadedClasses.get(loadedClasses);
    }
    else {
      Type targetType = TypeUtils.formType(target);   
      
      JavaClass parent = null;
      HashSet<Type> parents = new HashSet<>();
      
      HashMap<FunctionSignature, Function> methods = new HashMap<>();
      HashMap<FunctionSignature, Constructor> constructors = new HashMap<>();
      HashMap<String, Variable> varMap = new HashMap<>();
      
      JavaClass javaClass = new JavaClass(targetType, parents, methods, constructors, varMap);
      
      //add super type (start with parent)
      if (target.getSuperclass() != null) {
        parents.add(TypeUtils.formType(target.getSuperclass()));
      }
      
      //add interface types
      for(Class<?> interfaces : target.getInterfaces()) {
        parents.add(TypeUtils.formType(interfaces));
      }
      
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
        
        FunctionSignature signature = new FunctionSignature(targetType.getSimpleName(), params);
        JavaConstructor javaConstructor = new JavaConstructor(javaClass, signature, constructor);
        
        constructors.put(signature, javaConstructor); 
      }
      
      //add methods
      for (Method method : target.getMethods()) {
        Type [] params = new Type[method.getParameterCount()];
        for(int i = 0; i < params.length; i++) {
          params[i] = TypeUtils.formType(method.getParameterTypes()[i]);
        }
        
        FunctionSignature methodSig = new FunctionSignature(method.getName(), params);
        FunctionIdentity methodIdentity = new FunctionIdentity(methodSig, TypeUtils.formType(method.getReturnType()));
        
        methods.put(methodSig, new JavaMethod(methodIdentity, method));
      }
      
      return javaClass;
    }
  }
}