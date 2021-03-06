package jg.rhex.compile.components.structs;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jg.rhex.common.Descriptor;
import jg.rhex.compile.components.tnodes.atoms.TType;
import net.percederberg.grammatica.parser.Token;

public class RClass extends Parametric{
  
  private boolean isAnInterface;
  
  private Token name;
  private Set<Descriptor> descriptors;
  private List<TType> extensions;
    
  private Set<RVariable> classVariables;
  private List<RFunc> methods;
  
  private RFile hostFile;
  
  public RClass(Token name, Set<Descriptor> descriptors, List<TType> supers, boolean isAnInterface){
    this.name = name;
    this.descriptors = descriptors;
    this.extensions = supers;
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
    methods.add(func);
  }
  
  /**
   * Adds a class variable (static and non-static) to the class
   * @param variable - the RVariable to add
   * @return true if this variable (by it's name) has not already been added
   *         false if else
   */
  public boolean addClassVar(RVariable variable){
    return classVariables.add(variable);
  } 
  
  public void setHostFile(RFile hostFile){
    this.hostFile = hostFile;
  }
  
  public boolean isAnInterface() {
    return isAnInterface;
  }
  
  public RFile getHostFile(){
    return hostFile;
  }
  
  public Token getName() {
    return name;
  }

  public Set<Descriptor> getDescriptors() {
    return descriptors;
  }

  public List<TType> getSuperTypes() {
    return extensions;
  }

  public Set<RVariable> getClassVariables() {
    return classVariables;
  }

  public List<RFunc> getMethods() {
    return methods;
  }

}
