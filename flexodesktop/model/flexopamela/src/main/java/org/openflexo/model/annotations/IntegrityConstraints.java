package org.openflexo.model.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface IntegrityConstraints {

	public IntegrityConstraint[] value();
}
