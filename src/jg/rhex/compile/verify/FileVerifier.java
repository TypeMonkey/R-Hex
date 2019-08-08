package jg.rhex.compile.verify;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import jg.rhex.common.ArrayType;
import jg.rhex.common.FunctionIdentity;
import jg.rhex.common.FunctionSignature;
import jg.rhex.common.Type;
import jg.rhex.common.TypeUtils;
import jg.rhex.compile.RhexCompiler;
import jg.rhex.compile.components.errors.RepeatedStructureException;
import jg.rhex.compile.components.structs.RClass;
import jg.rhex.compile.components.structs.RFile;
import jg.rhex.compile.components.structs.RFunc;
import jg.rhex.compile.components.structs.RStatement;
import jg.rhex.compile.components.structs.RStatement.RStateDescriptor;
import jg.rhex.compile.components.structs.RVariable;
import jg.rhex.compile.components.structs.TypeParameter;
import jg.rhex.compile.components.tnodes.TCast;
import jg.rhex.compile.components.tnodes.TExpr;
import jg.rhex.compile.components.tnodes.TFuncCall;
import jg.rhex.compile.components.tnodes.TNode;
import jg.rhex.compile.components.tnodes.atoms.TType;
import jg.rhex.compile.verify.errors.SimilarFunctionException;
import jg.rhex.compile.verify.errors.UnfoundTypeException;

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
  
  private final Map<FunctionSignature, FunctionTuple> funcMap;
  
  public FileVerifier(RFile rhexFile, RhexCompiler compiler){
    this.rhexFile = rhexFile;
    this.compiler = compiler;
    
    funcMap = new HashMap<>();
    
    typeStore = new CompStore(rhexFile, compiler);
  }
  
  public void verify(){
    
   //Order of finding types:
    /*
     * 1.) T-Param  (WILL WORK ON AFTER VERS. 1.0)
     * 
     * 
     * 1.) File Local Types
     * 2.) Imported Types
     * 3.) Package Local Types
     * 5.) Custom classpath jars
     * 4.) JVM Classes ( in the java.lang.* package)
     */
    
    System.out.println("------STORE: "+rhexFile.getFileName());
    System.out.println(typeStore);
    System.out.println("------STORE DONE");
    verifyConstruction();
    verifyIdentifierReferences();
    inferTypesAndCasts();
  }

  private void inferTypesAndCasts() {
    // Let's scan type casts first
    // Check variable values first (file variables, class variables)
    // Check function statements next
    
    for(RVariable variable : rhexFile.getVariables()){
      
    }
  }

  private void verifyIdentifierReferences() {
    //Makes sure that references to variable names and functions are references to visible such functions and variables
    
    //check file variables first
    HashMap<String, RVariable> scope = new HashMap<>();
    for(RVariable variable : rhexFile.getVariables()){
      TNode valueNode = variable.getValue();
      
      if (valueNode instanceof TExpr) {
        
      }
      else if (condition) {
        
      }
      
      scope.put(variable.getIdentifier().getToken().getImage(), variable);
    }
  }
  
  /**
   * Checks for identical function signatures
   */
  private void verifyConstruction() {    
    //check for identical file functions
    for (RFunc rFunc : rhexFile.getFunctions()) {
      FunctionIdentity identity = formIdentity(rFunc);
      if (funcMap.put(identity.getFuncSig(), new FunctionTuple(identity, rFunc)) != null) {
        throw new SimilarFunctionException(identity, rFunc.getName(), rhexFile.getFileName());
      }
      
      System.out.println("**IDENTITY - FILE: "+identity);
    }
    
    //check for identical functions within each class
    for(RClass rClass : rhexFile.getClasses()) {
      System.out.println("----VERIFYING CLASS: "+rClass.getName().getImage());
      HashSet<FunctionSignature> signatures = new HashSet<>();
      for(RFunc rFunc : rClass.getMethods()) {
        if (!rFunc.isConstructor()) {
          FunctionIdentity identity = formIdentity(rFunc);
          if (signatures.add(identity.getFuncSig()) == false) {
            throw new SimilarFunctionException(identity, rFunc.getName(), rhexFile.getFileName());
          }
          
          System.out.println("**IDENTITY - CLASS "+rClass.getName().getImage()+" : "+identity);
        }
      }
    }
  }
  
  private FunctionIdentity formIdentity(RFunc rFunc) {
    //resolve the return type first
    TType returnType = rFunc.getReturnType();
    Type actualType = null;
    
    System.out.println("----FUNC: "+rFunc.getName().getImage());
    
    if (TypeUtils.isPrimitive(returnType.getBaseString())) {
      actualType = TypeUtils.PRIMITIVE_TYPES.get(returnType.getBaseString());
    }
    else if (returnType.getBaseString().equals("void")) {
      actualType = Type.VOID_TYPE;
    }
    else if (returnType.getBaseString().contains(".")) {
      //full type name provided
      String [] split = returnType.getBaseString().split("\\.");
      actualType = new Type(split[split.length - 1], returnType.getBaseString());

      if (!typeStore.confirmExistanceOfType(actualType)) {
        throw new UnfoundTypeException(returnType.getBaseType().get(0).getToken(), returnType.getBaseString(), rhexFile.getFileName());
      }
    }
    else {
      //only simple name provided
      String potential = typeStore.getFullName(returnType.getBaseString());
      if (potential == null) {
        throw new UnfoundTypeException(returnType.getBaseType().get(0).getToken(), returnType.getBaseString(), rhexFile.getFileName());
      }
      
      actualType = new Type(returnType.getBaseString(), potential);
      System.out.println("-----> EXISTS? "+actualType);
      if (!typeStore.confirmExistanceOfType(actualType)) {
        throw new UnfoundTypeException(returnType.getBaseType().get(0).getToken(), returnType.getBaseString(), rhexFile.getFileName());
      }
    }
    
    if (returnType.getArrayDimensions() > 0) {
      actualType = new ArrayType(returnType.getArrayDimensions(), actualType);
    }
    
    //now, resolve the types of the parameters
    Type [] paramTypes = new Type[rFunc.getParameterAmount()];
    
    for(int i = 0; i < rFunc.getParameterAmount(); i++) {
      RVariable variable = (RVariable) rFunc.getBody().getStatements().get(i);
      //we are guaranteed during the parsing stage that no parameter has an inferred type
      TType proType = variable.getProvidedType();
      Type concreteType = null;
      
      if (TypeUtils.isPrimitive(proType.getBaseString())) {
        concreteType = TypeUtils.PRIMITIVE_TYPES.get(proType.getBaseString());
      }
      else if (proType.getBaseString().contains(".")) {
        //full type name provided
        String [] split = proType.getBaseString().split("\\.");
        concreteType = new Type(split[split.length - 1], proType.getBaseString());

        if (!typeStore.confirmExistanceOfType(concreteType)) {
          throw new UnfoundTypeException(proType.getBaseType().get(0).getToken(), proType.getBaseString(), rhexFile.getFileName());
        }
      }
      else {
        //only simple name provided
        String potential = typeStore.getFullName(proType.getBaseString());
        if (potential == null) {
          throw new UnfoundTypeException(proType.getBaseType().get(0).getToken(), proType.getBaseString(), rhexFile.getFileName());
        }
        
        concreteType = new Type(proType.getBaseString(), potential);
        if (!typeStore.confirmExistanceOfType(concreteType)) {
          throw new UnfoundTypeException(proType.getBaseType().get(0).getToken(), proType.getBaseString(), rhexFile.getFileName());
        }
      }
      
      if (proType.getArrayDimensions() > 0) {
        concreteType = new ArrayType(proType.getArrayDimensions(), concreteType);
      }
      
      paramTypes[i] = concreteType;
    }
    
    return new FunctionIdentity(new FunctionSignature(rFunc.getName().getImage(), paramTypes), actualType);
  }
  
  private static class FunctionTuple{
    private final FunctionIdentity identity;
    private final RFunc func;
    
    private FunctionTuple(FunctionIdentity identity, RFunc func) {
      this.func = func; 
      this.identity = identity;
    }
  }
}
