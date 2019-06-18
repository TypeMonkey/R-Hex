package jg.rhex.compile.components.tnodes.atoms;

import jg.rhex.compile.components.tnodes.TOp;

public class TCParen extends TOp{

  public TCParen() {
    super(")");
  }

  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return value.toString()+"TCP";
  }

}
