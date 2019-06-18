package jg.rhex.compile.components.structs;

import java.util.List;
import java.util.Set;

import jg.rhex.compile.components.tnodes.TNode;
import jg.rhex.compile.components.tnodes.atoms.TIden;
import jg.rhex.compile.components.tnodes.atoms.TType;
import net.percederberg.grammatica.parser.Token;

/**
 * Represents variable declarations - including type declarations (ex: type [DESCRIPTORS] typeName = otherTypeName)
 * @author Jose
 *
 */
public class RVariable extends RStatement{
  
  private TIden identifier;  //variable name
  
  private Set<Descriptor> descriptors; //declaration descriptors
  
  private TType providedType; //the given type at the variable's declaration
  //if null, then type is given
  //if not null, then type must be inferred
  
  /**
   * Constructs an RVariable meant to represent a type declaration
   * @param identifier 
   * @param typeName
   * @param descriptors
   */
  public RVariable(TIden identifier, TType typeName, Set<Descriptor> descriptors) {
    super(RStateDescriptor.VAR_DEC, null, typeName);
    this.identifier = identifier;
    this.descriptors = descriptors;
  }
  
  /**
   * Constructs an RVariable meant to represent a general variable declaration
   * @param type - type of variable being declared, or null if this variable's type is supposed to be inferred
   * @param identifier
   * @param value
   * @param descriptors
   */
  public RVariable(TType type, TIden identifier, TNode value, Set<Descriptor> descriptors) {
    super(RStateDescriptor.VAR_DEC, null, value);
    
    this.identifier = identifier;
    this.descriptors = descriptors;
  }
  
  public TNode getValue(){
    return getStatement();
  }

  public TType getProvidedType() {
    return providedType;
  }
  
  public TIden getIdentifier(){
    return identifier;
  }
  
  public Set<Descriptor> getDescriptors() {
    return descriptors;
  }

  /**
   * Returns true if the type of this variable is to be inferred
   * false if the type is given
   * @return true if the type of this variable is to be inferred
   * false if the type is given
   * 
   */
  public boolean toBeInferred() {
    return providedType != null;
  }
}
