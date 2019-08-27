package jg.rhex.compile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.io.Files;

import jg.rhex.common.Descriptor;
import jg.rhex.common.FunctionSignature;
import jg.rhex.common.Type;
import jg.rhex.common.TypeUtils;
import jg.rhex.compile.components.FileBuilder;
import jg.rhex.compile.components.structs.RClass;
import jg.rhex.compile.components.structs.RFile;
import jg.rhex.compile.components.tnodes.atoms.TType;
import jg.rhex.compile.verify.ClassExtractor;
import jg.rhex.compile.verify.TypeAttacher;
import jg.rhex.compile.verify.TypeAttacher;
import jg.rhex.compile.verify.LineageChecker;
import jg.rhex.compile.verify.StructureVerifier;
import jg.rhex.compile.verify.errors.RedundantExtensionException;
import jg.rhex.compile.verify.errors.UnfoundTypeException;
import jg.rhex.runtime.components.Function;
import jg.rhex.runtime.components.GenClass;
import jg.rhex.runtime.components.java.JavaClass;
import jg.rhex.runtime.components.rhexspec.RhexClass;
import jg.rhex.runtime.components.rhexspec.RhexFile;

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
   * A main operation during initialization is the loading of .class files (from jars)
   */
  public void initialize() throws IOException{
    if (!initialized) {
      //TODO: Load classpath classes
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
        String binaryName = null;
        if (rhexFile.getPackDesignation() == null) {
          binaryName = rhexFile.getFileName();
        }
        else {
          binaryName = rhexFile.getPackDesignation()+"."+rhexFile.getFileName();
        }
        
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
            
        Set<RClass> rClasses = rhexFile.getClasses();
        for (RClass rClass : rClasses) {
          String classBinName = binaryName+"."+rClass.getName().getImage();
          //all classes should be nominally (in name) be unique
          //across all compiled files. This is because of how binary names are formed for classes
          //and the files that contain them:
          // <PACKAGE NAME>.<FILE NAME>.<CLASS NAME>
          // During formation phase, class name uniqueness is enforced file-local (so each class in a file
          // has a unique name). 
          // And the code above enforces file name uniqueness package-local (no two files in a package
          // has the same name).
          
          if (rhexFiles.containsKey(classBinName)) {
            /*
             * What about class name and file name conflict (binary names)?
             * 
             * org.what.hello.contFile.Class1 <--- Class name (package: org.what.hello, File: contFile, class: Class1)
             * org.what.hello.contFile.Class1 <--- File name (package: org.what.hello.contFile , File: Class1)
             * 
             * Yes, that would a be a terrible name to a file (by calling it "Class1"?!), but not all
             * programmers are fully aware. And so, safeguards must be built in.
             * 
             * If there is such a conflict, throw an error.
             */
            String mess = "Name Conflict! The class '"+classBinName+"' located at "+sourceFile+System.lineSeparator()+
                          "               has the same name as the file '"+classBinName+"' located at "+
                         rhexFiles.get(classBinName).getFilePath().getPath();
            throw new IllegalArgumentException(mess);            
          }
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
    HashMap<Type, RhexClass> classTemplates = new HashMap<>();
    
    HashMap<String, RhexFile> files = new HashMap<>();
    //collect all classes from all files
    for (RFile sourceFile : rhexFiles.values()) {
      String fileFullName = null;
      if (sourceFile.getPackDesignation() == null) {
        fileFullName = sourceFile.getFileName();
      }
      else {
        fileFullName = sourceFile.getPackDesignation()+"."+sourceFile.getFileName();
      }
      
      ClassExtractor verifier = new ClassExtractor(sourceFile, this);
      Map<Type, RhexClass> templates = verifier.extract();
      classTemplates.putAll(templates);
      
      RhexFile rhexFile = new RhexFile(sourceFile);
      for(RhexClass curClass : templates.values()){
        rhexFile.placeClass(curClass);
      }
      files.put(fileFullName, rhexFile);
    }
    
    //attach GenClasses to super types
    for(RhexClass rhexClass : classTemplates.values()) {      
      //get parent, then interfaces
      List<TType> allSupersTypes = rhexClass.getOriginal().getSuperTypes();
      if (rhexClass.isInterface()) {
        //interface can only implement other interfaces
        for(TType inter : allSupersTypes){
          GenClass otherSuper = classTemplates.get(inter.getAttachedType());
          System.out.println(inter.getAttachedType());
          if (otherSuper == null) {
            otherSuper = JavaClass.getJavaClassRep(inter.getAttachedType().getFullName());
            if (otherSuper == null) {
              throw new UnfoundTypeException(inter.getBaseType().get(0).getToken(),
                  inter.getBaseString(), 
                  TypeUtils.getHostFileName(inter.getAttachedType()));
            }
          }
          
          if(!rhexClass.addInterface(otherSuper)) {
            throw new RuntimeException("The interface "+rhexClass.getTypeInfo()+" has already implemented "+otherSuper.getTypeInfo());
          }
        }
      }
      else {
        if (allSupersTypes.size() == 0) {
          rhexClass.setParent(JavaClass.getJavaClassRep(Object.class));
        }
        else {
          for(int i = 0; i <  allSupersTypes.size(); i++) {
            GenClass otherSuper = classTemplates.get(allSupersTypes.get(i).getAttachedType());
            System.out.println(allSupersTypes.get(i).getAttachedType());
            if (otherSuper == null) {
              otherSuper = JavaClass.getJavaClassRep(allSupersTypes.get(i).getAttachedType().getFullName());
              if (otherSuper == null) {
                throw new UnfoundTypeException(allSupersTypes.get(i).getBaseType().get(0).getToken(),
                    allSupersTypes.get(i).getBaseString(), 
                    TypeUtils.getHostFileName(allSupersTypes.get(i).getAttachedType()));
              }
            }
            
            if (i > 0) {
              if (otherSuper.isInterface()) {
                if(!rhexClass.addInterface(otherSuper)) {
                  throw new RuntimeException("The class "+rhexClass.getTypeInfo()+" has already implemented "+otherSuper.getTypeInfo());
                }
              }
              else {
                throw new RuntimeException("The type '"+otherSuper.getTypeInfo()+"' isn't an interface");
              }
            }
            else {
              if (otherSuper.isInterface()) {
                System.out.println(" is interface: "+otherSuper.getTypeInfo());
                if(!rhexClass.addInterface(otherSuper)) {
                  throw new RuntimeException("The class "+rhexClass.getTypeInfo()+" has already implemented "+otherSuper.getTypeInfo());
                }
              }
              else {
                rhexClass.setParent(otherSuper);
              }
            }
          }
        }
      }
      
      if (rhexClass.getParent() == null) {
        rhexClass.setParent(JavaClass.getJavaClassRep(Object.class));
      }
    }
    
    System.out.println(">>>>>>>>ENFORCNG INHERITANCE");
    
    /*
     * Check for circular inheritance (not allowed)
     * Make sure all interface methods are implemented by decendants
     *  ^same as above, but for abstract classes
     *  
     */
    LineageChecker checker = new LineageChecker();
    for(RhexClass rhexClass : classTemplates.values()){
      checker.checkLineage(rhexClass);
    }
    
    /*
     * Now, extract file functions and variables
     */
    System.out.println(">>>>>>>>>>> ATTACHING TYPES <<<<<<<<<<");
    TypeAttacher attacher = new TypeAttacher(classTemplates, this);
    for (RhexFile container : files.values()) {
      System.out.println("   ---> FOR: "+container.getName());
      attacher.extract(container);
    }
    
    /*
     * Then, do type checking
     */   
    StructureVerifier structureVerifier = new StructureVerifier(classTemplates, files);
    System.out.println(">>>>>>>>>>> TYPE CHECKING <<<<<<<<<<");
    for(RhexFile file : files.values()){
      structureVerifier.verifyFileStructure(file);
    }
    
    currentStatus = Status.VERIFICATION;
  }
  
  /**
   * Retrieves the RhexFile with the given name
   * @param fileName - the binary name of the RhexFile to retrieve
   * @return the corresponding RhexFile, or null if no such file exist
   */
  public RFile retrieveFile(String fileName){
    return rhexFiles.get(fileName);
  }
  
  /**
   * Retrieves all RFiles grouped in the same package
   * @param packName - the package name (full binary name)
   * @return a Set of RFiles in the same package
   */
  public Set<RFile> getPackageFiles(String packName){
    return packages.get(packName);
  }
  
  /**
   * Retrieves all the RClasses in the same package
   * @return a Map mapping each class' full binary name to the class
   */
  public Map<String, RClass> getPackageClasses(String packName) {
    Set<RFile> rFiles = getPackageFiles(packName);
    if (rFiles == null) {
      return null;
    }
    
    HashMap<String, RClass> map = new HashMap<>();
    for (RFile rFile : rFiles) {
      for (RClass rClass : rFile.getClasses()) {
        map.put(packName+"."+rFile.getFileName()+"."+rClass.getName().getImage(), rClass);
      }
    }
    
    return map;
  }
  
  public Map<String, RFile> getSources(){
    return rhexFiles;
  }
  
  public Status getCurrentStatus() {
    return currentStatus;
  } 
  
}
