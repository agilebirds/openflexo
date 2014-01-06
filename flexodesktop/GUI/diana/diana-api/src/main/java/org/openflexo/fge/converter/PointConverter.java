package org.openflexo.fge.converter;

import java.util.StringTokenizer;

import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.factory.ModelFactory;

public class PointConverter extends Converter<FGEPoint> {
	public PointConverter(Class<? super FGEPoint> aClass) {
		super(aClass);
	}

	@Override
	public FGEPoint convertFromString(String value, ModelFactory factory) {
		try {
			FGEPoint returned = new FGEPoint();
			StringTokenizer st = new StringTokenizer(value, ",");
			if (st.hasMoreTokens()) {
				returned.x = Double.parseDouble(st.nextToken());
			}
			if (st.hasMoreTokens()) {
				returned.y = Double.parseDouble(st.nextToken());
			}
			return returned;
		} catch (NumberFormatException e) {
			// Warns about the exception
			System.err.println("Supplied value is not parsable as a FGEPoint:" + value);
			return null;
		}
	}

	@Override
	public String convertToString(FGEPoint aPoint) {
		if (aPoint != null) {
			return aPoint.x + "," + aPoint.y;
		} else {
			return null;
		}
	}
}