package org.openflexo.foundation.technologyadapter;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.view.diagram.model.View;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.PatternRole;

/**
 * <p>
 * A model slot is a named object providing symbolic access to a model conform to a meta-model (see {@link FlexoMetaModel}). <br>
 * It is defined at viewpoint level. <br>
 * A view (viewpoint instance) binds used slots to their models within the project.
 * 
 * @param <M>
 *            Type of {@link FlexoModel} handled by this ModelSlot
 * @param <MM>
 *            {@link FlexoMetaModel} which model handled by this ModelSlot is conform to
 * 
 * @author Luka Le Roux, Sylvain Guerin
 * @see org.openflexo.foundation.viewpoint.ViewPoint
 * @see org.openflexo.foundation.view.diagram.model.View
 * 
 */
public interface ModelSlot<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> {

	public String getName();

	public TechnologyAdapter<M, MM> getTechnologyAdapter();

	public MM getMetaModel();

	public void setMetaModel(MM metaModel);

	public void setName(String name);

	public boolean getIsReadOnly();

	public void setIsReadOnly(boolean isReadOnly);

	public boolean getIsRequired();

	public void setIsRequired(boolean isRequired);

	/**
	 * Instantiate and returns an empty model to be used as model for this slot.
	 * 
	 * @param view
	 * @param metaModel
	 * 
	 * @return a newly created model conform to supplied meta model
	 */
	public M createEmptyModel(View view, MM metaModel);

	/**
	 * Instantiate new action of required type
	 * 
	 * @param actionClass
	 * @return
	 */
	public <A extends EditionAction<M, MM, ?>> A createAction(Class<A> actionClass);

	public abstract BindingVariable<?> makePatternRolePathElement(PatternRole<?> pr, Bindable container);

}
