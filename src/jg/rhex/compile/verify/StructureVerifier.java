package jg.rhex.compile.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jg.rhex.common.ArrayType;
import jg.rhex.common.FunctionSignature;
import jg.rhex.common.Type;
import jg.rhex.common.TypeUtils;
import jg.rhex.compile.components.structs.RStateBlock;
import jg.rhex.compile.components.structs.RStatement;
import jg.rhex.compile.components.tnodes.TNode;
import jg.rhex.runtime.Context;
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
  
  public static class BlockNode{
    private RStateBlock current;
    private List<RStateBlock> children;
    
    public BlockNode(RStateBlock current){
      this.current = current;
      children = new ArrayList<>();
    }
    
    public void addChild(RStateBlock block){
      children.add(block);
    }
    
    public List<RStateBlock> getChildren(){
      return children;
    }
    
    public RStateBlock getCurrentBlock(){
      return current;
    }
  }
  
  public StructureVerifier(Map<Type, RhexClass> classes, Map<String, RhexFile> rhexFiles){
    this.classes = classes;
    this.rhexFiles = rhexFiles;
  }
  
  public void verifyFileStructure(RhexFile file){
    //first, check file variables and their values
    
    //this is needed to for SymbolTable
    HashMap<String, GenClass> packageClasses = new HashMap<>();
    HashMap<String, GenClass> fileClasses = new HashMap<>();
    
    HashMap<String, RhexFile> packageFiles = new HashMap<>();
    HashMap<String, RhexFile> importedFiles = new HashMap<>();
    
    HashMap<Type, GenClass> converted = new HashMap<>();
    for(Entry<Type, RhexClass> con : classes.entrySet()){
      converted.put(con.getKey(), con.getValue());
      
      String packDesig = con.getKey().getFullName();
      if (packDesig.startsWith(file.getOriginal().getPackDesignation())) {
        packageClasses.put(con.getKey().getSimpleName(), con.getValue());
      }
      if (packDesig.startsWith(file.getTypeInfo().getFullName())) {
        fileClasses.put(con.getKey().getSimpleName(), con.getValue());
      }
    }
    
    for (Entry<String, RhexFile> entry : rhexFiles.entrySet()) {
      if (entry.getKey().startsWith(file.getOriginal().getPackDesignation())) {
        packageFiles.put(entry.getValue().getName(), entry.getValue());
      }
    }
    
    for (Entry<String, GenClass> entry : file.getImportedClasses().entrySet()) {
      if (entry.getValue() instanceof RhexFile) {
        importedFiles.put(entry.getValue().getTypeInfo().getSimpleName(), (RhexFile) entry.getValue());
      }
    }
    
    SymbolTable table = new SymbolTable(getClass().getClassLoader(), rhexFiles, converted);
    
    Context context = new Context(table);
    context.setClassMaps(new ArrayList<>(Arrays.asList(fileClasses, file.getImportedClasses(), packageClasses)));
    context.setFileMaps(new ArrayList<>(Arrays.asList(importedFiles, packageFiles)));
    context.setFuncMaps(new ArrayList<>(Arrays.asList(file.getFileFunctions())));
    context.setLocalVarMap(new HashMap<>());
    
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
      
      GenClass varType = ExpressionTypeChecker.typeCheckExpression(value, file, context);
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
      context.addLocalVariable(variable);
    }
    
    //second, check file functions
    for (Function func : file.getFileFunctions().values()) {
      RhexFunction rhexFunction = (RhexFunction) func;
    }
  }
  
  
}
