package jg.rhex.compile.components.structs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jg.rhex.compile.components.tnodes.atoms.TType;
import net.percederberg.grammatica.parser.Token;

public class RClass {
  
  private boolean isAnInterface;
  
  private Token name;
  private Set<Descriptor> descriptors;
  private TType parent;
  private List<TType> extensions;
  
  private Set<TypeParameter> typeParameters;
  
  private List<RVariable> classVariables;
  private List<RFunc> methods;
  
  public RClass(Token name, Set<Descriptor> descriptors, TType parent, List<TType> extensions, boolean isAnInterface){
    this.name = name;
    this.descriptors = descriptors;
    this.parent = parent;
    this.extensions = extensions;
    this.isAnInterface = isAnInterface;
    
    typeParameters = new HashSet<>();
    classVariables = new ArrayList<RVariable>();
    methods = new ArrayList<RFunc>();
  }
  
  public void addMethod(RFunc func){
    methods.add(func);
  }
  
  public void addClassVar(RVariable variable){
    classVariables.add(variable);
  }
  
  /**
   * Adds a type parameter to this class
   * @param parameter - the type parameter
   * @return true if the type parameter's handle has already been used
   *         false if else
   */
  public boolean addTypeParameter(TypeParameter parameter){
    return typeParameters.add(parameter);
  }
  
  public void setTypeParameters(Set<TypeParameter> typeParameters){
    this.typeParameters = new HashSet<>(typeParameters);
  }
  
  public boolean isAnInterface() {
    return isAnInterface;
  }
  
  public Token getName() {
    return name;
  }
  
  public Set<TypeParameter> getTypeParameters(){
    return typeParameters;
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

  public List<RVariable> getClassVariables() {
    return classVariables;
  }

  public List<RFunc> getMethods() {
    return methods;
  } 
  
}
