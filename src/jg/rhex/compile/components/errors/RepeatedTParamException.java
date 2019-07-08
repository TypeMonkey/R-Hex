package jg.rhex.compile.components.errors;

import jg.rhex.compile.components.structs.TypeParameter;
import jg.rhex.compile.components.tnodes.atoms.TType;
import net.percederberg.grammatica.parser.Token;

public class RepeatedTParamException extends RhexConstructionException{

  public RepeatedTParamException(Token paramName) {
    super("Repeated type parameter '"+paramName.getImage()+"' at <ln:"+paramName.getStartLine()+">");
  }
  
  public RepeatedTParamException(Token funcName, TypeParameter target){
    super("Repeated function constraint '"+funcName+"' for tparam '"+
              target.getIdentifier().getImage()+"' at <ln:"+funcName.getStartLine()+">");
  }
  
  public RepeatedTParamException(TType redundantType, TypeParameter target){
    super("Repeated class extension '"+redundantType.getBaseString()+"' for tparam '"+
             target.getIdentifier().getImage()+"' at <ln:"+
                    redundantType.getBaseType().get(0).getToken().getStartLine()+">");
  }
}
