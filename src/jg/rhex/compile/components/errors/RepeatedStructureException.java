package jg.rhex.compile.components.errors;

import net.percederberg.grammatica.parser.Token;

public class RepeatedStructureException extends RhexConstructionException{

  public RepeatedStructureException(Token repeatedStrucName, String strucType) {
    super("Repeated "+strucType+" called '"+repeatedStrucName+"' at <ln:"+repeatedStrucName.getStartLine()+">");
  }

}
