package org.openflexo.model.converter;

import org.openflexo.localization.Language;
import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.factory.ModelFactory;

public class LanguageConverter extends Converter<Language> {

	public LanguageConverter() {
		super(Language.class);
	}

	@Override
	public Language convertFromString(String value, ModelFactory factory) {
		return Language.get(value);
	}

	@Override
	public String convertToString(Language value) {
		return value.getName();
	}
}