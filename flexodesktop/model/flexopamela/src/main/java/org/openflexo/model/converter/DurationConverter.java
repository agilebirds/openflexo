package org.openflexo.model.converter;

import java.text.ParseException;

import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.Duration;
import org.openflexo.toolbox.Duration.DurationUnit;

public class DurationConverter extends Converter<Duration> {

	public DurationConverter() {
		super(Duration.class);
	}

	@Override
	public Duration convertFromString(String aValue, ModelFactory factory) {
		try {
			return tryToConvertFromString(aValue);
		} catch (ParseException e) {
			// OK, abort
			return null;
		}
	}

	private Duration tryToConvertFromString(String aValue) throws ParseException {
		for (DurationUnit unit : DurationUnit.values()) {
			String unitSymbol = unit.getSymbol();
			if (aValue.endsWith(unitSymbol)) {
				try {
					long value = Long.parseLong(aValue.substring(0, aValue.length() - unitSymbol.length()));
					return new Duration(value, unit);
				} catch (NumberFormatException e) {
					// OK, abort
					throw new ParseException("Cannot parse as a Duration: " + aValue, -1);
				}
			}
		}
		throw new ParseException("Cannot parse as a Duration: " + aValue, -1);
	}

	@Override
	public String convertToString(Duration value) {
		if (value == null) {
			return "null";
		}
		return value.getSerializationRepresentation();
	}

}