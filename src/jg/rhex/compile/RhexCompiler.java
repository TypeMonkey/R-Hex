package jg.rhex.compile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.io.Files;

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
  
  //private Map<String, Class<?>> javaStandard; //the String keys are full binary names of the classes

  private Map<String, RFile> rhexFiles;
  private Map<String, Set<RFile>> packages;
  
  private Status currentStatus;
  private String [] providedFiles;
  
  private boolean initialized;
  
  /**
   * Constructs a RhexCompiler
   * @param files - the locations of the .rhex files to compile
   */
  public RhexCompiler(String ... files){
    rhexFiles = new HashMap<>();
    packages = new HashMap<>();
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
      /*
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
          //the minus 6 is to remove the ".class" extension
          binaryName = binaryName.substring(0, binaryName.length() - 6);
          String [] split = binaryName.split("\\.");
          if (split.length == 3 && !binaryName.contains("$")) {
            System.out.println(binaryName);

            try {
              Class<?> loadedClass = getClass().getClassLoader().loadClass(binaryName);
              javaStandard.put(loadedClass.getName(), loadedClass);
            } catch (ClassNotFoundException e) {
              e.printStackTrace();
            }      
          }  
        }
      }
      
      jarFile.close();
      */
      initialized = true;
    }
  }
  
  /**
   * Forms the source files
   */
  public void formSourceFiles(){
    for(String currentPath : providedFiles){
      if (Files.getFileExtension(currentPath).equals("rhex")) {
        File sourceFile = new File(currentPath);
        if (!sourceFile.exists()) {
          throw new IllegalArgumentException("The file '"+currentPath+"' doesn't exist!");
        }
        
        if (rhexFiles.containsKey(Files.getNameWithoutExtension(currentPath))) {
          throw new IllegalArgumentException("The file '"+currentPath+"' is already in use!");
        }
        
        FileBuilder fileBuilder = new FileBuilder(sourceFile);
        RFile rhexFile = fileBuilder.constructFile();
        String binaryName = rhexFile.getPackDesignation()+"."+Files.getNameWithoutExtension(currentPath);
        
        RFile current = rhexFiles.put(binaryName, rhexFile);
        if (current != null) {
          String mess = "File conflict! In the package '"+rhexFile.getPackDesignation()+"': "+System.lineSeparator()
                        +"       "+rhexFile.getFilePath().getPath()+"  and "+System.lineSeparator()
                        +"       "+current.getFilePath().getPath()+" are declared in the package.";
          
          throw new IllegalArgumentException(mess);
        }
        
        Set<RFile> packContents = packages.get(rhexFile.getPackDesignation());
        if (packContents == null) {
          packContents = new HashSet<>();       
          packages.put(rhexFile.getPackDesignation(), packContents);
        }
        else {
          packContents.add(rhexFile);
        }
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
  
  public Set<RFile> getPackageFiles(String packName){
    return packages.get(packName);
  }
  
  public Map<String, RFile> getSources(){
    return rhexFiles;
  }
  
  public Status getCurrentStatus() {
    return currentStatus;
  } 
  
}
