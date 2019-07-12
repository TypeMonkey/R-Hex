package jg.rhex.compile;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import jg.rhex.compile.components.FileBuilder;
import jg.rhex.compile.components.structs.RFile;

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
  
  private Map<String, Class<?>> javaStandardFullName;
  private Map<String, Class<?>> javaStandardSimpleName;

  private Map<String, RFile> rhexFiles;
  private Status currentStatus;
  private String [] providedFiles;
  
  private boolean initialized;
  
  /**
   * Constructs a RhexCompiler
   * @param files - the locations of the .rhex files to compile
   */
  public RhexCompiler(String ... files){
    rhexFiles = new HashMap<>();
    javaStandardFullName = new HashMap<>();
    javaStandardSimpleName = new HashMap<>();
    currentStatus = Status.NONE;
    providedFiles = files;
  }
  
  /**
   * Initializes the compiler. 
   * 
   * A main operation during initialization is the loading of standard java classes (java.lang)
   */
  public void initialize() throws IOException{
    if (!initialized) {
      URL url = Object.class.getResource("Object.class");

      JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();

      url = jarURLConnection.getJarFileURL();

      //System.out.println(url.getFile());

      String actual = url.getFile().replace("%20", " ");

      //System.out.println("NEW:  "+actual);

      JarFile jarFile = new JarFile(new File(actual));

      Enumeration<JarEntry> jarEntries = jarFile.entries();
      while (jarEntries.hasMoreElements()) {
        JarEntry jarEntry = jarEntries.nextElement();
        String name = jarEntry.getName();
        if (name.trim().startsWith("java/lang/")) {
          String binaryName = name.replace("/", ".");
          binaryName = binaryName.substring(0, binaryName.length() - 6);
          
          try {
            Class<?> loadedClass = getClass().getClassLoader().loadClass(binaryName);
            javaStandardFullName.put(loadedClass.getName(), loadedClass);
            javaStandardSimpleName.put(loadedClass.getSimpleName(), loadedClass);
          } catch (ClassNotFoundException e) {
            e.printStackTrace();
          }        
        }
      }
      
      jarFile.close();
      
      initialized = true;
    }
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
        RFile rhexFile = fileBuilder.constructFile();
               
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
    for (RFile sourceFile : rhexFiles.values()) {
      
    }
  }
  
  /**
   * Retrieves the RhexFile with the given name
   * @param fileName - the name of the RhexFile to retrieve
   * @return the corresponding RhexFile, or null if no such file exist
   */
  public RFile retrieveFile(String fileName){
    return rhexFiles.get(fileName);
  }
  
  public Class<?> findClass(String className){
    Class<?> ret = javaStandardFullName.get(className);
    if (ret == null) {
      ret = javaStandardSimpleName.get(className);
    }
    return ret;
  }
  
  public Map<String, RFile> getSources(){
    return rhexFiles;
  }
  
  public Status getCurrentStatus() {
    return currentStatus;
  } 
  
}
