package jg.rhex.compile.components;
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
import jg.rhex.compile.components.tnodes.TArrayAcc;
import jg.rhex.compile.components.tnodes.TCast;
import jg.rhex.compile.components.tnodes.TExpr;
import jg.rhex.compile.components.tnodes.TFuncCall;
import jg.rhex.compile.components.tnodes.TMemberInvoke;
import jg.rhex.compile.components.tnodes.TNode;
import jg.rhex.compile.components.tnodes.TOp;
import jg.rhex.compile.components.tnodes.atoms.TBool;
import jg.rhex.compile.components.tnodes.atoms.TCParen;
import jg.rhex.compile.components.tnodes.atoms.TComma;
import jg.rhex.compile.components.tnodes.atoms.TDouble;
import jg.rhex.compile.components.tnodes.atoms.TFloat;
import jg.rhex.compile.components.tnodes.atoms.TIden;
import jg.rhex.compile.components.tnodes.atoms.TInt;
import jg.rhex.compile.components.tnodes.atoms.TLong;
import jg.rhex.compile.components.tnodes.atoms.TNew;
import jg.rhex.compile.components.tnodes.atoms.TNumber;
import jg.rhex.compile.components.tnodes.atoms.TOParen;
import jg.rhex.compile.components.tnodes.atoms.TType;

public class NewSeer extends GramPracAnalyzer{

  protected Stack<ArrayDeque<TNode>> stack;
  protected Stack<TNode> actualNodes;
  
  public NewSeer(){
    stack = new Stack<>();
    actualNodes = new Stack<>();
  }
  
 //------UNIT (Atomic) visitations - all number types, string and char literals and variable names
  
  protected Node exitInteger(Token node)  throws ParseException{
    //System.out.println("INT: "+node.getImage()+" | children: "+getChildValues(node));
    //node.addValue(Double.parseDouble(node.getImage().toString()));
    actualNodes.add(new TInt(Integer.parseInt(node.getImage())));
    return node;
  } 
  
  protected Node exitDouble(Token node)  throws ParseException{
    //System.out.println("DOUBLE: "+node.getImage()+" | children: "+getChildValues(node));
    //node.addValue(Double.parseDouble(node.getImage().toString()));
    actualNodes.add(new TDouble(Double.parseDouble(node.getImage())));
    return node;
  } 
  
  protected Node exitFloat(Token node)  throws ParseException{
    //System.out.println("FLOAT: "+node.getImage()+" | children: "+getChildValues(node));
    //node.addValue(Double.parseDouble(node.getImage().toString()));
    String rawFloat = node.getImage();
    
    //We cutout the last character because explicitly declared floats numerals have an
    //upper or lower case "f" at the end
    actualNodes.add(new TFloat(Float.parseFloat(rawFloat.substring(0, rawFloat.length() - 1))));
    return node;
  } 
  
  protected Node exitLong(Token node)  throws ParseException{
    //System.out.println("LONG: "+node.getImage()+" | children: "+getChildValues(node));
    //node.addValue(Double.parseDouble(node.getImage().toString()));
    String rawLong = node.getImage();
    
    //We cutout the last character because explicitly declared Long numerals have an
    //upper or lower case "l" at the end
    actualNodes.add(new TLong(Long.parseLong(rawLong.substring(0, rawLong.length() - 1))));
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
    actualNodes.add(new TString(node.getImage()));
    return node;
  }
  
  protected Node exitChar(Token node){
    
    String actualChar = node.getImage().substring(1);
    actualChar = actualChar.substring(0, actualChar.length());
    
    actualNodes.add(new TChar(actualChar.charAt(0)));
    return node;
  }
  
  protected Node exitOpParen(Token node) throws ParseException {
    //node.addValue("(");
    actualNodes.add(new TOParen());
    return node;
  }

  protected Node exitClParen(Token node) throws ParseException {
    //node.addValue(")");
    actualNodes.add(new TCParen());
    return node;
  }
  
  protected Node exitNew(Token node) {
    System.out.println("------EXITED NEW!!!!");
    actualNodes.add(new TOp("new"));
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
    actualNodes.push(new TOp("+"));
    return (token);   
  }
  
  protected Node exitMinus(Token token) throws ParseException{
    //System.out.println("  > MINUS: "+token.getAllValues() +" | OPERANDS: "+currentStack);
    
    //token.addValue("+");
    actualNodes.push(new TOp("-"));
    return (token);   
  }
  
  protected Node exitMult(Token token) throws ParseException{
    //System.out.println("  > MULT: "+token.getAllValues() +" | OPERANDS: "+currentStack);
    
    //token.addValue("+");
    actualNodes.push(new TOp("*"));
    return (token);   
  }
  
  protected Node exitDiv(Token token) throws ParseException{
    //System.out.println("  > DIV: "+token.getAllValues() +" | OPERANDS: "+currentStack);
    
    //token.addValue("+");
    actualNodes.push(new TOp("/"));
    return (token);   
  }
  
  protected Node exitMod(Token token) throws ParseException{
    //System.out.println("  > MOD: "+token.getAllValues() +" | OPERANDS: "+currentStack);
    
    //token.addValue("+");
    actualNodes.push(new TOp("%"));
    return (token);   
  }
  
  protected Node exitBang(Token token) throws ParseException{
    //System.out.println("  > MOD: "+token.getAllValues() +" | OPERANDS: "+currentStack);
    
    //token.addValue("+");
    actualNodes.push(new TOp("!"));
    return (token);   
  }
  
