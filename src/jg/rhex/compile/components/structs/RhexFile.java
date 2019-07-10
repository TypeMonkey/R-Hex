package jg.rhex.compile.components.structs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RhexFile implements Sealable{

  private String fileName;
  
  private List<UseDeclaration> useDeclarations;
  
  private Set<RVariable> variables;
  private List<RFunc> functions;
  private Set<RClass> classes;
  
  private boolean isSealed;
  
  public RhexFile(String fileName){
    this.fileName = fileName;
    
    useDeclarations = new ArrayList<UseDeclaration>();
    variables = new LinkedHashSet<>();
    functions = new ArrayList<RFunc>();
    classes = new LinkedHashSet<>();
  }
  
  public void addUseDec(UseDeclaration useDeclaration){
    if (isSealed) {
      throw new IllegalStateException("This structure has been sealed!");
    }
    useDeclarations.add(useDeclaration);
  }
  
  /**
   * Adds a variable to this RhexFile
   * @param variable - the variable to add
   * @return true if this variable (by its name) is not already present in this file
   *         false if else
   */
  public boolean addVariable(RVariable variable){
    if (isSealed) {
      throw new IllegalStateException("This structure has been sealed!");
    }
    return variables.add(variable);
  }
  
  /**
   * Adds a class to this RhexFile
   * @param rClass - the class to add
   * @return true if this class (by its simple name) is not already present in this file
   *         false if else
   */
  public boolean addClass(RClass rClass){
    if (isSealed) {
      throw new IllegalStateException("This structure has been sealed!");
    }
    return classes.add(rClass);
  }
  
  public void addFunction(RFunc func){
    if (isSealed) {
      throw new IllegalStateException("This structure has been sealed!");
    }
    functions.add(func);
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

  @Override
  public void seal() {
    isSealed = true;
  }

  @Override
  public boolean isSealed() {
    return isSealed;
  }
}
