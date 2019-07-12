package jg.rhex.compile.verify;

import java.util.HashMap;
import java.util.Map;

import jg.rhex.compile.components.structs.RFile;
import jg.rhex.compile.components.structs.UseDeclaration;

public class TypeStore {
    
  private Map<String, UseableType> importedTypesSimple;  
  private Map<String, UseableType> importedTypesFull;

  
  private Map<String, UseableType> inFileClasses;
  
  public TypeStore(RFile rhexFile){
    importedTypesFull = new HashMap<>();
    importedTypesSimple = new HashMap<>();
    
    inFileClasses = new HashMap<>();
    
    loadAllClasses(rhexFile);
  }

  private void loadAllClasses(RFile rhexFile) {
    for (UseDeclaration use : rhexFile.getUseDeclarations()) {
      if () {
        
      }
    }
  }
  
}
