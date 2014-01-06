package org.flexo.model;

import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Initializer;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Parameter;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.StringConverter;
import org.openflexo.model.exceptions.InvalidDataException;
import org.openflexo.model.factory.ModelFactory;

@ModelEntity
public interface WKFAnnotation extends TestModelObject {

	public static final String TEXT = "text";

	@Override
	@Initializer
	public WKFAnnotation init();

	@Override
	@Initializer
	public WKFAnnotation init(@Parameter(TEXT) String text);

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
