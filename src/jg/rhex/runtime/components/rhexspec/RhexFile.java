package jg.rhex.runtime.components.rhexspec;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import jg.rhex.common.FunctionIdentity;
import jg.rhex.common.FunctionSignature;
import jg.rhex.common.Type;
import jg.rhex.compile.components.structs.RFile;
import jg.rhex.compile.components.structs.RVariable;
import jg.rhex.runtime.components.Function;
import jg.rhex.runtime.components.GenClass;
import jg.rhex.runtime.components.Variable;

public class RhexFile {
  
  private final RFile original;
  
  private Map<FunctionSignature, Function> fileFunctions;
  private Map<String, Set<FunctionIdentity>> funcsByName;
  
  private Map<Type, GenClass> fileClasses; //String keys are full, binary class names
  private Map<String, Variable> fileVariables;
  
  public RhexFile(RFile original) {
    this.original = original;
    
    fileFunctions = new HashMap<>();
    fileClasses = new HashMap<>();    
    fileVariables = new LinkedHashMap<>();
    
    funcsByName = new HashMap<>();
  }
  
  /**
   * Places the given RhexFunction into this RhexFile's function map
   * @param function - the RhexFunction to place
   * @return true if a function of the same signature hasn't been added
   */
  public boolean placeFunction(RhexFunction function){
    if (fileFunctions.put(function.getSignature(), function) == null) {
      if (funcsByName.containsKey(function.getName())) {
        funcsByName.get(function.getName()).add(function.getIdentity());
      }
      else {
        HashSet<FunctionIdentity> identities = new HashSet<>();
        identities.add(function.getIdentity());
        funcsByName.put(function.getName(), identities);
      }
      return true;
    }
    return false;
  }
  
  /**
   * Places the given RhexClass into this RhexFile's class map
   * @param rhexClass - the class to place
   * @return true if a class of the same name hasn't been added
   */
  public boolean placeClass(RhexClass rhexClass){
    return fileClasses.put(rhexClass.getTypeInfo(), rhexClass) == null;
  }
  
  /**
   * Places the given RhexVariable into this RhexFile's file variable map
   * @param variable - the variable to place
   * @return true if a variable of the same name hasn't been added yet
   */
  public boolean placeVariable(RhexVariable variable){
    return fileVariables.put(variable.getName(), variable) == null;
  }
  
  public Function getFunction(FunctionSignature signature){
    return fileFunctions.get(signature);
  }
  
  public Set<FunctionIdentity> getFunctions(String name){
    return funcsByName.get(name);
  }
  
  public GenClass getClass(Type type){
    return fileClasses.get(type);
  }
  
  public Variable getVariable(String name){
    return fileVariables.get(name);
  }
  
  public Map<FunctionSignature, Function> getFileFunctions() {
    return fileFunctions;
  }

  public Map<Type, GenClass> getFileClasses() {
    return fileClasses;
  }

  public Map<String, Variable> getFileVariables() {
    return fileVariables;
  }

  public RFile getOriginal(){
    return original;
  }
  
  public File getPath(){
    return original.getFilePath();
  }
  
  public String getName(){
    return original.getFileName();
  }
}
