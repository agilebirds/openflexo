package org.openflexo.fge;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.util.logging.Logger;

import org.openflexo.fge.BackgroundStyle.BackgroundStyleType;
import org.openflexo.fge.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.fge.ForegroundStyle.CapStyle;
import org.openflexo.fge.ForegroundStyle.DashStyle;
import org.openflexo.fge.ForegroundStyle.JoinStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation.ShapeBorder;
import org.openflexo.fge.TextureBackgroundStyle.TextureType;
import org.openflexo.fge.connectors.Connector;
import org.openflexo.fge.connectors.Connector.ConnectorType;
import org.openflexo.fge.connectors.CurveConnector;
import org.openflexo.fge.connectors.CurvedPolylinConnector;
import org.openflexo.fge.connectors.LineConnector;
import org.openflexo.fge.connectors.rpc.RectPolylinConnector;
import org.openflexo.fge.controller.MouseClickControl;
import org.openflexo.fge.controller.MouseClickControlAction.MouseClickControlActionType;
import org.openflexo.fge.controller.MouseControl.MouseButton;
import org.openflexo.fge.controller.MouseDragControl;
import org.openflexo.fge.controller.MouseDragControlAction.MouseDragControlActionType;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.shapes.Arc;
import org.openflexo.fge.shapes.Circle;
import org.openflexo.fge.shapes.Losange;
import org.openflexo.fge.shapes.Oval;
import org.openflexo.fge.shapes.Polygon;
import org.openflexo.fge.shapes.Rectangle;
import org.openflexo.fge.shapes.RectangularOctogon;
import org.openflexo.fge.shapes.RegularPolygon;
import org.openflexo.fge.shapes.Shape;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.fge.shapes.Square;
import org.openflexo.fge.shapes.Star;
import org.openflexo.fge.shapes.Triangle;
import org.openflexo.model.ModelContextLibrary;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.ToolBox;

/**
 * This is the default PAMELA model factory for class involved in FlexoGraphicalEngine model management<br>
 * All objects involved in a FGE model should be created using this factory in order to be well managed by PAMELA framework.<br>
 * In particular, please note that all objects belonging to the closure of an object graph managed using PAMELA must share the same factory.
 * 
 * This class also provides support for XML serializer/deserializer
 * 
 * Please feel free to override this class
 * 
 * @author sylvain
 * 
 */
public class FGEModelFactory extends ModelFactory {

	static final Logger logger = Logger.getLogger(FGEModelFactory.class.getPackage().getName());

	/**
	 * Creates a new model factory.
	 * 
	 * @throws ModelDefinitionException
	 */
	public FGEModelFactory() throws ModelDefinitionException {

		super(ModelContextLibrary.getCompoundModelContext(GraphicalRepresentation.class, DrawingGraphicalRepresentation.class,
				ShapeGraphicalRepresentation.class, ConnectorGraphicalRepresentation.class, GeometricGraphicalRepresentation.class));

		getStringEncoder().addConverter(FGEUtils.DATA_BINDING_CONVERTER);
		getStringEncoder().addConverter(FGEUtils.POINT_CONVERTER);
		getStringEncoder().addConverter(FGEUtils.STEPPED_DIMENSION_CONVERTER);

		logger.info("Created new FGEModelFactory...............................");

	}

	/**
	 * Creates and return a new DrawingGraphicalRepresentation, given a Drawing instance, and initialized with default values and default
	 * controls (drawing selection, rectangle selection and zoom)
	 * 
	 * @param aDrawing
	 * @return a newly created DrawingGraphicalRepresentation
	 */
	public <M> DrawingGraphicalRepresentation<M> makeDrawingGraphicalRepresentation(Drawing<M> aDrawing) {
		return makeDrawingGraphicalRepresentation(aDrawing, true);
	}

	/**
	 * Creates and return a new DrawingGraphicalRepresentation, given a Drawing instance, and initialized with default values and a flag
	 * indicating if default controls should be created (drawing selection, rectangle selection and zoom)
	 * 
	 * @param aDrawing
	 * @return a newly created DrawingGraphicalRepresentation
	 */
	public <M> DrawingGraphicalRepresentation<M> makeDrawingGraphicalRepresentation(Drawing<M> aDrawing, boolean initBasicControls) {
		DrawingGraphicalRepresentation<M> returned = newInstance(DrawingGraphicalRepresentation.class, true, initBasicControls);
		returned.setFGEModelFactory(this);
		returned.setDrawable(aDrawing.getModel());
		returned.setDrawing(aDrawing);
		return returned;
	}

