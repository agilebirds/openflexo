package org.openflexo.foundation.technologyadapter;

import java.util.logging.Logger;

import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.foundation.view.FreeModelSlotInstance;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration;
import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.foundation.viewpoint.ViewPointObject.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.toolbox.JavaUtils;

/**
 * Implementation of a ModelSlot in a given technology implementing model conformance.<br>
 * 
 */
public abstract class FreeModelSlot<RD extends ResourceData<RD>> extends ModelSlot<RD> {

	private static final Logger logger = Logger.getLogger(FreeModelSlot.class.getPackage().getName());

	protected FreeModelSlot(VirtualModel virtualModel, TechnologyAdapter technologyAdapter) {
		super(virtualModel, technologyAdapter);
	}

	protected FreeModelSlot(VirtualModelBuilder builder) {
		super(builder);
	}

	/**
	 * Instanciate a new model slot instance configuration for this model slot
	 */
	@Override
	public abstract ModelSlotInstanceConfiguration<? extends FreeModelSlot<RD>, RD> createConfiguration(CreateVirtualModelInstance<?> action);

	/**
	 * Instantiate a new IndividualPatternRole
	 * 
	 * @param ontClass
	 * @return
	 */
	public IndividualPatternRole<?> makeIndividualPatternRole(IFlexoOntologyClass ontClass) {
		Class<? extends IndividualPatternRole> individualPRClass = getPatternRoleClass(IndividualPatternRole.class);
		IndividualPatternRole<?> returned = makePatternRole(individualPRClass);
		returned.setOntologicType(ontClass);
		return returned;
	}

	/**
	 * Return a new String (full URI) uniquely identifying a new object in related technology, according to the conventions of related
	 * technology
	 * 
	 * @param msInstance
	 * @param proposedName
	 * @return
	 */
	public String generateUniqueURI(FreeModelSlotInstance msInstance, String proposedName) {
		if (msInstance == null || msInstance.getResourceData() == null) {
			return null;
		}
		return msInstance.getResourceURI() + "#" + generateUniqueURIName(msInstance, proposedName);
	}

	/**
	 * Return a new String (the simple name) uniquely identifying a new object in related technology, according to the conventions of
	 * related technology
	 * 
	 * @param msInstance
	 * @param proposedName
	 * @return
	 */
	public String generateUniqueURIName(FreeModelSlotInstance msInstance, String proposedName) {
		if (msInstance == null || msInstance.getResourceData() == null) {
			return proposedName;
		}
		return generateUniqueURIName(msInstance, proposedName, msInstance.getResourceURI() + "#");
	}

	public String generateUniqueURIName(FreeModelSlotInstance msInstance, String proposedName, String uriPrefix) {
		if (msInstance == null || msInstance.getResourceData() == null) {
			return proposedName;
		}
		String baseName = JavaUtils.getClassName(proposedName);
		/*boolean unique = false;
		int testThis = 0;
		while (!unique) {
			unique = msInstance.getResourceData().getObject(uriPrefix + baseName) == null;
			if (!unique) {
				testThis++;
				baseName = proposedName + testThis;
			}
		}*/
		return baseName;
	}

	public abstract TechnologyAdapterResource<RD, ?> createProjectSpecificEmptyResource(View view, String filename, String modelUri);

	public abstract TechnologyAdapterResource<RD, ?> createSharedEmptyResource(FlexoResourceCenter<?> resourceCenter, String relativePath,
			String filename, String modelUri);

	@Override
	public String getFMLRepresentation(FMLRepresentationContext context) {
		FMLRepresentationOutput out = new FMLRepresentationOutput(context);
		out.append("ModelSlot " + getName() + " type=" + getClass().getSimpleName() + " required=" + getIsRequired() + " readOnly="
				+ getIsReadOnly() + ";", context);
		return out.toString();
	}

	@Override
	public final String getURIForObject(ModelSlotInstance msInstance, Object o) {
		return getURIForObject((FreeModelSlotInstance<RD, ? extends FreeModelSlot<RD>>) msInstance, o);
	}

	@Override
	public final Object retrieveObjectWithURI(ModelSlotInstance msInstance, String objectURI) {
		return retrieveObjectWithURI((FreeModelSlotInstance<RD, ? extends FreeModelSlot<RD>>) msInstance, objectURI);
	}

	public abstract String getURIForObject(FreeModelSlotInstance<RD, ? extends FreeModelSlot<RD>> msInstance, Object o);

	public abstract Object retrieveObjectWithURI(FreeModelSlotInstance<RD, ? extends FreeModelSlot<RD>> msInstance, String objectURI);

}
