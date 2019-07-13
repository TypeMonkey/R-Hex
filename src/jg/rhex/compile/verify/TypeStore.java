package jg.rhex.compile.verify;


import jg.rhex.compile.RhexCompiler;
import jg.rhex.compile.components.structs.RFile;
import jg.rhex.compile.components.structs.UseDeclaration;

public class TypeStore {

  private final RhexCompiler compiler;
  
  
  public TypeStore(RFile rhexFile, RhexCompiler compiler){
    this.compiler = compiler;    
    loadAllClasses(rhexFile);
  }

  private void loadAllClasses(RFile rhexFile) {
    for (UseDeclaration use : rhexFile.getUseDeclarations()) {
      
    }
  }
  
}
