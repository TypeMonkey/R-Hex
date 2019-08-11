package jg.rhex.compile.verify.errors;

import jg.rhex.common.FunctionSignature;
import jg.rhex.compile.components.errors.RhexConstructionException;
import jg.rhex.compile.components.tnodes.atoms.TFuncCall;

public class UnfoundFunctionException extends RhexConstructionException{

  public UnfoundFunctionException(FunctionSignature signature, TFuncCall call, String fileName) {
    super("Cannot find function of signature '"+signature+"' in the call at <ln: "+call.getFuncName().getToken().getStartLine()+">", fileName);
  }

}
