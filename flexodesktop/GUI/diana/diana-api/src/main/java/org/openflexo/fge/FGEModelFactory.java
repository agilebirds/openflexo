package org.openflexo.fge;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.openflexo.fge.BackgroundStyle.BackgroundStyleType;
import org.openflexo.fge.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.fge.ForegroundStyle.CapStyle;
import org.openflexo.fge.ForegroundStyle.DashStyle;
import org.openflexo.fge.ForegroundStyle.JoinStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation.ShapeBorder;
import org.openflexo.fge.TextureBackgroundStyle.TextureType;
import org.openflexo.fge.connectors.ConnectorSpecification;
import org.openflexo.fge.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.fge.connectors.CurveConnectorSpecification;
import org.openflexo.fge.connectors.CurvedPolylinConnectorSpecification;
import org.openflexo.fge.connectors.LineConnectorSpecification;
import org.openflexo.fge.connectors.RectPolylinConnectorSpecification;
import org.openflexo.fge.control.DianaEditor;
import org.openflexo.fge.control.MouseClickControl;
import org.openflexo.fge.control.MouseClickControlAction;
import org.openflexo.fge.control.MouseControl.MouseButton;
import org.openflexo.fge.control.MouseDragControl;
import org.openflexo.fge.control.MouseDragControlAction;
import org.openflexo.fge.control.PredefinedMouseClickControlActionType;
import org.openflexo.fge.control.PredefinedMouseDragControlActionType;
import org.openflexo.fge.geom.FGEComplexCurve;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.shapes.Arc;
import org.openflexo.fge.shapes.Circle;
import org.openflexo.fge.shapes.ComplexCurve;
import org.openflexo.fge.shapes.Losange;
import org.openflexo.fge.shapes.Oval;
import org.openflexo.fge.shapes.Polygon;
import org.openflexo.fge.shapes.Rectangle;
import org.openflexo.fge.shapes.RectangularOctogon;
import org.openflexo.fge.shapes.RegularPolygon;
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
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
 * This class also provides support for XML serializing/deserializing
 * 
 * Please feel free to override this class
 * 
 * @author sylvain
 * 
 */
public abstract class FGEModelFactory extends ModelFactory {

	static final Logger logger = Logger.getLogger(FGEModelFactory.class.getPackage().getName());

	/**
	 * Creates a new model factory including all classes involved in FGE model
	 * 
	 * @throws ModelDefinitionException
	 */
	public FGEModelFactory() throws ModelDefinitionException {
		this(new ArrayList<Class<?>>());
	}

	/**
	 * Creates a new model factory including all supplied classes and all classes involved in FGE model
	 * 
	 * @throws ModelDefinitionException
	 */
	public FGEModelFactory(Class<?>... classes) throws ModelDefinitionException {
		this(Arrays.asList(classes));
	}

	/**
	 * Creates a new model factory including all supplied classes and all classes involved in FGE model
	 * 
	 * @throws ModelDefinitionException
	 */
	public FGEModelFactory(Collection<Class<?>> classes) throws ModelDefinitionException {

		super(ModelContextLibrary.getCompoundModelContext(appendGRClasses(classes)));

		getStringEncoder().addConverter(FGEUtils.DATA_BINDING_CONVERTER);
		getStringEncoder().addConverter(FGEUtils.POINT_CONVERTER);
		getStringEncoder().addConverter(FGEUtils.STEPPED_DIMENSION_CONVERTER);

		logger.info("Created new FGEModelFactory...............................");

		installImplementingClasses();

	}

	public abstract void installImplementingClasses() throws ModelDefinitionException;

	private static Class<?>[] appendGRClasses(Collection<Class<?>> classes) {
		Set<Class<?>> returned = new HashSet<Class<?>>(classes);
		returned.add(GraphicalRepresentation.class);
		returned.add(DrawingGraphicalRepresentation.class);
		returned.add(ShapeGraphicalRepresentation.class);
		returned.add(ConnectorGraphicalRepresentation.class);
		returned.add(GeometricGraphicalRepresentation.class);
		return returned.toArray(new Class<?>[returned.size()]);
	}

