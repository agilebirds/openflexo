package org.openflexo.model.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(value = { ElementType.METHOD, ElementType.TYPE })
public @interface XMLElement {

	public static final String DEFAULT_XML_TAG = "";
	public static final String NO_CONTEXT = "";
	public static final String NO_NAME_SPACE = "";

	public String xmlTag() default DEFAULT_XML_TAG;

	public String context() default NO_CONTEXT;

	public String namespace() default NO_NAME_SPACE;

	boolean primary() default false;

	public static class XMLElementImpl implements XMLElement {
		private String xmlTag;
		private String context;
		private String namespace;
		private boolean primary;

		public XMLElementImpl(String xmlTag, String context, String namespace, boolean primary) {
			this.xmlTag = xmlTag;
			this.context = context;
			this.namespace = namespace;
			this.primary = primary;
		}

		@Override
		public Class<? extends Annotation> annotationType() {
			return XMLElement.class;
		}

		@Override
		public String xmlTag() {
			return xmlTag;
		}

		@Override
		public String context() {
			return context;
		}

		@Override
		public String namespace() {
			return namespace;
		}

		@Override
		public boolean primary() {
			return primary;
		}

	}
}
