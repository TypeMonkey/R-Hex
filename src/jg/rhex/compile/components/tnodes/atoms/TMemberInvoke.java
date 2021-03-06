package jg.rhex.compile.components.tnodes.atoms;

import java.util.ArrayList;
import java.util.List;

import jg.rhex.compile.components.tnodes.TNode;

/**
 * Represents the "." operator.
 * 
 * Let xi ( 1 <= i <= n) be some variable name or function call
 * 
 * The dot operator designates which member of a value to retrieve for each
 * previous xi
 * 
 * Say we have x1.x2.x3.x4.x5:
 * 
 * The sequence of value retrieval is: 1.) Retrieve the member (function or
 * variable) name by x2 of the value referred by x1 2.) Retrieve the member
 * (function or variable) name by x3 of the value referred by x2 3.) Retrieve
 * the member (function or variable) name by x4 of the value referred by x3 4.)
 * Retrieve the member (function or variable) name by x5 of the value referred
 * by x4 5.) x5 is then the final result
 * 
 * TMemberInvoke stores this sequence as a list so that above would simply be
 * stored as [x1,x2,x3,x4,x5]
 * 
 * @author Jose
 *
 */
public class TMemberInvoke extends TAtom<List<TNode>> {

  public TMemberInvoke(List<TNode> nodes, int lineNumber, int colNumber) {
    super(nodes, lineNumber, colNumber);
  }

  public List<TNode> getSequence() {
    return getActValue();
  }

  @Override
  public String toString() {
    return "MEM ~ " + getActValue();
  }

}
