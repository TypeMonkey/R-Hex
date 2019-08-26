package jg.rhex.compile.verify;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jg.rhex.common.Descriptor;
import jg.rhex.common.FunctionIdentity;
import jg.rhex.common.FunctionSignature;
import jg.rhex.common.TypeUtils;
import jg.rhex.runtime.components.Function;
import jg.rhex.runtime.components.GenClass;
import jg.rhex.runtime.components.rhexspec.RhexClass;

public class LineageChecker {

  private Set<GenClass> visitedClasses;
  
  public LineageChecker(){
    visitedClasses = new HashSet<>();
  }
  
  public void checkLineage(RhexClass target){
    System.out.println("---> checking "+target.getTypeInfo());
    if (!visitedClasses.contains(target)) {
      System.out.println("   -> Type NOT visited. Checking....");
      
      GenClass parent = target.getParent();
      if (parent.decendsFrom(target)) {
        if (target.isInterface()) {
          System.out.println("parent: "+parent+" | "+parent.getInterfaces());
          throw new RuntimeException(target.getTypeInfo()+" is both a decendant and ancestor of one of its children "+target.getParent());
        }
        else {
          throw new RuntimeException(target.getTypeInfo()+" is both a decendant and ancestor of "+parent.getTypeInfo());
        }
      }
      else if (parent.getDescriptors().contains(Descriptor.FINAL)) {
        throw new RuntimeException(target.getTypeInfo()+" cannot extend the final class "+parent.getTypeInfo());
      }
      else {
        System.out.println(">>>>> The parent "+parent+" isn't a decendant of "+target);
      }
      
      Map<FunctionIdentity, GenClass> unimplementedFuncs = getAllRequiredAbstractFunctionSignatures(target);
      if (!unimplementedFuncs.isEmpty() && 
          !target.getDescriptors().contains(Descriptor.ABSTRACT) &&
          !target.isInterface()) {
        //throw error
        for(Entry<FunctionIdentity, GenClass> unimp : unimplementedFuncs.entrySet()){
          System.err.println(" FUNC: "+unimp.getKey()+" , from "+unimp.getValue().getTypeInfo());
        }
        throw new RuntimeException("Type Error: "+target.getTypeInfo()+" does not implement the following: "+unimplementedFuncs);
      }
      else {
        System.out.println("CLASS: "+target.getTypeInfo()+" follows inheritance");
      }
      
      visitedClasses.add(target);
    }
    else {
      System.out.println("   -> Type already visited. No need to check....");
    }
  }
  
  /**
   * Gets all the unimplemented, abstract functions that the target class has
   * @param target
   * @return
   */
  private Map<FunctionIdentity, GenClass> getAllRequiredAbstractFunctionSignatures(GenClass target){
    System.out.println("     *GATHER: "+target.getTypeInfo());
    HashMap<FunctionIdentity, GenClass> reqFunctions = new HashMap<>();
    for (Function function : target.getFunctionMap().values()) {
      if (function.getDescriptors().contains(Descriptor.ABSTRACT)) {
        reqFunctions.put(function.getIdentity(), target);
      }
    }
    
    if (target.getParent() != null) {
      //all func sigs are guarenteed to be abstract
      System.out.println("       *RECURSE TO! "+target.getParent().getTypeInfo());
      Map<FunctionIdentity, GenClass> parentAbstracts = getAllRequiredAbstractFunctionSignatures(target.getParent());
      System.out.println("       * RETURNED FOR TARGET "+target.getTypeInfo()+" FROM: "+target.getParent().getTypeInfo());
      for(FunctionIdentity signature : parentAbstracts.keySet()){
        //derived class' version of that function, if any
        Function derFunc = target.retrieveFunction(signature);
        System.out.println("         TO PUT? "+signature+" | from: "+parentAbstracts.get(signature));
        if (derFunc == null) {
          reqFunctions.put(signature, parentAbstracts.get(signature));
        }
        else if (derFunc.getDescriptors().contains(Descriptor.ABSTRACT)) {
          reqFunctions.put(signature, target.getParent());
        }
      }
      System.out.println("         * REQS: "+reqFunctions);
    }
    
    for(GenClass inter : target.getInterfaces()){
      System.out.println("       *RECURSE TO INTER! "+inter.getTypeInfo());
      Map<FunctionIdentity, GenClass> interAbstracts = getAllRequiredAbstractFunctionSignatures(inter);
      System.out.println("       * RETURNED FOR TARGET "+target.getTypeInfo()+" FROM: "+inter.getTypeInfo());
      reqFunctions.putAll(interAbstracts);
    }
    System.out.println("         * REQS: "+reqFunctions);
    
    visitedClasses.add(target);
    
    return reqFunctions;
  }
  
  /*
   *       
      GenClass parent = rhexClass.getParent();
      if (parent.decendsFrom(rhexClass)) {
        throw new RuntimeException(rhexClass.getTypeInfo()+" is both a decendant and ancestor of "+parent.getTypeInfo());
      }
      
      HashSet<FunctionSignature> methods = new HashSet<>(rhexClass.getFunctionMap().keySet());
      
      if (parent.getDescriptors().contains(Descriptor.ABSTRACT)) {
        HashSet<FunctionSignature> abstractMethods = new HashSet<>();
        
        for(Function function : parent.getFunctionMap().values()){
          if (function.getDescriptors().contains(Descriptor.ABSTRACT)) {
            abstractMethods.add(function.getSignature());
          }
        }
        
        HashSet<FunctionSignature> intesection = new HashSet<>(abstractMethods);
        intesection.retainAll(methods);
        
        if (abstractMethods.containsAll(intesection) && intesection.containsAll(abstractMethods)) {
          continue;
        }
        else {
          throw new RuntimeException("the class "+rhexClass.getTypeInfo()+" does not implement all abstract methods of class "+parent.getTypeInfo());
        }
      } 
      
      //now check all interfaces
      for(GenClass inter : rhexClass.getInterfaces()){
        if (inter.decendsFrom(rhexClass)) {
          throw new RuntimeException(rhexClass.getTypeInfo()+" is both a decendant and ancestor of "+inter.getTypeInfo());
        }
        HashSet<FunctionSignature> abstractMethods = new HashSet<>(inter.getFunctionMap().keySet());
        
        HashSet<FunctionSignature> intesection = new HashSet<>(abstractMethods);
        intesection.retainAll(methods);
        
        if (abstractMethods.containsAll(intesection) && intesection.containsAll(abstractMethods)) {
          continue;
        }
        else {
          System.out.println("METHS: "+rhexClass.getFunctionMap().keySet());
          System.out.println("ABS: "+abstractMethods);
          System.out.println("INTER: "+intesection);
          throw new RuntimeException("the class "+rhexClass.getTypeInfo()+" does not implement all abstract methods of interface "+inter.getTypeInfo());
        }
      }
      
    
   */
}
