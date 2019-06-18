package jg.rhex.compile;

public class MainCompile {

  /**
   * Main driver method. 
   * 
   * @param arg - list of .rhex files to compile
   */
  public static void main(String [] arg){
    System.out.println("--------------- R-HEX COMPILER 1.0 ---------------");
    System.out.println("* TARGETS: ");
    for(String f:arg){
      System.out.println(" -> "+f);
    }
    
    System.out.println("*** COMPILING....");
    RhexCompiler compiler = new RhexCompiler();
    compiler.formSourceFiles(arg);
  }
  
}
