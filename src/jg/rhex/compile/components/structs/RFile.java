package jg.rhex.compile.components.structs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.io.Files;

import jg.rhex.compile.components.tnodes.atoms.TIden;

public class RFile{

  private String packageDesignation;
  private String fileName;
  private File file;
  
  private List<UseDeclaration> useDeclarations;
  
  private Set<RVariable> variables;
  private List<RFunc> functions;
  private Set<RClass> classes;
    
  public RFile(File file){
    this.file = file;
    fileName = Files.getNameWithoutExtension(file.getAbsolutePath());
    
    useDeclarations = new ArrayList<UseDeclaration>();
    variables = new LinkedHashSet<>();
    functions = new ArrayList<RFunc>();
    classes = new LinkedHashSet<>();
  }
  
  public boolean equals(Object object) {
    if (object instanceof RFile) {
      RFile other = (RFile) object;
      return other.getFileName().equals(getFileName());
    }
    return false;
  }
  
  public int hashCode() {
    return fileName.hashCode();
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
    packageDesignation = "";
    for(int i = 0; i < designation.size(); i++){
      if (i == designation.size() - 1) {
        packageDesignation += designation.get(i).getToken().getImage();
      }
      else {
        packageDesignation += designation.get(i).getToken().getImage()+".";
      }
    }
  }
  
  /**
   * Adds a class to this RhexFile
   * @param rClass - the class to add
   * @return true if this class (by its simple name) is not already present in this file
   *         false if else
   */
  public boolean addClass(RClass rClass){
    rClass.setHostFile(this);
    return classes.add(rClass);
  }
  
  public void addFunction(RFunc func){
    functions.add(func);
  }
  
  public String getPackDesignation() {
    return packageDesignation;
  }

  public String getFileName() {
    return fileName;
  }
  
  public File getFilePath(){
    return file;
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
