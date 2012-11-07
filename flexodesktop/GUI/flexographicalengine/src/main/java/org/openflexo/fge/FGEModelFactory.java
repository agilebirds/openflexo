package org.openflexo.fge;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.util.logging.Logger;

import org.openflexo.fge.BackgroundStyle.BackgroundStyleType;
import org.openflexo.fge.BackgroundStyle.ColorGradient.ColorGradientDirection;
import org.openflexo.fge.BackgroundStyle.None;
import org.openflexo.fge.BackgroundStyle.Texture.TextureType;
import org.openflexo.fge.ForegroundStyle.CapStyle;
import org.openflexo.fge.ForegroundStyle.DashStyle;
import org.openflexo.fge.ForegroundStyle.JoinStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation.ShapeBorder;
import org.openflexo.fge.connectors.Connector.ConnectorType;
import org.openflexo.fge.controller.MouseClickControl;
import org.openflexo.fge.controller.MouseClickControlAction.MouseClickControlActionType;
import org.openflexo.fge.controller.MouseControl.MouseButton;
import org.openflexo.fge.controller.MouseDragControl;
import org.openflexo.fge.controller.MouseDragControlAction.MouseDragControlActionType;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.model.xml.XMLDeserializer;
import org.openflexo.model.xml.XMLSerializer;
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

	private XMLSerializer serializer;
	private XMLDeserializer deserializer;

	/**
	 * Creates a new model factory.
	 * 
	 * @throws ModelDefinitionException
	 */
	public FGEModelFactory() {

		super();

		getStringEncoder().addConverter(DataBinding.CONVERTER2);
		getStringEncoder().addConverter(GraphicalRepresentationUtils.POINT_CONVERTER_2);
		getStringEncoder().addConverter(GraphicalRepresentationUtils.STEPPED_DIMENSION_CONVERTER);

		try {
			importClass(GraphicalRepresentation.class);
			importClass(DrawingGraphicalRepresentation.class);
			importClass(ShapeGraphicalRepresentation.class);
			importClass(ConnectorGraphicalRepresentation.class);
			importClass(GeometricGraphicalRepresentation.class);
		} catch (ModelDefinitionException e) {
			logger.warning("Unexpected exception: " + e);
			e.printStackTrace();
		}

		deserializer = new XMLDeserializer(this);
		serializer = new XMLSerializer(getStringEncoder());

	}

	public XMLSerializer getSerializer() {
		return serializer;
	}

	public XMLDeserializer getDeserializer() {
		return deserializer;
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
		DrawingGraphicalRepresentation<M> returned = newInstance(DrawingGraphicalRepresentation.class);
		returned.setDrawable(aDrawing.getModel());
		returned.setDrawing(aDrawing);
		returned.setTextStyle(makeDefaultTextStyle());
		returned.setBackgroundColor(FGEConstants.DEFAULT_DRAWING_BACKGROUND_COLOR);
		returned.setWidth(FGEConstants.DEFAULT_DRAWING_WIDTH);
		returned.setHeight(FGEConstants.DEFAULT_DRAWING_HEIGHT);
		if (initBasicControls) {
			returned.addToMouseClickControls(MouseClickControl.makeMouseClickControl("Drawing selection", MouseButton.LEFT, 1,
					MouseClickControlActionType.SELECTION));
			returned.addToMouseDragControls(MouseDragControl.makeMouseDragControl("Rectangle selection", MouseButton.LEFT,
					MouseDragControlActionType.RECTANGLE_SELECTING));
			returned.addToMouseDragControls(MouseDragControl.makeMouseDragControl("Zoom", MouseButton.RIGHT,
					MouseDragControlActionType.ZOOM));
		}
		return returned;
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

		ShapeGraphicalRepresentation<O> returned = newInstance(ShapeGraphicalRepresentation.class);
		returned.setDrawable(aDrawable);
		returned.setDrawing(aDrawing);
		returned.setLayer(FGEConstants.DEFAULT_SHAPE_LAYER);
		returned.setTextStyle(makeDefaultTextStyle());
		returned.setForeground(makeDefaultForegroundStyle());
		returned.setBackground(makeColoredBackground(Color.WHITE));
		returned.setShadowStyle(makeDefaultShadowStyle());

		returned.addToMouseClickControls(MouseClickControl.makeMouseClickControl("Selection", MouseButton.LEFT, 1,
				MouseClickControlActionType.SELECTION));
		if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			returned.addToMouseClickControls(MouseClickControl.makeMouseMetaClickControl("Multiple selection", MouseButton.LEFT, 1,
					MouseClickControlActionType.MULTIPLE_SELECTION));
		} else {
			returned.addToMouseClickControls(MouseClickControl.makeMouseControlClickControl("Multiple selection", MouseButton.LEFT, 1,
					MouseClickControlActionType.MULTIPLE_SELECTION));
		}
		returned.addToMouseDragControls(MouseDragControl.makeMouseDragControl("Move", MouseButton.LEFT, MouseDragControlActionType.MOVE));
		returned.addToMouseDragControls(MouseDragControl.makeMouseDragControl("Zoom", MouseButton.RIGHT, MouseDragControlActionType.ZOOM));
		returned.addToMouseDragControls(MouseDragControl.makeMouseShiftDragControl("Rectangle selection", MouseButton.LEFT,
				MouseDragControlActionType.RECTANGLE_SELECTING));

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

		ConnectorGraphicalRepresentation<O> returned = newInstance(ConnectorGraphicalRepresentation.class);
		returned.setDrawable(aDrawable);
		returned.setDrawing(aDrawing);
		returned.setLayer(FGEConstants.DEFAULT_CONNECTOR_LAYER);
		returned.setForeground(makeDefaultForegroundStyle());
		returned.setTextStyle(makeDefaultTextStyle());

		returned.addToMouseClickControls(MouseClickControl.makeMouseClickControl("Selection", MouseButton.LEFT, 1,
				MouseClickControlActionType.SELECTION));
		if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			returned.addToMouseClickControls(MouseClickControl.makeMouseMetaClickControl("Multiple selection", MouseButton.LEFT, 1,
					MouseClickControlActionType.MULTIPLE_SELECTION));
		} else {
			returned.addToMouseClickControls(MouseClickControl.makeMouseControlClickControl("Multiple selection", MouseButton.LEFT, 1,
					MouseClickControlActionType.MULTIPLE_SELECTION));
		}

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

		GeometricGraphicalRepresentation<O> returned = newInstance(GeometricGraphicalRepresentation.class);
		returned.setDrawable(aDrawable);
		returned.setDrawing(aDrawing);
		returned.setLayer(FGEConstants.DEFAULT_OBJECT_LAYER);
		returned.setForeground(makeDefaultForegroundStyle());
		returned.setBackground(makeColoredBackground(Color.WHITE));
		returned.setTextStyle(makeDefaultTextStyle());

		returned.addToMouseClickControls(MouseClickControl.makeMouseClickControl("Selection", MouseButton.LEFT, 1,
				MouseClickControlActionType.SELECTION));
		if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			returned.addToMouseClickControls(MouseClickControl.makeMouseMetaClickControl("Multiple selection", MouseButton.LEFT, 1,
					MouseClickControlActionType.MULTIPLE_SELECTION));
		} else {
			returned.addToMouseClickControls(MouseClickControl.makeMouseControlClickControl("Multiple selection", MouseButton.LEFT, 1,
					MouseClickControlActionType.MULTIPLE_SELECTION));
		}

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
	 * Make a new foreground style (stroke style)
	 * 
	 * @return a newly created ForegroundStyle
	 */
	public ForegroundStyle makeNewForegroundStyle() {
		return newInstance(ForegroundStyle.class);
	}

	/**
	 * Make a new foreground style (stroke style), initialized with default values as declared in FGEConstants
	 * 
	 * @return a newly created ForegroundStyle
	 */
	public ForegroundStyle makeDefaultForegroundStyle() {
		ForegroundStyle returned = newInstance(ForegroundStyle.class);
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
		ShadowStyle shadow = newInstance(ShadowStyle.class);
		shadow.setDrawShadow(false);
		shadow.setShadowDepth(0);
		return shadow;
	}

	/**
	 * Make default shadow style (initialized with default values as declared in FGEConstants)
	 * 
	 * @return a newly created ShadowStyle
	 */
	public ShadowStyle makeDefaultShadowStyle() {
		ShadowStyle shadow = newInstance(ShadowStyle.class);
		shadow.setDrawShadow(true);
		shadow.setShadowDarkness(FGEConstants.DEFAULT_SHADOW_DARKNESS);
		shadow.setShadowDepth(FGEConstants.DEFAULT_SHADOW_DEEP);
		shadow.setShadowBlur(FGEConstants.DEFAULT_SHADOW_BLUR);
		return shadow;
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
		returned.setTop(top);
		returned.setBottom(bottom);
		returned.setLeft(left);
		returned.setRight(right);
		return returned;
	}

}
