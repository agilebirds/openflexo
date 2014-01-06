package org.openflexo.model.converter;

import java.util.logging.Level;

import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.StringUtils;

public class LevelConverter extends Converter<Level> {

	public LevelConverter() {
		super(Level.class);
	}

	@Override
	public Level convertFromString(String value, ModelFactory factory) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		if (value.equals("SEVERE")) {
			return Level.SEVERE;
		} else if (value.equals("WARNING")) {
			return Level.WARNING;
		} else if (value.equals("INFO")) {
			return Level.INFO;
		} else if (value.equals("CONFIG")) {
			return Level.CONFIG;
		} else if (value.equals("FINE")) {
			return Level.FINE;
		} else if (value.equals("FINER")) {
			return Level.FINER;
		} else if (value.equals("FINEST")) {
			return Level.FINEST;
		}
		return null;

	}

	@Override
	public String convertToString(Level value) {
		return value.getName();
	}
}