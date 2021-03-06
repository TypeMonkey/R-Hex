package jg.rhex.runtime.components.java;


import jg.rhex.runtime.components.Instance;

public class JavaInstance extends Instance{

  private final Object object;
  
  public JavaInstance(JavaClass backingType, Object object) {
    super(backingType, null);
    this.object = object;
  }

  public Object getInstance() {
    return object;
  }
  
  public static JavaInstance[] allJavaInstances(Instance ... instances) {
    JavaInstance [] javaInstances = new JavaInstance[instances.length];
    for (int i = 0; i < javaInstances.length; i++) {
      if (instances[i] instanceof JavaInstance) {
        javaInstances[i] = (JavaInstance) instances[i];
      }
      else {
        return null;
      }
    }
    return javaInstances;
  }
}
