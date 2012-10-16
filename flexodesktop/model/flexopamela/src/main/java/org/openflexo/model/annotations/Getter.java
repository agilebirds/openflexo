package org.openflexo.model.annotations;

import java.lang.annotation.Annotation;
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

	public String inverse() default UNDEFINED;

	public String defaultValue() default UNDEFINED;

	public static class GetterImpl implements Getter {
		private final String value;
		private final Cardinality cardinality;
		private final String inverse;
		private final String defaultValue;

		public GetterImpl(String value, Cardinality cardinality, String inverse, String defaultValue) {
			this.value = value;
			this.cardinality = cardinality;
			this.inverse = inverse;
			this.defaultValue = defaultValue;
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
	}
}
