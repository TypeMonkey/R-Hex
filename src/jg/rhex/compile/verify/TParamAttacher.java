package jg.rhex.compile.verify;

import jg.rhex.common.Type;
import jg.rhex.compile.components.structs.RFunc;
import jg.rhex.compile.components.structs.RStatement;
import jg.rhex.compile.components.structs.RVariable;
import jg.rhex.compile.components.structs.TypeParameter;
import jg.rhex.compile.components.structs.RStatement.RStateDescriptor;
import jg.rhex.compile.components.tnodes.TCast;
import jg.rhex.compile.components.tnodes.TExpr;
import jg.rhex.compile.components.tnodes.TFuncCall;
import jg.rhex.compile.components.tnodes.TNode;
import jg.rhex.compile.components.tnodes.atoms.TType;
import jg.rhex.compile.verify.errors.UnfoundTypeException;

public class TParamAttacher {
  //private void attachTypes() {
    //first attach tparams on fil functions
 // for(RFunc rFunc : rhexFile.getFunctions()){
      /*
       * Constructs we need to scan:
       * 1.) Variable declarations (including parameters)
       * 2.) Type casts (by the 'as' operator)
       * 3.) Function calls that may have type parameters
       */
      
      /*
      for(RStatement statement : rFunc.getBody().getStatements()) {
        if (statement.getDescriptor() == RStateDescriptor.VAR_DEC) {
          //this is a variable declaration. Scan it.
          RVariable variable = (RVariable) statement;
          TypeParameter potentialTP = rFunc.getTypeParameter(variable.getProvidedType().getBaseString());
          if (potentialTP != null) {
            variable.getProvidedType().associateTypeParameter(potentialTP);
          }
          else {
            //we are guaranteed during the parsing stage that parameters cannot have their types inferred
            TType provided = variable.getProvidedType();

            //we will resove inferred types later
            if (!provided.isInferredType()) {
              if (provided.getBaseString().contains(".")) {
                //then the provided is binary. 
                String [] split = provided.getBaseString().split("\\.");
                Type actual = new Type(split[split.length - 1], provided.getBaseString());

                if (!typeStore.confirmExistanceOfType(actual)) {
                  throw new UnfoundTypeException(provided.getBaseType().get(0).getToken(), actual.getFullName());
                }
                else {
                  provided.attachType(actual);
                }
              }
            }
          }
        }
        else {
          //this is not a variable declaration. Check its contents for function calls or casts
          //NOTE: Watch out for statement blocks.
          if (statement.getDescriptor() != RStateDescriptor.BLOCK) {
            //non block statements
            TNode actualExpression = statement.getStatement();
          }
        }
      }
    }
    
  }
  */
  
  /*
  private void scanStatements(RStatement statement, RFunc housingFunction) {
    
  }
  
  private void scanExpressions(TNode expr, RFunc housingFunction) {
    if (expr instanceof TExpr) {
      TExpr actExpr = (TExpr) expr; 
      for(TNode content : actExpr.getActValue()) {
        scanExpressions(content, housingFunction);
      }
    }
    else if (expr instanceof TFuncCall) {
      
    }
    else if (expr instanceof TCast) {
      
    }
  }
  */
}
