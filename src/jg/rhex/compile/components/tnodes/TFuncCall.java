package jg.rhex.compile.components.tnodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import jg.rhex.compile.components.tnodes.atoms.TAtom;

public class TFuncCall extends TAtom<List<TNode>> {

  private String name;

  public TFuncCall(String name) {
    super(new ArrayList<>());
    this.name = name;
  }

  public void setArgs(List<TNode> nodes) {
    getActValue().clear();
    getActValue().addAll(nodes);
  }

  public void setArgs(TNode... nodes) {
    getActValue().clear();
    getActValue().addAll(Arrays.asList(nodes));
  }

  public void addArgs(TNode... nodes) {
    getActValue().addAll(Arrays.asList(nodes));
  }

  public void addArgs(Collection<TNode> nodes) {
    getActValue().addAll(nodes);
  }

  public void addArg(TNode node) {
    getActValue().add(node);
  }

  public int argCount() {
    return getActValue().size();
  }

  public List<TNode> getArgList() {
    return getActValue();
  }

  public String getFuncName() {
    return name;
  }

  @Override
  public String toString() {
    String argListString = getActValue().toString();
    argListString = argListString.substring(1);
    argListString = argListString.substring(0, argListString.length());
    return "INV ~ " + name + " | ARGS: " + argListString;
  }

}
