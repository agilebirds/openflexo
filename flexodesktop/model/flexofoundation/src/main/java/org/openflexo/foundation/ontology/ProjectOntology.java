package org.openflexo.foundation.ontology;

import org.openflexo.foundation.ontology.owl.OWLClass;
import org.openflexo.foundation.ontology.owl.OWLOntology;
import org.openflexo.foundation.ontology.owl.OWLOntology.OntologyNotFoundException;
import org.openflexo.foundation.ontology.owl.OWLProperty;
import org.openflexo.foundation.ontology.owl.OntologyRestrictionClass;
import org.openflexo.foundation.ontology.owl.OntologyRestrictionClass.RestrictionType;
import org.openflexo.foundation.rm.StorageResourceData;

import com.hp.hpl.jena.ontology.OntModel;

public interface ProjectOntology extends FlexoOntology, StorageResourceData {

	/*
	 * All those methods should be dealt with a way or another. They shouldn't be any OWL specific code here.
	 */

	public boolean importOntology(OWLOntology basicOrganizationTreeOntology) throws OntologyNotFoundException;

	public OntologyRestrictionClass createRestriction(OWLClass subject, OWLProperty property, RestrictionType restrictionType,
			int cardinality, OWLClass object);

	public void describe();

	public OntModel getOntModel();

	public void setChanged();

}
