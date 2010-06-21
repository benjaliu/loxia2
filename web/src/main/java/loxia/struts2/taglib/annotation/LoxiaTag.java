package loxia.struts2.taglib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoxiaTag {
	String name();

    String tldBodyContent() default "JSP";

    String tldTagClass();

    String description();
}
