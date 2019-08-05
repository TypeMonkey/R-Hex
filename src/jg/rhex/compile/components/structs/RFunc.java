package jg.rhex.compile.components.structs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
public class RFunc extends Parametric{
  
  private Token name;
  private TType returnType;
  
  private List<TType> declaredExceptions;
  
  private Set<Descriptor> descriptors;
  
  private RStateBlock body;
  private int parameterAmount;
  
  private boolean isConstructor;
 
  public RFunc(){
    declaredExceptions = new ArrayList<>();
    descriptors = new HashSet<>();
    
    this.body = new RStateBlock(null, BlockType.GENERAL);  
  }
  
  
  public void setAsConstructor(boolean construc) {
    isConstructor = construc;
  }
  
  public void setParamAmnt(int paramAmnt) {
    this.parameterAmount = paramAmnt;
  }
  
  public void setName(Token name){
    this.name = name;
  }
  
  public void setReturnType(TType tType){
    this.returnType = tType;
  }
  
  public void addDeclaredException(TType exception){
    declaredExceptions.add(exception);
  }
  
  public void addStatement(RStatement statement) {
    body.addStatement(statement);
  }
  
  public void addStatements(List<RStatement> statements) {
    body.addStatements(statements);
  }
  
  public boolean addDescriptor(Descriptor descriptor){
    return descriptors.add(descriptor);
  }
  
  public TType getReturnType() {
    return returnType;
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
  
  public boolean isConstructor() {
    return isConstructor;
  }
 
}
