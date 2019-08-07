package jg.rhex.compile.components.structs;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jg.rhex.compile.components.tnodes.atoms.TIden;
import jg.rhex.compile.components.tnodes.atoms.TType;
import net.percederberg.grammatica.parser.Token;

public class TypeParameter {

  //The name to refer to the parameterized type
  private TIden handle;
  
  //Constraints
  private Set<TType> extendedClasses;
  
  public TypeParameter(TIden handle){
    this.handle = handle;
    
    extendedClasses = new HashSet<>();
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
  
  public Set<TType> getRequiredClasses() {
    return extendedClasses;
  }
  
  public TIden getHandle(){
    return handle;
  }
  
}
