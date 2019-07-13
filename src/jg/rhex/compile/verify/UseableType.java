package jg.rhex.compile.verify;

import jg.rhex.compile.components.structs.RClass;
import jg.rhex.compile.components.structs.RFile;

public class UseableType {
  
  private final RClass rClass;
  private final RFile rFile;
  
  private final Class<?> javaClass;
  
  public UseableType(RClass rClass, RFile rhexFile){
    this.rClass = rClass;
    this.rFile = rhexFile;
    this.javaClass = null;
  }
  
  public UseableType(Class<?> javaClass){
    this.javaClass = javaClass;
    this.rFile = null;
    this.rClass = null;
  }
  
  public boolean isAJavaClass(){
    return javaClass != null;
  }
  
  public boolean isAnRClass() {
    return rClass != null;
  }

  public RClass getRClass() {
    return rClass;
  }

  public Class<?> getJavaClass() {
    return javaClass;
  }
  
  public String getSimpleName(){
    if (isAJavaClass()) {
      return javaClass.getName();
    }
    return rClass.getName().getImage();
  }
  
  public String getFullName(){
    if (isAJavaClass()) {
      return javaClass.getName();
    }
    else {
      String pack = rFile.getPackDesignation();
      return pack+"."+rClass.getName().getImage();
    }
  }
}
