package org.openflexo.technologyadapter.owl;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.technologyadapter.DeclareEditionAction;
import org.openflexo.foundation.technologyadapter.DeclareEditionActions;
import org.openflexo.foundation.technologyadapter.DeclarePatternRole;
import org.openflexo.foundation.technologyadapter.DeclarePatternRoles;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.TypeSafeModelSlot;
import org.openflexo.foundation.view.TypeSafeModelSlotInstance;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.FetchRequest;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.technologyadapter.owl.model.OWLObject;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.technologyadapter.owl.rm.OWLOntologyResource;
import org.openflexo.technologyadapter.owl.viewpoint.DataPropertyStatementPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.OWLClassPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.OWLDataPropertyPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.OWLIndividualPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.OWLObjectPropertyPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.OWLPropertyPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.ObjectPropertyStatementPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.RestrictionStatementPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.SubClassStatementPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.editionaction.AddDataPropertyStatement;
import org.openflexo.technologyadapter.owl.viewpoint.editionaction.AddOWLClass;
import org.openflexo.technologyadapter.owl.viewpoint.editionaction.AddOWLIndividual;
import org.openflexo.technologyadapter.owl.viewpoint.editionaction.AddObjectPropertyStatement;
import org.openflexo.technologyadapter.owl.viewpoint.editionaction.AddRestrictionStatement;
import org.openflexo.technologyadapter.owl.viewpoint.editionaction.AddSubClassStatement;

/**
 * <p>
 * Implementation of the ModelSlot class for the OWL technology adapter
 * 
 * @author sylvain, luka
 * 
 */
@DeclarePatternRoles({ // All pattern roles available through this model slot
@DeclarePatternRole(FML = "OWLIndividual", patternRoleClass = OWLIndividualPatternRole.class),
		@DeclarePatternRole(FML = "OWLClass", patternRoleClass = OWLClassPatternRole.class),
		@DeclarePatternRole(FML = "OWLDataProperty", patternRoleClass = OWLDataPropertyPatternRole.class),
		@DeclarePatternRole(FML = "OWLObjectProperty", patternRoleClass = OWLObjectPropertyPatternRole.class),
		@DeclarePatternRole(FML = "OWLProperty", patternRoleClass = OWLPropertyPatternRole.class),
		@DeclarePatternRole(FML = "DataPropertyStatement", patternRoleClass = DataPropertyStatementPatternRole.class),
		@DeclarePatternRole(FML = "ObjectPropertyStatement", patternRoleClass = ObjectPropertyStatementPatternRole.class),
		// @DeclarePatternRole(FML = "RestrictionStatement", patternRoleClass = RestrictionStatementPatternRole.class),
		@DeclarePatternRole(FML = "SubClassStatement", patternRoleClass = SubClassStatementPatternRole.class) })
@DeclareEditionActions({ // All edition actions available through this model slot
@DeclareEditionAction(FML = "AddOWLIndividual", editionActionClass = AddOWLIndividual.class), // Add instance
		@DeclareEditionAction(FML = "AddOWLClass", editionActionClass = AddOWLClass.class), // Add class
		@DeclareEditionAction(FML = "AddDataPropertyStatement", editionActionClass = AddDataPropertyStatement.class), // Add class
		@DeclareEditionAction(FML = "AddObjectPropertyStatement", editionActionClass = AddObjectPropertyStatement.class), // Add class
		@DeclareEditionAction(FML = "AddRestrictionStatement", editionActionClass = AddRestrictionStatement.class), // Add class
		@DeclareEditionAction(FML = "AddSubClassStatement", editionActionClass = AddSubClassStatement.class), // Add class
})
public class OWLModelSlot extends TypeSafeModelSlot<OWLOntology, OWLOntology> {

	private static final Logger logger = Logger.getLogger(OWLModelSlot.class.getPackage().getName());

	/*public OWLModelSlot(ViewPoint viewPoint, OWLTechnologyAdapter adapter) {
		super(viewPoint, adapter);
	}*/

	public OWLModelSlot(VirtualModel<?> virtualModel, OWLTechnologyAdapter adapter) {
		super(virtualModel, adapter);
	}

	public OWLModelSlot(VirtualModelBuilder builder) {
		super(builder);
	}

	/*public OWLModelSlot(ViewPointBuilder builder) {
		super(builder);
	}*/

	@Override
	public Class<OWLTechnologyAdapter> getTechnologyAdapterClass() {
		return OWLTechnologyAdapter.class;
	}

	/**
	 * Instanciate a new model slot instance configuration for this model slot
	 */
	@Override
	public OWLModelSlotInstanceConfiguration createConfiguration(CreateVirtualModelInstance<?> action) {
		return new OWLModelSlotInstanceConfiguration(this, action);
	}

