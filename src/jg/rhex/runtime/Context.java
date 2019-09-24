package jg.rhex.runtime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jg.rhex.common.FunctionIdentity;
import jg.rhex.common.FunctionSignature;
import jg.rhex.common.Type;
import jg.rhex.runtime.components.Function;
import jg.rhex.runtime.components.GenClass;
import jg.rhex.runtime.components.Variable;
import jg.rhex.runtime.components.rhexspec.RhexFile;

public class Context {

  private final SymbolTable table;

  private List<Map<FunctionSignature, Function>> funcMaps;

  private List<Map<String, Variable>> varMaps;

  private List<Map<String, GenClass>> localClasses;

  private List<Map<String, RhexFile>> localFiles;

  public Context(SymbolTable table){
    this.table = table;
    funcMaps = new ArrayList<>();
    varMaps = new ArrayList<>();
    localClasses = new ArrayList<>();
    localFiles = new ArrayList<>();
  }

  public void setLocalFuncMap(Map<FunctionSignature, Function> fmap){
    if (funcMaps.size() == 0) {
      funcMaps.add(fmap);
    }
    else {
      funcMaps.set(0, fmap);
    }
  }

  public void setLocalVarMap(Map<String, Variable> varMap){
    if (varMaps.size() == 0) {
      varMaps.add(varMap);
    }
    else {
      varMaps.set(0, varMap);
    }
  }

  public void setLocalClassMap(Map<String, GenClass> classMap){
    if (localClasses.size() == 0) {
      localClasses.add(classMap);
    }
    else {
      localClasses.set(0, classMap);
    }
  }

  public void setLocalFileMap(Map<String, RhexFile> fileMap){
    if (localFiles.size() == 0) {
      localFiles.add(fileMap);
    }
    else {
      localFiles.set(0, fileMap);
    }
  }
  
  public void setFuncMaps(List<Map<FunctionSignature, Function>> fmaps){
    this.funcMaps = fmaps;
  }

  public void setVarMaps(List<Map<String, Variable>> varMaps){
    this.varMaps = varMaps;
  }

  public void setClassMaps(List<Map<String, GenClass>> classMaps){
    this.localClasses = classMaps;
  }

  public void setFileMaps(List<Map<String, RhexFile>> fileMaps){
    this.localFiles = fileMaps;
  }
  
  public boolean addLocalVariable(Variable variable){
    return varMaps.get(0).put(variable.getName(), variable) == null;
  }
  
  public RhexFile findRhexFile(String fullName) {
    return table.findFile(fullName);
  }
  
  public GenClass findGenClass(Type type) {
    return table.findClass(type);
  }
  
  public Set<FunctionIdentity> findFunctionIdentities(String name, boolean searchOnlyLocal){
    HashSet<FunctionIdentity> identities = new HashSet<>();
    for (Map<FunctionSignature, Function> fmap : funcMaps) {
      for (Entry<FunctionSignature, Function> fEntry : fmap.entrySet()) {
        if (fEntry.getKey().getName().equals(name)) {
          identities.add(fEntry.getValue().getIdentity());
        }
      }
      
      if (searchOnlyLocal) {
        break;
      }
    }
    return identities;
  }
  
  public Function findFunction(FunctionSignature signature, boolean searchOnlyLocal){
    for (Map<FunctionSignature, Function> map : funcMaps) {
      Function function = map.get(signature);
      if (function != null) {
        return function;
      }
      
      if (searchOnlyLocal) {
        break;
      }
    }
    
    return null;
  }
  
  public Variable findVariable(String name){
    for(Map<String, Variable> vMap: varMaps){
      Variable variable = vMap.get(name);
      if (variable != null) {
        return variable;
      }
    }
    return null;
  }
  
  public RhexFile findFile(String name){
    for (Map<String, RhexFile> fMap : localFiles) {
      RhexFile file = fMap.get(name);
      if (file != null) {
        return file;
      }
    }
    return null;
  }
  
  public GenClass findClass(String name){
    for (Map<String, GenClass> cMap : localClasses) {
      GenClass file = cMap.get(name);
      if (file != null) {
        return file;
      }
    }
    return null;
  }
  
  public SymbolTable getSymbolTable(){
    return table;
  }
}
