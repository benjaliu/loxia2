package cn.benjamin.loxia.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlException;

public class OgnlStack{
	private List<Object> stack = new ArrayList<Object>();
	private Map<String,Object> context;
	
	private Map<String,Object> expressions = new HashMap<String, Object>();
	
	private Object getExpression(String expr) throws OgnlException{
		synchronized (expressions) {
			Object o = expressions.get(expr);
			if(o == null){
				o = Ognl.parseExpression(expr);
				expressions.put(expr, o);
			}
			return o; 
		}		
	}
	
	public Object getValue(String expr){
		for(Object obj: stack){
			try {
				if("top".equals(expr)) return obj;
				return Ognl.getValue(getExpression(expr), context, obj);
			} catch (OgnlException e) {
				
			}
		}
		return null;
	}
	
	public OgnlStack(Object obj){
		stack.add(obj);
		context = new HashMap<String, Object>();
	}
	
	public OgnlStack(Object obj, Map<String,Object> context){
		stack.add(obj);
		this.context = context;
	}
	
	public void push(Object obj){
		stack.add(0, obj);
	}
	
	public Object pop(){
		if(stack.size() == 0) throw new RuntimeException("No elements to pop");
		return stack.remove(0);
	}
	
	public Object peek(){
		if(stack.size() ==0) throw new RuntimeException("No elements in stack");
		return stack.get(0);
	}
	
	public void addContext(String key, Object value){
		this.context.put(key, value);
	}
	
	public void removeContext(String key){
		this.context.remove(key);
	}
	
	public Object getContext(String key){
		return this.context.get(key);
	}
	
	public Map<String,Object> getContextMap(){
		return this.context;
	}
}
