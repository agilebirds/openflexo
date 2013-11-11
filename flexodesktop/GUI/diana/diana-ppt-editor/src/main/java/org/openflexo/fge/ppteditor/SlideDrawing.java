package org.openflexo.fge.ppteditor;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import org.apache.poi.hslf.model.AutoShape;
import org.apache.poi.hslf.model.MasterSheet;
import org.apache.poi.hslf.model.Picture;
import org.apache.poi.hslf.model.Shape;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextBox;
import org.apache.poi.hslf.model.TextRun;
import org.apache.poi.hslf.model.TextShape;
import org.apache.poi.hslf.usermodel.RichTextRun;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGECoreUtils;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.ForegroundStyle.DashStyle;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GRStructureVisitor;
import org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.fge.GraphicalRepresentation.ParagraphAlignment;
import org.openflexo.fge.GraphicalRepresentation.VerticalTextAlignment;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;

public class SlideDrawing extends DrawingImpl<Slide> {

	private static final Logger logger = Logger.getLogger(SlideDrawing.class.getPackage().getName());

	public SlideDrawing(Slide slide) {
		super(slide, FGECoreUtils.TOOLS_FACTORY, PersistenceMode.UniqueGraphicalRepresentations);
	}

	@Override
	public void init() {

		final DrawingGraphicalRepresentation drawingGR = getFactory().makeDrawingGraphicalRepresentation();
		drawingGR.setDrawWorkingArea(true);
		drawingGR.setWidth(getModel().getSlideShow().getPageSize().getWidth());
		drawingGR.setHeight(getModel().getSlideShow().getPageSize().getHeight());

		final DrawingGRBinding<Slide> slideBinding = bindDrawing(Slide.class, "slide", new DrawingGRProvider<Slide>() {
			@Override
			public DrawingGraphicalRepresentation provideGR(Slide drawable, FGEModelFactory factory) {
				return drawingGR;
			}
		});
		final ShapeGRBinding<AutoShape> autoShapeBinding = bindShape(AutoShape.class, "autoShape", new ShapeGRProvider<AutoShape>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(AutoShape drawable, FGEModelFactory factory) {
				return makeAutoShapeGraphicalRepresentation(drawable);
			}
		});

