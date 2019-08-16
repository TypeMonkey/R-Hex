package jg.rhex.runtime.components.java;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jg.rhex.common.FunctionIdentity;
import jg.rhex.common.FunctionSignature;
import jg.rhex.common.Type;
import jg.rhex.common.TypeUtils;
import jg.rhex.runtime.components.Constructor;
import jg.rhex.runtime.components.Function;
import jg.rhex.runtime.components.Variable;

public class JavaClassBuilder {

  private JavaClass parent;
  private Set<JavaClass> interfaces;
  
  private Map<FunctionSignature, Function> methods;
  private Map<FunctionSignature, Constructor> constructors;
  private Map<String, Variable> varMap;
  
  private boolean isInterface;
  
  private Type typeInfo;
  
  public JavaClassBuilder(boolean isInterface, Type typeInfo) {
    this(JavaClass.getJavaClassRep(Object.class), isInterface, typeInfo);
  }
  
  public JavaClassBuilder(JavaClass parent, boolean isInterface, Type typeInfo) {
    this.parent = parent;
    interfaces = new HashSet<>();
    
    methods = new HashMap<>();
    constructors = new HashMap<>();
    varMap = new HashMap<>();
    
    this.isInterface = isInterface;
    this.typeInfo = typeInfo;
  }
  
  public JavaClassBuilder placeMethod(Method method){
    Type [] paramTypes = new Type[method.getParameterCount()];
    for(int i = 0; i < paramTypes.length; i++){
      paramTypes[i] = TypeUtils.formType(method.getParameterTypes()[i]);
    }    
    
    Type returnType = TypeUtils.formType(method.getReturnType());    
    FunctionIdentity identity = new FunctionIdentity(new FunctionSignature(method.getName(), paramTypes), returnType);
    
    methods.put(identity.getFuncSig(), new JavaMethod(identity, method));
    return this;
  }
  
  public JavaClassBuilder placeConstructor(java.lang.reflect.Constructor<?> constructor){
    Type [] paramTypes = new Type[constructor.getParameterCount()];
    for(int i = 0; i < paramTypes.length; i++){
      paramTypes[i] = TypeUtils.formType(constructor.getParameterTypes()[i]);
    }    
    
    FunctionIdentity identity = new FunctionIdentity(new FunctionSignature(constructor.getName(), paramTypes), typeInfo);
    
    constructors.put(identity.getFuncSig(), new JavaConstructor(hostClass, signature, constructor));
    return this;
  }
  
  public JavaClass finish(){
    return new JavaClass(typeInfo, parent, interfaces, methods, constructors, varMap, isInterface);
  } 
}
