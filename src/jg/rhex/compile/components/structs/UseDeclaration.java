package jg.rhex.compile.components.structs;

import java.util.Objects;

import net.percederberg.grammatica.parser.Token;

public class UseDeclaration {
  
  private Token useToken;
  private String filePath;
  
  public UseDeclaration(Token useToken, String filePath){
    this.useToken = useToken;
    this.filePath = filePath;
  }
  
  public boolean equals(Object declaration){
    if (declaration instanceof UseDeclaration) {
      UseDeclaration decl = (UseDeclaration) declaration;
      return filePath.equals(decl.filePath);
    }
    return false;
  }
  
  public int hashCode(){
    return Objects.hash(filePath);
  }

  public Token getUseToken() {
    return useToken;
  }

  public String getFilePath() {
    return filePath;
  }
}
