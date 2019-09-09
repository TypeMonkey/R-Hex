package jg.rhex.compile.verify;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jg.rhex.common.ArrayType;
import jg.rhex.common.FunctionSignature;
import jg.rhex.common.Type;
import jg.rhex.compile.components.structs.RStatement;
import jg.rhex.compile.components.tnodes.TNode;
import jg.rhex.runtime.SymbolTable;
import jg.rhex.runtime.components.Function;
import jg.rhex.runtime.components.GenClass;
import jg.rhex.runtime.components.Variable;
import jg.rhex.runtime.components.rhexspec.RhexClass;
import jg.rhex.runtime.components.rhexspec.RhexFile;
import jg.rhex.runtime.components.rhexspec.RhexFunction;
import jg.rhex.runtime.components.rhexspec.RhexVariable;

public class StructureVerifier {
  
  private final Map<Type, RhexClass> classes;
  private final Map<String, RhexFile> rhexFiles;
  
  public StructureVerifier(Map<Type, RhexClass> classes, Map<String, RhexFile> rhexFiles){
    this.classes = classes;
    this.rhexFiles = rhexFiles;
  }
  
  public void verifyFileStructure(RhexFile file){
    //first, check file variables and their values
    
    //this is needed to for SymbolTable
    HashMap<Type, GenClass> converted = new HashMap<>();
    for(Entry<Type, RhexClass> con : classes.entrySet()){
      converted.put(con.getKey(), con.getValue());
    }
    
    SymbolTable table = new SymbolTable(getClass().getClassLoader(), rhexFiles, converted, new HashMap<>(), file.getFileFunctions());
    System.out.println("--->>>VERIFYING FILE VARIABLES<<<---");
    for (Variable variable : file.getFileVariables().values()) {
      RhexVariable actualVariable = (RhexVariable) variable;
      //at variable declaration, the variable being declared to cannot be referred to
      TNode value = actualVariable.getOriginal().getValue();
      
      System.out.println("------------CHECKING: "+actualVariable.getName()+" | "+value);
      
      GenClass declaredType = null;
      if (actualVariable.getType() instanceof ArrayType) {
        declaredType = table.findClass((ArrayType) actualVariable.getType());
      }
      else {
        declaredType = table.findClass(actualVariable.getType());
      }
      
      GenClass varType = ExpressionTypeChecker.typeCheckExpression(value, file, table);
      //the java.lang.Object is for smooth assignments from primitive types
      if (!declaredType.getTypeInfo().equals(Type.OBJECT) && 
          !varType.decendsFrom(declaredType)) {
        throw new RuntimeException("'"+variable.getName()+"' type of "+actualVariable.getType()+" isn't compatible with "
                       +"assigned type "+varType.getTypeInfo()
                       +" , at <ln:"+actualVariable.getOriginal().getIdentifier().getToken().getStartLine()+"> "
                       +" at file "+file.getName());
      }
      
      System.out.println(varType == null);
      System.out.println("  --->  TYPED CHECK F-VAR: "+variable.getName()+" | TYPE: "+varType+" | GIVEN: "+declaredType);
      
      //at the end of evaluation, then add the variable
      table.addLocalVariable(variable);
    }
    
    //second, check file functions
    for (Function func : file.getFileFunctions().values()) {
      RhexFunction rhexFunction = (RhexFunction) func;
    }
  }
  
  
}
