package jg.rhex.compile.verify;


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jg.rhex.compile.RhexCompiler;
import jg.rhex.compile.components.structs.RClass;
import jg.rhex.compile.components.structs.RFile;
import jg.rhex.compile.components.structs.UseDeclaration;

public class CompStore {

  private final RhexCompiler compiler;

  //the following map class simple names to binary names (key -> simple name, value -> binary name)
  private final Map<String, String> localTypes; //file local classes 

  //multiple imported ("use") types can share the same simple name
  private final Map<String, Set<UseDeclaration>> useTypes; //types imported by use statements

  private final Map<String, Set<String>> packageTypes; //types in the same package.
  //The value in this map is a string as a simple name can be used by several classes in the same package


  public CompStore(RFile rhexFile, RhexCompiler compiler){
    this.compiler = compiler;    
    localTypes = new HashMap<>();
    useTypes = new HashMap<>();
    packageTypes = new HashMap<>();

    loadAllClasses(rhexFile);
  }

  /**
   * Retrieves possible full binary names of types from the corresponding simple name
   * @param simpleName - the type's simple name
   * @return an array of possible corresponding binary names. The array will always
   *         have at least 1 possible string. If the first element is null, then that means
   *         no corresponding binary names were found.
   */
  public String getFullName(String simpleName){
    String potential = queryLocalTypes(simpleName);
    if (potential == null) {
      String [] potentials = queryUseTypes(simpleName);
      if (potentials == null || potentials.length != 1) {
        potentials = queryPackageTypes(simpleName);
        if (potentials == null || potentials.length != 1) {
          return null;
        }
        return potentials[0];
      }
      return potentials[0];
    }
    return potential;
  }

  public String queryLocalTypes(String simpleName) {
    return localTypes.get(simpleName);
  }

  public String [] queryPackageTypes(String simpleName) {
    Set<String> potentials = packageTypes.get(simpleName);
    if (potentials == null || potentials.size() == 0) {
      return null;
    }
    return potentials.toArray(new String[potentials.size()]);
  }

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
      String binaryName = rhexFile.getPackDesignation()+"."+rhexFile.getFileName()+"."+simpleName;
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
