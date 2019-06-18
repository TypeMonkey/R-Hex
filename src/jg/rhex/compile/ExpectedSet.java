package jg.rhex.compile;

import java.util.Arrays;
import java.util.HashSet;

import jg.rhex.compile.components.errors.FormationException;
import net.percederberg.grammatica.parser.Token;

public class ExpectedSet extends HashSet<Integer>{
  
  /**
   * Constructs an ExpectedSet with initial values
   * @param initialAmnt - the Integers to add to this set
   */
  public ExpectedSet(Integer ... initialAmnt){
    super(Arrays.asList(initialAmnt));
  }
  
  
  /**
   * Check whether a Token's ID is in this Set's values. If it's not, it throws a FormationException
   * @param toCheck - the Token to check
   * @param errorContext - the String message to include with the error if the Token's ID isn't in this set
   * @return true- if the Token's ID is in this set
   */
  public boolean noContainsThrow(Token toCheck, String errorContext){
    if (contains(toCheck.getId())) {
      return true;
    }
    else {
      if (errorContext == null || errorContext.isEmpty()) {
        throw FormationException.createException(toCheck, this);
      }
      throw FormationException.createException(errorContext, toCheck, this);
    }
  }
  
  /**
   * Clears this set of all elements, and replaces is it with new values
   * @param args - the new values to add to the set
   */
  public void replace(int ... args){
    this.clear();
    for(int x: args){
      this.add(x);
    }
  }
  
  
}
