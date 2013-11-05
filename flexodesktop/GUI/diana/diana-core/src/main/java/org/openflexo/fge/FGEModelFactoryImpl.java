package org.openflexo.fge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.openflexo.fge.ShapeGraphicalRepresentation.ShapeBorder;
import org.openflexo.fge.connectors.ConnectorSpecification;
import org.openflexo.fge.connectors.CurveConnectorSpecification;
import org.openflexo.fge.connectors.CurvedPolylinConnectorSpecification;
import org.openflexo.fge.connectors.LineConnectorSpecification;
import org.openflexo.fge.connectors.RectPolylinConnectorSpecification;
import org.openflexo.fge.connectors.impl.ConnectorSpecificationImpl;
import org.openflexo.fge.connectors.impl.CurveConnectorSpecificationImpl;
import org.openflexo.fge.connectors.impl.CurvedPolylinConnectorSpecificationImpl;
import org.openflexo.fge.connectors.impl.LineConnectorSpecificationImpl;
import org.openflexo.fge.connectors.impl.RectPolylinConnectorSpecificationImpl;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaEditor;
import org.openflexo.fge.control.MouseClickControl;
import org.openflexo.fge.control.MouseClickControlAction;
import org.openflexo.fge.control.MouseControl.MouseButton;
import org.openflexo.fge.control.MouseDragControl;
import org.openflexo.fge.control.MouseDragControlAction;
import org.openflexo.fge.control.PredefinedMouseClickControlActionType;
import org.openflexo.fge.control.PredefinedMouseDragControlActionType;
import org.openflexo.fge.control.actions.ContinuousSelectionAction;
import org.openflexo.fge.control.actions.MouseClickControlImpl;
import org.openflexo.fge.control.actions.MouseDragControlImpl;
import org.openflexo.fge.control.actions.MoveAction;
import org.openflexo.fge.control.actions.MultipleSelectionAction;
import org.openflexo.fge.control.actions.RectangleSelectingAction;
import org.openflexo.fge.control.actions.SelectionAction;
import org.openflexo.fge.control.actions.ZoomAction;
import org.openflexo.fge.impl.BackgroundImageBackgroundStyleImpl;
import org.openflexo.fge.impl.BackgroundStyleImpl;
import org.openflexo.fge.impl.ColorBackgroundStyleImpl;
import org.openflexo.fge.impl.ColorGradientBackgroundStyleImpl;
import org.openflexo.fge.impl.ConnectorGraphicalRepresentationImpl;
import org.openflexo.fge.impl.ContainerGraphicalRepresentationImpl;
import org.openflexo.fge.impl.DrawingGraphicalRepresentationImpl;
import org.openflexo.fge.impl.FGEStyleImpl;
import org.openflexo.fge.impl.ForegroundStyleImpl;
import org.openflexo.fge.impl.GraphicalRepresentationImpl;
import org.openflexo.fge.impl.NoneBackgroundStyleImpl;
import org.openflexo.fge.impl.ShadowStyleImpl;
import org.openflexo.fge.impl.ShapeGraphicalRepresentationImpl;
import org.openflexo.fge.impl.ShapeGraphicalRepresentationImpl.ShapeBorderImpl;
import org.openflexo.fge.impl.TextStyleImpl;
import org.openflexo.fge.impl.TextureBackgroundStyleImpl;
import org.openflexo.fge.shapes.Arc;
import org.openflexo.fge.shapes.Circle;
import org.openflexo.fge.shapes.ClosedCurve;
import org.openflexo.fge.shapes.Losange;
import org.openflexo.fge.shapes.Oval;
import org.openflexo.fge.shapes.Polygon;
import org.openflexo.fge.shapes.Rectangle;
import org.openflexo.fge.shapes.RectangularOctogon;
import org.openflexo.fge.shapes.RegularPolygon;
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.fge.shapes.Square;
import org.openflexo.fge.shapes.Star;
import org.openflexo.fge.shapes.Triangle;
import org.openflexo.fge.shapes.impl.ArcImpl;
import org.openflexo.fge.shapes.impl.CircleImpl;
import org.openflexo.fge.shapes.impl.ClosedCurveImpl;
import org.openflexo.fge.shapes.impl.LosangeImpl;
import org.openflexo.fge.shapes.impl.OvalImpl;
import org.openflexo.fge.shapes.impl.PolygonImpl;
import org.openflexo.fge.shapes.impl.RectangleImpl;
import org.openflexo.fge.shapes.impl.RectangularOctogonImpl;
import org.openflexo.fge.shapes.impl.RegularPolygonImpl;
import org.openflexo.fge.shapes.impl.ShapeSpecificationImpl;
import org.openflexo.fge.shapes.impl.SquareImpl;
import org.openflexo.fge.shapes.impl.StarImpl;
import org.openflexo.fge.shapes.impl.TriangleImpl;
import org.openflexo.model.exceptions.ModelDefinitionException;

