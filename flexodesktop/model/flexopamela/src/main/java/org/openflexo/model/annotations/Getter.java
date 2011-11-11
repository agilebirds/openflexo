package org.openflexo.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(value = ElementType.METHOD)
public @interface Getter {

	public static enum Cardinality {
		SINGLE, LIST, MAP;
	}

	public static final String UNDEFINED = "";

	public String value();

	public Cardinality cardinality() default Cardinality.SINGLE;

	public String defaultValue() default UNDEFINED;

	public String inverse() default UNDEFINED;
}
