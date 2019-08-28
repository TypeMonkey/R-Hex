package jg.rhex.compile.components.tnodes.atoms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import jg.rhex.compile.components.tnodes.TNode;

public class TArrayAcc extends TAtom<List<TNode>> {

  private TNode target;

  public TArrayAcc(TNode target) {
    super(new ArrayList<>(), target.getLineNumber(), target.getColNumber());
    this.target = target;
  }

  public TNode getTarget() {
    return target;
  }

  public List<TNode> getIndex() {
    return getActValue();
  }

  /*
  public void setTarget(Collection<TNode> body) {
    this.target = new TExpr(new ArrayList<>(body));
  }

  public void setTarget(TNode [] body) {
    this.target = new TExpr(new ArrayList<>(Arrays.asList(body)));
  }
  */

  public void setTarget(TNode target) {
    this.target = target;
  }

  public void setIndices(List<TNode> indices) {
    this.value = indices;
  }

  public void addIndexNode(TNode indexNode) {
    getActValue().add(indexNode);
  }

  public String toString() {
    String indicesString = value.toString();
    indicesString = indicesString.substring(1);
    indicesString = indicesString.substring(0, indicesString.length() - 1);

    return "ARR ~ TARGET: " + target + "  |  INDICES: " + indicesString;
  }

}
