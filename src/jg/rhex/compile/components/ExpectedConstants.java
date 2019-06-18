package jg.rhex.compile.components;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class ExpectedConstants {
  
  public static final Set<Integer> VISIBILITY;
    
  public static final Set<Integer> CLASS_DESC;
  
  public static final Set<Integer> VAR_FUNC_DESC;
    
  static{
    VISIBILITY = new HashSet<>(Arrays.asList(GramPracConstants.PRIV, 
        GramPracConstants.PUBL,
        GramPracConstants.PRO));

    CLASS_DESC = new HashSet<>(Arrays.asList(GramPracConstants.ABSTRACT, 
        GramPracConstants.FINAL));

    VAR_FUNC_DESC = new HashSet<>(Arrays.asList(GramPracConstants.STATIC, 
        GramPracConstants.VOLATILE,
        GramPracConstants.FINAL));
  }
}
