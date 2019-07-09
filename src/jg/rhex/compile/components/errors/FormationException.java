package jg.rhex.compile.components.errors;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;

import jg.rhex.compile.components.expr.GramPracConstants;
import net.percederberg.grammatica.parser.Token;

/**
 * Represents an unexpected sequence of Tokens during the
 * formation phase of source files
 * 
 * @author Jose
 *
 */
public class FormationException  extends RhexConstructionException{
  
  private static final HashMap<Integer, String> tokenIDMap;
  
  static{
    tokenIDMap = new HashMap<>();
    tokenIDMap.put(-1, "NOTHING");
    
    Field[] fields = GramPracConstants.class.getFields();
    
    for(Field field : fields){
      try {
        tokenIDMap.put(field.getInt(null), field.getName());
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }
  
  public FormationException(String context, Token encountered, String expecteds, String fileName){
    super("In forming '"+context+"' : Unexpted token '"+encountered.getImage()+"' <"+encountered.getName()+"> ! Expected "+expecteds, fileName);
  }
  
  public FormationException(Token encountered, String expecteds, String fileName){
    super("Unexpected token '"+encountered.getImage()+"' <"+encountered.getName()+"> ! Expected "+expecteds, fileName);
  }
  
  protected static String getStringDescribers(Set<Integer> expecteds){
    String desc = "";
    
    for(Integer integer : expecteds){
      desc += tokenIDMap.get(integer)+"  ";
    }
    
    return desc;
  }
  
  public static FormationException createException(Token encountered, Set<Integer> expectedTokens, String fileName){
    return new FormationException(encountered, getStringDescribers(expectedTokens), fileName);
  }
  
  public static FormationException createException(String context, Token encountered, Set<Integer> expectedTokens, String fileName){
    return new FormationException(context, encountered, getStringDescribers(expectedTokens), fileName);
  }
    
}