  protected Node exitEqual(Token token) throws ParseException{
    actualNodes.push(new TOp("="));
    return token;
  }
  
  protected Node exitEqMult(Token token) throws ParseException{
    actualNodes.push(new TOp("*="));
    return token;
  }

  protected Node exitEqAdd(Token token) throws ParseException{
    actualNodes.push(new TOp("+="));
    return token;
  }

  protected Node exitEqDiv(Token token) throws ParseException{
    actualNodes.push(new TOp("/="));
    return token;
  }

  protected Node exitEqMin(Token token) throws ParseException{
    actualNodes.push(new TOp("-="));
    return token;
  }

  protected Node exitEqMod(Token token) throws ParseException{
    actualNodes.push(new TOp("%="));
    return token;
  }
  
  protected Node exitGreat(Token token) throws ParseException{
    actualNodes.push(new TOp(">"));
    return token;
  }
  
  protected Node exitLess(Token token) throws ParseException{
    actualNodes.push(new TOp("<"));
    return token;
  }
  
  protected Node exitLsEq(Token token) throws ParseException{
    actualNodes.push(new TOp("<="));
    return token;
  }
  
  protected Node exitGrEq(Token token) throws ParseException{
    actualNodes.push(new TOp(">="));
    return token;
  }
  
  protected Node exitNotEq(Token token) throws ParseException{
    actualNodes.push(new TOp("!="));
    return token;
  }
  
  protected Node exitEqEq(Token token) throws ParseException{
    actualNodes.push(new TOp("=="));
    return token;
  }
  
  protected Node exitBoolAnd(Token token) throws ParseException{
    actualNodes.push(new TOp("&&"));
    return token;
  }
  
  protected Node exitBoolOr(Token token) throws ParseException{
    actualNodes.push(new TOp("||"));
    return token;
  }

  //----OPERATOR visitations DONE
  
  //EXPR visitations 
  
  protected void enterNumber(Production production) {
    System.out.println("----ENTER NUMBER");
    setEntrance();
  }
  
  protected Node exitNumber(Production production){
    System.out.println("----EXIT NUMBER");
    ArrayDeque<TNode> latest = exitEntrance();
    
    TNode potentialNumber = null;
    if ((potentialNumber = latest.poll()) instanceof TOp) {
      //negative number
      TNumber number = (TNumber) latest.poll();
      if (number instanceof TInt) {
        int originalValue = (int) number.getActValue();
        actualNodes.push(new TInt(-originalValue));
      }
      else if (number instanceof TLong) {
        long originalValue = (long) number.getActValue();
        actualNodes.push(new TLong(-originalValue));
      }
      else if (number instanceof TFloat) {
        float originalValue = (float) number.getActValue();
        actualNodes.push(new TFloat(-originalValue));
      }
      else if (number instanceof TDouble) {
        double originalValue = (double) number.getActValue();
        actualNodes.push(new TDouble(-originalValue));
      }
    }
    else {
      //positive number
      actualNodes.push(potentialNumber);
    }
   
    return production;
  }
  
  protected void enterAssgn(Production production){
    System.out.println("----Enter Assign");
    setEntrance();
  }
  
  protected Node exitAssgn(Production production){
    System.out.println("-----EXIT ASSGN");
    
    ArrayDeque<TNode> latest = exitEntrance();
    for(TNode node : latest){
      actualNodes.push(node);
    }
    return production;
  }
  
  protected void enterExpr(Production production){
    System.out.println("----enter expr");
    setEntrance();
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
  
  protected void enterParenExpr(Production production){
    System.out.println("----enter paren expr");
    setEntrance();
  }
  
  protected Node exitParenExpr(Production production){
    System.out.println("----exit paren expr");

    ArrayDeque<TNode> latest = exitEntrance();
    
    System.out.println("    ----***> "+latest);

    actualNodes.push(new TExpr(new ArrayList<>(latest))); 
    return production;
  }
  
  protected void enterFuncCall(Production production){
    System.out.println("----enter func call");
    setEntrance();
  }
  
  protected Node exitFuncCall(Production production){    
    ArrayDeque<TNode> latest = exitEntrance();
    System.out.println("----exit func call ");

    TFuncCall funcCall = new TFuncCall(latest.pollFirst().getValue().toString());
    
    //these two polls remove the ending and closing parenthesis of a function invocations
    //Ex: func ( ) <-- removes those outer parenthesis
    latest.pollFirst();
    latest.pollLast();  
    
    funcCall.addArgs(latest);
    
    if (actualNodes.peek() != null && actualNodes.peek().getValue().toString().equals("new")) {
      actualNodes.pop();
      actualNodes.push(new TNew(funcCall));
    }
    else {
      actualNodes.push(funcCall); 
    }
        
    return production;
  }
  
  protected void enterArrayAcc(Production production){
    setEntrance();
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
    
    actualNodes.add(previous);
    
    return production;
  }
  
  protected void enterInvoke(Production production){
    setEntrance();
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
  
  
  protected void enterTypeName(Production production){
    System.out.println("----> ENTER TYPE: "+actualNodes);
    setEntrance();   
  }
  
  protected Production exitTypeName(Production production){
    ArrayDeque<TNode> latest = exitEntrance();
    System.out.println("-----> EXIT TYPE: "+latest+" | "+actualNodes);
    actualNodes.push(new TType(new ArrayList<>(latest)));   
    return production;
  }
  
  protected void enterCast(Production production){
    System.out.println("----> ENTER CAST "+actualNodes);
    setEntrance();
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
    targetType.formalizeRawTypeBody();
    
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
