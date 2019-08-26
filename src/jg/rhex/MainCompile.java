package jg.rhex;

import java.io.IOException;

import com.google.common.collect.HashBiMap;

import jg.rhex.common.ArrayType;
import jg.rhex.common.Type;
import jg.rhex.compile.RhexCompiler;
import jg.rhex.runtime.components.java.JavaClass;

public class MainCompile {

  /**
   * Main driver method. 
   * 
   * @param arg - list of .rhex files to compile
   * @throws IOException 
   */
  public static void main(String [] arg) throws Exception {

    //String [] actArgs = {"samplesrcs/Source1.rhex",
    //                     "samplesrcs/Source2.rhex"};
    
    /*
    String [] actArgs = {"samplesrcs/Inheritance.rhex"};
    arg = actArgs;
    
    System.out.println("--------------- R-HEX COMPILER 1.0 ---------------");
    System.out.println("* TARGETS: ");
    for(String f:arg){
      System.out.println(" -> "+f);
    }

    System.out.println("*** COMPILING....");
    RhexCompiler compiler = new RhexCompiler(actArgs);
    compiler.initialize();
    compiler.formSourceFiles();
    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    
    System.out.println("-------VERIFYING--------");
    compiler.verifySources();
    */
    
    Object object = null;
    
    byte l = 10;
    int r = 5000;
    
    object = l + r;
    
    System.out.println(object.getClass());
  }
}
