package jg.rhex.compile.components.tnodes.atoms;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jg.rhex.common.Type;
import jg.rhex.compile.components.structs.TypeParameter;
import jg.rhex.compile.components.tnodes.TNode;
import net.percederberg.grammatica.parser.Token;

public class TType extends TAtom<List<TType>> {

  //These are for the verification stage
  private TypeParameter placeHolder;
  private Type concreteType;
  //--end
  
  private List<TIden> rawTypeBody;
  private int arrayDimensions;
  
  private Token inferType;
    
  public TType(List<TIden> rawBody) {
    super(new ArrayList<>());
    this.rawTypeBody = rawBody;
    this.arrayDimensions = 0;
  }
  
  public TType(Token inferredType) {
    super(new ArrayList<>());
    this.inferType = inferredType;
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
  
  public void associateTypeParameter(TypeParameter parameter){
    placeHolder = parameter;
  }
  
  public void attachType(Type type) {
    concreteType = type;
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
  
  public TypeParameter getAssocTypeParameter(){
    return placeHolder;
  }
  
  public Type getAttachedType() {
    return concreteType;
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
  
  public Token getInferToken() {
    return inferType;
  }
  
  public boolean isInferredType() {
    return inferType != null;
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
