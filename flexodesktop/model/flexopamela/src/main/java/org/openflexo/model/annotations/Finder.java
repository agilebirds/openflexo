package org.openflexo.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface Finder {

	public static final String NO_RECURSION = "---";
	public static final String DEFAULT_VALUE = "name";
	public static final String DEFAULT_NAME = "name";

	public String name() default DEFAULT_NAME;

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

	/**
	 * Whether this finder should try to perform recursive search. If yes, value should be a dot ('.') separated path to get from one object
	 * to the corresponding collection.
	 * 
	 * @return
	 */
	public String recursion() default NO_RECURSION;

	/**
	 * In case of recursion, this flag indicates to search first in the immediate collection and then to go in depth by "recursing".
	 * 
	 * @return
	 */
	public boolean iterateFirstRecurseThen() default true;
}
