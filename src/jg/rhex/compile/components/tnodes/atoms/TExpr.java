package jg.rhex.compile.components.tnodes.atoms;

import java.util.ArrayList;
import java.util.List;

import jg.rhex.compile.components.tnodes.TNode;

public class TExpr extends TAtom<List<TNode>> {

  public TExpr() {
    this(new ArrayList<>());
  }

  public TExpr(List<TNode> body) {
    super(body);
  }

  public void addNode(TNode node) {
    getActValue().add(node);
  }

  public void setBody(List<TNode> nodes) {
    this.value = nodes;
  }

  @Override
  public String toString() {
    return "EXPR ~ " + getActValue();
  }

}
