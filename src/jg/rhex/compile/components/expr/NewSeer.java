package jg.rhex.compile.components.expr;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

import net.percederberg.grammatica.parser.Node;
import net.percederberg.grammatica.parser.ParseException;
import net.percederberg.grammatica.parser.Production;
import net.percederberg.grammatica.parser.Token;
import jg.rhex.compile.components.tnodes.atoms.TChar;
import jg.rhex.compile.components.tnodes.atoms.TString;
import jg.rhex.compile.components.tnodes.TNode;
import jg.rhex.compile.components.tnodes.TOp;
import jg.rhex.compile.components.tnodes.atoms.TArrayAcc;
import jg.rhex.compile.components.tnodes.atoms.TBool;
import jg.rhex.compile.components.tnodes.atoms.TCParen;
import jg.rhex.compile.components.tnodes.atoms.TCast;
import jg.rhex.compile.components.tnodes.atoms.TComma;
import jg.rhex.compile.components.tnodes.atoms.TDouble;
import jg.rhex.compile.components.tnodes.atoms.TExpr;
import jg.rhex.compile.components.tnodes.atoms.TFloat;
import jg.rhex.compile.components.tnodes.atoms.TFuncCall;
import jg.rhex.compile.components.tnodes.atoms.TIden;
import jg.rhex.compile.components.tnodes.atoms.TInt;
import jg.rhex.compile.components.tnodes.atoms.TLong;
import jg.rhex.compile.components.tnodes.atoms.TMemberInvoke;
import jg.rhex.compile.components.tnodes.atoms.TNew;
import jg.rhex.compile.components.tnodes.atoms.TNull;
import jg.rhex.compile.components.tnodes.atoms.TNumber;
import jg.rhex.compile.components.tnodes.atoms.TOParen;
import jg.rhex.compile.components.tnodes.atoms.TType;
import jg.rhex.compile.components.tnodes.atoms.TUnary;

public class NewSeer extends GramPracAnalyzer{

  protected Stack<ArrayDeque<TNode>> stack;
  protected Stack<TNode> actualNodes;
  
  protected NewSeer(){
    stack = new Stack<>();
    actualNodes = new Stack<>();
  }
  
 //------UNIT (Atomic) visitations - all number types, string and char literals and variable names
  
  protected Node exitInteger(Token node)  throws ParseException{
    //System.out.println("INT: "+node.getImage()+" | children: "+getChildValues(node));
    //node.addValue(Double.parseDouble(node.getImage().toString()));
    actualNodes.add(new TInt(node));
    return node;
  } 
  
  protected Node exitDouble(Token node)  throws ParseException{
    //System.out.println("DOUBLE: "+node.getImage()+" | children: "+getChildValues(node));
    //node.addValue(Double.parseDouble(node.getImage().toString()));
    actualNodes.add(new TDouble(node));
    return node;
  } 
  
  protected Node exitFloat(Token node)  throws ParseException{
    //System.out.println("FLOAT: "+node.getImage()+" | children: "+getChildValues(node));
    //node.addValue(Double.parseDouble(node.getImage().toString()));
    String rawFloat = node.getImage();
    
    //We cutout the last character because explicitly declared floats numerals have an
    //upper or lower case "f" at the end
    actualNodes.add(new TFloat(node));
    return node;
  } 
  
  protected Node exitLong(Token node)  throws ParseException{
    //System.out.println("LONG: "+node.getImage()+" | children: "+getChildValues(node));
    //node.addValue(Double.parseDouble(node.getImage().toString()));
    String rawLong = node.getImage();
    
    //We cutout the last character because explicitly declared Long numerals have an
    //upper or lower case "l" at the end
    actualNodes.add(new TLong(node));
    return node;
  } 
  
  protected Node exitComma(Token node)  throws ParseException{
    //System.out.println("LONG: "+node.getImage()+" | children: "+getChildValues(node));
    //node.addValue(Double.parseDouble(node.getImage().toString()));
    actualNodes.add(new TComma());
    return node;
  } 
  
  protected Node exitTrue(Token node) throws ParseException{
    actualNodes.add(new TBool(true));
    return node;
  }
  
  protected Node exitFalse(Token node) throws ParseException{  
    actualNodes.add(new TBool(false));
    return node;
  }
  
  protected Node exitString(Token node){
    actualNodes.add(new TString(node));
    return node;
  }
  
  protected Node exitChar(Token node){
    actualNodes.add(new TChar(node));
    return node;
  }
  
  protected Node exitNull(Token node){
    actualNodes.add(new TNull(node));
    return node;
  }
  
  protected Node exitOpParen(Token node) throws ParseException {
    //node.addValue("(");
    actualNodes.add(new TOParen(node));
    return node;
  }

