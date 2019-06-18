package jg.rhex.compile.components.structs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jg.rhex.compile.components.tnodes.atoms.TType;
import net.percederberg.grammatica.parser.Token;

public class RClass {
  
  private Token name;
  private Set<Descriptor> descriptors;
  private TType parent;
  private Set<TType> implementations;
  
  private List<RVariable> classVariables;
  private List<RFunc> methods;
  
  public RClass(Token name, Set<Descriptor> descriptors, TType parent, Set<TType> implementations){
    this.name = name;
    this.descriptors = descriptors;
    this.parent = parent;
    this.implementations = implementations;
    
    classVariables = new ArrayList<RVariable>();
    methods = new ArrayList<RFunc>();
  }
  
  public void addMethod(RFunc func){
    methods.add(func);
  }
  
  public void addClassVar(RVariable variable){
    classVariables.add(variable);
  }
  
  public Token getName() {
    return name;
  }

  public Set<Descriptor> getDescriptors() {
    return descriptors;
  }

  public TType getParent() {
    return parent;
  }

  public Set<TType> getImplementations() {
    return implementations;
  }

  public List<RVariable> getClassVariables() {
    return classVariables;
  }

  public List<RFunc> getMethods() {
    return methods;
  } 
  
}
