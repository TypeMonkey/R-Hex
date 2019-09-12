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
import jg.rhex.runtime.components.java.JavaClass;

public class RhexFile extends GenClass{
  
  private final RFile original;
  
  private Map<String, GenClass> importedClasses;
  private Map<Type, GenClass> fileClasses; //String keys are full, binary class names
  
  public RhexFile(RFile original) {
   super(new Type(original.getFileName(), original.getPackDesignation()+"."+original.getFileName()), 
       JavaClass.getJavaClassRep(Object.class), 
       new HashSet<>(), 
       new HashMap<>(), 
       new HashMap<>(), 
       new LinkedHashMap<>(), 
       new HashSet<>(), false);
    
    this.original = original;
  }
  
  /**
   * Places the given RhexFunction into this RhexFile's function map
   * @param function - the RhexFunction to place
   * @return true if a function of the same signature hasn't been added
   */
  public boolean placeFunction(RhexFunction function){
    if (functionMap.put(function.getSignature(), function) == null) {
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
    return variableMap.put(variable.getName(), variable) == null;
  }
  
  public void setImpotedTypes(Map<String, GenClass> imported){
    this.importedClasses = imported;
  }
  
  public Function getFunction(FunctionSignature signature){
    return functionMap.get(signature);
  }
  
  public Set<FunctionIdentity> getFunctions(String name){
    return funcsByName.get(name);
  }
  
  public GenClass getClass(Type type){
    return fileClasses.get(type);
  }
  
  public Variable getVariable(String name){
    return variableMap.get(name);
  }
  
  public Map<FunctionSignature, Function> getFileFunctions() {
    return functionMap;
  }

  public Map<Type, GenClass> getFileClasses() {
    return fileClasses;
  }

  public Map<String, Variable> getFileVariables() {
    return variableMap;
  }
  
  public Map<String, GenClass> getImportedClasses(){
    return importedClasses;
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

  @Override
  public Map<String, Variable> cloneVariableMap() {
    // TODO Auto-generated method stub
    return null;
  }
}
