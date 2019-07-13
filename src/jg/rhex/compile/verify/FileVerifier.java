package jg.rhex.compile.verify;

import java.util.Map;

import jg.rhex.compile.RhexCompiler;
import jg.rhex.compile.components.structs.RFile;

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
  
  public FileVerifier(RFile rhexFile, RhexCompiler compiler){
    this.rhexFile = rhexFile;
    this.compiler = compiler;
  }
  
  public void verify(){
    
  }
}