		final ShapeGRBinding<TextBox> textBoxBinding = bindShape(TextBox.class, "textBox", new ShapeGRProvider<TextBox>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(TextBox drawable, FGEModelFactory factory) {
				return makeTextBoxGraphicalRepresentation(drawable);
			}
		});

		final ShapeGRBinding<Picture> pictureBinding = bindShape(Picture.class, "picture", new ShapeGRProvider<Picture>() {
			@Override
			public ShapeGraphicalRepresentation provideGR(Picture drawable, FGEModelFactory factory) {
				return makePictureGraphicalRepresentation(drawable);
			}
		});

		slideBinding.addToWalkers(new GRStructureVisitor<Slide>() {

			@Override
			public void visit(Slide slide) {

				MasterSheet master = slide.getMasterSheet();

				if (slide.getFollowMasterObjects()) {
					Shape[] sh = master.getShapes();
					for (int i = sh.length - 1; i >= 0; i--) {
						if (MasterSheet.isPlaceholder(sh[i]))
							continue;
						Shape shape = sh[i];
						if (shape instanceof Picture) {
							drawShape(pictureBinding, (Picture) shape, slide);
						} else if (shape instanceof AutoShape) {
							drawShape(autoShapeBinding, (AutoShape) shape, slide);
						} else if (shape instanceof TextBox) {
							drawShape(textBoxBinding, (TextBox) shape, slide);
						}
					}
				}

				for (Shape shape : slide.getShapes()) {
					if (shape instanceof Picture) {
						drawShape(pictureBinding, (Picture) shape, slide);
					} else if (shape instanceof AutoShape) {
						drawShape(autoShapeBinding, (AutoShape) shape, slide);
					} else if (shape instanceof TextBox) {
						drawShape(textBoxBinding, (TextBox) shape, slide);
					}
				}
			}
		});

		/*shapeBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), true);
		connectorBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), true);
		*/
	}

	private ShapeGraphicalRepresentation makeAutoShapeGraphicalRepresentation(AutoShape autoShape) {
		ShapeGraphicalRepresentation returned = getFactory().makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		returned.setX(autoShape.getAnchor2D().getX());
		returned.setY(autoShape.getAnchor2D().getY());
		returned.setWidth(autoShape.getAnchor2D().getWidth());
		returned.setHeight(autoShape.getAnchor2D().getHeight());
		returned.setBorder(getFactory().makeShapeBorder(0, 0, 0, 0));

		returned.setShadowStyle(getFactory().makeDefaultShadowStyle());

		if (autoShape.getLineColor() != null) {
			returned.setForeground(getFactory().makeForegroundStyle(autoShape.getLineColor(), (float) autoShape.getLineWidth(),
					DashStyle.values()[autoShape.getLineDashing()]));
		} else {
			returned.setForeground(getFactory().makeNoneForegroundStyle());
		}

		if (autoShape.getFillColor() != null) {
			returned.setBackground(getFactory().makeColoredBackground(autoShape.getFillColor()));
		} else {
			returned.setBackground(getFactory().makeEmptyBackground());
			returned.setShadowStyle(getFactory().makeNoneShadowStyle());
		}

		setTextProperties(returned, autoShape);

		System.out.println("autoshape text=" + autoShape.getText());
		System.out.println("getFillColor()=" + autoShape.getFillColor());
		System.out.println("getLineStyle()=" + autoShape.getLineStyle());
		System.out.println("getLineColor()=" + autoShape.getLineColor());
		System.out.println("getLineWidth()=" + autoShape.getLineWidth());

		System.out.println("gr=" + getFactory().stringRepresentation(returned));
		return returned;
	}

	private void setTextProperties(ShapeGraphicalRepresentation returned, TextShape textShape) {

		if (textShape.getTextRun() != null) {
			TextRun textRun = textShape.getTextRun();
			RichTextRun[] rt = textRun.getRichTextRuns();

			if (rt.length > 0) {
				RichTextRun rtr = rt[0];
				String fontName = rtr.getFontName();
				int fontSize = rtr.getFontSize();
				Color color = rtr.getFontColor();
				int fontStyle = Font.PLAIN | (rtr.isBold() ? Font.BOLD : Font.PLAIN) | (rtr.isItalic() ? Font.ITALIC : Font.PLAIN);
				Font f = new Font(fontName, fontStyle, fontSize);
				TextStyle textStyle = getFactory().makeTextStyle(color, f);
				returned.setTextStyle(textStyle);
			}
		}

		returned.setText(textShape.getText());

		returned.setIsFloatingLabel(false);
		returned.setIsMultilineAllowed(true);

		returned.setRelativeTextX(0.5);
		returned.setRelativeTextY(0.5);

		try {
			switch (textShape.getVerticalAlignment()) {
			case TextShape.AnchorTop:
				returned.setVerticalTextAlignment(VerticalTextAlignment.BOTTOM);
				break;
			case TextShape.AnchorMiddle:
				returned.setVerticalTextAlignment(VerticalTextAlignment.MIDDLE);
				break;
			case TextShape.AnchorBottom:
				returned.setVerticalTextAlignment(VerticalTextAlignment.TOP);
				break;
			case TextShape.AnchorTopCentered:
				returned.setVerticalTextAlignment(VerticalTextAlignment.BOTTOM);
				break;
			case TextShape.AnchorMiddleCentered:
				returned.setVerticalTextAlignment(VerticalTextAlignment.MIDDLE);
				break;
			case TextShape.AnchorBottomCentered:
				returned.setVerticalTextAlignment(VerticalTextAlignment.TOP);
				break;
			case TextShape.AnchorTopBaseline:
				returned.setVerticalTextAlignment(VerticalTextAlignment.BOTTOM);
				break;
			case TextShape.AnchorBottomBaseline:
				returned.setVerticalTextAlignment(VerticalTextAlignment.TOP);
				break;
			case TextShape.AnchorTopCenteredBaseline:
				returned.setVerticalTextAlignment(VerticalTextAlignment.BOTTOM);
				break;
			case TextShape.AnchorBottomCenteredBaseline:
				returned.setVerticalTextAlignment(VerticalTextAlignment.TOP);
				break;
			}
		} catch (NullPointerException e) {
			logger.warning("Unexpected POI exception while retrieving vertical alignment");
		}

		switch (textShape.getHorizontalAlignment()) {
		case TextShape.AlignLeft:
			returned.setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);
			returned.setParagraphAlignment(ParagraphAlignment.LEFT);
			break;
		case TextShape.AlignCenter:
			returned.setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
			returned.setParagraphAlignment(ParagraphAlignment.CENTER);
			break;
		case TextShape.AlignRight:
			returned.setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
			returned.setParagraphAlignment(ParagraphAlignment.RIGHT);
			break;
		case TextShape.AlignJustify:
			returned.setParagraphAlignment(ParagraphAlignment.RIGHT);
			returned.setParagraphAlignment(ParagraphAlignment.JUSTIFY);
			break;
		}

		returned.setLineWrap(true);

	}

	private ShapeGraphicalRepresentation makeTextBoxGraphicalRepresentation(TextBox textBox) {
		ShapeGraphicalRepresentation returned = getFactory().makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		returned.setX(textBox.getAnchor2D().getX());
		returned.setY(textBox.getAnchor2D().getY());
		returned.setWidth(textBox.getAnchor2D().getWidth());
		returned.setHeight(textBox.getAnchor2D().getHeight());
		returned.setBorder(getFactory().makeShapeBorder(0, 0, 0, 0));

		returned.setForeground(getFactory().makeNoneForegroundStyle());

		returned.setBackground(getFactory().makeEmptyBackground());
		returned.setShadowStyle(getFactory().makeNoneShadowStyle());

		setTextProperties(returned, textBox);

		System.out.println("textbox text=" + textBox.getText() + " gr=" + getFactory().stringRepresentation(returned));
		return returned;
	}

	private ShapeGraphicalRepresentation makePictureGraphicalRepresentation(Picture pictureShape) {
		ShapeGraphicalRepresentation returned = getFactory().makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		returned.setX(pictureShape.getAnchor2D().getX());
		returned.setY(pictureShape.getAnchor2D().getY());
		returned.setWidth(pictureShape.getAnchor2D().getWidth());
		returned.setHeight(pictureShape.getAnchor2D().getHeight());
		returned.setBorder(getFactory().makeShapeBorder(0, 0, 0, 0));

		BufferedImage image = new BufferedImage((int) pictureShape.getAnchor2D().getWidth(), (int) pictureShape.getAnchor2D().getHeight(),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
		graphics.translate(-pictureShape.getAnchor2D().getX(), -pictureShape.getAnchor2D().getY());
		graphics.clipRect((int) pictureShape.getAnchor2D().getX(), (int) pictureShape.getAnchor2D().getY(), (int) pictureShape
				.getAnchor2D().getWidth(), (int) pictureShape.getAnchor2D().getHeight());
		// graphics.transform(AffineTransform.getScaleInstance(WIDTH / d.width, WIDTH / d.width));

		graphics.setPaint(Color.WHITE);
		graphics.fillRect((int) pictureShape.getAnchor2D().getX(), (int) pictureShape.getAnchor2D().getY(), (int) pictureShape
				.getAnchor2D().getWidth(), (int) pictureShape.getAnchor2D().getHeight());

		pictureShape.getPictureData().draw(graphics, pictureShape);
		returned.setBackground(getFactory().makeImageBackground(image));
		returned.setForeground(getFactory().makeNoneForegroundStyle());

		System.out.println("picture gr = " + getFactory().stringRepresentation(returned));
		return returned;
	}
}
