package org.openflexo.fge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.openflexo.fge.ShapeGraphicalRepresentation.ShapeBorder;
import org.openflexo.fge.control.MouseClickControl;
import org.openflexo.fge.control.MouseClickControlAction;
import org.openflexo.fge.control.MouseClickControlAction.MouseClickControlActionType;
import org.openflexo.fge.control.MouseControl.MouseButton;
import org.openflexo.fge.control.MouseDragControl;
import org.openflexo.fge.control.MouseDragControlAction;
import org.openflexo.fge.control.MouseDragControlAction.MouseDragControlActionType;
import org.openflexo.fge.controller.MouseClickControlImpl;
import org.openflexo.fge.controller.MouseDragControlImpl;
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
		setImplementingClassForInterface(BackgroundStyleImpl.class, BackgroundStyle.class);
		setImplementingClassForInterface(NoneBackgroundStyleImpl.class, NoneBackgroundStyle.class);
		setImplementingClassForInterface(ColorBackgroundStyleImpl.class, ColorBackgroundStyle.class);
		setImplementingClassForInterface(ColorGradientBackgroundStyleImpl.class, ColorGradientBackgroundStyle.class);
		setImplementingClassForInterface(TextureBackgroundStyleImpl.class, TextureBackgroundStyle.class);
		setImplementingClassForInterface(BackgroundImageBackgroundStyleImpl.class, BackgroundImageBackgroundStyle.class);
		setImplementingClassForInterface(ForegroundStyleImpl.class, ForegroundStyle.class);
		setImplementingClassForInterface(ShadowStyleImpl.class, ShadowStyle.class);
		setImplementingClassForInterface(TextStyleImpl.class, TextStyle.class);
		setImplementingClassForInterface(FGEStyleImpl.class, FGEStyle.class);
	}

	@Override
	public MouseClickControl makeMouseClickControl(String aName, MouseButton button, int clickCount, boolean shiftPressed,
			boolean ctrlPressed, boolean metaPressed, boolean altPressed) {
		return new MouseClickControlImpl(aName, button, clickCount, shiftPressed, ctrlPressed, metaPressed, altPressed);
	}

	@Override
	public MouseClickControl makeMouseClickControl(String aName, MouseButton button, int clickCount,
			MouseClickControlActionType actionType, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed) {
		return new MouseClickControlImpl(aName, button, clickCount, actionType, shiftPressed, ctrlPressed, metaPressed, altPressed);
	}

	@Override
	public MouseClickControl makeMouseClickControl(String aName, MouseButton button, int clickCount, MouseClickControlAction action,
			boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed) {
		return new MouseClickControlImpl(aName, button, clickCount, action, shiftPressed, ctrlPressed, metaPressed, altPressed);
	}

	@Override
	public MouseDragControl makeMouseDragControl(String aName, MouseButton button, boolean shiftPressed, boolean ctrlPressed,
			boolean metaPressed, boolean altPressed) {
		return new MouseDragControlImpl(aName, button, shiftPressed, ctrlPressed, metaPressed, altPressed);
	}

	@Override
	public MouseDragControl makeMouseDragControl(String aName, MouseButton button, MouseDragControlActionType actionType,
			boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed) {
		return new MouseDragControlImpl(aName, button, actionType, shiftPressed, ctrlPressed, metaPressed, altPressed);
	}

	@Override
	public MouseDragControl makeMouseDragControl(String aName, MouseButton button, MouseDragControlAction action, boolean shiftPressed,
			boolean ctrlPressed, boolean metaPressed, boolean altPressed) {
		return new MouseDragControlImpl(aName, button, action, shiftPressed, ctrlPressed, metaPressed, altPressed);
	}

}
