package jg.rhex.compile.components.structs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jg.rhex.compile.components.tnodes.atoms.TIden;

public class RFile{

  private List<TIden> packageDesignation;
  private String fileName;
  
  private List<UseDeclaration> useDeclarations;
  
  private Set<RVariable> variables;
  private List<RFunc> functions;
  private Set<RClass> classes;
    
  public RFile(String fileName){
    this.fileName = fileName;
    
    useDeclarations = new ArrayList<UseDeclaration>();
    variables = new LinkedHashSet<>();
    functions = new ArrayList<RFunc>();
    classes = new LinkedHashSet<>();
  }
  
  public void addUseDec(UseDeclaration useDeclaration){
    useDeclarations.add(useDeclaration);
  }
  
  /**
   * Adds a variable to this RhexFile
   * @param variable - the variable to add
   * @return true if this variable (by its name) is not already present in this file
   *         false if else
   */
  public boolean addVariable(RVariable variable){
    return variables.add(variable);
  }
  
  public void setPackDesignation(List<TIden> designation) {
    this.packageDesignation = designation;
  }
  
  /**
   * Adds a class to this RhexFile
   * @param rClass - the class to add
   * @return true if this class (by its simple name) is not already present in this file
   *         false if else
   */
  public boolean addClass(RClass rClass){
    return classes.add(rClass);
  }
  
  public void addFunction(RFunc func){
    functions.add(func);
  }
  
  public List<TIden> getPackDesignation() {
    return packageDesignation;
  }

  public String getFileName() {
    return fileName;
  }

  public List<UseDeclaration> getUseDeclarations() {
    return useDeclarations;
  }

  public Set<RVariable> getVariables() {
    return variables;
  }

  public List<RFunc> getFunctions() {
    return functions;
  }

  public Set<RClass> getClasses() {
    return classes;
  }
}
