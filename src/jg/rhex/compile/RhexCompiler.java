package jg.rhex.compile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import jg.rhex.compile.components.FileBuilder;
import jg.rhex.compile.components.structs.RhexFile;

/**
 * Represents the front-end of the core Rhex compiler
 * @author Jose Guaro
 *
 */
public class RhexCompiler {
  
  /**
   * Describes the status of the compilation process
   * @author Jose Guaro
   *
   */
  public enum Status{
    /**
     * The Rhex Compiler hasn't finished a compilation phase yet
     */
    NONE,
    
    /**
     * Formation of sources has finished
     */
    FORMATION,  
    
    /**
     * Verification of code (name checking, type verification and inference, etc) has finished
     */
    VERIFICATION, 
    
    /**
     * Code generation has ended. 
     * 
     * For Rhex 1.0, Rhex code is interpreted. This Status is a good indicator
     * that code compilation has finished
     * 
     */
    GENERATION;
  }
  
  private Map<String, RhexFile> rhexFiles;
  private Status currentStatus;
  private String [] providedFiles;
  
  /**
   * Constructs a RhexCompiler
   * @param files - the locations of the .rhex files to compile
   */
  public RhexCompiler(String ... files){
    rhexFiles = new HashMap<String, RhexFile>();
    currentStatus = Status.NONE;
    providedFiles = files;
  }
  
  /**
   * Forms the source files
   */
  public void formSourceFiles(){
    for(String currentPath : providedFiles){
      if (FilenameUtils.getExtension(currentPath).equals("rhex")) {
        File sourceFile = new File(currentPath);
        if (!sourceFile.exists()) {
          throw new IllegalArgumentException("The file '"+currentPath+"' doesn't exist!");
        }
        
        if (rhexFiles.containsKey(FilenameUtils.getBaseName(currentPath))) {
          throw new IllegalArgumentException("The file '"+currentPath+"' is already in use!");
        }
        
        FileBuilder fileBuilder = new FileBuilder(sourceFile);
        RhexFile rhexFile = fileBuilder.constructFile();
               
        rhexFiles.put(FilenameUtils.getBaseName(currentPath), rhexFile);
      }
      else {
        throw new IllegalArgumentException("The path '"+currentPath+"' doesn't direct to a .rhex file!");
      }
    }
    currentStatus = Status.FORMATION;
  }
  
  /**
   * Verifies and checks the source files
   */
  public void verifySources(){
    for (RhexFile sourceFile : rhexFiles.values()) {
      
    }
  }
  
  /**
   * Retrieves the RhexFile with the given name
   * @param fileName - the name of the RhexFile to retrieve
   * @return the corresponding RhexFile, or null if no such file exist
   */
  public RhexFile retrieveFile(String fileName){
    return rhexFiles.get(fileName);
  }
  
  public Map<String, RhexFile> getSources(){
    return rhexFiles;
  }
  
  public Status getCurrentStatus() {
    return currentStatus;
  } 
}
