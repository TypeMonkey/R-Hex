package jg.rhex.compile.verify;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import jg.rhex.common.FunctionIdentity;
import jg.rhex.common.FunctionSignature;
import jg.rhex.common.Type;
import jg.rhex.compile.RhexCompiler;
import jg.rhex.compile.components.structs.RClass;
import jg.rhex.compile.components.structs.RFile;
import jg.rhex.compile.components.structs.RFunc;
import jg.rhex.compile.components.structs.RVariable;
import jg.rhex.compile.components.tnodes.atoms.TType;
import jg.rhex.compile.verify.errors.SimilarFunctionException;
import jg.rhex.compile.verify.errors.UnfoundTypeException;
import jg.rhex.runtime.components.rhexspec.RhexClass;
import jg.rhex.runtime.components.rhexspec.RhexConstructor;
import jg.rhex.runtime.components.rhexspec.RhexFunction;
import jg.rhex.runtime.components.rhexspec.RhexVariable;

/**
 * Extracts the classes in an RFile
 * 
 * @author Jose Guaro
 *
 */
public class ClassExtractor {

  private final RFile rhexFile;
  private final NameResolver typeStore;
    
  public ClassExtractor(RFile rhexFile, RhexCompiler compiler){
    this.rhexFile = rhexFile;        
    typeStore = new NameResolver(rhexFile, compiler);
  }
  
  public Map<Type, RhexClass> extract(){
    HashMap<Type, RhexClass> fileClasses = new HashMap<>();
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

    System.out.println(" NAMES: "+typeStore);
    
    for(RClass rawClass : rhexFile.getClasses()) {
      RhexClass template = attachClassComponents(rawClass);
      fileClasses.put(template.getTypeInfo(), template);
    }
    
    return fileClasses;
  }
  
  /**
   * Verifies file classes and their functions and variables
   */
  private RhexClass attachClassComponents(RClass rawClass){
    //verify class declaration and add them to the file's classmap

    String binaryName = null;
    if (rhexFile.getPackDesignation() == null) {
      binaryName = rhexFile.getFileName()+"."+rawClass.getName().getImage();
    }
    else {
      binaryName = rhexFile.getPackDesignation()+"."+rhexFile.getFileName()+"."+rawClass.getName().getImage();
    }
    
    Type classTypeInfo = new Type(rawClass.getName().getImage(), binaryName);

    RhexClass classTemplate = new RhexClass(classTypeInfo, rawClass);
    
    //get Type for parent and interfaces
    for(TType superType : rawClass.getSuperTypes()) {
      superType.attachType(retrieveType(superType));
    }

    //verify class variables next
    for(RVariable variable : rawClass.getClassVariables()){
      TType provided = variable.getProvidedType();
      Type actual = retrieveType(provided);

      provided.attachType(actual);

      System.out.println("** VAR-VERIFIED: "+actual+" for "+
          variable.getIdentifier().getToken().getImage()+
          " , "+rhexFile.getFileName()+" <ln:"+variable.getIdentifier().getToken().getStartLine()+">");

      classTemplate.placeVariable(new RhexVariable(variable));
    }

    HashSet<FunctionSignature> signatures = new HashSet<>();

    //verify local variable in class functions
    for(RFunc rFunc : rawClass.getMethods()){
      FunctionIdentity identity = null;
      
      HashSet<Type> exceptions = new HashSet<>();
      for (TType type : rFunc.getDeclaredExceptions()) {
        Type actual = retrieveType(type);
        type.attachType(actual);
        if (!exceptions.add(actual)) {
          throw new RuntimeException("The function: "+rFunc.getName().getImage()+
              " repeats the exception '"+actual+"' , at <ln:"+
              type.getBaseType().get(0).getToken().getStartLine()+">");
        }
      }
      
      if (!rFunc.isConstructor()) {
        identity = formIdentity(rFunc);
        if (signatures.add(identity.getFuncSig()) == false) {
          throw new SimilarFunctionException(identity, rFunc.getName(), rhexFile.getFileName());
        }

        classTemplate.placeFunction(new RhexFunction(identity, rFunc, exceptions));
        
        System.out.println("**IDENTITY - CLASS "+rawClass.getName().getImage()+" : "+identity);
      }
      else {
        identity = formConstructorIdentity(rFunc, classTemplate.getTypeInfo());
        
        classTemplate.placeConstructor(new RhexConstructor(classTemplate, identity.getFuncSig(), rFunc, exceptions));
      }
      
      System.out.println("<<<<<< PLACED: "+identity.getFuncSig()+"  |  HOST: "+classTemplate.getTypeInfo());
    }

    return classTemplate;
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

    return new FunctionIdentity(new FunctionSignature(rFunc.getName().getImage(), paramTypes, rFunc.getDescriptors()), hostType);
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
      return new FunctionIdentity(new FunctionSignature(rFunc.getName().getImage(), paramTypes, rFunc.getDescriptors()));
    }
    else {
      Type actualType = retrieveType(returnType);
      return new FunctionIdentity(new FunctionSignature(rFunc.getName().getImage(), paramTypes, rFunc.getDescriptors()), actualType);
    }   
  }
  
  private Type retrieveType(TType proType){
    Type concreteType = typeStore.retrieveType(proType);
    
    if (concreteType == null) {
      throw new UnfoundTypeException(proType.getBaseType().get(0).getToken(), proType.getBaseString(), rhexFile.getFileName());
    }
    else {
      return concreteType;
    }
  }

  /*
   * Verifies file functions and variables
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
  */
  
  /*
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
  */
  
}
