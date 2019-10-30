
package app;



import java.io.*;

import java.util.*;

import java.util.regex.*;



import structures.Stack;



public class Expression {



    public static String delims = " \t*+-/()[]";

            

    /**

     * Populates the vars list with simple variables, and arrays lists with arrays

     * in the expression. For every variable (simple or array), a SINGLE instance is created 

     * and stored, even if it appears more than once in the expression.

     * At this time, values for all variables and all array items are set to

     * zero - they will be loaded from a file in the loadVariableValues method.

     * 

     * @param expr The expression

     * @param vars The variables array list - already created by the caller

     * @param arrays The arrays array list - already created by the caller

     */

    public static void 

    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {

        String s = "(";
            for(int i = 0; i<expr.length();i++) { //iterate through chars in string

                if (Character.isDigit(expr.charAt(i))) {

                    continue;

                }else{

                    s = s + expr.charAt(i);
                }

                }

            s += ")";

            String expression = s.replaceAll(" ",""); 

            StringTokenizer st = new StringTokenizer(expression,"+-*/[]() ", false);

            while(st.hasMoreTokens()){

                String temp = st.nextToken(" \t*+-/()[]");

                if(expression.charAt(expression.lastIndexOf(temp) + temp.length()) == '['){

                    Array temparray = new Array(temp);

                    if(arrays.contains(temparray) != true){

                    arrays.add(temparray);

                    }

                }else{

                    Variable tempVarriable = new Variable(temp);

                    if(vars.contains(tempVarriable) != true ){

                    vars.add(tempVarriable);

                    }

                }

            }
    }

    

    /**

     * Loads values for variables and arrays in the expression

     * 

     * @param sc Scanner for values input

     * @throws IOException If there is a problem with the input 

     * @param vars The variables array list, previously populated by makeVariableLists

     * @param arrays The arrays array list - previously populated by makeVariableLists

     */

    public static void 

    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 

