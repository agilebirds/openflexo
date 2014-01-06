package org.openflexo.fge.converter;

import java.util.StringTokenizer;

import org.openflexo.fge.geom.FGESteppedDimensionConstraint;
import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.factory.ModelFactory;

public class SteppedDimensionConverter extends Converter<FGESteppedDimensionConstraint> {
	public SteppedDimensionConverter(Class<? super FGESteppedDimensionConstraint> aClass) {
		super(aClass);
	}

	@Override
	public FGESteppedDimensionConstraint convertFromString(String value, ModelFactory factory) {
		try {
			Double hStep = null;
			Double vStep = null;
			StringTokenizer st = new StringTokenizer(value, ",");
			if (st.hasMoreTokens()) {
				hStep = Double.parseDouble(st.nextToken());
			}
			if (st.hasMoreTokens()) {
				vStep = Double.parseDouble(st.nextToken());
			}
			return new FGESteppedDimensionConstraint(hStep, vStep);
		} catch (NumberFormatException e) {
			// Warns about the exception
			System.err.println("Supplied value is not parsable as a FGESteppedDimensionConstraint:" + value);
			return null;
		}
	}

	@Override
	public String convertToString(FGESteppedDimensionConstraint aDim) {
		if (aDim != null) {
			return aDim.getHorizontalStep() + "," + aDim.getVerticalStep();
		} else {
			return null;
		}
	}
}