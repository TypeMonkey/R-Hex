package jg.rhex.compile.components.structs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RhexFile {

  private String fileName;
  
  private Set<UseDeclaration> useDeclarations;
  
  private List<RVariable> variables;
  private List<RFunc> functions;
  private List<RClass> classes;
  
  public RhexFile(String fileName){
    this.fileName = fileName;
    
    useDeclarations = new HashSet<UseDeclaration>();
    variables = new ArrayList<RVariable>();
    functions = new ArrayList<RFunc>();
    classes = new ArrayList<RClass>();
  }
  
  public void addUseDec(UseDeclaration useDeclaration){
    useDeclarations.add(useDeclaration);
  }
  
  public void addVariable(RVariable variable){
    variables.add(variable);
  }
  
  public void addClass(RClass rClass){
    classes.add(rClass);
  }
  
  public void addFunction(RFunc func){
    functions.add(func);
  }

  public String getFileName() {
    return fileName;
  }

  public Set<UseDeclaration> getUseDeclarations() {
    return useDeclarations;
  }

  public List<RVariable> getVariables() {
    return variables;
  }

  public List<RFunc> getFunctions() {
    return functions;
  }

  public List<RClass> getClasses() {
    return classes;
  }
}
