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
public @interface Adder {

	public String value();

	public boolean onInitializationOnly() default false;

	public static class AdderImpl implements Adder {

		private final String value;
		private final boolean onInitializationOnly;

		public AdderImpl(String value, boolean onInitializationOnly) {
			super();
			this.value = value;
			this.onInitializationOnly = onInitializationOnly;
		}

		@Override
		public Class<? extends Annotation> annotationType() {
			return Adder.class;
		}

		@Override
		public String value() {
			return value;
		}

		@Override
		public boolean onInitializationOnly() {
			return onInitializationOnly;
		}
	}
}
