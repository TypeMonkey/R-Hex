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
  
  public void placeFunction(RhexFunction function){
    fileFunctions.put(function.getSignature(), function);
  }
  
  public void placeClass(RhexClass rhexClass){
    fileClasses.put(rhexClass.getTypeInfo(), rhexClass);
  }
  
  public void placeVariable(RhexVariable variable){
    fileVariables.put(variable.getName(), variable);
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
  
  public File getPath(){
    return original.getFilePath();
  }
  
  public String getName(){
    return original.getFileName();
  }
}
