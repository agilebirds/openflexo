package org.openflexo.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.METHOD, ElementType.TYPE })
public @interface XMLElement {

	public static final String DEFAULT_XML_TAG = "";
	public static final String NO_CONTEXT = "";
	public static final String NO_NAME_SPACE = "";

	public String xmlTag() default DEFAULT_XML_TAG;
	public String context() default NO_CONTEXT;
	public String namespace() default NO_NAME_SPACE;
	boolean primary() default false;
}
