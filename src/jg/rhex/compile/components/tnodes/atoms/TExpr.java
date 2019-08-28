package jg.rhex.compile.components.tnodes.atoms;

import java.util.ArrayList;
import java.util.List;

import jg.rhex.compile.components.tnodes.TNode;

public class TExpr extends TAtom<List<TNode>> {

  public TExpr(int lineNumber, int colNumber) {
    this(new ArrayList<>(), lineNumber, colNumber);
  }

  public TExpr(List<TNode> body, int lineNumber, int colNumber) {
    super(body, lineNumber, colNumber);
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
