package jg.rhex.compile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jg.rhex.compile.components.structs.RhexFile;

/**
 * Represents the front-end of the core Rhex compiler
 * @author Jose
 *
 */
public class RhexCompiler {
  
  /**
   * Map containing all Rhex files that were compiled
   */
  private Map<String, RhexFile> rhexFiles;
  
  /**
   * Constructs an RhexCompiler.
   */
  public RhexCompiler(){
    rhexFiles = new HashMap<String, RhexFile>();
  }
  
  
  public List<RhexFile> formSourceFiles(String ... files){
    
    return new ArrayList<>(rhexFiles.values());
  }
  
}
