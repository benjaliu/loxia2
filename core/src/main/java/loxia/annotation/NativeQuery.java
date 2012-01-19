package loxia.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import loxia.dao.ColumnTranslator;
import loxia.dao.support.DummyColumnTranslator;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NativeQuery {
	String value() default "";
	boolean pagable() default false;
	boolean withGroupby() default false;
	Class<?> model() default DEFAULT.class;
	Class<? extends ColumnTranslator> translator() default DummyColumnTranslator.class; 
	String[] alias() default {};
	Class<?>[] clazzes() default {};
	
	public static final class DEFAULT {}
}
