package cn.benjamin.loxia.utils;

import java.util.HashSet;
import java.util.Set;

public class TextParseUtil {

    public static String translateVariables(String expression, OgnlStack stack) {
        return translateVariables('%', expression, stack, null).toString();
    }
    
    
    public static String translateVariables(String expression, OgnlStack stack, ParsedValueEvaluator evaluator) {
    	return translateVariables('%', expression, stack, evaluator).toString();
    }

    public static String translateVariables(char open, String expression, OgnlStack stack) {
        return translateVariables(open, expression, stack, null).toString();
    }
    
    public static String translateVariables(char open, String expression, OgnlStack stack, ParsedValueEvaluator evaluator) {
        // deal with the "pure" expressions first!
        //expression = expression.trim();
    	String result = expression;

        while (true) {
            int start = expression.indexOf(open + "{");
            int length = expression.length();
            int x = start + 2;
            int end;
            char c;
            int count = 1;
            while (start != -1 && x < length && count != 0) {
                c = expression.charAt(x++);
                if (c == '{') {
                    count++;
                } else if (c == '}') {
                    count--;
                }
            }
            end = x - 1;

            if ((start != -1) && (end != -1) && (count == 0)) {
                String var = expression.substring(start + 2, end);

                Object o = stack.getValue(var);
                if (evaluator != null) {
                	o = evaluator.evaluate(o);
                }
                

                String left = expression.substring(0, start);
                String right = expression.substring(end + 1);
                if (o != null) {
                    if (stringSet(left)) {
                        result = left + o;
                    } else {
                        result = o.toString();
                    }

                    if (stringSet(right)) {
                        result = result + right;
                    }

                    expression = left + o + right;
                } else {
                    // the variable doesn't exist, so don't display anything
                    result = left + right;
                    expression = left + right;
                }
            } else {
                break;
            }
        }

        return result;
    }
    
    public final static boolean stringSet(String string) {
        return (string != null) && !"".equals(string);
    }

    /**
     * Returns a set from comma delimted Strings.
     * @param s The String to parse.
     * @return A set from comma delimted Strings.
     */
    public static Set<String> commaDelimitedStringToSet(String s) {
        Set<String> set = new HashSet<String>();
        String[] split = s.split(",");
        for (int i = 0; i < split.length; i++) {
            String trimmed = split[i].trim();
            if (trimmed.length() > 0)
                set.add(trimmed);
        }
        return set;
    }
    
    public static interface ParsedValueEvaluator {
    	
    	/**
    	 * Evaluated the value parsed by Ognl value stack.
    	 * 
    	 * @param parsedValue - value parsed by ognl value stack
    	 * @return return the evaluted value.
    	 */
    	Object evaluate(Object parsedValue);
    }
}