  protected Node exitClParen(Token node) throws ParseException {
    //node.addValue(")");
    actualNodes.add(new TCParen(node));
    return node;
  }
  
  protected Node exitNew(Token node) {
    System.out.println("------EXITED NEW!!!!");
    actualNodes.add(new TOp(node));
    return node;
  }
  
  protected Node exitName(Token node) throws ParseException{
    System.out.println("NAME: "+node.getImage()+" | children: "+getChildValues(node));
    node.addValue(node.getImage());
    actualNodes.add(new TIden(node));
    return node;
  }
  //------UNIT (Atomic) visitations - all number types, string and char literals and variable names - DONE
  
  //-----OPERATOR visitations
  
  protected Node exitPlus(Token token) throws ParseException{
    //System.out.println("  > PLUS: "+token.getAllValues() +" | OPERANDS: "+currentStack);
    
    //token.addValue("+");
    actualNodes.push(new TOp(token));
    return (token);   
  }
  
  protected Node exitMinus(Token token) throws ParseException{
    //System.out.println("  > MINUS: "+token.getAllValues() +" | OPERANDS: "+currentStack);
    
    //token.addValue("+");
    actualNodes.push(new TOp(token));
    return (token);   
  }
  
  protected Node exitMult(Token token) throws ParseException{
    //System.out.println("  > MULT: "+token.getAllValues() +" | OPERANDS: "+currentStack);
    
    //token.addValue("+");
    actualNodes.push(new TOp(token));
    return (token);   
  }
  
  protected Node exitDiv(Token token) throws ParseException{
    //System.out.println("  > DIV: "+token.getAllValues() +" | OPERANDS: "+currentStack);
    
    //token.addValue("+");
    actualNodes.push(new TOp(token));
    return (token);   
  }
  
  protected Node exitMod(Token token) throws ParseException{
    //System.out.println("  > MOD: "+token.getAllValues() +" | OPERANDS: "+currentStack);
    
    //token.addValue("+");
    actualNodes.push(new TOp(token));
    return (token);   
  }
  
  protected Node exitBang(Token token) throws ParseException{
    //System.out.println("  > MOD: "+token.getAllValues() +" | OPERANDS: "+currentStack);
    
    //token.addValue("+");
    actualNodes.push(new TOp(token));
    return (token);   
  }
  
  protected Node exitEqual(Token token) throws ParseException{
    actualNodes.push(new TOp(token));
    return token;
  }
  
  protected Node exitEqMult(Token token) throws ParseException{
    actualNodes.push(new TOp(token));
    return token;
  }

  protected Node exitEqAdd(Token token) throws ParseException{
    actualNodes.push(new TOp(token));
    return token;
  }

  protected Node exitEqDiv(Token token) throws ParseException{
    actualNodes.push(new TOp(token));
    return token;
  }

  protected Node exitEqMin(Token token) throws ParseException{
    actualNodes.push(new TOp(token));
    return token;
  }

  protected Node exitEqMod(Token token) throws ParseException{
    actualNodes.push(new TOp(token));
    return token;
  }
  
  protected Node exitGreat(Token token) throws ParseException{
    actualNodes.push(new TOp(token));
    return token;
  }
  
  protected Node exitLess(Token token) throws ParseException{
    actualNodes.push(new TOp(token));
    return token;
  }
  
  protected Node exitLsEq(Token token) throws ParseException{
    actualNodes.push(new TOp(token));
    return token;
  }
  
  protected Node exitGrEq(Token token) throws ParseException{
    actualNodes.push(new TOp(token));
    return token;
  }
  
  protected Node exitNotEq(Token token) throws ParseException{
    actualNodes.push(new TOp(token));
    return token;
  }
  
  protected Node exitEqEq(Token token) throws ParseException{
    actualNodes.push(new TOp(token));
    return token;
  }
  
  protected Node exitBoolAnd(Token token) throws ParseException{
    actualNodes.push(new TOp(token));
    return token;
  }
  
  protected Node exitBoolOr(Token token) throws ParseException{
    actualNodes.push(new TOp(token));
    return token;
  }

  //----OPERATOR visitations DONE
  
  //EXPR visitations 
  
  protected void enterUnary(Production production){
    System.out.println("---ENTER UNARY");
    setEntrance();
  }
  
  protected void enterAssgn(Production production){
    System.out.println("----Enter Assign");
    setEntrance();
  }
  
  protected void enterExpr(Production production){
    System.out.println("----enter expr");
    setEntrance();
  }
  
  protected void enterParenExpr(Production production){
    System.out.println("----enter paren expr");
    setEntrance();
  }
  
