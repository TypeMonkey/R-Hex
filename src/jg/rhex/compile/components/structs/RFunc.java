package jg.rhex.compile.components.structs;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import jg.rhex.compile.components.structs.RStateBlock.BlockType;
import jg.rhex.compile.components.tnodes.atoms.TType;
import net.percederberg.grammatica.parser.Token;

/**
 * Represents an Rhex function/method
 * 
 * Note: A function's/method's body statements is represented in an RStateblock.
 * If a function/method parameter count is n, then the first n-statements
 * in this RStateBlock are RVariables that represent parameters (in-order , from left to right, as declared).
 * 
 * @author Jose
 *
 */
public class RFunc {
  
  private Token name;
  private TType returnType;
  private List<TType> declaredExceptions;
  private Set<Descriptor> descriptors;
  private RStateBlock body;
  private int parameterAmount;
  private boolean isPublic;
 
  public RFunc(Token name, boolean isPublic, int parameterAmount, List<TType> decExceptions, Set<Descriptor> descriptors){
    this.name = name;
    this.isPublic = isPublic;
    this.parameterAmount = parameterAmount;
    this.declaredExceptions = decExceptions;
    this.descriptors = descriptors;
    this.body = new RStateBlock(null, BlockType.GENERAL);
  }
  
  public void setReturnType(TType returnType){
    this.returnType = returnType;
  }
  
  public void addStatement(RStatement statement){
    body.addStatement(statement);
  }
  
  public void addStatements(Collection<RStatement> statements){
    body.addStatements(statements);
  }
  
  public TType getReturnType() {
    return returnType;
  }
  
  public boolean isPublic(){
    return isPublic;
  }

  public Token getName() {
    return name;
  }

  public RStateBlock getBody() {
    return body;
  }

  public List<TType> getDeclaredExceptions() {
    return declaredExceptions;
  }

  public Set<Descriptor> getDescriptors() {
    return descriptors;
  }

  public int getParameterAmount() {
    return parameterAmount;
  }
}
