package org.openflexo.swing.converter;

import java.util.Enumeration;

import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.swing.LookAndFeel;

@Deprecated
public class LookAndFeelConverter extends Converter<LookAndFeel> {

	public LookAndFeelConverter() {
		super(LookAndFeel.class);
	}

	@Override
	public LookAndFeel convertFromString(String value, ModelFactory factory) {
		Enumeration<LookAndFeel> en = LookAndFeel.availableValues().elements();
		while (en.hasMoreElements()) {
			LookAndFeel laf = en.nextElement();
			if (laf.getClassName().equals(value)) {
				return laf;
			}
		}
		return LookAndFeel.getDefaultLookAndFeel();
	}

	@Override
	public String convertToString(LookAndFeel value) {
		return value.getClassName();
	};
}