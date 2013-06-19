package org.openflexo.foundation.technologyadapter;

import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.view.TypeSafeModelSlotInstance;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration;
import org.openflexo.foundation.viewpoint.AbstractCreationScheme;
import org.openflexo.foundation.viewpoint.AddIndividual;
import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.foundation.viewpoint.ViewPointObject.FMLRepresentationContext.FMLRepresentationOutput;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.toolbox.JavaUtils;
import org.openflexo.toolbox.StringUtils;

/**
 * Implementation of a ModelSlot in a technology conform to FlexoOntology layer (strict metamodelling) This model slot is dedicated to
 * strict-metamodelling as it provides a symbolic access to a model conform to a meta-model (see {@link FlexoMetaModel}). <br>
 * 
 */
public abstract class TypeSafeModelSlot<M extends FlexoModel<M, MM>, MM extends FlexoMetaModel<MM>> extends ModelSlot<M> {

	private static final Logger logger = Logger.getLogger(TypeSafeModelSlot.class.getPackage().getName());

	private FlexoMetaModelResource<M, MM> metaModelResource;
	private String metaModelURI;

	/*protected TypeSafeModelSlot(ViewPoint viewPoint, TechnologyAdapter technologyAdapter) {
		super(viewPoint, technologyAdapter);
	}*/

	protected TypeSafeModelSlot(VirtualModel<?> virtualModel, TechnologyAdapter technologyAdapter) {
		super(virtualModel, technologyAdapter);
	}

	protected TypeSafeModelSlot(VirtualModelBuilder builder) {
		super(builder);
	}

	/*public TypeSafeModelSlot(ViewPointBuilder builder) {
		super(builder);
	}*/

	@Override
	public ModelSlotInstanceConfiguration<TypeSafeModelSlot<M, MM>, M> createConfiguration(CreateVirtualModelInstance<?> action) {
		return new TypeSafeModelSlotInstanceConfiguration<M, MM, TypeSafeModelSlot<M, MM>>(this, action);
	}

	public IndividualPatternRole<?> makeIndividualPatternRole(IFlexoOntologyClass ontClass) {
		Class<? extends IndividualPatternRole> individualPRClass = getPatternRoleClass(IndividualPatternRole.class);
		IndividualPatternRole<?> returned = makePatternRole(individualPRClass);
		returned.setOntologicType(ontClass);
		return returned;
	}

