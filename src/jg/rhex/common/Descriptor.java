package jg.rhex.common;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import jg.rhex.compile.components.expr.GramPracConstants;

public enum Descriptor {
  STATIC,   //static variables, methods
  ABSTRACT, //abstract methods, classes
  FINAL,    //final variables, methods, classes
  SYNCH,   //synchronized variables, methods
  VOLATILE, //volatile variables
  PUBLIC, //public member
  PRIVATE; //private member
  
  /**
   * Returns the Descriptor enum equivalent of a Token's ID
   * @param tokenID - the numerical ID of a Token
   * @return the Descriptor equivalent
   */
  public static Descriptor getEnumEquivalent(int tokenID){
    switch (tokenID) {
    case GramPracConstants.STATIC:
      return STATIC;
    case GramPracConstants.ABSTRACT:
      return ABSTRACT;
    case GramPracConstants.FINAL:
      return FINAL;
    case GramPracConstants.SYNCH:
      return SYNCH;
    case GramPracConstants.VOLATILE:
      return VOLATILE;
    case GramPracConstants.PUBL:
      return PUBLIC;
    case GramPracConstants.PRIV:
      return PRIVATE;
    default:
      return null;
    }
  }
  
  public static Set<Descriptor> translateModifiers(int javaModifiers){
    HashSet<Descriptor> descriptors = new HashSet<>();
    if (Modifier.isStatic(javaModifiers)) {
      descriptors.add(STATIC);
    }
    if (Modifier.isAbstract(javaModifiers)) {
      descriptors.add(ABSTRACT);
    }
    if (Modifier.isFinal(javaModifiers)) {
      descriptors.add(FINAL);
    }
    if (Modifier.isPublic(javaModifiers)) {
      descriptors.add(PUBLIC);
    }
    if (Modifier.isPrivate(javaModifiers)) {
      descriptors.add(PRIVATE);
    }
    if (Modifier.isSynchronized(javaModifiers)) {
      descriptors.add(SYNCH);
    }
    if (Modifier.isVolatile(javaModifiers)) {
      descriptors.add(VOLATILE);
    }
    return descriptors;
  }
}
