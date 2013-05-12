package org.openflexo.wkf.utils;

import java.awt.geom.AffineTransform;

import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.graphics.ShapePainter;
import org.openflexo.foundation.wkf.WKFDataObject;
import org.openflexo.foundation.wkf.WKFDataObject.DataObjectType;

public class DataObjectShapePainter implements ShapePainter {

	private static final FGEPolygon ARROW = new FGEPolygon(Filling.NOT_FILLED, new FGEPoint(0, 0.3), new FGEPoint(0.6, 0.3), new FGEPoint(
			0.6, 0.12), new FGEPoint(1, 0.5), new FGEPoint(0.6, 0.88), new FGEPoint(0.6, 0.7), new FGEPoint(0, 0.7)).transform(
			AffineTransform.getScaleInstance(0.24, 0.24)).transform(AffineTransform.getTranslateInstance(0.25, 0.05));

	private static final FGERectangle RECT = new FGERectangle(0.485, 0.85, 0.03, 0.12);
	private static final FGERectangle LEFT = new FGERectangle(RECT);
	private static final FGERectangle RIGHT = new FGERectangle(RECT);
	static {
		LEFT.x -= RECT.width * 1.5;
		RIGHT.x += RECT.width * 1.5;
	}
	private final ShapeGraphicalRepresentation<WKFDataObject> graphicalRepresentation;

	public DataObjectShapePainter(ShapeGraphicalRepresentation<WKFDataObject> graphicalRepresentation) {
		super();
		this.graphicalRepresentation = graphicalRepresentation;
	}

	@Override
	public void paintShape(FGEShapeGraphics g) {
		if (graphicalRepresentation.getDrawable().getType() == DataObjectType.INPUT) {
			g.drawPolygon(ARROW);
		} else if (graphicalRepresentation.getDrawable().getType() == DataObjectType.OUTPUT) {
			g.fillPolygon(ARROW);
		}
		if (graphicalRepresentation.getDrawable().isCollection()) {
			g.fillRect(LEFT.x, LEFT.y, LEFT.width, LEFT.height);
			g.fillRect(RECT.x, RECT.y, RECT.width, RECT.height);
			g.fillRect(RIGHT.x, RIGHT.y, RIGHT.width, RIGHT.height);
		}
	}
}
