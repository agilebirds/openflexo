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
public @interface Setter {

	public String value();

	public static class SetterImpl implements Setter {

		private String value;

		public SetterImpl(String value) {
			this.value = value;
		}

		@Override
		public Class<? extends Annotation> annotationType() {
			return Setter.class;
		}

		@Override
		public String value() {
			return value;
		}

	}
}
