package jg.rhex.compile.verify;

import java.util.HashMap;
import java.util.Map;

import jg.rhex.common.Type;
import jg.rhex.compile.components.structs.RStatement;
import jg.rhex.compile.components.tnodes.TNode;
import jg.rhex.runtime.components.GenClass;
import jg.rhex.runtime.components.Variable;
import jg.rhex.runtime.components.rhexspec.RhexFile;
import jg.rhex.runtime.components.rhexspec.RhexVariable;

public class StructureVerifier {
  
  public StructureVerifier(Map<Type, GenClass> classes, Map<String, RhexFile> rhexFiles){
    
  }
  
  public void verifyFileStructure(){
    //first, check file variables and their values
    HashMap<String, Variable> localScope = new HashMap<>();
    for (RhexVariable variable : file.getFileVariables().values()) {
      //at variable declaration, the variable being declared to cannot be referred to
      TNode value = variable.getOriginal().getValue();
      
      
      //at the end of evaluation, then add the variable
      localScope.put(variable.getName(), variable);
    }
  }
}
