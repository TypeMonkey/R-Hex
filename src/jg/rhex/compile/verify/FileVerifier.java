package jg.rhex.compile.verify;

import jg.rhex.compile.components.structs.RhexFile;

/**
 * Verifies the structure and type correctness of a source file
 * 
 * This class will also do type inference.
 * 
 * @author Jose Guaro
 *
 */
public class FileVerifier {

  private final RhexFile rhexFile;
  
  public FileVerifier(RhexFile rhexFile){
    this.rhexFile = rhexFile;
  }
  
  public void verify(){
    
  }
}
