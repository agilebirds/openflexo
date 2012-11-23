package org.openflexo.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface CloningStrategy {

	public static enum StrategyType {
		CLONE, REFERENCE, IGNORE, FACTORY;
	}

	public static final String UNDEFINED = "";

	public StrategyType value() default StrategyType.CLONE;

	public String factory() default UNDEFINED;
}
