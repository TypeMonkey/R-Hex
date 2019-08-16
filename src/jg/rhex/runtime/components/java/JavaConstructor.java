package jg.rhex.runtime.components.java;

import java.lang.reflect.InvocationTargetException;

import jg.rhex.common.FunctionSignature;
import jg.rhex.common.Type;
import jg.rhex.runtime.components.Constructor;
import jg.rhex.runtime.components.GenClass;
import jg.rhex.runtime.components.Instance;
import jg.rhex.runtime.components.SymbolTable;

public class JavaConstructor extends Constructor{

  private final java.lang.reflect.Constructor<?> constructor;
  
  public JavaConstructor(Type hostType, FunctionSignature signature, java.lang.reflect.Constructor<?> constructor) {
    super(hostType, signature);
    this.constructor = constructor;
  }

  @Override
  public Instance createInstance(SymbolTable table, Instance... parameters) {
    //Any argument to a Java method and constructor must be a JavaInstance
    JavaInstance [] instances = JavaInstance.allJavaInstances(parameters);
    if (instances == null) {
      throw new RuntimeException("All arguments to Java methods and constructors must be Java instances");
    }
    else {
      Object [] actualObjects = new Object[instances.length];
      for (int i = 0; i < actualObjects.length; i++) {
        actualObjects[i] = instances[i].getInstance();
      }
      
      try {
        return new JavaInstance((JavaClass) getHostClass(), constructor.newInstance(actualObjects));
      } catch (Exception e) {
        throw new RuntimeException("Error invoking constructor: "+e.getMessage());
      } 
    }
  }
  

}