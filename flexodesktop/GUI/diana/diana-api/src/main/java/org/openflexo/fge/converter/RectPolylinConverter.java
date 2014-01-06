package org.openflexo.fge.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectPolylin;
import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.exceptions.InvalidDataException;
import org.openflexo.model.factory.ModelFactory;

public class RectPolylinConverter extends Converter<FGERectPolylin> {
	public RectPolylinConverter(Class<? super FGERectPolylin> aClass) {
		super(aClass);
	}

	@Override
	public FGERectPolylin convertFromString(String value, ModelFactory factory) {
		try {
			List<FGEPoint> points = new ArrayList<FGEPoint>();
			StringTokenizer st = new StringTokenizer(value, ";");
			while (st.hasMoreTokens()) {
				String nextPoint = st.nextToken();
				try {
					points.add(FGEUtils.POINT_CONVERTER.convertFromString(nextPoint, factory));
				} catch (InvalidDataException e) {
					e.printStackTrace();
				}
			}
			return new FGERectPolylin(points);
		} catch (NumberFormatException e) {
			// Warns about the exception
			System.err.println("Supplied value is not parsable as a FGEPoint:" + value);
			return null;
		}
	}

	@Override
	public String convertToString(FGERectPolylin aPolylin) {
		if (aPolylin != null) {
			StringBuffer sb = new StringBuffer();
			boolean isFirst = true;
			for (FGEPoint pt : aPolylin.getPoints()) {
				if (!isFirst) {
					sb.append(";");
				}
				sb.append(FGEUtils.POINT_CONVERTER.convertToString(pt));
				isFirst = false;
			}
			return sb.toString();
		} else {
			return null;
		}
	}
}