    throws IOException {

        while (sc.hasNextLine()) {

            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());

            int numTokens = st.countTokens();

            String tok = st.nextToken();

            Variable var = new Variable(tok);

            Array arr = new Array(tok);

            int vari = vars.indexOf(var);

            int arri = arrays.indexOf(arr);

            if (vari == -1 && arri == -1) {

                continue;

            }

            int num = Integer.parseInt(st.nextToken());

            if (numTokens == 2) { // scalar symbol

                vars.get(vari).value = num;

            } else { // array symbol

                arr = arrays.get(arri);

                arr.values = new int[num];

                // following are (index,val) pairs

                while (st.hasMoreTokens()) {

                    tok = st.nextToken();

                    StringTokenizer stt = new StringTokenizer(tok," (,)");

                    int index = Integer.parseInt(stt.nextToken());

                    int val = Integer.parseInt(stt.nextToken());

                    arr.values[index] = val;              

                }

            }

        }

    }
    /**

     * Evaluates the expression.

     * 

     * @param vars The variables array list, with values for all variables in the expression

     * @param arrays The arrays array list, with values for all array items

     * @return Result of evaluation

     */

    public static float 

    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {

                String ans = "";

                 ans = evulatevariable(expr, vars, arrays);

                String finalans = "";

                String idextemp = "";

                for(int i =0;i<ans.length();i++){

                      if("(".contains(Character.toString(ans.charAt(i)))){

                          int tempi = i;

                          Stack<String> squarebracket = new Stack<String>();

                          squarebracket.push("(");

                          while(!squarebracket.isEmpty()){

                              i++;

                              if(ans.charAt(i) == '('){

                                  squarebracket.push("(");

                              }

                              if(ans.charAt(i) == ')'){

                                  squarebracket.pop();

                              }

                          }

                          finalans += Integer.toString(Math.round(evaluate(ans.substring(tempi+1,i+1),vars,arrays)));

                      }else if(!")[]".contains(Character.toString(ans.charAt(i)))){

                          finalans += ans.charAt(i);

                      }else if(")".contains(Character.toString(ans.charAt(i)))){

                          continue;

                      }else if("[".contains(Character.toString(ans.charAt(i)))){

                           idextemp = "";

                          int tempis = i-1;

                          while(!"()*[]+-/".contains(Character.toString(ans.charAt(tempis)))){

                              String indextemptwo = "";

                              indextemptwo = Character.toString(ans.charAt(tempis))+ idextemp ;

                              idextemp = indextemptwo;

                              if(tempis<=0){

                                  break;

                              }

                              tempis --;

                          }

                          int tempi = i;

                          Stack<String> squarebracket = new Stack<String>();

                          squarebracket.push("[");

                          while(!squarebracket.isEmpty()){

                              i++;

                              if(ans.charAt(i) == '['){

                                  squarebracket.push("[");

                              }

                              if(ans.charAt(i) == ']'){

                                  squarebracket.pop();
                              }
                          }

                          finalans += Integer.toString((evaluatearray(ans.substring(tempi+1,i+1),vars,arrays,idextemp)));

                          finalans= finalans.replaceAll(idextemp, "");
                      }
                 }
                String exp = finalans;

                 Stack<String> operators = new Stack<String>();

                 Stack<String> operends = new Stack<String>();

                 for(int i=0;i<exp.length();i++){

                     String tempnum = "";

                     while(Character.isDigit(exp.charAt(i))){

                         tempnum += Character.toString(exp.charAt(i));

                         if(i < exp.length()){

                         i++;

                         }

                         if( i == exp.length()){

                             break;

                         }

                         }

                     if(tempnum != ""){

                         operends.push(tempnum);

                         tempnum = "";

                         i--;

                     }

                     else if(operators.isEmpty() || expvalue(exp.charAt(i)) > expvalue(operators.peek().charAt(0)) ){

                         operators.push(Character.toString(exp.charAt(i)));

                     }

                     else if(expvalue(exp.charAt(i)) <= expvalue(operators.peek().charAt(0))){                       
                         {

                         if(operators.peek()=="+"||operators.peek()=="*"){

                         String tempa = operends.pop();

                         String tempb = operends.pop();

                         String tempopp = operators.pop();

                         String temprslt = String.valueOf(arthmitic(tempa,tempb,tempopp)); 

                         operends.push(temprslt);

                         i--;

                         }

                         else{

                             String tempa = operends.pop();

                             String tempb = operends.pop();

                             String tempopp = operators.pop();

                             String temprslt = String.valueOf(arthmitic(tempb,tempa,tempopp)); 

                             operends.push(temprslt);

                                 i--;

                                 }                     

                     }

                     }

                     }

                 while(!operators.isEmpty()){

                     String tempa = operends.pop();

                     String tempb = operends.pop();

                     String tempopp = operators.pop();

                     String temprslt = String.valueOf(arthmitic(tempb,tempa,tempopp)); 

                     operends.push(temprslt);

                 }

                 return(Float.parseFloat(operends.peek()));

        }        



        private static int evaluatearray(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays, String arrayindex){

                 int result = 0;

                 String resultstring = "";

                 for(int i=0;i<expr.length();i++){

                     if(expr.charAt(i) == ' ') {

                        continue;

                    }

                     if(Character.isDigit(expr.charAt(i))){

                         String tempdigit = "";

                         while(Character.isDigit(expr.charAt(i))){

                             tempdigit += expr.charAt(i);

                             if( i == expr.length()){

                                     break;

                                 }

                             if(Character.isDigit(expr.charAt(i+1))){

                                 i++;

                                 }

                             if(!Character.isDigit(expr.charAt(i+1))){

                                 break;

                                 }

                         }

                         resultstring += tempdigit;

                     } else if("*+-/".contains(Character.toString(expr.charAt(i)))){

                         resultstring += expr.charAt(i);
                     }

                     else if("[".contains(Character.toString(expr.charAt(i)))){

                         String idextemp = "";

                         int tempis = i-1;

                         while(!"()*[]+-/".contains(Character.toString(expr.charAt(tempis)))){

                             String indextemptwo = "";

                           indextemptwo = Character.toString(expr.charAt(tempis))+ idextemp ;

                           idextemp = indextemptwo;

                          if(tempis<=0){

                              break;

                          }

                          tempis --;

                         }

                         int tempi = i;

                      Stack<String> squarebracket = new Stack<String>();

                      squarebracket.push("[");

                      while(!squarebracket.isEmpty()){

                          i++;

                          if(expr.charAt(i) == '['){

                              squarebracket.push("[");

                          }

                          if(expr.charAt(i) == ']'){

                              squarebracket.pop();

                          }

                               resultstring= resultstring.replaceAll(idextemp, "");

                      }
                         resultstring += Integer.toString(evaluatearray(expr.substring(tempi+1,i+1),vars,arrays,idextemp));

                     }else if("]".contains(Character.toString(expr.charAt(i)))){

                         continue;
                     }
                         else if("(".contains(Character.toString(expr.charAt(i)))){

                        int itemp = i;

                        Stack<String> squarebracket = new Stack<String>();

                        squarebracket.push("(");

                        while(!squarebracket.isEmpty()){

                            i++;

                            if(expr.charAt(i) == '('){

                                squarebracket.push("(");
                            }

                            if(expr.charAt(i) == ')'){

                                squarebracket.pop();
                            }
                        }
                           resultstring += Integer.toString(Math.round(evaluate(expr.substring(itemp+1,i+1),vars,arrays)));

                     } else if(")".contains(Character.toString(expr.charAt(i)))){
                         continue;
                 }

                 }

                 result = Math.round(evaluate(resultstring,vars,arrays));

                 Array resultarrayobject =  arrays.get(arrays.indexOf(new Array(arrayindex)));

                 int [] resultarrayvalue = resultarrayobject.values;

                 return resultarrayvalue[result] ;
                 }



        private static int expvalue (char i ){

            int value = 0 ;

            switch(i){

            case ('+'):

                value = 1;

                break;

            case '-':

                value = 1;

                break;

            case '*':

                value = 2;

                break;

            case '/':

                value = 2;

                break;

            }

            return value;

        }



        private static int arthmitic (String a, String b, String c ){

            int value = 0 ;

            switch(c){

            case ("+"):

                value = Integer.parseInt(a)+Integer.parseInt(b);

                break;

            case "-":

                value = Integer.parseInt(a)-Integer.parseInt(b);

                break;

            case "*":

                value = Integer.parseInt(a)*Integer.parseInt(b);

                break;

            case "/":

                value = Integer.parseInt(a)/Integer.parseInt(b);

                break;

            }

            return value;     

        }

            

            private static String evulatevariable(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {

            String exp = expr.replaceAll(" ", "");

            String variabletemp = "";

            String finalanswer = "";

            for(int i = 0; i<exp.length();i++) { 

                if(Character.isDigit(exp.charAt(i))) {

                    finalanswer += exp.charAt(i);

                }else if(!delims.contains(Character.toString(exp.charAt(i)))) { 

                    variabletemp += exp.charAt(i);

                }else if("[".contains(Character.toString(exp.charAt(i)))) {

                    finalanswer += variabletemp;

                    finalanswer += exp.charAt(i);

                    variabletemp = "";

                }else if ("+-*/()]".contains(Character.toString(exp.charAt(i)))){  

                    if(variabletemp.length()!=0) {

                        Variable result = vars.get(vars.indexOf(new Variable(variabletemp)));

                        finalanswer += result.value;

                    } 

                    finalanswer += exp.charAt(i);

                    variabletemp = "";

                 }

             }

            if (variabletemp.length()!=0 ) {

                Variable result = vars.get(vars.indexOf(new Variable(variabletemp)));

                finalanswer += result.value;

            }

            

            return finalanswer;



            }

        }

