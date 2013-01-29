package org.openflexo.technologyadapter.owl;

import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.technologyadapter.DeclareEditionAction;
import org.openflexo.foundation.technologyadapter.DeclareEditionActions;
import org.openflexo.foundation.technologyadapter.DeclarePatternRole;
import org.openflexo.foundation.technologyadapter.DeclarePatternRoles;
import org.openflexo.foundation.technologyadapter.FlexoOntologyModelSlot;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
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
 * @author Luka Le Roux
 * 
 */
@DeclarePatternRoles({ @DeclarePatternRole(OWLIndividualPatternRole.class), // Instances
		@DeclarePatternRole(OWLClassPatternRole.class), // Classes
		@DeclarePatternRole(OWLDataPropertyPatternRole.class), // Data properties
		@DeclarePatternRole(OWLObjectPropertyPatternRole.class), // Object properties
		@DeclarePatternRole(OWLPropertyPatternRole.class), // Properties
		@DeclarePatternRole(DataPropertyStatementPatternRole.class), // Data property statements
		@DeclarePatternRole(ObjectPropertyStatementPatternRole.class), // Object property statements
		@DeclarePatternRole(RestrictionStatementPatternRole.class), // Restriction statements
		@DeclarePatternRole(SubClassStatementPatternRole.class) // Subclass statement */
})
@DeclareEditionActions({ @DeclareEditionAction(AddOWLIndividual.class), // Add instance
		@DeclareEditionAction(AddOWLClass.class), // Add class
		@DeclareEditionAction(AddDataPropertyStatement.class), // Add class
		@DeclareEditionAction(AddObjectPropertyStatement.class), // Add class
		@DeclareEditionAction(AddRestrictionStatement.class), // Add class
		@DeclareEditionAction(AddSubClassStatement.class), // Add class
})
public class OWLModelSlot extends FlexoOntologyModelSlot<OWLOntology, OWLOntology> {

	private static final Logger logger = Logger.getLogger(OWLModelSlot.class.getPackage().getName());

	public OWLModelSlot(ViewPoint viewPoint, OWLTechnologyAdapter adapter) {
		super(viewPoint, adapter);
	}

	public OWLModelSlot(VirtualModel<?> virtualModel, OWLTechnologyAdapter adapter) {
		super(virtualModel, adapter);
	}

	public OWLModelSlot(VirtualModelBuilder builder) {
		super(builder);
	}

	public OWLModelSlot(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public Class<OWLTechnologyAdapter> getTechnologyAdapterClass() {
		return OWLTechnologyAdapter.class;
	}

	@Override
	public BindingVariable makePatternRolePathElement(PatternRole<?> pr, Bindable container) {
		/*if (pr instanceof SubClassStatementPatternRole) {
			return new IsAStatementPatternRolePathElement((SubClassStatementPatternRole) pr, container);
		} else if (pr instanceof ObjectPropertyStatementPatternRole) {
			return new ObjectPropertyStatementPatternRolePathElement((ObjectPropertyStatementPatternRole) pr, container);
		} else if (pr instanceof DataPropertyStatementPatternRole) {
			return new DataPropertyStatementPatternRolePathElement((DataPropertyStatementPatternRole) pr, container);
		} else if (pr instanceof RestrictionStatementPatternRole) {
			return new RestrictionStatementPatternRolePathElement((RestrictionStatementPatternRole) pr, container);
		} else {
			logger.warning("Unexpected " + pr);
			return null;
		}*/
		return null;
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
	public <EA extends EditionAction<?, ?, ?>> EA makeEditionAction(Class<EA> editionActionClass) {
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
		} else {
			return super.makeEditionAction(editionActionClass);
		}
	}

}
