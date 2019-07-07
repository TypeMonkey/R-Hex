package jg.rhex.compile.components.tnodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import jg.rhex.compile.components.tnodes.atoms.TAtom;
import jg.rhex.compile.components.tnodes.atoms.TIden;
import jg.rhex.compile.components.tnodes.atoms.TType;

public class TFuncCall extends TAtom<List<TNode>> {  
  
  private TIden name;
  private List<TType> typeArguments;
  
  public TFuncCall(TIden name) {
    super(new ArrayList<>());
    this.name = name;
    this.typeArguments = new ArrayList<>();
  }
  
  public void setArgs(List<TNode> nodes){
    getActValue().clear();
    getActValue().addAll(nodes);
  }
  
  public void setArgs(TNode ... nodes){
    getActValue().clear();
    getActValue().addAll(Arrays.asList(nodes));
  }
  
  public void addArgs(TNode ... nodes){
    getActValue().addAll(Arrays.asList(nodes));
  }
  
  public void addArgs(Collection<TNode> nodes){
    getActValue().addAll(nodes);
  }
  
  public void addArg(TNode node){
    getActValue().add(node);
  }
  
  public void addGenericType(TType type){
    typeArguments.add(type);
  }
  
  public int argCount(){
    return getActValue().size();
  }
  
  public List<TNode> getArgList(){
    return getActValue();
  }
  
  public List<TType> getGenericTypes(){
    return typeArguments;
  }

  public TIden getFuncName() {
    return name;
  }
  
  @Override
  public String toString() {
    return "INV ~ "+name+" | ARGS: "+getActValue()+" || "+typeArguments;
  }

}
