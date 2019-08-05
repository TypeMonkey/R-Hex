package jg.rhex.compile.components.structs;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jg.rhex.compile.components.tnodes.atoms.TType;
import net.percederberg.grammatica.parser.Token;

public class TypeParameter {

  //The name to refer to the parameterized type
  private Token identifier;
  
  //Constraints
  private Set<TType> extendedClasses;
  private Set<FunctionInfo> expectedFunctions;
  private Map<String, TypeParameter> tParamsInScope;
  
  public TypeParameter(Token identifier){
    this.identifier = identifier;
    
    extendedClasses = new HashSet<>();
    expectedFunctions = new HashSet<>();
    tParamsInScope = new LinkedHashMap<>();
  }
  
  public boolean equals(Object object){
    if (object instanceof TypeParameter) {
      TypeParameter typeParameter = (TypeParameter) object;
      return typeParameter.identifier.getImage().equals(identifier.getImage());
    }
    return false;
  }
  
  public int hashCode() {
    return identifier.getImage().hashCode();
  }
  
  /**
   * Adds a TypeParameter to be used by function constraints/extended classes
   * within this TypeParameter
   * @param tparam - the TypeParameter to add
   * @return true if this TypeParameter has not already been added
   *         false if else
   */
  public boolean addTParam(TypeParameter tparam){
    return tParamsInScope.put(tparam.getIdentifier().getImage(), tparam) == null;
  }
  
  /**
   * Adds a function to be required on this type
   * @param key
   * @return true if this FunctionKey was not already required
   *         false if this FunctionKey was already required
   */
  public boolean addReqFunction(FunctionInfo key){
    return expectedFunctions.add(key);
  }
  
  /**
   * Adds a class (in the form of a class) to be required to be extended on this type
   * @param key
   * @return true if this class was not already required
   *         false if this class was already required
   */
  public boolean addReqClass(TType className){
    return extendedClasses.add(className);
  }
  
  public Map<String, TypeParameter> getNestedTParams(){
    return tParamsInScope;
  }
  
  public Set<FunctionInfo> getExpectedFunctions() {
    return expectedFunctions;
  }
  
  public Set<TType> getRequiredClasses() {
    return extendedClasses;
  }
  
  public Token getIdentifier(){
    return identifier;
  }
  
}
