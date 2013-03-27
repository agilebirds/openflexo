package org.openflexo.foundation.viewpoint;

import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.technologyadapter.DeclareEditionAction;
import org.openflexo.foundation.technologyadapter.DeclareEditionActions;
import org.openflexo.foundation.technologyadapter.DeclarePatternRole;
import org.openflexo.foundation.technologyadapter.DeclarePatternRoles;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;

/**
 * Implementation of the ModelSlot class for the Openflexo built-in diagram technology adapter
 * 
 * @author sylvain
 * 
 */
@DeclarePatternRoles({ @DeclarePatternRole(EditionPatternPatternRole.class) // EditionPattern
})
@DeclareEditionActions({ @DeclareEditionAction(AddEditionPatternInstance.class) // Add EditionPattern
})
public class VirtualModelModelSlot<VMI extends VirtualModelInstance<VMI, VM>, VM extends VirtualModel<VM>> extends ModelSlot<VMI, VM> {

	private static final Logger logger = Logger.getLogger(VirtualModelModelSlot.class.getPackage().getName());

	public VirtualModelModelSlot(ViewPoint viewPoint, VirtualModelTechnologyAdapter<VMI, VM> adapter) {
		super(viewPoint, adapter);
	}

	public VirtualModelModelSlot(VirtualModel<?> virtualModel, VirtualModelTechnologyAdapter<VMI, VM> adapter) {
		super(virtualModel, adapter);
	}

	public VirtualModelModelSlot(VirtualModelBuilder builder) {
		super(builder);
	}

	public VirtualModelModelSlot(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public String getFullyQualifiedName() {
		return "VirtualModelModelSlot";
	}

	@Override
	public Class getTechnologyAdapterClass() {
		return VirtualModelTechnologyAdapter.class;
	}

	@Override
	public BindingVariable makePatternRolePathElement(PatternRole<?> pr, Bindable container) {
		/*if (pr instanceof ShapePatternRole) {
			return new ShapePatternRolePathElement((ShapePatternRole) pr, container);
		} else if (pr instanceof ConnectorPatternRole) {
			return new ConnectorPatternRolePathElement((ConnectorPatternRole) pr, container);
		} else {
			return null;
		}*/
		return null;
	}

	@Override
	public <PR extends PatternRole<?>> PR makePatternRole(Class<PR> patternRoleClass) {
		if (EditionPatternPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new EditionPatternPatternRole(null);
		}
		logger.warning("Unexpected pattern role: " + patternRoleClass.getName());
		return null;
	}

	@Override
	public <PR extends PatternRole<?>> String defaultPatternRoleName(Class<PR> patternRoleClass) {
		if (EditionPatternPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "editionPatternInstance";
		}
		logger.warning("Unexpected pattern role: " + patternRoleClass.getName());
		return null;
	}

	@Override
	public <EA extends EditionAction<?, ?, ?>> EA makeEditionAction(Class<EA> editionActionClass) {
		if (AddEditionPatternInstance.class.isAssignableFrom(editionActionClass)) {
			return (EA) new AddEditionPatternInstance(null);
		}
		return null;
	}

	@Override
	public <FR extends FetchRequest<?, ?, ?>> FR makeFetchRequest(Class<FR> fetchRequestClass) {
		return null;
	}

	@Override
	public ModelSlotInstanceConfiguration<? extends VirtualModelModelSlot<VMI, VM>> createConfiguration(CreateVirtualModelInstance<?> action) {
		return new VirtualModelModelSlotInstanceConfiguration<VirtualModelModelSlot<VMI, VM>>(this, action);
	}
}
