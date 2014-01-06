package org.openflexo.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openflexo.model.annotations.Getter.Cardinality;

/**
 * Annotation for a property identifier (a string encoding key of a property)
 * 
 * @author Sylvain
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface PropertyIdentifier {

	public static final String UNDEFINED = "";

	/**
	 * The type of this parameter
	 * 
	 * @return the type of this parameter
	 */
	public Class<?> type();

	/**
	 * The cardinality of this getter
	 * 
	 * @return the cardinality of the getter
	 */
	public Cardinality cardinality() default Cardinality.SINGLE;

	public boolean isPrimitive() default false;
}
