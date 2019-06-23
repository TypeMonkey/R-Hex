package jg.rhex.compile.components.tnodes.atoms;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import jg.rhex.compile.components.tnodes.TExpr;
import jg.rhex.compile.components.tnodes.TNode;
import net.percederberg.grammatica.parser.Token;

public class TType extends TAtom<List<TType>> {

  private Token baseType;
  private List<TNode> rawTypeBody;
  private boolean hasBeenFormalized;
  
  public TType(List<TNode> rawBody) {
    super(new ArrayList<>());
    this.rawTypeBody = rawBody;
  }
  
  public TType(Token baseType){
    super(new ArrayList<>());
    this.baseType = baseType;
    hasBeenFormalized = true;
  }

  public void setGenericArgTypes(List<TType> generics){
    getActValue().clear();
    getActValue().addAll(generics);
  }
  
  public void addGenericArgType(TType generic){
    getActValue().add(generic);
  }
  
  public List<TType> getGenericTypeArgs(){
    return getActValue();
  }
  
  public Token getBaseType() {
    return baseType;
  }
  
  public boolean hasBeenFormalized(){
    return hasBeenFormalized;
  }
  
  public void formalizeRawTypeBody(){
    ArrayDeque<TNode> deque = new ArrayDeque<>(rawTypeBody);
    
    TIden iden = (TIden) deque.poll();
    baseType = iden.getActValue();  //first TNode is an TIden that is the base type's name
    
    while (!deque.isEmpty()) {
      TNode commaOrType = deque.poll();
      if (commaOrType instanceof TType) {
        TType type = (TType) commaOrType;
        type.formalizeRawTypeBody();
        
        addGenericArgType(type);
      }
    }
    
    rawTypeBody.clear();
    rawTypeBody = null;
    
    hasBeenFormalized = true;
  }

  @Override
  public String toString() {
    return "TYPE ~ "+baseType+" | "+getActValue();
  }

}
