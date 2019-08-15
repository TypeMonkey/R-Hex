package jg.rhex.compile.verify;


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jg.rhex.common.ArrayType;
import jg.rhex.common.Type;
import jg.rhex.common.TypeUtils;
import jg.rhex.compile.RhexCompiler;
import jg.rhex.compile.components.structs.RClass;
import jg.rhex.compile.components.structs.RFile;
import jg.rhex.compile.components.structs.UseDeclaration;
import jg.rhex.compile.components.tnodes.atoms.TType;
import jg.rhex.compile.verify.errors.UnfoundTypeException;

public class TypeStore {

  private final RhexCompiler compiler;

  //the following map class simple names to binary names (key -> simple name, value -> binary name)
  private final Map<String, String> localTypes; //file local classes 

  //multiple imported ("use") types can share the same simple name
  private final Map<String, Set<UseDeclaration>> useTypes; //types imported by use statements

  private final Map<String, Set<String>> packageTypes; //types in the same package.
  //The value in this map is a string as a simple name can be used by several classes in the same package

  public TypeStore(RFile rhexFile, RhexCompiler compiler){
    this.compiler = compiler;    
    localTypes = new HashMap<>();
    useTypes = new HashMap<>();
    packageTypes = new HashMap<>();

    loadAllClasses(rhexFile);
  }
  
  public Type retrieveType(TType proType){
    Type concreteType = null;

    if (TypeUtils.isPrimitive(proType.getBaseString())) {
      concreteType = TypeUtils.PRIMITIVE_TYPES.get(proType.getBaseString());
    }
    else if (TypeUtils.isVoid(proType.getBaseString())) {
      concreteType = Type.VOID_TYPE;
    }
    else if (proType.getBaseString().contains(".")) {
      //full type name provided
      String [] split = proType.getBaseString().split("\\.");
      concreteType = new Type(split[split.length - 1], proType.getBaseString());

      if (!confirmExistanceOfType(concreteType)) {
        return null;
      }
    }
    else {
      //only simple name provided
      String potential = getFullName(proType.getBaseString());
      if (potential == null) {
        return null;
      }
      
      concreteType = new Type(proType.getBaseString(), potential);
      if (!confirmExistanceOfType(concreteType)) {
        return null;
      }
    }
    
    if (proType.getArrayDimensions() > 0) {
      concreteType = new ArrayType(proType.getArrayDimensions(), concreteType);
    }
    
    return concreteType;
  }

  /**
   * Retrieves possible full binary names of types from the corresponding simple name
   * @param simpleName - the type's simple name
   * @return the full name of the Type, or null if none were found
   */
  public String getFullName(String simpleName){    
    String potential = queryLocalTypes(simpleName);
    if (potential == null) {
      String [] potentials = queryUseTypes(simpleName);
      if (potentials == null || potentials.length != 1) {
        potentials = queryPackageTypes(simpleName);
        if (potentials == null || potentials.length != 1) {
          String anotherPotential = queryJavaLang(simpleName);
          if (anotherPotential == null) {
            return null;
          }
          return anotherPotential;
        }
        return potentials[0];
      }
      return potentials[0];
    }
    return potential;
  }

  /**
   * Queries the java.lang.* package for the existence of a class with
   * the provided simple name
   * @param simpleName - the simple name to find
   * @return the full, binary name or null if it doesn't exist
   */
  public String queryJavaLang(String simpleName){
    final String JAVA_LANG_PREFIX = "java.lang.";
    if (compiler.findJavaClass(JAVA_LANG_PREFIX+simpleName) != null) {
      return JAVA_LANG_PREFIX+simpleName;
    }
    return null;
  }
  
  /**
   * Queries file-local classes for the existence of a class with
   * the provided simple name
   * @param simpleName - the simple name to find
   * @return the full, binary name or null if it doesn't exist
   */
  public String queryLocalTypes(String simpleName) {
    return localTypes.get(simpleName);
  }

  /**
   * Queries the host package the existence of a class with
   * the provided simple name
   * @param simpleName - the simple name to find
   * @return the full, binary name or null if it doesn't exist
   */
  public String [] queryPackageTypes(String simpleName) {
    Set<String> potentials = packageTypes.get(simpleName);
    if (potentials == null || potentials.size() == 0) {
      return null;
    }
    return potentials.toArray(new String[potentials.size()]);
  }

  /**
   * Queries imported types for the existence of a class with
   * the provided simple name
   * @param simpleName - the simple name to find
   * @return the full, binary name or null if it doesn't exist
   */
  public String[] queryUseTypes(String simpleName) {
    Set<UseDeclaration> useDeclarations = useTypes.get(simpleName);
    if (useDeclarations == null || useDeclarations.size() == 0) {
      return null;
    }
    
    String [] potentials = new String[useDeclarations.size()];
    
    int i = 0;
    for (UseDeclaration useDec : useDeclarations) {
      potentials[i] = useDec.getBaseImport().getBaseString();
      i++;
    }
    
    return potentials;
  }

  private void loadAllClasses(RFile rhexFile) {
    loadLocalTypes(rhexFile);
    loadUseClasses(rhexFile);
    loadPackageTypes(rhexFile);
  }

  private void loadUseClasses(RFile rhexFile) {
    for (UseDeclaration use : rhexFile.getUseDeclarations()) {
      int fullNameSize = use.getBaseImport().getBaseType().size();
      String simpleName = use.getBaseImport().getBaseType().get(fullNameSize-1).getToken().getImage();    
      
      if (useTypes.containsKey(simpleName)) {
        useTypes.get(simpleName).add(use);
      }
      else {
        useTypes.put(simpleName, new HashSet<>(Arrays.asList(use)));
      }
    }
  }

  private void loadLocalTypes(RFile rhexFile) {
    for (RClass rClass : rhexFile.getClasses()) {
      String simpleName = rClass.getName().getImage();
      String binaryName = rhexFile.getPackDesignation()+"."+rhexFile.getFileName()+"."+simpleName;

      localTypes.put(simpleName, binaryName);
    }
  }

  private void loadPackageTypes(RFile rhexFile){
    Map<String, RClass> map = compiler.getPackageClasses(rhexFile.getPackDesignation());
    for (RClass rClass : map.values()) {
      String simpleName = rClass.getName().getImage();
      String binaryName = rhexFile.getPackDesignation()+"."+rClass.getHostFile().getFileName()+"."+simpleName;
      Set<String> potentials = packageTypes.get(simpleName);
      if (potentials == null) {
        potentials = new HashSet<>();
        potentials.add(binaryName);
        packageTypes.put(simpleName, potentials);
      }
      else {
        potentials.add(binaryName);
      }
    }
  }
  
  public boolean confirmExistanceOfType(Type type) {
    return compiler.retrieveClass(type.getFullName()) != null || 
        compiler.findJavaClass(type.getFullName()) != null;
  }

  public Map<String, String> getLocalTypes() {
    return localTypes;
  }

  public Map<String, Set<UseDeclaration>> getUseTypes() {
    return useTypes;
  }

  public Map<String, Set<String>> getPackageTypes() {
    return packageTypes;
  }
  
  public String toString(){
    String xString = "LOCAL :::::  "+localTypes+System.lineSeparator();
    xString += "USE :::::  "+useTypes+System.lineSeparator();
    xString += "PACK :::::: "+packageTypes;
    return xString;
  }
}
