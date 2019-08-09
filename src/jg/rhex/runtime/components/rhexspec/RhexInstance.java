package jg.rhex.runtime.components.rhexspec;

import java.util.Map;

import jg.rhex.runtime.components.GenClass;
import jg.rhex.runtime.components.Instance;
import jg.rhex.runtime.components.Variable;

public class RhexInstance extends Instance{

  private RhexInstance(GenClass backingType, Map<String, Variable> variableScope) {
    super(backingType, variableScope);
  }

  public RhexInstance createInstance(RhexClass backingType){
    return new RhexInstance(backingType, backingType.cloneVariableMap());
  }
}
