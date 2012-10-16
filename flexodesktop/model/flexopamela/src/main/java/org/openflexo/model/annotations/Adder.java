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

	public static class AdderImpl implements Adder {

		private String value;

		public AdderImpl(String value) {
			super();
			this.value = value;
		}

		@Override
		public Class<? extends Annotation> annotationType() {
			return Adder.class;
		}

		@Override
		public String value() {
			return value;
		}
	}
}
