package org.openflexo.foundation.technologyadapter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.view.diagram.model.View;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.ViewPointObject;

/**
 * Provides default implementation of the {@link ModelSlot} interface.
 * 
 * @author Luka Le Roux, Sylvain Guerin
 * 
 * @param <M>
 *            Type of {@link FlexoModel} handled by this ModelSlot
 * @param <MM>
 *            {@link FlexoMetaModel} which model handled by this ModelSlot is conform to
 * 
 * @see org.openflexo.foundation.viewpoint.ViewPoint
 * @see org.openflexo.foundation.view.diagram.model.View
 */
public abstract class ModelSlotImpl<M extends FlexoModel<MM>, MM extends FlexoMetaModel> extends ViewPointObject implements
		ModelSlot<M, MM> {

	private static final Logger logger = Logger.getLogger(ModelSlotImpl.class.getPackage().getName());

	private String name;
	private boolean isRequired;
	private boolean isReadOnly;
	private MM metaModel;

	private ViewPoint viewPoint;

	protected ModelSlotImpl(ViewPoint viewPoint) {
		super(null);
		this.viewPoint = viewPoint;
	}

	protected ModelSlotImpl(ViewPointBuilder builder) {
		super(builder);
		if (builder != null) {
			this.viewPoint = builder.getViewPoint();
		}
	}

	@Override
	public ViewPoint getViewPoint() {
		return viewPoint;
	}

	@Override
	public final M createEmptyModel(View view, MM metaModel) {
		return getTechnologyAdapter().createNewModel(view.getProject(), metaModel);
	};

	/**
	 * Instantiate new action of required type<br>
	 * Default implementation. Override when required.
	 * 
	 * @param actionClass
	 * @return
	 */
	@Override
	public <A extends EditionAction<? extends ModelSlot<M, MM>, M, MM, ?>> A createAction(Class<A> actionClass) {
		Class[] constructorParams = new Class[1];
		constructorParams[0] = ViewPointBuilder.class;
		try {
			Constructor<A> c = actionClass.getConstructor(constructorParams);
			return c.newInstance(null);
		} catch (SecurityException e) {
			logger.warning("Unexpected SecurityException " + e);
			e.printStackTrace();
			return null;
		} catch (NoSuchMethodException e) {
			logger.warning("Unexpected NoSuchMethodException " + e);
			e.printStackTrace();
			return null;
		} catch (IllegalArgumentException e) {
			logger.warning("Unexpected IllegalArgumentException " + e);
			e.printStackTrace();
			return null;
		} catch (InstantiationException e) {
			logger.warning("Unexpected InstantiationException " + e);
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			logger.warning("Unexpected InvocationTargetException " + e);
			e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			logger.warning("Unexpected InvocationTargetException " + e);
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public MM getMetaModel() {
		return metaModel;
	}

	@Override
	public void setMetaModel(MM metaModel) {
		this.metaModel = metaModel;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean getIsReadOnly() {
		return isReadOnly;
	}

	@Override
	public void setIsReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	@Override
	public boolean getIsRequired() {
		return isRequired;
	}

	@Override
	public void setIsRequired(boolean isRequired) {
		this.isRequired = isRequired;
	}

	@Deprecated
	@Override
	public String getInspectorName() {
		return null;
	}

	@Override
	public BindingModel getBindingModel() {
		return viewPoint.getBindingModel();
	}

	@Override
	public String getLanguageRepresentation() {
		return "ModelSlot " + getName() + " conformTo " + getMetaModel().getURI() + " access=READ cardinality=ONE";
	}

}
