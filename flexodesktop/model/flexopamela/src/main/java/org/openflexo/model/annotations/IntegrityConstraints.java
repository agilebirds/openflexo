package org.openflexo.model.annotations;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface IntegrityConstraints {

	public IntegrityConstraint[] value();
}
