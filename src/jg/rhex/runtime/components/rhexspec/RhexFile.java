package jg.rhex.runtime.components.rhexspec;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import jg.rhex.common.FunctionSignature;
import jg.rhex.common.Type;
import jg.rhex.compile.components.structs.RFile;
import jg.rhex.compile.components.structs.RVariable;

public class RhexFile {
  
  private final RFile original;
  
  private Map<FunctionSignature, RhexFunction> fileFunctions;
  private Map<Type, RhexClass> fileClasses; //String keys are full, binary class names
  private Map<String, RhexVariable> fileVariables;
  
  public RhexFile(RFile original) {
    this.original = original;
    
    fileFunctions = new HashMap<>();
    fileClasses = new HashMap<>();    
    fileVariables = new LinkedHashMap<>();
  }
  
  /**
   * Places the given RhexFunction into this RhexFile's function map
   * @param function - the RhexFunction to place
   * @return true if a function of the same signature hasn't been added
   */
  public boolean placeFunction(RhexFunction function){
    return fileFunctions.put(function.getSignature(), function) == null;
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
  
  public RhexFunction getFunction(FunctionSignature signature){
    return fileFunctions.get(signature);
  }
  
  public RhexClass getClass(Type type){
    return fileClasses.get(type);
  }
  
  public RhexVariable getVariable(String name){
    return fileVariables.get(name);
  }
  
  public Map<FunctionSignature, RhexFunction> getFileFunctions() {
    return fileFunctions;
  }

  public Map<Type, RhexClass> getFileClasses() {
    return fileClasses;
  }

  public Map<String, RhexVariable> getFileVariables() {
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
