package org.flexo.model;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.StringConverter;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.model.xml.DefaultStringEncoder.Converter;
import org.openflexo.model.xml.InvalidDataException;

@ModelEntity
public interface WKFAnnotation extends FlexoModelObject {

	public static final String TEXT = "text";

	@Initializer
	public void init();

	@Initializer
	public void init(@Parameter(TEXT) String text);

	@Getter(TEXT)
	public String getText();

	@Setter(TEXT)
	public void setText(String s);

	@StringConverter
	public static final Converter<WKFAnnotation> CONVERTER = new WKFAnnotationConverter();

	public static class WKFAnnotationConverter extends Converter<WKFAnnotation> {

		public WKFAnnotationConverter() {
			super(WKFAnnotation.class);
		}

		@Override
		public WKFAnnotation convertFromString(String value, ModelFactory factory) throws InvalidDataException {
			return factory.newInstance(WKFAnnotation.class, value);
		}

		@Override
		public String convertToString(WKFAnnotation value) {
			return value.getText();
		}

	}

}
