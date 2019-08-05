package jg.rhex.compile.components.structs;

import java.util.Set;

import jg.rhex.compile.components.tnodes.atoms.TIden;
import jg.rhex.compile.components.tnodes.atoms.TType;
import net.percederberg.grammatica.parser.Token;

public class UseDeclaration{
  
  private Token useToken;
  private TType baseImport;
  private Set<TIden> funcsToImport;
  
  public UseDeclaration(Token useToken, TType baseImport) {
    this(useToken, baseImport, null);
  }
  
  public UseDeclaration(Token useToken, TType baseImport, Set<TIden> funcsToImport){
    this.useToken = useToken;
    this.baseImport = baseImport;
    this.funcsToImport = funcsToImport;
  }
  
  public boolean equals(Object object){
    if (object instanceof UseDeclaration) {
      UseDeclaration declaration = (UseDeclaration) object;
      return declaration.getBaseImport().getBaseString().equals(baseImport.getBaseString());
    }
    return false;
  }
  
  public int hashCode(){
    return baseImport.getBaseString().hashCode();
  }

  public Token getUseToken() {
    return useToken;
  }

  public TType getBaseImport() {
    return baseImport;
  }

  public Set<TIden> getImportedFuncs(){
    return funcsToImport;
  }
  
  public String toString(){
    return "USE ~ "+baseImport.getBaseString();
  }
}
