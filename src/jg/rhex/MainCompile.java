package jg.rhex;

import java.io.IOException;

import com.google.common.collect.HashBiMap;

import jg.rhex.compile.RhexCompiler;

public class MainCompile {

  /**
   * Main driver method. 
   * 
   * @param arg - list of .rhex files to compile
   * @throws IOException 
   */
  public static void main(String [] arg) throws IOException {
    
    System.out.println("--------------- R-HEX COMPILER 1.0 ---------------");
    System.out.println("* TARGETS: ");
    for(String f:arg){
      System.out.println(" -> "+f);
    }

    System.out.println("*** COMPILING....");
    RhexCompiler compiler = new RhexCompiler();
    compiler.initialize();
    
    
  }
  
}
