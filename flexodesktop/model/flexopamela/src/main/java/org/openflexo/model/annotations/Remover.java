package org.openflexo.model.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface Remover {

	public String value();

	public static class RemoverImpl implements Remover {
		private final String value;

		public RemoverImpl(String value) {
			this.value = value;
		}

		@Override
		public Class<? extends Annotation> annotationType() {
			return Remover.class;
		}

		@Override
		public String value() {
			return value;
		}

	}
}
