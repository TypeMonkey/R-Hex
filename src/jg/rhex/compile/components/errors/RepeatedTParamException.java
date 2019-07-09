package jg.rhex.compile.components.errors;

import jg.rhex.compile.components.structs.TypeParameter;
import jg.rhex.compile.components.tnodes.atoms.TType;
import net.percederberg.grammatica.parser.Token;

public class RepeatedTParamException extends RhexConstructionException{

  public RepeatedTParamException(Token paramName, String fileName) {
    super("Repeated type parameter '"+paramName.getImage()+"' at <ln:"+paramName.getStartLine()+">", fileName);
  }
  
  public RepeatedTParamException(Token funcName, TypeParameter target, String fileName){
    super("Repeated function constraint '"+funcName+"' for tparam '"+
              target.getIdentifier().getImage()+"' at <ln:"+funcName.getStartLine()+">", fileName);
  }
  
  public RepeatedTParamException(TType redundantType, TypeParameter target, String fileName){
    super("Repeated class extension '"+redundantType.getBaseString()+"' for tparam '"+
             target.getIdentifier().getImage()+"' at <ln:"+
                    redundantType.getBaseType().get(0).getToken().getStartLine()+">", fileName);
  }
}
