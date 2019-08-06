package jg.rhex.compile.components.errors;

import jg.rhex.compile.components.structs.TypeParameter;
import jg.rhex.compile.components.tnodes.atoms.TType;
import net.percederberg.grammatica.parser.Token;

public class RepeatedTParamException extends RhexConstructionException{

  public RepeatedTParamException(Token paramName, String fileName) {
    super("Repeated type parameter '"+paramName.getImage()+"' at <ln:"+paramName.getStartLine()+">", fileName);
  }
  
  public RepeatedTParamException(TType redundantType, String fileName){
    super("Repeated class extension '"+redundantType.getBaseString()+"' for tparam at <ln:"+
                    redundantType.getBaseType().get(0).getToken().getStartLine()+">", fileName);
  }
}
