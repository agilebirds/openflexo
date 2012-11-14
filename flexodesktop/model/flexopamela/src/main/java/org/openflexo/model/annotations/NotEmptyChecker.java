package org.openflexo.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate that the default implementation of the method should indicate whether the value of the property identified by
 * <code>id</code> is:
 * <ul>
 * <li>for single cardinality: is not null</li>
 * <li>for multiple cardinality (Lists and Maps): is not null and not empty</li>
 * </ul>
 * 
 * @author Guillaume
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface NotEmptyChecker {

	/**
	 * The identifier of the property.
	 * 
	 * @return
	 */
	public String value();
}
