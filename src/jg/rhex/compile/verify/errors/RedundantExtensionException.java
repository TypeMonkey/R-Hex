package jg.rhex.compile.verify.errors;

import jg.rhex.compile.components.errors.RhexConstructionException;
import jg.rhex.compile.components.tnodes.atoms.TType;

public class RedundantExtensionException extends RhexConstructionException{

  public RedundantExtensionException(String className, TType originalType, String fileName) {
    super(className+" has already extended "+originalType.getBaseString()+
        " at <ln:"+originalType.getBaseType().get(0).getToken().getStartLine()+">", fileName);
  }

}
