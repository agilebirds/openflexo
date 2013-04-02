package org.openflexo.foundation.technologyadapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openflexo.foundation.viewpoint.FetchRequest;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(value = ElementType.TYPE)
public @interface DeclareFetchRequest {

	public Class<? extends FetchRequest> value();
}
