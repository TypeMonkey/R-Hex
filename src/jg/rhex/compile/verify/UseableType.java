package jg.rhex.compile.verify;

import jg.rhex.compile.components.structs.RClass;

public class UseableType {
  
  private final RClass rClass;
  private final Class<?> javaClass;
  
  public UseableType(RClass rClass){
    this.rClass = rClass;
    this.javaClass = null;
  }
  
  public UseableType(Class<?> javaClass){
    this.javaClass = javaClass;
    this.rClass = null;
  }
  
  public boolean isAJavaClass(){
    return javaClass != null;
  }
  
  public boolean isAnRClass() {
    return rClass != null;
  }

  public RClass getrClass() {
    return rClass;
  }

  public Class<?> getJavaClass() {
    return javaClass;
  }
}