	/**
	 * Sets and apply default properties (text style, background and size) to supplied DrawingGraphicalRepresentation
	 * 
	 * @param drawingGraphicalRepresentation
	 */
	public void applyDefaultProperties(DrawingGraphicalRepresentation<?> drawingGraphicalRepresentation) {
		drawingGraphicalRepresentation.setTextStyle(makeDefaultTextStyle());
		drawingGraphicalRepresentation.setBackgroundColor(FGEConstants.DEFAULT_DRAWING_BACKGROUND_COLOR);
		drawingGraphicalRepresentation.setWidth(FGEConstants.DEFAULT_DRAWING_WIDTH);
		drawingGraphicalRepresentation.setHeight(FGEConstants.DEFAULT_DRAWING_HEIGHT);
	}

	/**
	 * Sets and apply default basic controls (drawing selection, rectangle selection and zoom) to supplied DrawingGraphicalRepresentation
	 * 
	 * @param drawingGraphicalRepresentation
	 */
	public void applyBasicControls(DrawingGraphicalRepresentation<?> drawingGraphicalRepresentation) {
		drawingGraphicalRepresentation.addToMouseClickControls(MouseClickControl.makeMouseClickControl("Drawing selection",
				MouseButton.LEFT, 1, MouseClickControlActionType.SELECTION));
		drawingGraphicalRepresentation.addToMouseDragControls(MouseDragControl.makeMouseDragControl("Rectangle selection",
				MouseButton.LEFT, MouseDragControlActionType.RECTANGLE_SELECTING));
		drawingGraphicalRepresentation.addToMouseDragControls(MouseDragControl.makeMouseDragControl("Zoom", MouseButton.RIGHT,
				MouseDragControlActionType.ZOOM));
	}

	/**
	 * Creates and return a new ShapeGraphicalRepresentation, given a Drawable and a Drawing instance, initialized with default values as a
	 * rectangle shape
	 * 
	 * @param aDrawing
	 *            the drawing where the shape is defined
	 * @param aDrawable
	 *            object supposed to be represented by returned graphical representation
	 * 
	 * @return a newly created ShapeGraphicalRepresentation
	 */
	public <O> ShapeGraphicalRepresentation<O> makeShapeGraphicalRepresentation(O aDrawable, Drawing<?> aDrawing) {

		ShapeGraphicalRepresentation<O> returned = newInstance(ShapeGraphicalRepresentation.class, true, true);
		returned.setFGEModelFactory(this);
		returned.setDrawable(aDrawable);
		returned.setDrawing(aDrawing);
		return returned;
	}

	/**
	 * Sets and apply default properties (border, layer, text style, foreground, background and shadow) to supplied
	 * ShapeGraphicalRepresentation
	 * 
	 * @param shapeGraphicalRepresentation
	 */
	public void applyDefaultProperties(ShapeGraphicalRepresentation<?> shapeGraphicalRepresentation) {
		shapeGraphicalRepresentation.setBorder(makeShapeBorder());
		shapeGraphicalRepresentation.setLayer(FGEConstants.DEFAULT_SHAPE_LAYER);
		shapeGraphicalRepresentation.setTextStyle(makeDefaultTextStyle());
		shapeGraphicalRepresentation.setForeground(makeDefaultForegroundStyle());
		shapeGraphicalRepresentation.setBackground(makeColoredBackground(Color.WHITE));
		shapeGraphicalRepresentation.setShadowStyle(makeDefaultShadowStyle());
	}

