package jg.rhex.runtime;

import java.util.ArrayList;
import java.util.List;

import jg.rhex.runtime.components.java.JavaClass;

public class Tester {

  public static void main(String [] args) {
    System.out.println("test");
    
    JavaClass repClass = JavaClass.getJavaClassRep(ArrayList.class);
  }
  
}
