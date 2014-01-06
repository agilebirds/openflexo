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
@Target(value = ElementType.ANNOTATION_TYPE)
public @interface SomeResources {

	public Class<? extends org.openflexo.foundation.resource.FlexoResource> resourceType();

	public String pattern() default "*";

}
