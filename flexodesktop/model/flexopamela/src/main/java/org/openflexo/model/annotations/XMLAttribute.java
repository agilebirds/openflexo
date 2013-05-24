package org.openflexo.model.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface XMLAttribute {

	public static final String DEFAULT_XML_TAG = "";
	public static final String DEFAULT_NAMESPACE = "";

	public String xmlTag() default DEFAULT_XML_TAG;

	public String namespace() default DEFAULT_NAMESPACE;

	public static class XMLAttributeImpl implements XMLAttribute {
		private final String xmlTag;
		private final String namespace;

		public XMLAttributeImpl(String xmlTag, String namespace) {
			this.xmlTag = xmlTag;
			this.namespace = namespace;
		}

		@Override
		public Class<? extends Annotation> annotationType() {
			return XMLAttribute.class;
		}

		@Override
		public String xmlTag() {
			return xmlTag;
		}

		@Override
		public String namespace() {
			return namespace;
		}

	}
}
