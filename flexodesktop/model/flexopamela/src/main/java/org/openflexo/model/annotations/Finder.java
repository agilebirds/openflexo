package org.openflexo.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(value = ElementType.METHOD)
public @interface Finder {

	public static final String DEFAULT_VALUE = "name";

	/**
	 * The name of the property on which the finder should iterate to find the object
	 * 
	 * @return
	 */
	public String collection();

	/**
	 * The attribute which should match the finder argument
	 * 
	 * @return
	 */
	public String attribute() default DEFAULT_VALUE;

	/**
	 * Wheter this finder returns a single object or several
	 * 
	 * @return
	 */
	public boolean isMultiValued() default false;
}
