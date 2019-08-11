package jg.rhex.compile.verify;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;

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
import jg.rhex.compile.components.structs.RStateBlock;
import jg.rhex.compile.components.structs.RStatement;
import jg.rhex.compile.components.structs.RStatement.RStateDescriptor;
import jg.rhex.compile.components.structs.RVariable;
import jg.rhex.compile.components.structs.TypeParameter;
import jg.rhex.compile.components.tnodes.TNode;
import jg.rhex.compile.components.tnodes.atoms.TCast;
import jg.rhex.compile.components.tnodes.atoms.TExpr;
import jg.rhex.compile.components.tnodes.atoms.TFuncCall;
import jg.rhex.compile.components.tnodes.atoms.TType;
import jg.rhex.compile.verify.errors.RedundantExtensionException;
import jg.rhex.compile.verify.errors.SimilarFunctionException;
import jg.rhex.compile.verify.errors.UnfoundTypeException;
import jg.rhex.runtime.components.rhexspec.RhexClass;
import jg.rhex.runtime.components.rhexspec.RhexConstructor;
import jg.rhex.runtime.components.rhexspec.RhexFile;
import jg.rhex.runtime.components.rhexspec.RhexFunction;
import jg.rhex.runtime.components.rhexspec.RhexVariable;

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
  private final TypeStore typeStore;
  
  private final RhexFile verifiedFile;
  
  private final Map<FunctionSignature, RhexFunction> funcMap;
  
  public FileVerifier(RFile rhexFile, RhexCompiler compiler){
    this.rhexFile = rhexFile;
    this.compiler = compiler;
    
    funcMap = new HashMap<>();
    
    verifiedFile = new RhexFile(rhexFile);
    
    typeStore = new TypeStore(rhexFile, compiler);
  }
  
  public RhexFile verify(){

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
    
    //TNodes that need type attachement:
    /*
     * TCast
     * TNew?
     * TType (of course)
     * TFuncCall (when tparams are supported)
     */

    System.out.println("------STORE: "+verifiedFile.getOriginal().getFileName());
    System.out.println(typeStore);
    System.out.println("------STORE DONE");
    
    //these methods attach type information to variable declarations
    //these methods also make sure that function signatures are unique
    attachFileComponents();    
    attachClassComponents();
 
    //verifies inheritance 
    //(all non-abstract classes implement abstract methods from abstract classes and interfaces)
    //(parent constructors are called)
    verifyInheritance();
    
    return verifiedFile;
  }
  
  private void verifyInheritance() {
    
  }

  /**
   * Verifies file functions and variables
   */
  private void attachFileComponents(){
    //verify file variables first
    for (RVariable variable : rhexFile.getVariables()) {
      TType provided = variable.getProvidedType();
      Type actual = retrieveType(provided);
      
      provided.attachType(actual);
      
      System.out.println("** VAR-VERIFIED: "+actual+" for "+
          variable.getIdentifier().getToken().getImage()+
          " , "+rhexFile.getFileName()+" <ln:"+variable.getIdentifier().getToken().getStartLine()+">");
      
      //file level variable uniqueness is enforced during the formation stage. No need to check for name conflicts
      verifiedFile.placeVariable(new RhexVariable(variable));
    }
    
    //check for identical file functions
    for (RFunc rFunc : rhexFile.getFunctions()) {       
      RStateBlock funcBody = rFunc.getBody();
      for(int i = rFunc.getParameterAmount(); i < funcBody.getStatements().size(); i++){
        if (funcBody.getStatements().get(i).getDescriptor() == RStateDescriptor.VAR_DEC) {
          RVariable variable = (RVariable) funcBody.getStatements().get(i);
          Type actual = retrieveType(variable.getProvidedType());
          variable.getProvidedType().attachType(actual);
          
          System.out.println("** VAR-VERIFIED: "+actual+" for "+
              variable.getIdentifier().getToken().getImage()+
              " , "+rhexFile.getFileName()+" <ln:"+variable.getIdentifier().getToken().getStartLine()+">");
        }
        else if(funcBody.getStatements().get(i).getDescriptor() == RStateDescriptor.BLOCK){
          verifyVariablesHelper(funcBody);
        }
      }
      
      FunctionIdentity identity = formIdentity(rFunc);
      RhexFunction function = new RhexFunction(identity, rFunc);
      if (!verifiedFile.placeFunction(function)) {
        throw new SimilarFunctionException(identity, rFunc.getName(), rhexFile.getFileName());
      }     
      
      System.out.println("**IDENTITY - FILE: "+identity);
    }
 
  }
  
  /**
   * Verifies file classes and their functions and variables
   */
  private void attachClassComponents(){
    //verify class declaration and add them to the file's classmap
    for(RClass rClass : rhexFile.getClasses()){
      String binaryName = rhexFile.getPackDesignation()+"."+rhexFile.getFileName()+"."+rClass.getName().getImage();
      Type classTypeInfo = new Type(rClass.getName().getImage(), binaryName);
      
      HashSet<Type> superTypes = new HashSet<>();
      for(TType parent : rClass.getSuperTypes()){
        Type superType = retrieveType(parent);
        if(!superTypes.add(superType)){
          throw new RedundantExtensionException(rClass.getName().getImage(), parent, rhexFile.getFileName());
        }
      }
      
      //file-local class uniqueness is enforced during the formation phase
      verifiedFile.placeClass(new RhexClass(classTypeInfo, superTypes, rClass));
    }    
    
    //verify class variables next
    for(RhexClass rhexClass : verifiedFile.getFileClasses().values()){
      RClass rClass = rhexClass.getOriginal();
      for(RVariable variable : rClass.getClassVariables()){
        TType provided = variable.getProvidedType();
        Type actual = retrieveType(provided);
        
        provided.attachType(actual);
        
        System.out.println("** VAR-VERIFIED: "+actual+" for "+
            variable.getIdentifier().getToken().getImage()+
            " , "+rhexFile.getFileName()+" <ln:"+variable.getIdentifier().getToken().getStartLine()+">");
        
        rhexClass.placeVariable(new RhexVariable(variable));
      }
      
      HashSet<FunctionSignature> signatures = new HashSet<>();
      
      //verify local variable in class functions
      for(RFunc rFunc : rClass.getMethods()){
        RStateBlock funcBody = rFunc.getBody();
        
        FunctionIdentity identity = null;
        if (!rFunc.isConstructor()) {
          identity = formIdentity(rFunc);
          if (signatures.add(identity.getFuncSig()) == false) {
            throw new SimilarFunctionException(identity, rFunc.getName(), rhexFile.getFileName());
          }

          System.out.println("**IDENTITY - CLASS "+rClass.getName().getImage()+" : "+identity);
        }
        else {
          identity = formConstructorIdentity(rFunc, rhexClass.getTypeInfo());
        }
        
        
        for(int i = rFunc.getParameterAmount(); i < funcBody.getStatements().size(); i++){
          if (funcBody.getStatements().get(i).getDescriptor() == RStateDescriptor.VAR_DEC) {
            RVariable variable = (RVariable) funcBody.getStatements().get(i);
            Type actual = retrieveType(variable.getProvidedType());
            variable.getProvidedType().attachType(actual);
            
            System.out.println("** VAR-VERIFIED: "+actual+" for "+
                                 variable.getIdentifier().getToken().getImage()+
                                 " , "+rhexFile.getFileName()+" <ln:"+variable.getIdentifier().getToken().getStartLine()+">");
          }
          else if(funcBody.getStatements().get(i).getDescriptor() == RStateDescriptor.BLOCK){
            verifyVariablesHelper(funcBody);
          }
        }
        
        if (rFunc.isConstructor()) {
          rhexClass.placeConstructor(new RhexConstructor(rhexClass, identity.getFuncSig(), rFunc));
        }
        else {
          rhexClass.placeFunction(new RhexFunction(identity, rFunc));
        }
      }
    }
    
  }
  
  private void verifyVariablesHelper(RStateBlock stateBlock){
    for (int i = 0; i < stateBlock.getStatements().size(); i++) {
      RStatement statement = stateBlock.getStatements().get(i);
      if (statement.getDescriptor() == RStateDescriptor.VAR_DEC) {
        RVariable variable = (RVariable) statement;
        Type actual = retrieveType(variable.getProvidedType());
        
        variable.getProvidedType().attachType(actual);
        
        System.out.println("** VAR-VERIFIED: "+actual+" for "+
            variable.getIdentifier().getToken().getImage()+
            " , "+rhexFile.getFileName()+" <ln:"+variable.getIdentifier().getToken().getStartLine()+">");
      }
      else if (statement.getDescriptor() == RStateDescriptor.BLOCK) {
        verifyVariablesHelper((RStateBlock) statement);
      }
    }
  }
  
  private FunctionIdentity formConstructorIdentity(RFunc rFunc, Type hostType){
    System.out.println("----CONSTRUCTOR: "+rFunc.getName().getImage());

    //resolve the types of the parameters
    Type [] paramTypes = new Type[rFunc.getParameterAmount()];

    for(int i = 0; i < rFunc.getParameterAmount(); i++) {
      RVariable variable = (RVariable) rFunc.getBody().getStatements().get(i);
      //we are guaranteed during the parsing stage that no parameter has an inferred type
      TType proType = variable.getProvidedType();
      Type concreteType = retrieveType(proType);
      paramTypes[i] = concreteType;
    }

    return new FunctionIdentity(new FunctionSignature(rFunc.getName().getImage(), paramTypes), hostType);
  }
  
  private FunctionIdentity formIdentity(RFunc rFunc) {   
    System.out.println("----FUNC: "+rFunc.getName().getImage());
    
    //resolve the types of the parameters
    Type [] paramTypes = new Type[rFunc.getParameterAmount()];
    
    for(int i = 0; i < rFunc.getParameterAmount(); i++) {
      RVariable variable = (RVariable) rFunc.getBody().getStatements().get(i);
      //we are guaranteed during the parsing stage that no parameter has an inferred type
      TType proType = variable.getProvidedType();
      Type concreteType = retrieveType(proType);
      paramTypes[i] = concreteType;
    }
    
    //then, resolve the return type first
    TType returnType = rFunc.getReturnType();
    if (returnType.getBaseString().equals("void")) {
      return new FunctionIdentity(new FunctionSignature(rFunc.getName().getImage(), paramTypes));
    }
    else {
      Type actualType = retrieveType(returnType);
      return new FunctionIdentity(new FunctionSignature(rFunc.getName().getImage(), paramTypes), actualType);
    }   
  }
  
  private Type retrieveType(TType proType){
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
    
    return concreteType;
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
