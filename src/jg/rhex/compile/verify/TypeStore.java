package jg.rhex.compile.verify;

import java.util.HashMap;
import java.util.Map;

import jg.rhex.common.Type;
import jg.rhex.runtime.components.GenClass;
import jg.rhex.runtime.components.java.JavaClass;

public class TypeStore {

   private Map<Type, GenClass> allTypes; 
  
   public TypeStore(Map<Type, GenClass> rhexTypes){
     allTypes = new HashMap<>();
     allTypes.putAll(rhexTypes);
   }
  
   public GenClass lookup(Type type){
     GenClass ret = allTypes.get(type);
     if (ret == null) {
       ret = JavaClass.getJavaClassRep(type.getFullName());
       allTypes.put(type, ret);
     }
     
     return ret;
   }
}
