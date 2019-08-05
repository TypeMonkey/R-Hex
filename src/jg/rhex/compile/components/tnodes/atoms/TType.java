package jg.rhex.compile.components.tnodes.atoms;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jg.rhex.compile.components.structs.TypeParameter;
import jg.rhex.compile.components.tnodes.TNode;

public class TType extends TAtom<List<TType>> {

  private TypeParameter heldParameter;
  private List<TIden> rawTypeBody;
  private int arrayDimensions;
  
  public TType(List<TIden> rawBody) {
    super(new ArrayList<>());
    this.rawTypeBody = rawBody;
    this.arrayDimensions = 0;
  }
  
  public boolean equals(Object object){
    if (object instanceof TType) {
      TType other = (TType) object;
      
      if (other.getGenericTypeArgs().size() == getGenericTypeArgs().size()) {
        for(int i = 0; i < getGenericTypeArgs().size() ; i++){
          if (!other.getGenericTypeArgs().get(i).equals(getGenericTypeArgs().get(i))) {
            return false;
          }
        }
        
        return other.getBaseString().equals(getBaseString()) && arrayDimensions == other.arrayDimensions;
      }
      else {
        return false;
      }
    }
    return false;
  }
  
  public int hashCode(){
    return Objects.hash(getBaseString(), getActValue());
  }

  public void attachTypeParameter(TypeParameter attachee){
    heldParameter = attachee;
  }
  
  public void setGenericArgTypes(List<TType> generics){
    getActValue().clear();
    getActValue().addAll(generics);
  }
  
  public void setArrayDimensions(int arrayDim) {
    this.arrayDimensions = arrayDim;
  }
  
  public void addGenericArgType(TType generic){
    getActValue().add(generic);
  }
  
  public TypeParameter getAttachedTypeParameter(){
    return heldParameter;
  }
  
  public List<TType> getGenericTypeArgs(){
    return getActValue();
  }
  
  public int getArrayDimensions() {
    return arrayDimensions;
  }
  
  public List<TIden> getBaseType(){
    return rawTypeBody;
  }
  
  public String getBaseString(){
    String x = "";
    
    for(int i = 0; i < rawTypeBody.size(); i++){
      if (i == rawTypeBody.size() - 1) {
        x += rawTypeBody.get(i).getActValue().getImage();
      }
      else {
        x += rawTypeBody.get(i).getActValue().getImage() + "."; 
      }
    }
    
    return x;
  }

  @Override
  public String toString() {
    return "TYPE ~ "+getBaseString()+" : GENS: "+getActValue();
  }

}
