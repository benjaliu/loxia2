package loxia.struts2.interceptor;

import java.util.Comparator;

import com.opensymphony.xwork2.interceptor.ParametersInterceptor;

public class ParametersExInterceptor extends ParametersInterceptor {

	/**
	 *
	 */
	private static final long serialVersionUID = -1479075388257596102L;

	@SuppressWarnings("unchecked")
	static final Comparator listOrderedCollator = new Comparator() {
        public int compare(Object arg0, Object arg1) {
        	String regex = ".+\\(-?(\\d)+\\)\\..*";
        	String twentyzero = "00000000000000000000";
            String s1 = (String) arg0;
            String s2 = (String) arg1;
            if(s1.matches(regex) && s2.matches(regex)){
            	int delim1 = s1.indexOf('(');
            	int delim2 = s2.indexOf('(');
            	if(delim1 == delim2 && s1.substring(0,delim1).equals(s2.substring(0,delim1))){
            		String s11 = s1.substring(delim1 +1);
            		String s21 = s2.substring(delim1 +1);

            		// change XX) to 000000000000000000XX) and
            		// change -XX) to 100000000000000000XX)
            		int delim3 = s11.indexOf(')');
            		int delim4 = s21.indexOf(')');

            		String strnum1 = s11.substring(0,delim3);
            		String strnum2 = s21.substring(0,delim4);
            		if(strnum1.indexOf('-') == 0)
            			strnum1 = "1" + twentyzero.substring(strnum1.length()+1) + strnum1.substring(1);
            		else
            			strnum1 = twentyzero.substring(strnum1.length()) + strnum1;
            		if(strnum2.indexOf('-') == 0)
            			strnum2 = "1" + twentyzero.substring(strnum2.length()+1) + strnum2.substring(1);
            		else
            			strnum2 = twentyzero.substring(strnum2.length()) + strnum2;
            		s11 = strnum1 + s11.substring(delim3);
            		s21 = strnum2 + s21.substring(delim4);
            		return s11.compareTo(s21);
            	}else{
            		return s1.compareTo(s2);
            	}
            }else{
            	return s1.compareTo(s2);
            }
        };
    };

	@SuppressWarnings("unchecked")
	protected Comparator getOrderedComparator() {
        return listOrderedCollator;
    }
}
