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

	public boolean onInitializationOnly() default false;

	public static class SetterImpl implements Setter {

		private final String value;
		private final boolean onInitializationOnly;

		public SetterImpl(String value, boolean onInitializationOnly) {
			this.value = value;
			this.onInitializationOnly = onInitializationOnly;
		}

		@Override
		public Class<? extends Annotation> annotationType() {
			return Setter.class;
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
