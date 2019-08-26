package jg.rhex.compile.verify;

import java.util.HashMap;
import java.util.Map;

import jg.rhex.common.FunctionSignature;
import jg.rhex.common.Type;
import jg.rhex.compile.components.structs.RStatement;
import jg.rhex.compile.components.tnodes.TNode;
import jg.rhex.runtime.SymbolTable;
import jg.rhex.runtime.components.Function;
import jg.rhex.runtime.components.GenClass;
import jg.rhex.runtime.components.Variable;
import jg.rhex.runtime.components.rhexspec.RhexFile;
import jg.rhex.runtime.components.rhexspec.RhexVariable;

public class StructureVerifier {
  
  private final Map<Type, GenClass> classes;
  private final Map<String, RhexFile> rhexFiles;
  
  public StructureVerifier(Map<Type, GenClass> classes, Map<String, RhexFile> rhexFiles){
    this.classes = classes;
    this.rhexFiles = rhexFiles;
  }
  
  public void verifyFileStructure(RhexFile file){
    //first, check file variables and their values
    SymbolTable table = new SymbolTable(rhexFiles, classes, new HashMap<>(), file.getFileFunctions());
    for (Variable variable : file.getFileVariables().values()) {
      RhexVariable actualVariable = (RhexVariable) variable;
      //at variable declaration, the variable being declared to cannot be referred to
      TNode value = actualVariable.getOriginal().getValue();
      
      GenClass varType = 
      ExpressionTypeChecker.typeCheckExpression(value, file, table);
      
      //at the end of evaluation, then add the variable
      table.addLocalVariable(variable);
    }
  }
}
