package jg.rhex.runtime.components;

import java.util.Set;

import jg.rhex.common.Descriptor;
import jg.rhex.common.Type;

public abstract class Variable {

  private final Set<Descriptor> descriptors;
  private final Type type;
  private final String name;
  
  protected Instance instance;
    
  public Variable(Set<Descriptor> descriptors, Type type, String name) {
    this(descriptors, type, name, null);
  }
  
  public Variable(Set<Descriptor> descriptors,Type type, String name, Instance instance){
    this.descriptors = descriptors;
    this.type = type;
    this.name = name;
    this.instance = instance;
  }
  
  public abstract Variable clone();
  
  public abstract Instance getValue();
  
  public abstract void setValue(Instance instance);
  
  public boolean containsDescriptor(Descriptor descriptor){
    return descriptors.contains(descriptor);
  }
  
  public Set<Descriptor> getDescriptors(){
    return descriptors;
  }

  public Type getType() {
    return type;
  }

  public String getName() {
    return name;
  }
}
