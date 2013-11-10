package org.openflexo.fge.ppteditor;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.apache.poi.hslf.model.AutoShape;
import org.apache.poi.hslf.model.Picture;
import org.apache.poi.hslf.model.Shape;
import org.apache.poi.hslf.model.SimpleShape;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextBox;
import org.apache.poi.hslf.model.TextShape;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGECoreUtils;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.ForegroundStyle.DashStyle;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GRStructureVisitor;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;

public class SlideDrawing extends DrawingImpl<Slide> {

	public SlideDrawing(Slide slide) {
		super(slide, FGECoreUtils.TOOLS_FACTORY, PersistenceMode.UniqueGraphicalRepresentations);
	}

	@Override
	public void init() {

		final DrawingGraphicalRepresentation drawingGR = getFactory().makeDrawingGraphicalRepresentation();

		final DrawingGRBinding<Slide> slideBinding = bindDrawing(Slide.class, "slide", new DrawingGRProvider<Slide>() {
			@Override
			public DrawingGraphicalRepresentation provideGR(Slide drawable, FGEModelFactory factory) {
				return drawingGR;
			}
		});
		final ShapeGRBinding<Shape> shapeBinding = bindShape(Shape.class, "shape", new ShapeGRProvider<Shape>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(Shape drawable, FGEModelFactory factory) {
				return makeShapeGraphicalRepresentation(drawable);
			}
		});

		slideBinding.addToWalkers(new GRStructureVisitor<Slide>() {

			@Override
			public void visit(Slide slide) {
				for (Shape shape : slide.getShapes()) {
					drawShape(shapeBinding, shape, slide);
				}
			}
		});

		/*shapeBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), true);
		connectorBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), true);
		*/
	}

	private ShapeGraphicalRepresentation makeShapeGraphicalRepresentation(Shape shape) {
		ShapeGraphicalRepresentation returned = getFactory().makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		returned.setX(shape.getAnchor2D().getX());
		returned.setY(shape.getAnchor2D().getY());
		returned.setWidth(shape.getAnchor2D().getWidth());
		returned.setHeight(shape.getAnchor2D().getHeight());
		returned.setBorder(getFactory().makeShapeBorder(0, 0, 0, 0));

		if (shape instanceof SimpleShape) {

			SimpleShape simpleShape = (SimpleShape) shape;

			// returned.setForeground(getFactory().makeDefaultForegroundStyle());
			if (shape instanceof TextShape) {

				TextShape textShape = (TextShape) shape;

				returned.setText(textShape.getText());
				returned.setIsFloatingLabel(false);
				returned.setIsMultilineAllowed(true);
				// returned.setHorizontalTextAlignment(textShape.getHorizontalAlignment());

				if (shape instanceof TextBox) {
					returned.setForeground(getFactory().makeNoneForegroundStyle());
					returned.setBackground(getFactory().makeEmptyBackground());
					returned.setShadowStyle(getFactory().makeNoneShadowStyle());
				}

				else if (shape instanceof AutoShape) {

					returned.setForeground(getFactory().makeForegroundStyle(simpleShape.getLineColor(), (float) simpleShape.getLineWidth(),
							DashStyle.values()[simpleShape.getLineDashing()]));

					if (simpleShape.getFillColor() != null) {
						returned.setBackground(getFactory().makeColoredBackground(simpleShape.getFillColor()));
					} else {
						returned.setBackground(getFactory().makeEmptyBackground());
						returned.setShadowStyle(getFactory().makeNoneShadowStyle());
					}

				}
			} else if (shape instanceof Picture) {
				Picture pictureShape = (Picture) shape;

				BufferedImage image = new BufferedImage((int) shape.getAnchor2D().getWidth(), (int) shape.getAnchor2D().getHeight(),
						BufferedImage.TYPE_INT_RGB);
				Graphics2D graphics = image.createGraphics();
				graphics.translate(-shape.getAnchor2D().getX(), -shape.getAnchor2D().getY());
				// graphics.transform(AffineTransform.getScaleInstance(WIDTH / d.width, WIDTH / d.width));
				pictureShape.getPictureData().draw(graphics, pictureShape);
				returned.setBackground(getFactory().makeImageBackground(image));
				returned.setForeground(getFactory().makeNoneForegroundStyle());
			}
		}

		System.out.println("gr=" + getFactory().stringRepresentation(returned));
		return returned;
	}
}