	/**
	 * Sets and apply default basic controls (drawing selection, rectangle selection and zoom) to supplied ShapeGraphicalRepresentation
	 * 
	 * @param shapeGraphicalRepresentation
	 */
	public void applyBasicControls(ShapeGraphicalRepresentation<?> shapeGraphicalRepresentation) {
		shapeGraphicalRepresentation.addToMouseClickControls(MouseClickControl.makeMouseClickControl("Selection", MouseButton.LEFT, 1,
				MouseClickControlActionType.SELECTION));
		if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			shapeGraphicalRepresentation.addToMouseClickControls(MouseClickControl.makeMouseMetaClickControl("Multiple selection",
					MouseButton.LEFT, 1, MouseClickControlActionType.MULTIPLE_SELECTION));
		} else {
			shapeGraphicalRepresentation.addToMouseClickControls(MouseClickControl.makeMouseControlClickControl("Multiple selection",
					MouseButton.LEFT, 1, MouseClickControlActionType.MULTIPLE_SELECTION));
		}
		shapeGraphicalRepresentation.addToMouseDragControls(MouseDragControl.makeMouseDragControl("Move", MouseButton.LEFT,
				MouseDragControlActionType.MOVE));
		shapeGraphicalRepresentation.addToMouseDragControls(MouseDragControl.makeMouseDragControl("Zoom", MouseButton.RIGHT,
				MouseDragControlActionType.ZOOM));
		shapeGraphicalRepresentation.addToMouseDragControls(MouseDragControl.makeMouseShiftDragControl("Rectangle selection",
				MouseButton.LEFT, MouseDragControlActionType.RECTANGLE_SELECTING));
	}

	/**
	 * Creates and return a new ShapeGraphicalRepresentation, given a Drawable and a Drawing instance, initialized with default values and
	 * given a shape type
	 * 
	 * @param aDrawing
	 *            the drawing where the shape is defined
	 * @param aDrawable
	 *            object supposed to be represented by returned graphical representation
	 * 
	 * @return a newly created ShapeGraphicalRepresentation
	 */
	public <O> ShapeGraphicalRepresentation<O> makeShapeGraphicalRepresentation(ShapeType shapeType, O aDrawable, Drawing<?> aDrawing) {
		ShapeGraphicalRepresentation<O> returned = makeShapeGraphicalRepresentation(aDrawable, aDrawing);
		returned.setShapeType(shapeType);
		return returned;
	}

	/**
	 * Creates and return a new ShapeGraphicalRepresentation, given a Drawable and a Drawing instance, initialized with default values and
	 * given a shape type
	 * 
	 * @param aDrawing
	 *            the drawing where the shape is defined
	 * @param aDrawable
	 *            object supposed to be represented by returned graphical representation
	 * 
	 * @return a newly created ShapeGraphicalRepresentation
	 */
	public <O> ShapeGraphicalRepresentation<O> makeShapeGraphicalRepresentation(ShapeGraphicalRepresentation<O> aGR, O aDrawable,
			Drawing<?> aDrawing) {
		ShapeGraphicalRepresentation<O> returned = makeShapeGraphicalRepresentation(aDrawable, aDrawing);
		returned.setsWith(aGR);
		return returned;
	}

	/**
	 * Creates and return a new ConnectorGraphicalRepresentation, given a Drawable and a Drawing instance, initialized with default values
	 * 
	 * @param aDrawing
	 *            the drawing where the shape is defined
	 * @param aDrawable
	 *            object supposed to be represented by returned graphical representation
	 * 
	 * @return a newly created ConnectorGraphicalRepresentation
	 */
	public <O> ConnectorGraphicalRepresentation<O> makeConnectorGraphicalRepresentation(O aDrawable, Drawing<?> aDrawing) {

		ConnectorGraphicalRepresentation<O> returned = newInstance(ConnectorGraphicalRepresentation.class, true, true);
		returned.setFGEModelFactory(this);
		returned.setDrawable(aDrawable);
		returned.setDrawing(aDrawing);
		return returned;
	}

	/**
	 * Creates and return a new ConnectorGraphicalRepresentation, given a Drawable and a Drawing instance, initialized with default values.<br>
	 * The newly created connector is also initialized with connector type, and bound to supplied start and end shapes
	 * 
	 * @param aDrawing
	 *            the drawing where the shape is defined
	 * @param aDrawable
	 *            object supposed to be represented by returned graphical representation
	 * 
	 * @return a newly created ConnectorGraphicalRepresentation
	 */
	public <O> ConnectorGraphicalRepresentation<O> makeConnectorGraphicalRepresentation(ConnectorType aConnectorType,
			ShapeGraphicalRepresentation<?> aStartObject, ShapeGraphicalRepresentation<?> anEndObject, O aDrawable, Drawing<?> aDrawing) {

		ConnectorGraphicalRepresentation<O> returned = makeConnectorGraphicalRepresentation(aDrawable, aDrawing);
		returned.setStartObject(aStartObject);
		returned.setEndObject(anEndObject);
		returned.setConnectorType(aConnectorType);

		return returned;
	}

	/**
	 * Sets and apply default properties (layer, text style, foreground) to supplied ConnectorGraphicalRepresentation
	 * 
	 * @param connectorGraphicalRepresentation
	 */
	public void applyDefaultProperties(ConnectorGraphicalRepresentation<?> connectorGraphicalRepresentation) {
		connectorGraphicalRepresentation.setLayer(FGEConstants.DEFAULT_CONNECTOR_LAYER);
		connectorGraphicalRepresentation.setForeground(makeDefaultForegroundStyle());
		connectorGraphicalRepresentation.setTextStyle(makeDefaultTextStyle());
	}

	/**
	 * Sets and apply default basic controls (drawing selection, rectangle selection and zoom) to supplied ConnectorGraphicalRepresentation
	 * 
	 * @param connectorGraphicalRepresentation
	 */
	public void applyBasicControls(ConnectorGraphicalRepresentation<?> connectorGraphicalRepresentation) {
		connectorGraphicalRepresentation.addToMouseClickControls(MouseClickControl.makeMouseClickControl("Selection", MouseButton.LEFT, 1,
				MouseClickControlActionType.SELECTION));
		if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			connectorGraphicalRepresentation.addToMouseClickControls(MouseClickControl.makeMouseMetaClickControl("Multiple selection",
					MouseButton.LEFT, 1, MouseClickControlActionType.MULTIPLE_SELECTION));
		} else {
			connectorGraphicalRepresentation.addToMouseClickControls(MouseClickControl.makeMouseControlClickControl("Multiple selection",
					MouseButton.LEFT, 1, MouseClickControlActionType.MULTIPLE_SELECTION));
		}
	}

	/**
	 * Creates and return a new GeometricGraphicalRepresentation, given a Drawable and a Drawing instance, initialized with default values
	 * 
	 * @param aDrawing
	 *            the drawing where the shape is defined
	 * @param aDrawable
	 *            object supposed to be represented by returned graphical representation
	 * 
	 * @return a newly created GeometricGraphicalRepresentation
	 */
	public <O> GeometricGraphicalRepresentation<O> makeGeometricGraphicalRepresentation(O aDrawable, Drawing<?> aDrawing) {

		GeometricGraphicalRepresentation<O> returned = newInstance(GeometricGraphicalRepresentation.class, true, true);
		returned.setFGEModelFactory(this);
		returned.setDrawable(aDrawable);
		returned.setDrawing(aDrawing);

		applyDefaultProperties(returned);
		applyBasicControls(returned);

		return returned;
	}

	/**
	 * Creates and return a new GeometricGraphicalRepresentation, given a Drawable and a Drawing instance, initialized with default values
	 * 
	 * @param aDrawing
	 *            the drawing where the shape is defined
	 * @param aDrawable
	 *            object supposed to be represented by returned graphical representation
	 * 
	 * @return a newly created GeometricGraphicalRepresentation
	 */
	public <O> GeometricGraphicalRepresentation<O> makeGeometricGraphicalRepresentation(FGEArea geometricObject, O aDrawable,
			Drawing<?> aDrawing) {

		GeometricGraphicalRepresentation<O> returned = makeGeometricGraphicalRepresentation(aDrawable, aDrawing);
		returned.setGeometricObject(geometricObject);
		return returned;
	}

	/**
	 * Sets and apply default properties (layer, text style, foreground) to supplied ConnectorGraphicalRepresentation
	 * 
	 * @param connectorGraphicalRepresentation
	 */
	public void applyDefaultProperties(GeometricGraphicalRepresentation<?> geometricGraphicalRepresentation) {
		geometricGraphicalRepresentation.setLayer(FGEConstants.DEFAULT_OBJECT_LAYER);
		geometricGraphicalRepresentation.setForeground(makeDefaultForegroundStyle());
		geometricGraphicalRepresentation.setBackground(makeColoredBackground(Color.WHITE));
		geometricGraphicalRepresentation.setTextStyle(makeDefaultTextStyle());
	}

	/**
	 * Sets and apply default basic controls (drawing selection, rectangle selection and zoom) to supplied ConnectorGraphicalRepresentation
	 * 
	 * @param connectorGraphicalRepresentation
	 */
	public void applyBasicControls(GeometricGraphicalRepresentation<?> geometricGraphicalRepresentation) {
		geometricGraphicalRepresentation.addToMouseClickControls(MouseClickControl.makeMouseClickControl("Selection", MouseButton.LEFT, 1,
				MouseClickControlActionType.SELECTION));
		if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			geometricGraphicalRepresentation.addToMouseClickControls(MouseClickControl.makeMouseMetaClickControl("Multiple selection",
					MouseButton.LEFT, 1, MouseClickControlActionType.MULTIPLE_SELECTION));
		} else {
			geometricGraphicalRepresentation.addToMouseClickControls(MouseClickControl.makeMouseControlClickControl("Multiple selection",
					MouseButton.LEFT, 1, MouseClickControlActionType.MULTIPLE_SELECTION));
		}
	}

	public Connector makeConnector(ConnectorType type, ConnectorGraphicalRepresentation<?> aGraphicalRepresentation) {

		if (type == ConnectorType.LINE) {
			return makeLineConnector(aGraphicalRepresentation);
		} else if (type == ConnectorType.RECT_POLYLIN) {
			return makeRectPolylinConnector(aGraphicalRepresentation);
		} else if (type == ConnectorType.CURVE) {
			return makeCurveConnector(aGraphicalRepresentation);
		} else if (type == ConnectorType.CURVED_POLYLIN) {
			return makeCurvedPolylinConnector(aGraphicalRepresentation);
		} else if (type == ConnectorType.CUSTOM) {
			return null;
		}
		logger.warning("Unexpected type: " + type);
		return null;

	}

	/**
	 * Creates and return a new LineConnector, given a connector graphical representation
	 * 
	 * @param aGR
	 * @return a newly created LineConnector
	 */
	public LineConnector makeLineConnector(ConnectorGraphicalRepresentation<?> aGR) {
		LineConnector returned = newInstance(LineConnector.class);
		returned.setFGEModelFactory(this);
		returned.setGraphicalRepresentation(aGR);
		return returned;
	}

	/**
	 * Creates and return a new CurveConnector, given a connector graphical representation
	 * 
	 * @param aGR
	 * @return a newly created CurveConnector
	 */
	public CurveConnector makeCurveConnector(ConnectorGraphicalRepresentation<?> aGR) {
		CurveConnector returned = newInstance(CurveConnector.class);
		returned.setFGEModelFactory(this);
		returned.setGraphicalRepresentation(aGR);
		return returned;
	}

	/**
	 * Creates and return a new RectPolylinConnector, given a connector graphical representation
	 * 
	 * @param aGR
	 * @return a newly created RectPolylinConnector
	 */
	public RectPolylinConnector makeRectPolylinConnector(ConnectorGraphicalRepresentation<?> aGR) {
		RectPolylinConnector returned = newInstance(RectPolylinConnector.class);
		returned.setFGEModelFactory(this);
		returned.setGraphicalRepresentation(aGR);
		return returned;
	}

	/**
	 * Creates and return a new CurvedPolylinConnector, given a connector graphical representation
	 * 
	 * @param aGR
	 * @return a newly created CurvedPolylinConnector
	 */
	public CurvedPolylinConnector makeCurvedPolylinConnector(ConnectorGraphicalRepresentation<?> aGR) {
		CurvedPolylinConnector returned = newInstance(CurvedPolylinConnector.class);
		returned.setFGEModelFactory(this);
		returned.setGraphicalRepresentation(aGR);
		return returned;
	}

	/**
	 * Make a new foreground style (stroke style)
	 * 
	 * @return a newly created ForegroundStyle
	 */
	public ForegroundStyle makeNewForegroundStyle() {
		ForegroundStyle returned = newInstance(ForegroundStyle.class);
		returned.setFGEModelFactory(this);
		return returned;
	}

	/**
	 * Make a new foreground style (stroke style), initialized with default values as declared in FGEConstants
	 * 
	 * @return a newly created ForegroundStyle
	 */
	public ForegroundStyle makeDefaultForegroundStyle() {
		ForegroundStyle returned = newInstance(ForegroundStyle.class);
		returned.setFGEModelFactory(this);
		returned.setNoStroke(false);
		returned.setColor(Color.BLACK);
		returned.setLineWidth(1.0);
		returned.setJoinStyle(JoinStyle.JOIN_MITER);
		returned.setCapStyle(CapStyle.CAP_SQUARE);
		returned.setDashStyle(DashStyle.PLAIN_STROKE);
		return returned;
	}

	/**
	 * Make a new foreground style (stroke style), with no stroke (invisible stroke)
	 * 
	 * @param aColor
	 *            the color to assign to line
	 * 
	 * @return a newly created ForegroundStyle
	 */
	public ForegroundStyle makeNoneForegroundStyle() {
		ForegroundStyle returned = makeDefaultForegroundStyle();
		returned.setNoStroke(true);
		return returned;
	}

	/**
	 * Make a new foreground style (stroke style), initialized with default values as declared in FGEConstants and a specific color
	 * 
	 * @param aColor
	 *            the color to assign to line
	 * 
	 * @return a newly created ForegroundStyle
	 */
	public ForegroundStyle makeForegroundStyle(Color aColor) {
		ForegroundStyle returned = makeDefaultForegroundStyle();
		returned.setColor(aColor);
		return returned;
	}

	/**
	 * Make a new foreground style (stroke style), initialized with default values as declared in FGEConstants, with a specific color and a
	 * specific line width
	 * 
	 * @param aColor
	 *            the color to assign to line
	 * @param aLineWidth
	 *            the width of line
	 * 
	 * @return a newly created ForegroundStyle
	 */
	public ForegroundStyle makeForegroundStyle(Color aColor, float aLineWidth) {
		ForegroundStyle returned = makeForegroundStyle(aColor);
		returned.setLineWidth(aLineWidth);
		return returned;
	}

	/**
	 * Make a new foreground style (stroke style), initialized with values supplied as parameters
	 * 
	 * @param aColor
	 *            the color to assign to line
	 * @param aLineWidth
	 *            the width of line
	 * @param joinStyle
	 * @param capStyle
	 * @param dashStyle
	 * @return a newly created ForegroundStyle
	 */
	public ForegroundStyle makeForegroundStyle(Color aColor, float aLineWidth, JoinStyle joinStyle, CapStyle capStyle, DashStyle dashStyle) {
		ForegroundStyle returned = makeForegroundStyle(aColor);
		returned.setLineWidth(aLineWidth);
		returned.setJoinStyle(joinStyle);
		returned.setCapStyle(capStyle);
		returned.setDashStyle(dashStyle);
		return returned;
	}

	/**
	 * Make a new foreground style (stroke style), initialized with values supplied as parameters
	 * 
	 * @param aColor
	 *            the color to assign to line
	 * @param aLineWidth
	 *            the width of line
	 * @param dashStyle
	 * @return a newly created ForegroundStyle
	 */
	public ForegroundStyle makeForegroundStyle(Color aColor, float aLineWidth, DashStyle dashStyle) {
		ForegroundStyle returned = makeForegroundStyle(aColor);
		returned.setLineWidth(aLineWidth);
		returned.setDashStyle(dashStyle);
		return returned;
	}

	/**
	 * Make a new background style as empty background (invisible)
	 * 
	 * @return a newly created BackgroundStyle
	 */
	public BackgroundStyle.None makeEmptyBackground() {
		BackgroundStyle.None returned = newInstance(None.class);
		returned.setFGEModelFactory(this);
		return returned;
	}

	/**
	 * Make a new background style as plain colored background (invisible)
	 * 
	 * @param aColor
	 *            color to be used as plain colored background
	 * @return a newly created BackgroundStyle
	 */
	public BackgroundStyle.Color makeColoredBackground(Color aColor) {
		BackgroundStyle.Color returned = newInstance(BackgroundStyle.Color.class);
		returned.setFGEModelFactory(this);
		returned.setColor(aColor);
		return returned;
	}

	/**
	 * Make a new background style as color gradient with two colors
	 * 
	 * @param color1
	 * @param color2
	 * @param direction
	 * 
	 * @return a newly created BackgroundStyle
	 */
	public BackgroundStyle.ColorGradient makeColorGradientBackground(Color color1, Color color2, ColorGradientDirection direction) {
		BackgroundStyle.ColorGradient returned = newInstance(BackgroundStyle.ColorGradient.class);
		returned.setFGEModelFactory(this);
		returned.setColor1(color1);
		returned.setColor2(color2);
		returned.setDirection(direction);
		return returned;
	}

	/**
	 * Make a new background style as textured background with two colors
	 * 
	 * @param color1
	 * @param color2
	 * @param textureType
	 * 
	 * @return a newly created BackgroundStyle
	 */
	public BackgroundStyle.Texture makeTexturedBackground(TextureType textureType, Color color1, Color color2) {
		BackgroundStyle.Texture returned = newInstance(BackgroundStyle.Texture.class);
		returned.setFGEModelFactory(this);
		returned.setColor1(color1);
		returned.setColor2(color2);
		returned.setTextureType(textureType);
		return returned;
	}

	/**
	 * Make a new background style as image background, given a file encoding image
	 * 
	 * @param imageFile
	 *            the file where image is located (most image format allowed)
	 * 
	 * @return a newly created BackgroundStyle
	 */
	public BackgroundStyle.BackgroundImage makeImageBackground(File imageFile) {
		BackgroundStyle.BackgroundImage returned = newInstance(BackgroundStyle.BackgroundImage.class);
		returned.setFGEModelFactory(this);
		returned.setImageFile(imageFile);
		return returned;
	}

	/**
	 * Make a new background style as image background, given an image
	 * 
	 * @param image
	 * 
	 * @return a newly created BackgroundStyle
	 */
	public BackgroundStyle.BackgroundImage makeImageBackground(Image image) {
		BackgroundStyle.BackgroundImage returned = newInstance(BackgroundStyle.BackgroundImage.class);
		returned.setFGEModelFactory(this);
		returned.setImage(image);
		return returned;
	}

	/**
	 * Make a new background style given type of background to create
	 * 
	 * @param type
	 * @return a newly created BackgroundStyle
	 */
	public BackgroundStyle makeBackground(BackgroundStyleType type) {
		if (type == BackgroundStyleType.NONE) {
			return makeEmptyBackground();
		} else if (type == BackgroundStyleType.COLOR) {
			return makeColoredBackground(java.awt.Color.WHITE);
		} else if (type == BackgroundStyleType.COLOR_GRADIENT) {
			return makeColorGradientBackground(java.awt.Color.WHITE, java.awt.Color.BLACK, ColorGradientDirection.SOUTH_EAST_NORTH_WEST);
		} else if (type == BackgroundStyleType.TEXTURE) {
			return makeTexturedBackground(TextureType.TEXTURE1, java.awt.Color.RED, java.awt.Color.WHITE);
		} else if (type == BackgroundStyleType.IMAGE) {
			return makeImageBackground((File) null);
		}
		return null;
	}

	/**
	 * Make a shadow style without any shadow
	 * 
	 * @return a newly created ShadowStyle
	 */
	public ShadowStyle makeNoneShadowStyle() {
		ShadowStyle returned = newInstance(ShadowStyle.class);
		returned.setFGEModelFactory(this);
		returned.setDrawShadow(false);
		returned.setShadowDepth(0);
		return returned;
	}

	/**
	 * Make default shadow style (initialized with default values as declared in FGEConstants)
	 * 
	 * @return a newly created ShadowStyle
	 */
	public ShadowStyle makeDefaultShadowStyle() {
		ShadowStyle returned = newInstance(ShadowStyle.class);
		returned.setFGEModelFactory(this);
		returned.setDrawShadow(true);
		returned.setShadowDarkness(FGEConstants.DEFAULT_SHADOW_DARKNESS);
		returned.setShadowDepth(FGEConstants.DEFAULT_SHADOW_DEEP);
		returned.setShadowBlur(FGEConstants.DEFAULT_SHADOW_BLUR);
		return returned;
	}

	/**
	 * Make a new text style, initialized with default values as declared in FGEConstants
	 * 
	 * @return a newly created TextStyle
	 */
	public TextStyle makeDefaultTextStyle() {
		return makeTextStyle(FGEConstants.DEFAULT_TEXT_COLOR, FGEConstants.DEFAULT_TEXT_FONT);
	}

	/**
	 * Make a new text style, initialized with supplied font and color
	 * 
	 * @param aColor
	 * @param aFont
	 * 
	 * @return a newly created TextStyle
	 */
	public TextStyle makeTextStyle(Color aColor, Font aFont) {
		TextStyle returned = newInstance(TextStyle.class);
		returned.setFGEModelFactory(this);
		returned.setFont(aFont);
		returned.setColor(aColor);
		return returned;
	}

	/**
	 * Make a new border, initialized with default values as in FGEConstants
	 * 
	 * @return a newly created ShapeBorder
	 */
	public ShapeBorder makeShapeBorder() {
		ShapeBorder returned = newInstance(ShapeBorder.class);
		returned.setFGEModelFactory(this);
		returned.setTop(FGEConstants.DEFAULT_BORDER_SIZE);
		returned.setBottom(FGEConstants.DEFAULT_BORDER_SIZE);
		returned.setLeft(FGEConstants.DEFAULT_BORDER_SIZE);
		returned.setRight(FGEConstants.DEFAULT_BORDER_SIZE);
		return returned;
	}

	/**
	 * Make a new border, initialized with supplied values
	 * 
	 * @return a newly created ShapeBorder
	 */
	public ShapeBorder makeShapeBorder(int top, int bottom, int left, int right) {
		ShapeBorder returned = newInstance(ShapeBorder.class);
		returned.setFGEModelFactory(this);
		returned.setTop(top);
		returned.setBottom(bottom);
		returned.setLeft(left);
		returned.setRight(right);
		return returned;
	}

	/**
	 * Make a new Shape from corresponding ShapeType and set with supplied graphical representation
	 * 
	 * @param type
	 * @param aGraphicalRepresentation
	 * @return a newly created Shape
	 */
	public Shape makeShape(ShapeType type, ShapeGraphicalRepresentation<?> aGraphicalRepresentation) {

		Shape returned = makeShape(type);
		if (returned != null) {
			returned.setGraphicalRepresentation(aGraphicalRepresentation);
		}
		return returned;
	}

	/**
	 * Make a new Shape from corresponding ShapeType
	 * 
	 * @param type
	 * @return a newly created Shape
	 */
	public Shape makeShape(ShapeType type) {
		Shape returned = null;
		if (type == ShapeType.SQUARE) {
			returned = newInstance(Square.class);
		} else if (type == ShapeType.RECTANGLE) {
			returned = newInstance(Rectangle.class);
		} else if (type == ShapeType.TRIANGLE) {
			returned = newInstance(Triangle.class);
		} else if (type == ShapeType.LOSANGE) {
			returned = newInstance(Losange.class);
		} else if (type == ShapeType.RECTANGULAROCTOGON) {
			returned = newInstance(RectangularOctogon.class);
		} else if (type == ShapeType.POLYGON) {
			returned = newInstance(RegularPolygon.class);
		} else if (type == ShapeType.CUSTOM_POLYGON) {
			returned = newInstance(Polygon.class);
		} else if (type == ShapeType.OVAL) {
			returned = newInstance(Oval.class);
		} else if (type == ShapeType.CIRCLE) {
			returned = newInstance(Circle.class);
		} else if (type == ShapeType.STAR) {
			returned = newInstance(Star.class);
		} else if (type == ShapeType.ARC) {
			returned = newInstance(Arc.class);
		} else {
			logger.warning("Unexpected ShapeType: " + type);
		}

		if (returned != null) {
			returned.setFGEModelFactory(this);
		}

		return returned;
	}

	/**
	 * Make a new Polygon with supplied polygon
	 * 
	 * @param aGraphicalRepresentation
	 * @param aPolygon
	 * @return a newly created Polygon
	 */
	public Polygon makePolygon(ShapeGraphicalRepresentation aGraphicalRepresentation, FGEPolygon aPolygon) {
		Polygon polygon = newInstance(Polygon.class);
		for (FGEPoint pt : aPolygon.getPoints()) {
			polygon.addToPoints(pt);
		}
		polygon.updateShape();
		return polygon;
	}

	/**
	 * Make a new Polygon with supplied points
	 * 
	 * @param aGraphicalRepresentation
	 * @param aPolygon
	 * @return a newly created Polygon
	 */
	public Polygon makePolygon(ShapeGraphicalRepresentation aGraphicalRepresentation, FGEPoint... points) {
		Polygon polygon = newInstance(Polygon.class);
		for (FGEPoint pt : points) {
			polygon.addToPoints(pt);
		}
		polygon.updateShape();
		return polygon;
	}

	/**
	 * Instanciate a new instance with parameters defining whether default properties and/or defaut basic controls should be assigned to
	 * newly created instances for GraphicalRepresentation instances
	 * 
	 * 
	 * @param implementedInterface
	 * @param initWithDefaultProperties
	 * @param initWithBasicControls
	 * @return
	 */
	public <I extends GraphicalRepresentation<?>> I newInstance(Class<I> implementedInterface, boolean initWithDefaultProperties,
			boolean initWithBasicControls) {
		I returned = super.newInstance(implementedInterface);
		if (returned instanceof ShapeGraphicalRepresentation) {
			if (initWithDefaultProperties) {
				applyDefaultProperties((ShapeGraphicalRepresentation<?>) returned);
			}
			if (initWithBasicControls) {
				applyBasicControls((ShapeGraphicalRepresentation<?>) returned);
			}
		} else if (returned instanceof ConnectorGraphicalRepresentation) {
			if (initWithDefaultProperties) {
				applyDefaultProperties((ConnectorGraphicalRepresentation<?>) returned);
			}
			if (initWithBasicControls) {
				applyBasicControls((ConnectorGraphicalRepresentation<?>) returned);
			}
		} else if (returned instanceof DrawingGraphicalRepresentation) {
			if (initWithDefaultProperties) {
				applyDefaultProperties((DrawingGraphicalRepresentation<?>) returned);
			}
			if (initWithBasicControls) {
				applyBasicControls((DrawingGraphicalRepresentation<?>) returned);
			}
		} else if (returned instanceof GeometricGraphicalRepresentation) {
			if (initWithDefaultProperties) {
				applyDefaultProperties((GeometricGraphicalRepresentation<?>) returned);
			}
			if (initWithBasicControls) {
				applyBasicControls((GeometricGraphicalRepresentation<?>) returned);
			}
		}
		return returned;
	}

}
