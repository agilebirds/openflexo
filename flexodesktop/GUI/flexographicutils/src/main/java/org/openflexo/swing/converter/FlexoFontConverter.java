package org.openflexo.swing.converter;

import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.swing.FlexoFont;

@Deprecated
public class FlexoFontConverter extends Converter<FlexoFont> {

	public FlexoFontConverter() {
		super(FlexoFont.class);
	}

	@Override
	public FlexoFont convertFromString(String value, ModelFactory factory) {
		return FlexoFont.stringToFont(value);
	}

	@Override
	public String convertToString(FlexoFont value) {
		return value.toString();
	};
}