	/**
	 * Deserialized object are always set with basic controls
	 */
	@Override
	public <I> void objectHasBeenDeserialized(I newlyCreatedObject, Class<I> implementedInterface) {
		super.objectHasBeenDeserialized(newlyCreatedObject, implementedInterface);
		if (newlyCreatedObject instanceof DrawingGraphicalRepresentation) {
			applyBasicControls((DrawingGraphicalRepresentation) newlyCreatedObject);
		}
		if (newlyCreatedObject instanceof ShapeGraphicalRepresentation) {
			applyBasicControls((ShapeGraphicalRepresentation) newlyCreatedObject);
		}
		if (newlyCreatedObject instanceof ConnectorGraphicalRepresentation) {
			applyBasicControls((ConnectorGraphicalRepresentation) newlyCreatedObject);
		}
		if (newlyCreatedObject instanceof GeometricGraphicalRepresentation) {
			applyBasicControls((GeometricGraphicalRepresentation) newlyCreatedObject);
		}
	}

	@Override
	public <I> void objectHasBeenCreated(I newlyCreatedObject, Class<I> implementedInterface) {
		if (newlyCreatedObject instanceof FGEObject) {
			((FGEObject) newlyCreatedObject).setFactory(this);
		}
		super.objectHasBeenCreated(newlyCreatedObject, implementedInterface);
		if (newlyCreatedObject instanceof DrawingGraphicalRepresentation) {
			applyBasicControls((DrawingGraphicalRepresentation) newlyCreatedObject);
		}
		if (newlyCreatedObject instanceof ShapeGraphicalRepresentation) {
			applyBasicControls((ShapeGraphicalRepresentation) newlyCreatedObject);
		}
		if (newlyCreatedObject instanceof ConnectorGraphicalRepresentation) {
			applyBasicControls((ConnectorGraphicalRepresentation) newlyCreatedObject);
		}
		if (newlyCreatedObject instanceof GeometricGraphicalRepresentation) {
			applyBasicControls((GeometricGraphicalRepresentation) newlyCreatedObject);
		}
	}

	/**
	 * Creates and return a new DrawingGraphicalRepresentation, given a Drawing instance, and initialized with default values and default
	 * controls (drawing selection, rectangle selection and zoom)
	 * 
	 * @param aDrawing
	 * @return a newly created DrawingGraphicalRepresentation
	 */
	public DrawingGraphicalRepresentation makeDrawingGraphicalRepresentation(/*Drawing<?> aDrawing*/) {
		return makeDrawingGraphicalRepresentation(/*aDrawing,*/true);
	}

	/**
	 * Creates and return a new DrawingGraphicalRepresentation, given a Drawing instance, and initialized with default values and a flag
	 * indicating if default controls should be created (drawing selection, rectangle selection and zoom)
	 * 
	 * @param aDrawing
	 * @return a newly created DrawingGraphicalRepresentation
	 */
	public DrawingGraphicalRepresentation makeDrawingGraphicalRepresentation(/*Drawing<?> aDrawing,*/boolean initBasicControls) {
		DrawingGraphicalRepresentation returned = newInstance(DrawingGraphicalRepresentation.class, true, initBasicControls);
		returned.setFactory(this);
		// returned.setDrawable(aDrawing.getModel());
		// returned.setDrawing(aDrawing);
		return returned;
	}

