/**
 * 
 */
package org.openflexo.antar.binding;

import org.openflexo.toolbox.Duration.DurationStringConverter;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.DateConverter;

public class StaticBindingFactory extends StringEncoder.Converter<StaticBinding> {

	private final DateConverter dateConverter = new DateConverter();
	private final DurationStringConverter durationConverter = new DurationStringConverter();
	boolean warnOnFailure = true;

	public StaticBindingFactory() {
		super(StaticBinding.class);
	}

	public void setWarnOnFailure(boolean aFlag) {
		warnOnFailure = aFlag;
	}

	@Override
	public StaticBinding convertFromString(String value) {
		return convertFromString(value, null);
	}

	public StaticBinding convertFromString(String aValue, Bindable bindable) {
		if (aValue.startsWith("$") && aValue.length() > 1) {
			return convertFromString(aValue.substring(1));
		}

		if (aValue.equalsIgnoreCase("true") || aValue.equalsIgnoreCase("yes")) {
			return new BooleanStaticBinding(true);
		} else if (aValue.equalsIgnoreCase("false") || aValue.equalsIgnoreCase("no")) {
			return new BooleanStaticBinding(false);
		} else if (aValue.startsWith("\"") && aValue.endsWith("\"") && aValue.length() > 1) {
			return new StringStaticBinding(aValue.substring(1, aValue.length() - 1));
		} else if (aValue.startsWith("'") && aValue.endsWith("'") && aValue.length() > 1) {
			return new StringStaticBinding(aValue.substring(1, aValue.length() - 1));
		}
		try {
			return new IntegerStaticBinding(Long.parseLong(aValue));
		} catch (NumberFormatException e) {
			try {
				return new FloatStaticBinding(Double.parseDouble(aValue));
			} catch (NumberFormatException e2) {
			}
		}
		// Found nothing....
		return null;
	}

	@Override
	public String convertToString(StaticBinding value) {
		return value.getSerializationRepresentation();
	}

}