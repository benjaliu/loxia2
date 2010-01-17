package cn.benjamin.loxia.web.taglib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoxiaTagAttribute {
	String name() default "";

    boolean required() default false;

    boolean rtexprvalue() default true;

    String description();

    String defaultValue() default "";

    String type() default "String";
}
