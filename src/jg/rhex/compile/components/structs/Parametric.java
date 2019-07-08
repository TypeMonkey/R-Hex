package jg.rhex.compile.components.structs;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Describes Rhex structures that accept types as a parameter (and hence support parametric
 * polymorphsim)
 * 
 * @author Jose Guaro
 *
 */
public abstract class Parametric {

  protected Set<TypeParameter> typeParameters;
  
  /**
   * Constructs a Parametric structure
   */
  public Parametric() {
    typeParameters = new LinkedHashSet<>();
  }
  
  /**
   * Adds a type parameter to this structure
   * @param parameter - the type parameter
   * @return true if the type parameter's handle hasn't already been used
   *         false if else
   */
  public boolean addTypeParameter(TypeParameter parameter){
    return typeParameters.add(parameter);
  }
  
  /**
   * Returns all type parameters declared with this structure
   * @return the Set of TypeParameters
   */
  public Set<TypeParameter> getTypeParameters(){
    return typeParameters;
  }
  
}