public class FGEModelFactoryImpl extends FGEModelFactory {

	/**
	 * Creates a new model factory including all classes involved in FGE model
	 * 
	 * @throws ModelDefinitionException
	 */
	public FGEModelFactoryImpl() throws ModelDefinitionException {
		this(new ArrayList<Class<?>>());
	}

	/**
	 * Creates a new model factory including all supplied classes and all classes involved in FGE model
	 * 
	 * @throws ModelDefinitionException
	 */
	public FGEModelFactoryImpl(Class<?>... classes) throws ModelDefinitionException {
		this(Arrays.asList(classes));
	}

	/**
	 * Creates a new model factory including all supplied classes and all classes involved in FGE model
	 * 
	 * @throws ModelDefinitionException
	 */
	public FGEModelFactoryImpl(Collection<Class<?>> classes) throws ModelDefinitionException {

		super(classes);
	}

	@Override
	public void installImplementingClasses() throws ModelDefinitionException {

		setImplementingClassForInterface(GraphicalRepresentationImpl.class, GraphicalRepresentation.class);
		setImplementingClassForInterface(ShapeGraphicalRepresentationImpl.class, ShapeGraphicalRepresentation.class);
		setImplementingClassForInterface(ConnectorGraphicalRepresentationImpl.class, ConnectorGraphicalRepresentation.class);
		setImplementingClassForInterface(DrawingGraphicalRepresentationImpl.class, DrawingGraphicalRepresentation.class);
		setImplementingClassForInterface(ContainerGraphicalRepresentationImpl.class, ContainerGraphicalRepresentation.class);

		setImplementingClassForInterface(ShapeBorderImpl.class, ShapeBorder.class);

		setImplementingClassForInterface(FGEStyleImpl.class, FGEStyle.class);
		setImplementingClassForInterface(ForegroundStyleImpl.class, ForegroundStyle.class);
		setImplementingClassForInterface(ShadowStyleImpl.class, ShadowStyle.class);
		setImplementingClassForInterface(TextStyleImpl.class, TextStyle.class);
		setImplementingClassForInterface(BackgroundStyleImpl.class, BackgroundStyle.class);
		setImplementingClassForInterface(NoneBackgroundStyleImpl.class, NoneBackgroundStyle.class);
		setImplementingClassForInterface(ColorBackgroundStyleImpl.class, ColorBackgroundStyle.class);
		setImplementingClassForInterface(ColorGradientBackgroundStyleImpl.class, ColorGradientBackgroundStyle.class);
		setImplementingClassForInterface(TextureBackgroundStyleImpl.class, TextureBackgroundStyle.class);
		setImplementingClassForInterface(BackgroundImageBackgroundStyleImpl.class, BackgroundImageBackgroundStyle.class);

		setImplementingClassForInterface(ShapeSpecificationImpl.class, ShapeSpecification.class);
		setImplementingClassForInterface(ArcImpl.class, Arc.class);
		setImplementingClassForInterface(CircleImpl.class, Circle.class);
		setImplementingClassForInterface(LosangeImpl.class, Losange.class);
		setImplementingClassForInterface(OvalImpl.class, Oval.class);
		setImplementingClassForInterface(PolygonImpl.class, Polygon.class);
		setImplementingClassForInterface(RectangleImpl.class, Rectangle.class);
		setImplementingClassForInterface(RectangularOctogonImpl.class, RectangularOctogon.class);
		setImplementingClassForInterface(RegularPolygonImpl.class, RegularPolygon.class);
		setImplementingClassForInterface(SquareImpl.class, Square.class);
		setImplementingClassForInterface(StarImpl.class, Star.class);
		setImplementingClassForInterface(TriangleImpl.class, Triangle.class);
		setImplementingClassForInterface(ClosedCurveImpl.class, ClosedCurve.class);

		setImplementingClassForInterface(ConnectorSpecificationImpl.class, ConnectorSpecification.class);
		setImplementingClassForInterface(LineConnectorSpecificationImpl.class, LineConnectorSpecification.class);
		setImplementingClassForInterface(CurveConnectorSpecificationImpl.class, CurveConnectorSpecification.class);
		setImplementingClassForInterface(RectPolylinConnectorSpecificationImpl.class, RectPolylinConnectorSpecification.class);
		setImplementingClassForInterface(CurvedPolylinConnectorSpecificationImpl.class, CurvedPolylinConnectorSpecification.class);

	}

