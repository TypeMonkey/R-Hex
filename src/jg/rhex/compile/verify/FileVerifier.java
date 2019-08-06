package jg.rhex.compile.verify;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import jg.rhex.common.FunctionSignature;
import jg.rhex.compile.RhexCompiler;
import jg.rhex.compile.components.structs.RFile;
import jg.rhex.compile.components.structs.RFunc;

/**
 * Verifies the structure and type correctness of a source file
 * 
 * This class will also do type inference.
 * 
 * @author Jose Guaro
 *
 */
public class FileVerifier {

  private final RFile rhexFile;
  private final RhexCompiler compiler;
  private final CompStore typeStore;
  
  private final Map<FunctionSignature, RFunc> funcMap;
  
  public FileVerifier(RFile rhexFile, RhexCompiler compiler){
    this.rhexFile = rhexFile;
    this.compiler = compiler;
    
    funcMap = new HashMap<>();
    
    typeStore = new CompStore(rhexFile, compiler);
  }
  
  public void verify(){
    
  //Order of finding types:
    /*
     * 1.) T-Param
     * 2.) File Local Types
     * 3.) Imported Types
     * 4.) Package Local Types
     * 5.) JVM Classes (either in the java.lang.* or in jars included in the classpath)
     */
    
    System.out.println("------STORE: "+rhexFile.getFileName());
    System.out.println(typeStore);
    System.out.println("------STORE DONE");
    verifyConstruction();
  }
  
  /**
   * Checks for identical function signatures
   */
  private void verifyConstruction() {
    HashSet<FunctionSignature> funcSignatures = new HashSet<>();
    
    //check for identical file functions
    
    
    //check for identical functions within each class
    
  }

}
