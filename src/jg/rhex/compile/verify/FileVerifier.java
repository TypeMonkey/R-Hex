package jg.rhex.compile.verify;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jg.rhex.common.FunctionKey;
import jg.rhex.common.Type;
import jg.rhex.common.TypeRequirements;
import jg.rhex.common.TypeRequirements.FunctionReturn;
import jg.rhex.compile.RhexCompiler;
import jg.rhex.compile.components.errors.RepeatedStructureException;
import jg.rhex.compile.components.structs.FunctionInfo;
import jg.rhex.compile.components.structs.RClass;
import jg.rhex.compile.components.structs.RFile;
import jg.rhex.compile.components.structs.RFunc;
import jg.rhex.compile.components.structs.RVariable;
import jg.rhex.compile.components.structs.TypeParameter;
import jg.rhex.compile.components.structs.UseDeclaration;
import jg.rhex.compile.components.tnodes.atoms.TIden;
import jg.rhex.compile.components.tnodes.atoms.TType;
import jg.rhex.compile.verify.errors.UnfoundFileException;
import jg.rhex.compile.verify.errors.UnfoundTypeException;
import jg.rhex.runtime.comps.RhexFile;
import jg.rhex.runtime.comps.TypeInformation;

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
  
  private final Map<FunctionKey, RFunc> funcMap;
  
  public FileVerifier(RFile rhexFile, RhexCompiler compiler){
    this.rhexFile = rhexFile;
    this.compiler = compiler;
    
    funcMap = new HashMap<>();
    
    typeStore = new CompStore(rhexFile, compiler);
  }
  
  public void verify(){
    System.out.println("------STORE: "+rhexFile.getFileName());
    System.out.println(typeStore);
    System.out.println("------STORE DONE");
    verifyConstruction();
    createFunctionMap();
  }
  
  /**
   * Checks for identical function signatures
   */
  private void verifyConstruction() {
    HashSet<FunctionInfo> funcSignatures = new HashSet<>();
    
    //check for identical file functions
    for (RFunc func : rhexFile.getFunctions()) {
      if (!funcSignatures.add(FunctionInfo.formFunctionInfo(func))) {
        throw new RepeatedStructureException(func.getName(), "Function", rhexFile.getFileName());
      }
    }
    
    //check for identical functions within each class
    for (RClass rClass : rhexFile.getClasses()) {
      funcSignatures.clear();
      for (RFunc func : rClass.getMethods()) {
        if (!funcSignatures.add(FunctionInfo.formFunctionInfo(func))) {
          throw new RepeatedStructureException(func.getName(), "Class Function", rhexFile.getFileName());
        }
      }
    }
  }

  /**
   * Fills the function map
   */
  private void createFunctionMap() {
    //first fill with file-local functions
    for (RFunc func : rhexFile.getFunctions()) {
      FunctionInfo functionInfo = FunctionInfo.formFunctionInfo(func);
      
      //get true identities of parameter types
      Type [] paramTypes = new Type[functionInfo.getParameterTypes().length];
      for(int i = 0; i < paramTypes.length; i++){
        RVariable param = (RVariable) func.getBody().getStatements().get(i);
        if (param.getProvidedType().getBaseType().size() == 1) {
          //This is a simple name
          String binaryName = typeStore.getFullName(param.getProvidedType().getBaseString());
          if (binaryName == null) {
            throw new UnfoundTypeException(param.getIdentifier().getActValue(), 
                param.getProvidedType().getBaseString());
          }
          else {
            paramTypes[i] = new Type(param.getProvidedType().getBaseString(), binaryName);
          }
        }
        else {
          //this is a full name
          UseableType useable = compiler.retrieveClass(param.getProvidedType().getBaseString());
          if (useable == null) {
            useable = compiler.findJavaClass(param.getProvidedType().getBaseString());
            if (useable == null) {
              throw new UnfoundTypeException(param.getIdentifier().getActValue(), 
                  param.getProvidedType().getBaseString());
            }
          }
          
          paramTypes[i] = useable.getTypeInformation();
        }
      }
      
      funcMap.put(new FunctionKey(func.getName().getImage(), paramTypes), func);
    }
    
    for (UseDeclaration useDeclaration : rhexFile.getUseDeclarations()) {
      for (TIden funcName : useDeclaration.getImportedFuncs()) {
        String binaryFileName = useDeclaration.getBaseImport().getBaseString();
        RFile source = compiler.retrieveFile(binaryFileName);
        if (source == null) {
          throw new UnfoundFileException(useDeclaration.getUseToken(), binaryFileName);
        }
        
        for(RFunc func : source.getFunctions()){
          if (func.getName().getImage().equals(funcName.getActValue().getImage())) {
            
          }
        }
      }
    }
  }
  
  private FunctionKey formFunctionKey(RFunc rFunc){    
    //Stores actual type information of parameters
    Type [] parameterTypes = new Type[rFunc.getParameterAmount()];
    
    for(int i = 0; i < parameterTypes.length; i++){
      
      //First, check if parameter type name is listed int he function's t-Param
      //Order of finding types:
      /*
       * 1.) T-Param
       * 2.) File Local Types
       * 3.) Imported Types
       * 4.) Package Local Types
       * 5.) JVM Classes (either in the java.lang.* or in jars included in the classpath)
       */
      
      RVariable parameter = (RVariable) rFunc.getBody().getStatements().get(i);
      TType indivParam = parameter.getProvidedType();
      String baseType = indivParam.getBaseString();
      TypeParameter actualTypeParameter = rFunc.getTypeParameter(baseType);
      if (actualTypeParameter == null) {
        if (baseType.contains(".")) {
          //this is a full, binary name
          String [] split = baseType.split("\\.");
          parameterTypes[i] = new Type(split[split.length - 1], baseType);
        }
        else {
          //this is a simple name. Commence type lookup
          String binaryName = typeStore.getFullName(baseType);
          if (binaryName == null) {
            throw new UnfoundTypeException(indivParam.getBaseType().get(0).getActValue(), 
                baseType);
          }
          parameterTypes[i] = new Type(baseType, binaryName);
        }
      }
      else {
         
      }
      
      i++;
    }
    
    return null;
  }
  
  private TypeRequirements formTypeReqs(TypeParameter typeParameter){
    
    return null;
  }
}
