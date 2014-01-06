package org.openflexo.model.converter;

import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.FlexoVersion;

public class FlexoVersionConverter extends Converter<FlexoVersion> {

	public FlexoVersionConverter() {
		super(FlexoVersion.class);
	}

	@Override
	public FlexoVersion convertFromString(String value, ModelFactory factory) {
		return new FlexoVersion(value);
	}

	@Override
	public String convertToString(FlexoVersion value) {
		return value.toString();
	}

}