  protected void enterFuncCall(Production production){
    System.out.println("----enter func call");
    setEntrance();
  }
  
  protected void enterArrayAcc(Production production){
    System.out.println("----enter ARRAY ACC call");
    setEntrance();
  }
  
  protected void enterInvoke(Production production){
    System.out.println("----enter INVOKE");
    setEntrance();
  }
  
  protected void enterTypeName(Production production){
    System.out.println("----> ENTER TYPE: "+actualNodes);
    setEntrance();   
  }
  
  protected void enterGeneric(Production production){
    System.out.println("---> ENTER GENERIC!  "+actualNodes);
    setEntrance();
  }

  protected void enterCast(Production production){
    System.out.println("----> ENTER CAST "+actualNodes);
    setEntrance();
  }
  
  protected void enterConstructor(Production production){
    System.out.println("----> ENTER CONSTRUCTOR  "+actualNodes);
    setEntrance();
  }
  
  protected Node exitConstructor(Production production) {
    ArrayDeque<TNode> latest = exitEntrance();
    System.out.println("-----EXIT CONSTRUCTOR  "+latest+" | "+latest.peek());
    
    ArrayList<TIden> binaryName = new ArrayList<>();
    //remove the "new" operator
    latest.poll();
    
    while (!(latest.peek() instanceof TFuncCall)) {
      binaryName.add((TIden) latest.poll());
    }
    
    TFuncCall funcCall = (TFuncCall) latest.poll();
    binaryName.add(funcCall.getFuncName());
    System.out.println("CONS: "+binaryName);
    
    TNew constructorCall = new TNew(binaryName, funcCall);
    actualNodes.add(constructorCall);
    
    return production;
  }
  
  protected Node exitArrayTypeNotation(Production production) {
    System.out.println("-----> EXIT ARR TYPE NOTA: "+actualNodes);
    if (actualNodes.peek() instanceof TInt) {
     TInt currentCount = (TInt) actualNodes.pop();
     
     actualNodes.push(new TInt(currentCount.getNumber()+1));
    }
    else {
      TInt currentCount = new TInt(1);
      actualNodes.push(currentCount);
    }
    return production;
  }
  
  protected Node exitUnary(Production production){
    ArrayDeque<TNode> latest = exitEntrance();
    System.out.println("----EXIT UNARY "+latest);
    
    TNode potentialOp = latest.poll();
    
    if (potentialOp instanceof TOp) {
      TOp op = (TOp) potentialOp;
      if (latest.size() == 1) {
        actualNodes.push(new TUnary<>(latest.poll(), op));
      }
      else {
        TExpr expr = new TExpr(new ArrayList<>(latest));
        actualNodes.push(new TUnary<>(expr, op));
      }
      
      System.out.println("----POST: Top");
    }
    else {
      actualNodes.push(potentialOp);
      for(TNode node : latest){
        actualNodes.push(node);
      }
      
      System.out.println("---POST: "+actualNodes+" | BUT: "+latest);
    }
    
    return production;
  }

  protected Node exitAssgn(Production production){
    System.out.println("-----EXIT ASSGN");
    
    ArrayDeque<TNode> latest = exitEntrance();
    for(TNode node : latest){
      actualNodes.push(node);
    }
    return production;
  }

  protected Node exitExpr(Production production){
    System.out.println("----exit expr");
  
    ArrayDeque<TNode> latest = exitEntrance();
    
    for(TNode node : latest){
      actualNodes.push(node);
    }
    //actualNodes.push(new TExpr(new ArrayList<>(latest))); 
    return production;
  }

  protected Node exitParenExpr(Production production){
    System.out.println("----exit paren expr");
  
    ArrayDeque<TNode> latest = exitEntrance();
    
    System.out.println("    ----***> "+latest);
  
    actualNodes.push(new TExpr(new ArrayList<>(latest))); 
    return production;
  }

  protected Node exitFuncCall(Production production){    
    ArrayDeque<TNode> latest = exitEntrance();
    System.out.println("----exit func call    "+latest);

    TFuncCall funcCall = new TFuncCall((TIden) latest.pollFirst());
    
    while ( !(latest.peek() instanceof TOParen) ) {
      TNode current = latest.pollFirst();
      if (current instanceof TComma) {
        continue;
      }
      
      funcCall.addGenericType((TType) current);
    }

    //these two polls remove the ending and closing parenthesis of a function invocations
    //Ex: func ( ) <-- removes those outer parenthesis
    latest.pollFirst();
    latest.pollLast();  
    
    while (!latest.isEmpty()) {
      TNode current = latest.pollFirst();
      if (current instanceof TComma) {
        continue;
      }
      
      funcCall.addArg(current);
    }

    actualNodes.push(funcCall); 

    return production;
  }

