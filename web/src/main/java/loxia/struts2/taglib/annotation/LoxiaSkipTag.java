/**
 * 
 * 
 * @author treacy
 * @date 2010-11-22
 */
package loxia.struts2.taglib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoxiaSkipTag {
	String name();

    String tldBodyContent() default "JSP";

    String tldTagClass();

    String description();
}
