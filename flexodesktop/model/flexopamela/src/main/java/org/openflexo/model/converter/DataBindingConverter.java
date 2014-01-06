package org.openflexo.model.converter;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.exceptions.InvalidDataException;
import org.openflexo.model.factory.ModelFactory;

public class DataBindingConverter extends Converter<DataBinding<?>> {

	public DataBindingConverter() {
		super(DataBinding.class);
	}

	@Override
	public DataBinding<?> convertFromString(String value, ModelFactory factory) throws InvalidDataException {
		return new DataBinding<Object>(value);
	}

	@Override
	public String convertToString(DataBinding<?> value) {
		if (value.isSet()) {
			return value.toString();
		}
		return null;
	}
}