	/**
	 * Sets and apply default properties (text style, background and size) to supplied DrawingGraphicalRepresentation
	 * 
	 * @param drawingGraphicalRepresentation
	 */
	public void applyDefaultProperties(DrawingGraphicalRepresentation drawingGraphicalRepresentation) {
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
	public void applyBasicControls(DrawingGraphicalRepresentation drawingGraphicalRepresentation) {
		drawingGraphicalRepresentation.addToMouseClickControls(makeMouseClickControl("Drawing selection", MouseButton.LEFT, 1,
				PredefinedMouseClickControlActionType.SELECTION));
		drawingGraphicalRepresentation.addToMouseDragControls(makeMouseDragControl("Rectangle selection", MouseButton.LEFT,
				PredefinedMouseDragControlActionType.RECTANGLE_SELECTING));
		drawingGraphicalRepresentation.addToMouseDragControls(makeMouseDragControl("Zoom", MouseButton.RIGHT,
				PredefinedMouseDragControlActionType.ZOOM));
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
	public <O> ShapeGraphicalRepresentation makeShapeGraphicalRepresentation() {

		ShapeGraphicalRepresentation returned = newInstance(ShapeGraphicalRepresentation.class, true, true);
		returned.setFactory(this);
		// returned.setDrawable(aDrawable);
		// returned.setDrawing(aDrawing);
		return returned;
	}

	/**
	 * Sets and apply default properties (border, layer, text style, foreground, background and shadow) to supplied
	 * ShapeGraphicalRepresentation
	 * 
	 * @param shapeGraphicalRepresentation
	 */
	public void applyDefaultProperties(ShapeGraphicalRepresentation shapeGraphicalRepresentation) {
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
	public void applyBasicControls(ShapeGraphicalRepresentation shapeGraphicalRepresentation) {
		shapeGraphicalRepresentation.addToMouseClickControls(makeMouseClickControl("Selection", MouseButton.LEFT, 1,
				PredefinedMouseClickControlActionType.SELECTION));
		if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			shapeGraphicalRepresentation.addToMouseClickControls(makeMouseMetaClickControl("Multiple selection", MouseButton.LEFT, 1,
					PredefinedMouseClickControlActionType.MULTIPLE_SELECTION));
		} else {
			shapeGraphicalRepresentation.addToMouseClickControls(makeMouseControlClickControl("Multiple selection", MouseButton.LEFT, 1,
					PredefinedMouseClickControlActionType.MULTIPLE_SELECTION));
		}
		shapeGraphicalRepresentation.addToMouseDragControls(makeMouseDragControl("Move", MouseButton.LEFT,
				PredefinedMouseDragControlActionType.MOVE));
		shapeGraphicalRepresentation.addToMouseDragControls(makeMouseDragControl("Zoom", MouseButton.RIGHT,
				PredefinedMouseDragControlActionType.ZOOM));
		shapeGraphicalRepresentation.addToMouseDragControls(makeMouseShiftDragControl("Rectangle selection", MouseButton.LEFT,
				PredefinedMouseDragControlActionType.RECTANGLE_SELECTING));
	}

	/**
	 * Creates and return a new ShapeGraphicalRepresentation, initialized with default values and given a shape type
	 * 
	 * @return a newly created ShapeGraphicalRepresentation
	 */
	public <O> ShapeGraphicalRepresentation makeShapeGraphicalRepresentation(ShapeType shapeType) {
		ShapeGraphicalRepresentation returned = makeShapeGraphicalRepresentation();
		returned.setShapeType(shapeType);
		return returned;
	}

	/**
	 * Creates and return a new ShapeGraphicalRepresentation, given a ShapeSpecification
	 * 
	 * @return a newly created ShapeGraphicalRepresentation
	 */
	public <O> ShapeGraphicalRepresentation makeShapeGraphicalRepresentation(ShapeSpecification shapeSpecification) {
		ShapeGraphicalRepresentation returned = makeShapeGraphicalRepresentation();
		returned.setShapeSpecification(shapeSpecification);
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
	public <O> ShapeGraphicalRepresentation makeShapeGraphicalRepresentation(ShapeGraphicalRepresentation aGR) {
		ShapeGraphicalRepresentation returned = makeShapeGraphicalRepresentation();
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
	public <O> ConnectorGraphicalRepresentation makeConnectorGraphicalRepresentation() {

		ConnectorGraphicalRepresentation returned = newInstance(ConnectorGraphicalRepresentation.class, true, true);
		returned.setFactory(this);
		// returned.setDrawable(aDrawable);
		// returned.setDrawing(aDrawing);
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
	public <O> ConnectorGraphicalRepresentation makeConnectorGraphicalRepresentation(ConnectorType aConnectorType) {

		ConnectorGraphicalRepresentation returned = makeConnectorGraphicalRepresentation();
		// returned.setStartObject(aStartObject);
		// returned.setEndObject(anEndObject);
		returned.setConnectorType(aConnectorType);

		return returned;
	}

	/**
	 * Sets and apply default properties (layer, text style, foreground) to supplied ConnectorGraphicalRepresentation
	 * 
	 * @param connectorGraphicalRepresentation
	 */
	public void applyDefaultProperties(ConnectorGraphicalRepresentation connectorGraphicalRepresentation) {
		connectorGraphicalRepresentation.setLayer(FGEConstants.DEFAULT_CONNECTOR_LAYER);
		connectorGraphicalRepresentation.setForeground(makeDefaultForegroundStyle());
		connectorGraphicalRepresentation.setTextStyle(makeDefaultTextStyle());
	}

	/**
	 * Sets and apply default basic controls (drawing selection, rectangle selection and zoom) to supplied ConnectorGraphicalRepresentation
	 * 
	 * @param connectorGraphicalRepresentation
	 */
	public void applyBasicControls(ConnectorGraphicalRepresentation connectorGraphicalRepresentation) {
		connectorGraphicalRepresentation.addToMouseClickControls(makeMouseClickControl("Selection", MouseButton.LEFT, 1,
				PredefinedMouseClickControlActionType.SELECTION));
		if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			connectorGraphicalRepresentation.addToMouseClickControls(makeMouseMetaClickControl("Multiple selection", MouseButton.LEFT, 1,
					PredefinedMouseClickControlActionType.MULTIPLE_SELECTION));
		} else {
			connectorGraphicalRepresentation.addToMouseClickControls(makeMouseControlClickControl("Multiple selection", MouseButton.LEFT,
					1, PredefinedMouseClickControlActionType.MULTIPLE_SELECTION));
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
	public <O> GeometricGraphicalRepresentation makeGeometricGraphicalRepresentation() {

		GeometricGraphicalRepresentation returned = newInstance(GeometricGraphicalRepresentation.class, true, true);
		returned.setFactory(this);
		// returned.setDrawable(aDrawable);
		// returned.setDrawing(aDrawing);

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
	public <O> GeometricGraphicalRepresentation makeGeometricGraphicalRepresentation(FGEArea geometricObject) {

		GeometricGraphicalRepresentation returned = makeGeometricGraphicalRepresentation();
		returned.setGeometricObject(geometricObject);
		return returned;
	}

	/**
	 * Sets and apply default properties (layer, text style, foreground) to supplied ConnectorGraphicalRepresentation
	 * 
	 * @param connectorGraphicalRepresentation
	 */
	public void applyDefaultProperties(GeometricGraphicalRepresentation geometricGraphicalRepresentation) {
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
	public void applyBasicControls(GeometricGraphicalRepresentation geometricGraphicalRepresentation) {
		geometricGraphicalRepresentation.addToMouseClickControls(makeMouseClickControl("Selection", MouseButton.LEFT, 1,
				PredefinedMouseClickControlActionType.SELECTION));
		if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			geometricGraphicalRepresentation.addToMouseClickControls(makeMouseMetaClickControl("Multiple selection", MouseButton.LEFT, 1,
					PredefinedMouseClickControlActionType.MULTIPLE_SELECTION));
		} else {
			geometricGraphicalRepresentation.addToMouseClickControls(makeMouseControlClickControl("Multiple selection", MouseButton.LEFT,
					1, PredefinedMouseClickControlActionType.MULTIPLE_SELECTION));
		}
	}

	public ConnectorSpecification makeConnector(ConnectorType type) {

		if (type == ConnectorType.LINE) {
			return makeLineConnector();
		} else if (type == ConnectorType.RECT_POLYLIN) {
			return makeRectPolylinConnector();
		} else if (type == ConnectorType.CURVE) {
			return makeCurveConnector();
		} else if (type == ConnectorType.CURVED_POLYLIN) {
			return makeCurvedPolylinConnector();
		}
		logger.warning("Unexpected type: " + type);
		return null;

	}

	/**
	 * Creates and return a new LineConnectorSpecification, given a connector graphical representation
	 * 
	 * @param aGR
	 * @return a newly created LineConnectorSpecification
	 */
	public LineConnectorSpecification makeLineConnector() {
		LineConnectorSpecification returned = newInstance(LineConnectorSpecification.class);
		returned.setFactory(this);
		// aGR.setConnectorSpecification(returned);
		// returned.setGraphicalRepresentation(aGR);
		return returned;
	}

	/**
	 * Creates and return a new CurveConnectorSpecification, given a connector graphical representation
	 * 
	 * @param aGR
	 * @return a newly created CurveConnectorSpecification
	 */
	public CurveConnectorSpecification makeCurveConnector() {
		CurveConnectorSpecification returned = newInstance(CurveConnectorSpecification.class);
		returned.setFactory(this);
		// aGR.setConnectorSpecification(returned);
		// returned.setGraphicalRepresentation(aGR);
		return returned;
	}

	/**
	 * Creates and return a new RectPolylinConnectorSpecification, given a connector graphical representation
	 * 
	 * @param aGR
	 * @return a newly created RectPolylinConnectorSpecification
	 */
	public RectPolylinConnectorSpecification makeRectPolylinConnector() {
		RectPolylinConnectorSpecification returned = newInstance(RectPolylinConnectorSpecification.class);
		returned.setFactory(this);
		// aGR.setConnectorSpecification(returned);
		// returned.setGraphicalRepresentation(aGR);
		return returned;
	}

	/**
	 * Creates and return a new CurvedPolylinConnectorSpecification, given a connector graphical representation
	 * 
	 * @param aGR
	 * @return a newly created CurvedPolylinConnectorSpecification
	 */
	public CurvedPolylinConnectorSpecification makeCurvedPolylinConnector() {
		CurvedPolylinConnectorSpecification returned = newInstance(CurvedPolylinConnectorSpecification.class);
		returned.setFactory(this);
		// aGR.setConnectorSpecification(returned);
		// returned.setGraphicalRepresentation(aGR);
		return returned;
	}

	/**
	 * Make a new foreground style (stroke style)
	 * 
	 * @return a newly created ForegroundStyle
	 */
	public ForegroundStyle makeNewForegroundStyle() {
		ForegroundStyle returned = newInstance(ForegroundStyle.class);
		returned.setFactory(this);
		return returned;
	}

	/**
	 * Make a new foreground style (stroke style), initialized with default values as declared in FGEConstants
	 * 
	 * @return a newly created ForegroundStyle
	 */
	public ForegroundStyle makeDefaultForegroundStyle() {
		ForegroundStyle returned = newInstance(ForegroundStyle.class);
		returned.setFactory(this);
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
	 * Make a new colored background style initialized with default values as declared in FGEConstants
	 * 
	 * @return a newly created ForegroundStyle
	 */
	public BackgroundStyle makeDefaultBackgroundStyle() {
		return makeColoredBackground(FGEConstants.DEFAULT_BACKGROUND_COLOR);
	}

	/**
	 * Make a new background style as empty background (invisible)
	 * 
	 * @return a newly created BackgroundStyle
	 */
	public NoneBackgroundStyle makeEmptyBackground() {
		NoneBackgroundStyle returned = newInstance(NoneBackgroundStyle.class);
		returned.setFactory(this);
		return returned;
	}

	/**
	 * Make a new background style as plain colored background (invisible)
	 * 
	 * @param aColor
	 *            color to be used as plain colored background
	 * @return a newly created BackgroundStyle
	 */
	public ColorBackgroundStyle makeColoredBackground(Color aColor) {
		ColorBackgroundStyle returned = newInstance(ColorBackgroundStyle.class);
		returned.setFactory(this);
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
	public ColorGradientBackgroundStyle makeColorGradientBackground(Color color1, Color color2, ColorGradientDirection direction) {
		ColorGradientBackgroundStyle returned = newInstance(ColorGradientBackgroundStyle.class);
		returned.setFactory(this);
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
	public TextureBackgroundStyle makeTexturedBackground(TextureType textureType, Color color1, Color color2) {
		TextureBackgroundStyle returned = newInstance(TextureBackgroundStyle.class);
		returned.setFactory(this);
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
	public BackgroundImageBackgroundStyle makeImageBackground(File imageFile) {
		BackgroundImageBackgroundStyle returned = newInstance(BackgroundImageBackgroundStyle.class);
		returned.setFactory(this);
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
	public BackgroundImageBackgroundStyle makeImageBackground(Image image) {
		BackgroundImageBackgroundStyle returned = newInstance(BackgroundImageBackgroundStyle.class);
		returned.setFactory(this);
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
		returned.setFactory(this);
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
		returned.setFactory(this);
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
		returned.setFactory(this);
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
		returned.setFactory(this);
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
		returned.setFactory(this);
		returned.setTop(top);
		returned.setBottom(bottom);
		returned.setLeft(left);
		returned.setRight(right);
		return returned;
	}

	/**
	 * Make a new border, initialized with an other border
	 * 
	 * @return a newly created ShapeBorder
	 */
	public ShapeBorder makeShapeBorder(ShapeBorder border) {
		ShapeBorder returned = newInstance(ShapeBorder.class);
		returned.setFactory(this);
		returned.setTop(border.getTop());
		returned.setBottom(border.getBottom());
		returned.setLeft(border.getLeft());
		returned.setRight(border.getRight());
		return returned;
	}

	/**
	 * Make a new ShapeSpecification from corresponding ShapeType
	 * 
	 * @param type
	 * @return a newly created ShapeSpecification
	 */
	public ShapeSpecification makeShape(ShapeType type) {
		ShapeSpecification returned = null;
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
		} else if (type == ShapeType.COMPLEX_CURVE) {
			returned = newInstance(ComplexCurve.class);
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
			returned.setFactory(this);
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
	public Polygon makePolygon(FGEPolygon aPolygon) {
		Polygon polygon = newInstance(Polygon.class);
		for (FGEPoint pt : aPolygon.getPoints()) {
			polygon.addToPoints(pt);
		}
		return polygon;
	}

	/**
	 * Make a new Polygon with supplied points
	 * 
	 * @param aGraphicalRepresentation
	 * @param aPolygon
	 * @return a newly created Polygon
	 */
	public Polygon makePolygon(FGEPoint... points) {
		Polygon polygon = newInstance(Polygon.class);
		for (FGEPoint pt : points) {
			polygon.addToPoints(pt);
		}
		return polygon;
	}

	/**
	 * Make a new ComplexCurve with supplied curve
	 * 
	 * @param aCurve
	 * 
	 * @return a newly created ComplexCurve
	 */
	public ComplexCurve makeComplexCurve(FGEComplexCurve aCurve) {
		ComplexCurve curve = newInstance(ComplexCurve.class);
		for (FGEPoint pt : aCurve.getPoints()) {
			curve.addToPoints(pt);
		}
		curve.setClosure(aCurve.getClosure());
		return curve;
	}

	/**
	 * Make a new ComplexCurve with supplied points
	 * 
	 * @param points
	 * 
	 * @return a newly created ComplexCurve
	 */
	public ComplexCurve makeComplexCurve(FGEPoint... points) {
		ComplexCurve curve = newInstance(ComplexCurve.class);
		for (FGEPoint pt : points) {
			curve.addToPoints(pt);
		}
		return curve;
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
	public <I extends GraphicalRepresentation> I newInstance(Class<I> implementedInterface, boolean initWithDefaultProperties,
			boolean initWithBasicControls) {
		I returned = super.newInstance(implementedInterface);
		if (returned instanceof ShapeGraphicalRepresentation) {
			if (initWithDefaultProperties) {
				applyDefaultProperties((ShapeGraphicalRepresentation) returned);
			}
			if (initWithBasicControls) {
				applyBasicControls((ShapeGraphicalRepresentation) returned);
			}
		} else if (returned instanceof ConnectorGraphicalRepresentation) {
			if (initWithDefaultProperties) {
				applyDefaultProperties((ConnectorGraphicalRepresentation) returned);
			}
			if (initWithBasicControls) {
				applyBasicControls((ConnectorGraphicalRepresentation) returned);
			}
		} else if (returned instanceof DrawingGraphicalRepresentation) {
			if (initWithDefaultProperties) {
				applyDefaultProperties((DrawingGraphicalRepresentation) returned);
			}
			if (initWithBasicControls) {
				applyBasicControls((DrawingGraphicalRepresentation) returned);
			}
		} else if (returned instanceof GeometricGraphicalRepresentation) {
			if (initWithDefaultProperties) {
				applyDefaultProperties((GeometricGraphicalRepresentation) returned);
			}
			if (initWithBasicControls) {
				applyBasicControls((GeometricGraphicalRepresentation) returned);
			}
		}
		return returned;
	}

	public MouseClickControl<? extends DianaEditor<?>> makeMouseClickControl(String aName, MouseButton button, int clickCount) {
		return makeMouseClickControl(aName, button, clickCount, false, false, false, false);
	}

	public <E extends DianaEditor<?>> MouseClickControl<E> makeMouseClickControl(String aName, MouseButton button, int clickCount,
			MouseClickControlAction<E> action) {
		return makeMouseClickControl(aName, button, clickCount, action, false, false, false, false);
	}

	public MouseClickControl<? extends DianaEditor<?>> makeMouseShiftClickControl(String aName, MouseButton button, int clickCount) {
		return makeMouseClickControl(aName, button, clickCount, true, false, false, false);
	}

	public MouseClickControl<? extends DianaEditor<?>> makeMouseControlClickControl(String aName, MouseButton button, int clickCount) {
		return makeMouseClickControl(aName, button, clickCount, false, true, false, false);
	}

	public MouseClickControl<? extends DianaEditor<?>> makeMouseMetaClickControl(String aName, MouseButton button, int clickCount) {
		return makeMouseClickControl(aName, button, clickCount, false, false, true, false);
	}

	public MouseClickControl<? extends DianaEditor<?>> makeMouseAltClickControl(String aName, MouseButton button, int clickCount) {
		return makeMouseClickControl(aName, button, clickCount, false, false, false, true);
	}

	public MouseClickControl<? extends DianaEditor<?>> makeMouseClickControl(String aName, MouseButton button, int clickCount,
			PredefinedMouseClickControlActionType actionType) {
		return makeMouseClickControl(aName, button, clickCount, actionType, false, false, false, false);
	}

	public MouseClickControl<? extends DianaEditor<?>> makeMouseShiftClickControl(String aName, MouseButton button, int clickCount,
			PredefinedMouseClickControlActionType actionType) {
		return makeMouseClickControl(aName, button, clickCount, actionType, true, false, false, false);
	}

	public MouseClickControl<? extends DianaEditor<?>> makeMouseControlClickControl(String aName, MouseButton button, int clickCount,
			PredefinedMouseClickControlActionType actionType) {
		return makeMouseClickControl(aName, button, clickCount, actionType, false, true, false, false);
	}

	public MouseClickControl<? extends DianaEditor<?>> makeMouseMetaClickControl(String aName, MouseButton button, int clickCount,
			PredefinedMouseClickControlActionType actionType) {
		return makeMouseClickControl(aName, button, clickCount, actionType, false, false, true, false);
	}

	public MouseClickControl<? extends DianaEditor<?>> makeMouseAltClickControl(String aName, MouseButton button, int clickCount,
			PredefinedMouseClickControlActionType actionType) {
		return makeMouseClickControl(aName, button, clickCount, actionType, false, false, false, true);
	}

	public abstract MouseClickControl<? extends DianaEditor<?>> makeMouseClickControl(String aName, MouseButton button, int clickCount,
			boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed);

	public abstract MouseClickControl<? extends DianaEditor<?>> makeMouseClickControl(String aName, MouseButton button, int clickCount,
			PredefinedMouseClickControlActionType actionType, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed,
			boolean altPressed);

	public abstract <E extends DianaEditor<?>> MouseClickControl<E> makeMouseClickControl(String aName, MouseButton button, int clickCount,
			MouseClickControlAction<E> action, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed);

	public MouseDragControl<? extends DianaEditor<?>> makeMouseDragControl(String aName, MouseButton button) {
		return makeMouseDragControl(aName, button, false, false, false, false);
	}

	public MouseDragControl<? extends DianaEditor<?>> makeMouseShiftDragControl(String aName, MouseButton button) {
		return makeMouseDragControl(aName, button, true, false, false, false);
	}

	public MouseDragControl<? extends DianaEditor<?>> makeMouseControlDragControl(String aName, MouseButton button) {
		return makeMouseDragControl(aName, button, false, true, false, false);
	}

	public MouseDragControl<? extends DianaEditor<?>> makeMouseMetaDragControl(String aName, MouseButton button) {
		return makeMouseDragControl(aName, button, false, false, true, false);
	}

	public MouseDragControl<? extends DianaEditor<?>> makeMouseAltDragControl(String aName, MouseButton button) {
		return makeMouseDragControl(aName, button, false, false, false, true);
	}

	public MouseDragControl<? extends DianaEditor<?>> makeMouseDragControl(String aName, MouseButton button,
			PredefinedMouseDragControlActionType actionType) {
		return makeMouseDragControl(aName, button, actionType, false, false, false, false);
	}

	public MouseDragControl<? extends DianaEditor<?>> makeMouseShiftDragControl(String aName, MouseButton button,
			PredefinedMouseDragControlActionType actionType) {
		return makeMouseDragControl(aName, button, actionType, true, false, false, false);
	}

	public MouseDragControl<? extends DianaEditor<?>> makeMouseControlDragControl(String aName, MouseButton button,
			PredefinedMouseDragControlActionType actionType) {
		return makeMouseDragControl(aName, button, actionType, false, true, false, false);
	}

	public MouseDragControl<? extends DianaEditor<?>> makeMouseMetaDragControl(String aName, MouseButton button,
			PredefinedMouseDragControlActionType actionType) {
		return makeMouseDragControl(aName, button, actionType, false, false, true, false);
	}

	public MouseDragControl<? extends DianaEditor<?>> makeMouseAltDragControl(String aName, MouseButton button,
			PredefinedMouseDragControlActionType actionType) {
		return makeMouseDragControl(aName, button, actionType, false, false, false, true);
	}

	public abstract MouseDragControl<? extends DianaEditor<?>> makeMouseDragControl(String aName, MouseButton button, boolean shiftPressed,
			boolean ctrlPressed, boolean metaPressed, boolean altPressed);

	public abstract MouseDragControl<? extends DianaEditor<?>> makeMouseDragControl(String aName, MouseButton button,
			PredefinedMouseDragControlActionType actionType, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed,
			boolean altPressed);

	public abstract <E extends DianaEditor<?>> MouseDragControl<E> makeMouseDragControl(String aName, MouseButton button,
			MouseDragControlAction<E> action, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed);

	public abstract MouseDragControlAction<? extends DianaEditor<?>> makeMouseDragControlAction(
			PredefinedMouseDragControlActionType actionType);

	public abstract MouseClickControlAction<? extends DianaEditor<?>> makeMouseClickControlAction(
			PredefinedMouseClickControlActionType actionType);

}