  protected Node exitArrayAcc(Production production){
    ArrayDeque<TNode> latest = exitEntrance();
    
    TNode actualIndex = null;
    if (latest.size() == 1) {
      actualIndex = latest.pop();
    }
    else {
      actualIndex = new TExpr(new ArrayList<TNode>(latest));
    }
    
    System.out.println("----> array exit: "+latest);
    
    TNode previous = actualNodes.pop();
    if (previous instanceof TArrayAcc) {
      TArrayAcc acc = (TArrayAcc) previous;
      acc.addIndexNode(actualIndex);
    }
    else {
      TNode node = previous;
      TArrayAcc arrayAcc = new TArrayAcc(node);
      arrayAcc.addIndexNode(actualIndex);   
      
      previous = arrayAcc;
    }
    
    actualNodes.push(previous);
    
    return production;
  }

  protected Node exitInvoke(Production production){
    //So, given the invocation sequence: x1.x2.x3.x4.x5 
    //(where each xi is either a function call or variable)
    //  "latest" contains x2 , x3 , x4 , x5
    // x1 has been parsed already. We need to pop it from the main node stack
    // and push it back in with x1 x2 x3 x4 x5 as a single sequence
    // of dot invocations
    ArrayDeque<TNode> latest = exitEntrance();
    latest.addFirst(actualNodes.pop()); //pop x1 off the main node stack
    
    TMemberInvoke invoke = new TMemberInvoke();
    invoke.setSequence(new ArrayList<>(latest));
    
    actualNodes.push(invoke);
    
    return production;
  }

  protected Node exitGeneric(Production production){
    ArrayDeque<TNode> latest = exitEntrance();
    System.out.println("---> EXIIT GENERIC:  "+latest);
    
    //remove opening '!(' and closing ')' as with generics
    latest.removeFirst();
    latest.removeFirst();
    latest.removeLast();
    
    for(TNode cur: latest){
      actualNodes.push(cur);
    }
    
    System.out.println("-----> GIVEN: "+latest+"  ||  "+actualNodes);
    
    return production;
  }

  protected Production exitTypeName(Production production){
    ArrayDeque<TNode> latest = exitEntrance();
    System.out.println("-----> EXIT TYPE: "+latest+" | "+actualNodes);
   
    
    ArrayList<TIden> base = new ArrayList<>();
    int arrayDimension = 0;
    
    while (!latest.isEmpty()) {
      TNode current = latest.pollFirst();
      System.out.println("~ ADD BASE: "+current);
      if (current instanceof TType) {
        latest.addFirst(current);
        break;
      }
      else if (current instanceof TInt) {
        arrayDimension = ((TInt) current).getNumber();
        break;
      }
      base.add((TIden) current);
    }
    
    TType baseTType = new TType(base);
    baseTType.setArrayDimensions(arrayDimension);
    
    while (!latest.isEmpty()) {
      TNode current = latest.pollFirst();
      System.out.println("ADDDING: "+current);
      if (current instanceof TComma) {
        continue;
      }
      else if (current instanceof TInt) {
        arrayDimension = ((TInt) current).getNumber();
        baseTType.setArrayDimensions(arrayDimension);
        break;
      }
      baseTType.addGenericArgType((TType) current);
    }
    
    actualNodes.push(baseTType);
    
    return production;
  }

  protected Production exitCast(Production production){
    ArrayDeque<TNode> latest = exitEntrance();
    System.out.println(" ----> EXIT CAST: "+latest+" | "+actualNodes);
    
    //Because of our grammar, we can guarantee that "latest" is of one size and contains
    //only a TType
    
    ArrayList<TNode> targetBody = new ArrayList<>();
    while (actualNodes.peek() != null) {
      targetBody.add(0,actualNodes.pop());
    }
    
    TType targetType = (TType) latest.pollFirst();
    
    TCast cast = new TCast(new TExpr(targetBody), targetType);
    actualNodes.push(cast);
    return production;
  }  
  //EXPR visitations DONE
  
  //HELPER methods
  private void setEntrance(){
    stack.add(new ArrayDeque<>());
    actualNodes.push(null); //add marker
  }
  
  private ArrayDeque<TNode> exitEntrance(){
    ArrayDeque<TNode> latest = stack.pop();

    while (actualNodes.peek() != null) {
      latest.addFirst(actualNodes.pop());
    }
    
    actualNodes.pop(); //removes marker

    return latest;
  }
  //HELPER methods DONE

  /**
   * Resets this Analyzer for reuse with a different source
   */
  public void reset() {
    stack.clear();
    actualNodes.clear();
  }
  
  public Stack<TNode> getStackNodes() {
    return actualNodes;
  }
}
