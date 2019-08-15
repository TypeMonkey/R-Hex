package jg.rhex.runtime.components.java;

import java.lang.reflect.Field;
import java.util.Set;

import jg.rhex.common.Descriptor;
import jg.rhex.common.Type;
import jg.rhex.common.TypeUtils;
import jg.rhex.runtime.components.Instance;
import jg.rhex.runtime.components.Variable;

public class JavaStaticVariable extends Variable{

  private final Field field;
  
  public JavaStaticVariable(Set<Descriptor> descriptors, Field field) {
    super(descriptors, TypeUtils.formType(field.getType()), field.getName());
    if (descriptors.contains(Descriptor.STATIC) == false) {
      throw new IllegalArgumentException("Field must be static.");
    }
    this.field = field;
  }

  @Override
  public Variable clone() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Instance getValue() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setValue(Instance instance) {
    // TODO Auto-generated method stub
    
  }

}
