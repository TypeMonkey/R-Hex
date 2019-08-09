package jg.rhex;

import java.io.IOException;

import com.google.common.collect.HashBiMap;

import jg.rhex.common.ArrayType;
import jg.rhex.common.Type;
import jg.rhex.compile.RhexCompiler;

public class MainCompile {

  /**
   * Main driver method. 
   * 
   * @param arg - list of .rhex files to compile
   * @throws IOException 
   */
  public static void main(String [] arg) throws IOException {

    String [] actArgs = {"samplesrcs/Source1.rhex",
                         "samplesrcs/Source2.rhex"};
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
    
    for (String string : compiler.getRhexTypes().keySet()) {
      System.out.println("  NAME? "+string);
    }
    
    System.out.println("-------VERIFYING--------");
    compiler.verifySources();
    
  }
}
