package jg.rhex.compile.verify;

import jg.rhex.common.Type;
import jg.rhex.compile.components.structs.RClass;
import jg.rhex.compile.components.structs.RFile;

public class UseableType {
  
  private final RClass rClass;
  private final RFile rFile;
  
  private final Class<?> javaClass;
  
  private final Type type;
  
  public UseableType(RClass rClass, RFile rhexFile){
    this.rClass = rClass;
    this.rFile = rhexFile;
    this.javaClass = null;
    this.type = new Type(getSimpleName(), getFullName());
  }
  
  public UseableType(Class<?> javaClass){
    this.javaClass = javaClass;
    this.rFile = null;
    this.rClass = null;
    this.type = new Type(getSimpleName(), getFullName());
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
      return javaClass.getSimpleName();
    }
    return rClass.getName().getImage();
  }
  
  public String getFullName(){
    if (isAJavaClass()) {
      return javaClass.getName();
    }
    else {
      String pack = rFile.getPackDesignation();
      return pack+"."+rFile.getFileName()+"."+rClass.getName().getImage();
    }
  }
  
  public Type getTypeInformation(){
    return type;
  }
}