	@Override
	public MouseClickControl<AbstractDianaEditor<?, ?, ?>> makeMouseClickControl(String aName, MouseButton button, int clickCount,
			boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed) {
		return new MouseClickControlImpl<AbstractDianaEditor<?, ?, ?>>(aName, button, clickCount, null, shiftPressed, ctrlPressed,
				metaPressed, altPressed, this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public MouseClickControl<AbstractDianaEditor<?, ?, ?>> makeMouseClickControl(String aName, MouseButton button, int clickCount,
			PredefinedMouseClickControlActionType actionType, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed,
			boolean altPressed) {
		return new MouseClickControlImpl<AbstractDianaEditor<?, ?, ?>>(aName, button, clickCount,
				(MouseClickControlAction<AbstractDianaEditor<?, ?, ?>>) makeMouseClickControlAction(actionType), shiftPressed, ctrlPressed,
				metaPressed, altPressed, this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E extends DianaEditor<?>> MouseClickControl<E> makeMouseClickControl(String aName, MouseButton button, int clickCount,
			MouseClickControlAction<E> action, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed) {
		// Little compiling hack to "cast" generics E (the goal is to change upper bounds of E from DianaEditor<?> to AbstractDianaEditor<?,
		// ?, ?>)
		return (MouseClickControl<E>) makeMouseClickControl2(aName, button, clickCount,
				(MouseClickControlAction<AbstractDianaEditor<?, ?, ?>>) action, shiftPressed, ctrlPressed, metaPressed, altPressed);
	}

	// Little compiling hack to "cast" generics E (the goal is to change upper bounds of E from DianaEditor<?> to AbstractDianaEditor<?, ?,
	// ?>)
	private <E2 extends AbstractDianaEditor<?, ?, ?>> MouseClickControl<E2> makeMouseClickControl2(String aName, MouseButton button,
			int clickCount, MouseClickControlAction<E2> action, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed,
			boolean altPressed) {
		return new MouseClickControlImpl<E2>(aName, button, clickCount, action, shiftPressed, ctrlPressed, metaPressed, altPressed, this);
	}

	@Override
	public MouseDragControl<? extends AbstractDianaEditor<?, ?, ?>> makeMouseDragControl(String aName, MouseButton button,
			boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed) {
		return new MouseDragControlImpl<AbstractDianaEditor<?, ?, ?>>(aName, button, null, shiftPressed, ctrlPressed, metaPressed,
				altPressed, this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public MouseDragControl<AbstractDianaEditor<?, ?, ?>> makeMouseDragControl(String aName, MouseButton button,
			PredefinedMouseDragControlActionType actionType, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed,
			boolean altPressed) {
		return new MouseDragControlImpl<AbstractDianaEditor<?, ?, ?>>(aName, button,
				(MouseDragControlAction<AbstractDianaEditor<?, ?, ?>>) makeMouseDragControlAction(actionType), shiftPressed, ctrlPressed,
				metaPressed, altPressed, this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E extends DianaEditor<?>> MouseDragControl<E> makeMouseDragControl(String aName, MouseButton button,
			MouseDragControlAction<E> action, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed) {
		// Little compiling hack to "cast" generics E (the goal is to change upper bounds of E from DianaEditor<?> to AbstractDianaEditor<?,
		// ?, ?>)
		return (MouseDragControl<E>) makeMouseDragControl2(aName, button, (MouseDragControlAction<AbstractDianaEditor<?, ?, ?>>) action,
				shiftPressed, ctrlPressed, metaPressed, altPressed);
	}

	// Little compiling hack to "cast" generics E (the goal is to change upper bounds of E from DianaEditor<?> to AbstractDianaEditor<?, ?,
	// ?>)
	private <E2 extends AbstractDianaEditor<?, ?, ?>> MouseDragControl<E2> makeMouseDragControl2(String aName, MouseButton button,
			MouseDragControlAction<E2> action, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed) {
		return new MouseDragControlImpl<E2>(aName, button, action, shiftPressed, ctrlPressed, metaPressed, altPressed, this);
	}

	public MouseDragControlAction<? extends AbstractDianaEditor<?, ?, ?>> makeMouseDragControlAction(
			PredefinedMouseDragControlActionType actionType) {
		switch (actionType) {
		case MOVE:
			return new MoveAction();
		case RECTANGLE_SELECTING:
			return new RectangleSelectingAction();
		case ZOOM:
			return new ZoomAction();
		default:
			logger.warning("Unexpected actionType " + actionType);
			return null;
		}
	}

	public MouseClickControlAction<? extends AbstractDianaEditor<?, ?, ?>> makeMouseClickControlAction(
			PredefinedMouseClickControlActionType actionType) {
		switch (actionType) {
		case SELECTION:
			return new SelectionAction();
		case CONTINUOUS_SELECTION:
			return new ContinuousSelectionAction();
		case MULTIPLE_SELECTION:
			return new MultipleSelectionAction();
		default:
			logger.warning("Unexpected actionType " + actionType);
			return null;
		}
	}

}
