package org.openflexo.foundation.resource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for a FlexoResource
 * 
 * @author sylvain
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface FlexoResourceDefinition {

	public Class<? extends ResourceData<?>> resourceDataClass();

	public SomeResources[] contains() default {};

	public RequiredResource[] require() default {};
}
