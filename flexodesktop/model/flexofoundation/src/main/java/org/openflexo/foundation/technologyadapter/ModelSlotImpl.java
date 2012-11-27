package org.openflexo.foundation.technologyadapter;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.view.View;
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
 * @see org.openflexo.foundation.view.View
 */
public abstract class ModelSlotImpl<M extends FlexoModel<MM>, MM extends FlexoMetaModel> extends ViewPointObject implements
		ModelSlot<M, MM> {

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