	public AddIndividual<? extends TypeSafeModelSlot, ?> makeAddIndividualAction(IndividualPatternRole<?> patternRole,
			AbstractCreationScheme creationScheme) {
		Class<? extends AddIndividual<? extends TypeSafeModelSlot, ?>> addIndividualClass = (Class<? extends AddIndividual<? extends TypeSafeModelSlot, ?>>) getEditionActionClass(AddIndividual.class);
		AddIndividual<? extends TypeSafeModelSlot, ?> returned = makeEditionAction(addIndividualClass);

		returned.setAssignation(new DataBinding(patternRole.getPatternRoleName()));
		if (creationScheme.getParameter("uri") != null) {
			returned.setIndividualName(new DataBinding("parameters.uri"));
		}
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
	public String generateUniqueURI(TypeSafeModelSlotInstance msInstance, String proposedName) {
		if (msInstance == null || msInstance.getResourceData() == null) {
			return null;
		}
		return msInstance.getModelURI() + "#" + generateUniqueURIName(msInstance, proposedName);
	}

	/**
	 * Return a new String (the simple name) uniquely identifying a new object in related technology, according to the conventions of
	 * related technology
	 * 
	 * @param msInstance
	 * @param proposedName
	 * @return
	 */
	public String generateUniqueURIName(TypeSafeModelSlotInstance msInstance, String proposedName) {
		if (msInstance == null || msInstance.getResourceData() == null) {
			return proposedName;
		}
		return generateUniqueURIName(msInstance, proposedName, msInstance.getModelURI() + "#");
	}

	public String generateUniqueURIName(TypeSafeModelSlotInstance msInstance, String proposedName, String uriPrefix) {
		if (msInstance == null || msInstance.getResourceData() == null) {
			return proposedName;
		}
		String baseName = JavaUtils.getClassName(proposedName);
		boolean unique = false;
		int testThis = 0;
		while (!unique) {
			unique = msInstance.getResourceData().getObject(uriPrefix + baseName) == null;
			if (!unique) {
				testThis++;
				baseName = proposedName + testThis;
			}
		}
		return baseName;
	}

	public final FlexoModelResource<M, MM> createProjectSpecificEmptyModel(View view, String filename, String modelUri,
			FlexoMetaModelResource<M, MM> metaModelResource) {
		return (FlexoModelResource<M, MM>) getTechnologyAdapter().createEmptyModel(view.getProject(), filename, modelUri,
				metaModelResource, getTechnologyAdapter().getTechnologyContextManager());
	};

	public final FlexoModelResource<M, MM> createSharedEmptyModel(FlexoResourceCenter resourceCenter, String relativePath, String filename,
			String modelUri, FlexoMetaModelResource<M, MM> metaModelResource) {
		if (resourceCenter instanceof FileSystemBasedResourceCenter) {
			return (FlexoModelResource<M, MM>) getTechnologyAdapter().createEmptyModel((FileSystemBasedResourceCenter) resourceCenter,
					relativePath, filename, modelUri, metaModelResource, getTechnologyAdapter().getTechnologyContextManager());
		} else {
			logger.warning("Cannot create shared model in a non file-system-based resource center");
			return null;
		}
	};

	public FlexoMetaModelResource<M, MM> getMetaModelResource() {
		if (metaModelResource == null && StringUtils.isNotEmpty(metaModelURI) && getInformationSpace() != null) {
			metaModelResource = (FlexoMetaModelResource<M, MM>) getInformationSpace().getMetaModelWithURI(metaModelURI);
			logger.info("Looked-up " + metaModelResource);
		}
		// Temporary hack to lookup parent slot (to be refactored)
		if (metaModelResource == null && getVirtualModel() != null && getViewPoint() != null) {
			if (getViewPoint().getModelSlot(getName()) != null) {
				return ((TypeSafeModelSlot) getViewPoint().getModelSlot(getName())).getMetaModelResource();
			}
		}
		return metaModelResource;
	}

	public void setMetaModelResource(FlexoMetaModelResource<M, MM> metaModelResource) {
		this.metaModelResource = metaModelResource;
	}

	public String getMetaModelURI() {
		if (metaModelResource != null) {
			return metaModelResource.getURI();
		}
		return metaModelURI;
	}

	public void setMetaModelURI(String metaModelURI) {
		this.metaModelURI = metaModelURI;
	}

	@Override
	public String getFMLRepresentation(FMLRepresentationContext context) {
		FMLRepresentationOutput out = new FMLRepresentationOutput(context);
		out.append("ModelSlot " + getName() + " type=" + getClass().getSimpleName() + " conformTo=\"" + getMetaModelURI() + "\""
				+ " required=" + getIsRequired() + " readOnly=" + getIsReadOnly() + ";", context);
		return out.toString();
	}

	@Override
	public final String getURIForObject(ModelSlotInstance msInstance, Object o) {
		return getURIForObject((TypeSafeModelSlotInstance<M, MM, ? extends TypeSafeModelSlot<M, MM>>) msInstance, o);
	}

	@Override
	public final Object retrieveObjectWithURI(ModelSlotInstance msInstance, String objectURI) {
		return retrieveObjectWithURI((TypeSafeModelSlotInstance<M, MM, ? extends TypeSafeModelSlot<M, MM>>) msInstance, objectURI);
	}

	public abstract String getURIForObject(TypeSafeModelSlotInstance<M, MM, ? extends TypeSafeModelSlot<M, MM>> msInstance, Object o);

	public abstract Object retrieveObjectWithURI(TypeSafeModelSlotInstance<M, MM, ? extends TypeSafeModelSlot<M, MM>> msInstance,
			String objectURI);

}
