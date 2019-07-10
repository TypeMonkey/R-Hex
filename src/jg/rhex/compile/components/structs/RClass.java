package jg.rhex.compile.components.structs;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jg.rhex.compile.components.tnodes.atoms.TType;
import net.percederberg.grammatica.parser.Token;

public class RClass extends Parametric{
  
  private boolean isAnInterface;
  private boolean isSealed;
  
  private Token name;
  private Set<Descriptor> descriptors;
  private TType parent;
  private List<TType> extensions;
    
  private Set<RVariable> classVariables;
  private List<RFunc> methods;
  
  public RClass(Token name, Set<Descriptor> descriptors, TType parent, List<TType> extensions, boolean isAnInterface){
    this.name = name;
    this.descriptors = descriptors;
    this.parent = parent;
    this.extensions = extensions;
    this.isAnInterface = isAnInterface;
    
    classVariables = new LinkedHashSet<>();
    methods = new ArrayList<RFunc>();
  }
  
  public boolean equals(Object object) {
    if (object instanceof RClass) {
      RClass rClass = (RClass) object;
      return rClass.getName().getImage().equals(name.getImage());
    }
    return false;
  }
  
  public int hashCode() {
    return name.getImage().hashCode();
  }
  
  public void addMethod(RFunc func){
    if (isSealed) {
      throw new IllegalStateException("This structure has been sealed!");
    }
    methods.add(func);
  }
  
  /**
   * Adds a class variable (static and non-static) to the class
   * @param variable - the RVariable to add
   * @return true if this variable (by it's name) has not already been added
   *         false if else
   */
  public boolean addClassVar(RVariable variable){
    if (isSealed) {
      throw new IllegalStateException("This structure has been sealed!");
    }
    return classVariables.add(variable);
  } 
  
  public void setTypeParameters(Set<TypeParameter> typeParameters){
    if (isSealed) {
      throw new IllegalStateException("This structure has been sealed!");
    }
    this.typeParameters = new LinkedHashSet<>(typeParameters);
  }
  
  public boolean isAnInterface() {
    return isAnInterface;
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

  public List<TType> getExtentions() {
    return extensions;
  }

  public Set<RVariable> getClassVariables() {
    return classVariables;
  }

  public List<RFunc> getMethods() {
    return methods;
  }

}
