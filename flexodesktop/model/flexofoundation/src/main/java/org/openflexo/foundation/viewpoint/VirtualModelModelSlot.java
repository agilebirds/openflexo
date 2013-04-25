package org.openflexo.foundation.viewpoint;

import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.rm.VirtualModelResource;
import org.openflexo.foundation.technologyadapter.DeclareEditionAction;
import org.openflexo.foundation.technologyadapter.DeclareEditionActions;
import org.openflexo.foundation.technologyadapter.DeclareFetchRequest;
import org.openflexo.foundation.technologyadapter.DeclareFetchRequests;
import org.openflexo.foundation.technologyadapter.DeclarePatternRole;
import org.openflexo.foundation.technologyadapter.DeclarePatternRoles;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.toolbox.StringUtils;

/**
 * Implementation of the ModelSlot class for the Openflexo built-in diagram technology adapter
 * 
 * @author sylvain
 * 
 */
@DeclarePatternRoles({ @DeclarePatternRole(EditionPatternInstancePatternRole.class) // EditionPattern
})
@DeclareEditionActions({ @DeclareEditionAction(AddEditionPatternInstance.class) // Add EditionPatternInstance
})
@DeclareFetchRequests({ @DeclareFetchRequest(SelectEditionPatternInstance.class) // Select EditionPatternInstance
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
		if (EditionPatternInstancePatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new EditionPatternInstancePatternRole(null);
		}
		logger.warning("Unexpected pattern role: " + patternRoleClass.getName());
		return null;
	}

	@Override
	public <PR extends PatternRole<?>> String defaultPatternRoleName(Class<PR> patternRoleClass) {
		if (EditionPatternInstancePatternRole.class.isAssignableFrom(patternRoleClass)) {
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
		if (SelectEditionPatternInstance.class.isAssignableFrom(fetchRequestClass)) {
			return (FR) new SelectEditionPatternInstance(null);
		}
		return null;
	}

	@Override
	public ModelSlotInstanceConfiguration<? extends VirtualModelModelSlot<VMI, VM>> createConfiguration(CreateVirtualModelInstance<?> action) {
		return new VirtualModelModelSlotInstanceConfiguration<VirtualModelModelSlot<VMI, VM>>(this, action);
	}

	private VirtualModelResource<?> virtualModelResource;
	private String virtualModelURI;

	public VirtualModelResource<?> getVirtualModelResource() {
		if (virtualModelResource == null && StringUtils.isNotEmpty(virtualModelURI)) {
			virtualModelResource = getViewPoint().getVirtualModelNamed(virtualModelURI).getResource();
			logger.info("Looked-up " + virtualModelResource);
		}
		return virtualModelResource;
	}

	public void setVirtualModelResource(VirtualModelResource<?> virtualModelResource) {
		this.virtualModelResource = virtualModelResource;
	}

	@Override
	public String getMetaModelURI() {
		if (virtualModelResource != null) {
			return virtualModelResource.getURI();
		}
		return virtualModelURI;
	}

	@Override
	public void setMetaModelURI(String metaModelURI) {
		this.virtualModelURI = metaModelURI;
	}

	/**
	 * Return adressed virtual model (the virtual model this model slot specifically adresses, not the one in which it is defined)
	 * 
	 * @return
	 */
	public VirtualModel getAddressedVirtualModel() {
		if (getViewPoint() != null && StringUtils.isNotEmpty(virtualModelURI)) {
			return getViewPoint().getVirtualModelNamed(virtualModelURI);
		}
		return null;
	}

	/**
	 * Return flag indicating if this model slot is the reflexive model slot for virtual model container
	 * 
	 * @return
	 */
	public boolean isReflexiveModelSlot() {
		return getName().equals(VirtualModel.REFLEXIVE_MODEL_SLOT_NAME) && getVirtualModelResource() == getVirtualModel().getResource();
	}

	/** 
	 * 
	 * @param msInstance
	 * @param o
	 * @return URI as String
	 */
	public String getURIForObject(ModelSlotInstance msInstance, Object o)
	  {
	    logger.warning("This  Je ne devrais jamais passé là normalement....");
	    return null;
	  }

	/**
	 * @param msInstance
	 * @param objectURI
	 * @return the Object
	 */
	  public Object retrieveObjectWithURI(ModelSlotInstance msInstance, String objectURI)
	  {
	    logger.warning("Whouata, Tu es sûr de toi là? Je ne devrais jamais passé là normalement....");
	    return null;
}
