package org.openflexo.technologyadapter.owl;

import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.technologyadapter.FlexoOntologyModelSlot;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.technologyadapter.owl.viewpoint.DataPropertyStatementPatternRole;
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
	public String getFullyQualifiedName() {
		return "OWLModelSlot";
	}

	@Override
	public String getClassNameKey() {
		return "owl_model_slot";
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

}
