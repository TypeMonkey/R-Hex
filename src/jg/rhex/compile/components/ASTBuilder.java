package jg.rhex.compile.components;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import jg.rhex.compile.components.tnodes.TExpr;
import jg.rhex.compile.components.tnodes.TFuncCall;
import jg.rhex.compile.components.tnodes.TNode;
import jg.rhex.compile.components.tnodes.TOp;
import jg.rhex.compile.components.tnodes.atoms.TAtom;
import jg.rhex.compile.components.tnodes.atoms.TCParen;
import jg.rhex.compile.components.tnodes.atoms.TComma;
import jg.rhex.compile.components.tnodes.atoms.TOParen;

public class ASTBuilder {
  
  public Deque<TNode> build(Collection<TNode> tokens){
    return build(tokens.toArray(new TNode[tokens.size()]));
  }
  
  public Deque<TNode> build(TNode [] tokens){
    Stack<TOp> operators = new Stack<>();
    Deque<TNode> output = new ArrayDeque<>();
    
    //<--CONTINUE to actual parsing-->
    
    Deque<TNode> sourceQueue = new ArrayDeque<>(Arrays.asList(tokens));
    
    while(!sourceQueue.isEmpty()){
      TNode current = sourceQueue.pollFirst();
      if (current instanceof TOParen) {
        TOParen leftParen = (TOParen) current;
        operators.push(leftParen);
      }
      else if (current instanceof TCParen) {
        while (!(operators.peek() instanceof TOParen)) {
          TOp operator = (TOp) operators.pop();
          output.add(operator);
        }
        
        if (!operators.isEmpty() && (operators.peek() instanceof TOParen)) {
          operators.pop();
        }
      }
      else if (current instanceof TOp) {
        TOp curOp = (TOp) current;
        while ( operators.isEmpty() == false && 
                (
                    !isLeft(operators.peek()) &&
                     (
                       precedence(curOp.getOpString(), operators.peek().getOpString()) < 0 ||
                       (
                        precedence(curOp.getOpString(), operators.peek().getOpString()) == 0 &&
                        associativity(curOp.getOpString(), operators.peek().getOpString()).equals("LR")
                       )
                     )
                 ) 
                )
        {          
          output.add(operators.pop());
        }
        operators.push(curOp);
      }
      else if (current instanceof TExpr) {
        TExpr expr = (TExpr) current;
        //What this does is that when given an expression surroudned by parenthesis : (expr)
        //what we can do is that we can transform the expr enclosed without expanding it
        //on the source queue.
        //Because even with infix, enclosed expr are self contained.
        //ex:   (9+2)*5 ==> to postfix ==> (92+) 5 *
        expr.setBody(new ArrayList<>(build(expr.getActValue().toArray(new TNode[expr.getActValue().size()]))));
        output.add(expr);
      }
      else if (current instanceof TFuncCall) {
        parseFuncArgs((TFuncCall) current);
        output.add(current);
      }
      else if (current instanceof TAtom<?>) {
        TAtom<?> atom = (TAtom<?>) current;
        output.add(atom);
      }
    }
    
    
    while (!operators.isEmpty()) {
      System.out.println("CONC ADDING: "+operators.peek());
      output.add(operators.pop());
    }
    
    return output;
    //return switchRight(output);
  }
  
  private boolean isLeft(TOp node){
    System.out.println("CHECK: "+node.getActValue()+" | "+node.getClass()+" | "+(node instanceof TOParen));
    return node instanceof TOParen;
  }
  
  private void parseFuncArgs(TFuncCall call){
    //if function invocation actually has args, then parse it
    if (!call.getArgList().isEmpty()) {
      
      ArrayList<TNode> individArgs = new ArrayList<>();
      
      //first, separate the TFuncCall object's original argument body
      //into actual separate arguments (split the original list by commas)
      
      ArrayList<TNode> temp = new ArrayList<>();
      for(TNode node : call.getArgList()){
        if (node instanceof TComma) {
          Deque<TNode> parsed = build(temp);
          
          //if this is a "true" atom, then there's no need to wrap it around a TExpr node
          if (parsed.size() == 1) {
            individArgs.add(parsed.poll());
          }
          else {
            individArgs.add(new TExpr(new ArrayList<>(parsed)));
          }
          temp = new ArrayList<>();
        }
        else {
          temp.add(node);
        }
      }
      
      //add last argument after the last comma
      Deque<TNode> lastArg = build(temp);
      if (lastArg.size() == 1) {
        individArgs.add(lastArg.poll());
      }
      else {
        individArgs.add(new TExpr(new ArrayList<>(lastArg)));
      }
      
      //replace the TFuncCall object's arg list with our new one
      call.setArgs(individArgs);
    }
  }
  
  /**
   * Returns LR if operator associativity is Left-Right
   * Returns RL if operator associativity is Right-Left
   * Returns null if operators aren't in the same precedence level
   * @return
   */
  private String associativity(String ... ops){  
    System.out.println("CHECK ASSC: "+Arrays.toString(ops));
    Set<String> allops = new HashSet<>();
    for(String pString : ops){
      allops.add(pString);
    }
    
    System.out.println("CHECK ASSC 1: "+allops);
    
    Set<String> equals = new HashSet<>();
    equals.add("=");
    equals.add("/=");
    equals.add("+=");
    equals.add("*=");
    equals.add("%=");
    equals.add("-=");
    
    Set<String> addMinus = new HashSet<>();
    addMinus.add("+");
    addMinus.add("-");
    
    Set<String> factor = new HashSet<>();
    factor.add("*");
    factor.add("/");
    factor.add("%");

    addMinus.retainAll(allops);
    factor.retainAll(allops);
    equals.retainAll(allops);
    
    if (addMinus.size() == allops.size()) {
      return "LR";
    }
    else if (factor.size() == allops.size()) {
      return "LR";
    }
    else if (equals.size() == allops.size()) {
      return "RL";
    }
    else{
      System.out.println(addMinus.size()+" | "+factor+" | "+allops.size());
      return null;
    }
  }
  
  /**
   * If op1 < op2 precedence wise, negative is returned
   * If op1 > op2 precedence wise, positive is returned
   * If op1 = op2 precedence wise, 0 is returned
   * @param op1
   * @param op2
   * @return
   */
  private int precedence(String op1, String op2){
    System.out.println("OP1 : "+op1+" | OP2: "+op2);
    HashMap<String, Integer> map = new HashMap<>();
    map.put("*", 3);
    map.put("/", 3);
    map.put("%", 3);
    map.put("-", 2);
    map.put("+", 2);
    map.put("=", 1);
    map.put("/=", 1);
    map.put("+=", 1);
    map.put("*=", 1);
    map.put("%=", 1);
    map.put("-=", 1);
    
    return map.get(op1) - map.get(op2);
  }
}