	/**
	 * Creates and return a new {@link PatternRole} of supplied class.<br>
	 * This responsability is delegated to the OWL-specific {@link OWLModelSlot} which manages with introspection its own
	 * {@link PatternRole} types related to OWL technology
	 * 
	 * @param patternRoleClass
	 * @return
	 */
	@Override
	public <PR extends PatternRole<?>> PR makePatternRole(Class<PR> patternRoleClass) {
		if (OWLClassPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new OWLClassPatternRole(null);
		} else if (OWLIndividualPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new OWLIndividualPatternRole(null);
		} else if (OWLPropertyPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new OWLPropertyPatternRole(null);
		} else if (OWLDataPropertyPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new OWLDataPropertyPatternRole(null);
		} else if (OWLObjectPropertyPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new OWLObjectPropertyPatternRole(null);
		} else if (DataPropertyStatementPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new DataPropertyStatementPatternRole(null);
		} else if (ObjectPropertyStatementPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new ObjectPropertyStatementPatternRole(null);
		} else if (RestrictionStatementPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new RestrictionStatementPatternRole(null);
		} else if (SubClassStatementPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return (PR) new SubClassStatementPatternRole(null);
		}
		logger.warning("Unexpected pattern role: " + patternRoleClass.getName());
		return null;
	}

	@Override
	public <PR extends PatternRole<?>> String defaultPatternRoleName(Class<PR> patternRoleClass) {
		if (OWLClassPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "class";
		} else if (OWLIndividualPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "individual";
		} else if (OWLPropertyPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "property";
		} else if (OWLDataPropertyPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "dataProperty";
		} else if (OWLObjectPropertyPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "objectProperty";
		} else if (DataPropertyStatementPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "fact";
		} else if (ObjectPropertyStatementPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "fact";
		} else if (RestrictionStatementPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "restriction";
		} else if (SubClassStatementPatternRole.class.isAssignableFrom(patternRoleClass)) {
			return "fact";
		}
		return super.defaultPatternRoleName(patternRoleClass);
	}

	/**
	 * Creates and return a new {@link EditionAction} of supplied class.<br>
	 * This responsability is delegated to the OWL-specific {@link OWLModelSlot} which manages with introspection its own
	 * {@link EditionAction} types related to OWL technology
	 * 
	 * @param editionActionClass
	 * @return
	 */
	@Override
	public <EA extends EditionAction<?, ?>> EA makeEditionAction(Class<EA> editionActionClass) {
		if (AddOWLIndividual.class.isAssignableFrom(editionActionClass)) {
			return (EA) new AddOWLIndividual(null);
		} else if (AddOWLClass.class.isAssignableFrom(editionActionClass)) {
			return (EA) new AddOWLClass(null);
		} else if (AddDataPropertyStatement.class.isAssignableFrom(editionActionClass)) {
			return (EA) new AddDataPropertyStatement(null);
		} else if (AddObjectPropertyStatement.class.isAssignableFrom(editionActionClass)) {
			return (EA) new AddObjectPropertyStatement(null);
		} else if (AddRestrictionStatement.class.isAssignableFrom(editionActionClass)) {
			return (EA) new AddRestrictionStatement(null);
		} else if (AddSubClassStatement.class.isAssignableFrom(editionActionClass)) {
			return (EA) new AddSubClassStatement(null);
		}
		return null;
	}

	@Override
	public <FR extends FetchRequest<?, ?>> FR makeFetchRequest(Class<FR> fetchRequestClass) {
		return null;
	}

	@Override
	public String getURIForObject(
			TypeSafeModelSlotInstance<OWLOntology, OWLOntology, ? extends TypeSafeModelSlot<OWLOntology, OWLOntology>> msInstance, Object o) {
		return ((OWLObject) o).getURI();
	}

	@Override
	public Object retrieveObjectWithURI(
			TypeSafeModelSlotInstance<OWLOntology, OWLOntology, ? extends TypeSafeModelSlot<OWLOntology, OWLOntology>> msInstance,
			String objectURI) {
		return msInstance.getResourceData().getObject(objectURI);
	}

	@Override
	public Type getType() {
		return OWLOntology.class;
	}

	@Override
	public OWLTechnologyAdapter getTechnologyAdapter() {
		return (OWLTechnologyAdapter) super.getTechnologyAdapter();
	}

	@Override
	public OWLOntologyResource createProjectSpecificEmptyModel(View view, String filename, String modelUri,
			FlexoMetaModelResource<OWLOntology, OWLOntology> metaModelResource) {
		return getTechnologyAdapter().createNewOntology(view.getProject(), filename, modelUri, metaModelResource);
	}

	@Override
	public OWLOntologyResource createSharedEmptyModel(FlexoResourceCenter<?> resourceCenter, String relativePath, String filename,
			String modelUri, FlexoMetaModelResource<OWLOntology, OWLOntology> metaModelResource) {
		return getTechnologyAdapter().createNewOntology((FileSystemBasedResourceCenter) resourceCenter, relativePath, filename, modelUri,
				(OWLOntologyResource) metaModelResource);
	}

}
