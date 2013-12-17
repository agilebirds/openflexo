package org.openflexo.foundation.rm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openflexo.foundation.xml.XMLResourceDataBuilder;

/**
 * Annotation for a FlexoResource
 * 
 * @author sylvain
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface FlexoResourceDefinition {

	public Class<? extends XMLStorageResourceData> resourceDataClass();

	public boolean hasBuilder() default false;

	public Class<? extends XMLResourceDataBuilder> builderClass() default XMLResourceDataBuilder.class;

	public SomeResources[] contains() default {};

	public RequiredResource[] require() default {};
}
