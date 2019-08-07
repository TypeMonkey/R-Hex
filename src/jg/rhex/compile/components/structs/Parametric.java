package jg.rhex.compile.components.structs;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Describes Rhex structures that accept types as a parameter (and hence support parametric
 * polymorphsim)
 * 
 * @author Jose Guaro
 *
 */
public abstract class Parametric {

  protected Map<String, TypeParameter> typeParameters;
  
  /**
   * Constructs a Parametric structure
   */
  public Parametric() {
    typeParameters = new LinkedHashMap<>();
  }
  
  /**
   * Adds a type parameter to this structure
   * @param parameter - the type parameter
   * @return true if the type parameter's handle hasn't already been used
   *         false if else
   */
  public boolean addTypeParameter(TypeParameter parameter){
    return typeParameters.put(parameter.getHandle().getActValue().getImage(), parameter) == null;
  }
 
  /**
   * Clears the current type parameter map and adds all the typeparamters
   * in the provided set
   * @param setParameters - the new type parameters to add to the paramter map
   */
  public void setTypeParameters(Set<TypeParameter> setParameters){
    typeParameters.clear();
    for (TypeParameter typeParameter : setParameters) {
      this.typeParameters.put(typeParameter.getHandle().getActValue().getImage(), typeParameter);
    }
  }
  
  /**
   * Retrieves the type parameter with the provided handle
   * @param handle - the handle name of the strign parameter
   * @return the TypeParameter
   */
  public TypeParameter getTypeParameter(String handle){
    return typeParameters.get(handle);
  }
  
  /**
   * Returns all type parameters declared with this structure
   * @return the Set of TypeParameters
   */
  public Map<String, TypeParameter> getTypeParameters(){
    return typeParameters;
  }
  
}
