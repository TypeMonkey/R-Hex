package jg.rhex.compile.components.errors;

public abstract class RhexConstructionException extends RuntimeException{
  
  public RhexConstructionException(String mess, String fileName){
    super(mess+" in "+fileName+".rhex");
  }
  
}
