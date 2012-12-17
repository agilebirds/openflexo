package org.openflexo.technologyadapter.owl;

import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.technologyadapter.FlexoOntologyModelSlot;
import org.openflexo.foundation.viewpoint.OntologicObjectPatternRole;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.technologyadapter.owl.viewpoint.DataPropertyStatementPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.OWLClassPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.OWLDataPropertyPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.OWLIndividualPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.OWLObjectPropertyPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.OWLPropertyPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.ObjectPropertyStatementPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.OntologicStatementPatternRolePathElement.DataPropertyStatementPatternRolePathElement;
import org.openflexo.technologyadapter.owl.viewpoint.OntologicStatementPatternRolePathElement.IsAStatementPatternRolePathElement;
import org.openflexo.technologyadapter.owl.viewpoint.OntologicStatementPatternRolePathElement.ObjectPropertyStatementPatternRolePathElement;
import org.openflexo.technologyadapter.owl.viewpoint.OntologicStatementPatternRolePathElement.RestrictionStatementPatternRolePathElement;
import org.openflexo.technologyadapter.owl.viewpoint.RestrictionStatementPatternRole;
import org.openflexo.technologyadapter.owl.viewpoint.SubClassStatementPatternRole;

/**
 * <p>
 * Implementation of the ModelSlot class for the OWL technology adapter
 * 
 * @author Luka Le Roux
 * 
 */
public class OWLModelSlot extends FlexoOntologyModelSlot<OWLOntology, OWLOntology> {

	private static final Logger logger = Logger.getLogger(OWLModelSlot.class.getPackage().getName());

	public OWLModelSlot(ViewPoint viewPoint, OWLTechnologyAdapter adapter) {
		super(viewPoint, adapter);
	}

	public OWLModelSlot(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public Class<OWLTechnologyAdapter> getTechnologyAdapterClass() {
		return OWLTechnologyAdapter.class;
	}

	@Override
	public BindingVariable<?> makePatternRolePathElement(PatternRole<?> pr, Bindable container) {
		if (pr instanceof SubClassStatementPatternRole) {
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
		}
	}

	@Override
	public <PR extends OntologicObjectPatternRole<?>> PR makePatternRole(Class<PR> patternRoleClass) {
		if (OWLClassPatternRole.class.isAssignableFrom(patternRoleClass)) {
			PR returned = (PR) new OWLClassPatternRole(null);
			returned.setPatternRoleName("class");
			return returned;
		} else if (OWLIndividualPatternRole.class.isAssignableFrom(patternRoleClass)) {
			PR returned = (PR) new OWLIndividualPatternRole(null);
			returned.setPatternRoleName("individual");
			return returned;
		} else if (OWLPropertyPatternRole.class.isAssignableFrom(patternRoleClass)) {
			PR returned = (PR) new OWLPropertyPatternRole(null);
			returned.setPatternRoleName("property");
			return returned;
		} else if (OWLDataPropertyPatternRole.class.isAssignableFrom(patternRoleClass)) {
			PR returned = (PR) new OWLDataPropertyPatternRole(null);
			returned.setPatternRoleName("dataProperty");
			return returned;
		} else if (OWLObjectPropertyPatternRole.class.isAssignableFrom(patternRoleClass)) {
			PR returned = (PR) new OWLObjectPropertyPatternRole(null);
			returned.setPatternRoleName("objectProperty");
			return returned;
		} else if (DataPropertyStatementPatternRole.class.isAssignableFrom(patternRoleClass)) {
			PR returned = (PR) new DataPropertyStatementPatternRole(null);
			returned.setPatternRoleName("fact");
			return returned;
		} else if (ObjectPropertyStatementPatternRole.class.isAssignableFrom(patternRoleClass)) {
			PR returned = (PR) new ObjectPropertyStatementPatternRole(null);
			returned.setPatternRoleName("fact");
			return returned;
		} else if (RestrictionStatementPatternRole.class.isAssignableFrom(patternRoleClass)) {
			PR returned = (PR) new RestrictionStatementPatternRole(null);
			returned.setPatternRoleName("restriction");
			return returned;
		} else if (SubClassStatementPatternRole.class.isAssignableFrom(patternRoleClass)) {
			PR returned = (PR) new SubClassStatementPatternRole(null);
			returned.setPatternRoleName("fact");
			return returned;
		}
		logger.warning("Unexpected pattern role: " + patternRoleClass.getName());
		return null;
	}
}
