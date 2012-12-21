package org.openflexo.model.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openflexo.model.StringEncoder;
import org.openflexo.model.factory.ModelFactory;

/**
 * Annotation for a getter
 * 
 * @author Guillaume
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface Getter {

	public static enum Cardinality {
		SINGLE, LIST, MAP;
	}

	public static final String UNDEFINED = "";

	/**
	 * The property identifier of this getter
	 * 
	 * @return the property identifier of this getter
	 */
	public String value();

	/**
	 * The cardinality of this getter
	 * 
	 * @return the cardinality of the getter
	 */
	public Cardinality cardinality() default Cardinality.SINGLE;

	/**
	 * The inverse property identifier of this getter. Depending on the cardinality of this property and its inverse, this property will be
	 * either set or added to the inverse property of this property value.
	 * 
	 * @return
	 */
	public String inverse() default UNDEFINED;

	/**
	 * A string convertable value that is set by default on the property identified by this getter
	 * 
	 * @return the string converted default value.
	 */
	public String defaultValue() default UNDEFINED;

	/**
	 * Indicates that the type returned by this getter can be converted to a string using a {@link Converter}. Upon
	 * serialization/deserialization, the {@link ModelFactory} will provide, through its {@link StringEncoder}, an appropriate
	 * {@link Converter}. Failing to do that will result in an Exception
	 * 
	 * The default value is <code>false</code>
	 * 
	 * @return true if the type returned by this getter can be converted to a string.
	 */
	public boolean isStringConvertable() default false;

	/**
	 * Indicates that the type returned by this getter should not be included in the model. This flag allows to manipulate types that are
	 * unknown to PAMELA. If set to true, PAMELA will not try to interpret those classes and will not be able to serialize them
	 * 
	 * @return true if PAMELA should not import the type of the property identified by this getter.
	 */
	public boolean ignoreType() default false;

	public static class GetterImpl implements Getter {
		private final String value;
		private final Cardinality cardinality;
		private final String inverse;
		private final String defaultValue;
		private final boolean stringConvertable;
		private final boolean ignoreType;

		public GetterImpl(String value, Cardinality cardinality, String inverse, String defaultValue, boolean stringConvertable,
				boolean ignoreType) {
			this.value = value;
			this.cardinality = cardinality;
			this.inverse = inverse;
			this.defaultValue = defaultValue;
			this.stringConvertable = stringConvertable;
			this.ignoreType = ignoreType;
		}

		@Override
		public Class<? extends Annotation> annotationType() {
			return Getter.class;
		}

		@Override
		public String value() {
			return value;
		}

		@Override
		public Cardinality cardinality() {
			return cardinality;
		}

		@Override
		public String defaultValue() {
			return defaultValue;
		}

		@Override
		public String inverse() {
			return inverse;
		}

		@Override
		public boolean isStringConvertable() {
			return stringConvertable;
		}

		@Override
		public boolean ignoreType() {
			return ignoreType;
		}
	}
}
