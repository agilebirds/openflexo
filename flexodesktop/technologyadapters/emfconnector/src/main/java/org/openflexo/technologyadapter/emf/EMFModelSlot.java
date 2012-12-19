package org.openflexo.technologyadapter.emf;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.technologyadapter.FlexoOntologyModelSlot;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;

/**
 * Implementation of the ModelSlot class for the EMF technology adapter
 * 
 * @author sylvain
 * 
 */
public class EMFModelSlot extends FlexoOntologyModelSlot<EMFModel, EMFMetaModel> {

	/**
	 * 
	 * Constructor.
	 * 
	 * @param viewPoint
	 * @param adapter
	 */
	public EMFModelSlot(ViewPoint viewPoint, EMFTechnologyAdapter adapter) {
		super(viewPoint, adapter);
	}

	/**
	 * 
	 * Constructor.
	 * 
	 * @param builder
	 */
	public EMFModelSlot(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public Class<EMFTechnologyAdapter> getTechnologyAdapterClass() {
		return EMFTechnologyAdapter.class;
	}

	@Override
	public <PR extends PatternRole<?>> PR makePatternRole(Class<PR> patternRoleClass) {
		// TODO
		return null;
	}

	@Override
	public <PR extends PatternRole<?>> String defaultPatternRoleName(Class<PR> patternRoleClass) {
		return super.defaultPatternRoleName(patternRoleClass);
	}

	@Override
	public BindingVariable<?> makePatternRolePathElement(PatternRole<?> pr, Bindable container) {
		return null;
	}

}
