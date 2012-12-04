package org.openflexo.technologyadapter.owl.viewpoint;

import java.util.logging.Logger;

import org.openflexo.foundation.ontology.EditionPatternReference;
import org.openflexo.foundation.ontology.EditionPatternReference.ActorReference;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.foundation.xml.FlexoWorkflowBuilder;
import org.openflexo.foundation.xml.VEShemaBuilder;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.technologyadapter.owl.model.OntologyRestrictionClass;

public class RestrictionStatementActorReference extends ActorReference {

	static final Logger logger = FlexoLogger.getLogger(RestrictionStatementActorReference.class.getPackage().toString());

	public OntologyRestrictionClass restriction;
	// public String subjectURI;
	public String propertyURI;
	public String objectURI;

	public RestrictionStatementActorReference(OntologyRestrictionClass o, String aPatternRole, EditionPatternReference ref) {
		super(ref.getProject());
		setPatternReference(ref);
		patternRole = aPatternRole;
		restriction = o;
		// subjectURI = o.getSubject().getURI();
		objectURI = o.getObject().getURI();
		propertyURI = o.getProperty().getURI();
	}

	// Constructor used during deserialization
	public RestrictionStatementActorReference(VEShemaBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	// Constructor used during deserialization
	public RestrictionStatementActorReference(FlexoProcessBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	// Constructor used during deserialization
	public RestrictionStatementActorReference(FlexoWorkflowBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	@Override
	public String getClassNameKey() {
		return "restriction_statement_actor_reference";
	}

	@Override
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OntologyRestrictionClass retrieveObject() {
		if (restriction == null) {
			OntologyObject object = getProject().retrieveOntologyObject(objectURI);
			OntologyProperty property = (OntologyProperty) getProject().retrieveOntologyObject(propertyURI);
			if (object instanceof OntologyClass) {
				// restriction = subject.getObjectRestrictionStatement(property, (OntologyClass) object);
				// logger.info("Found restriction: " + restriction);
				// TODO implement this
				logger.warning("Not implemented !!!");
			}
		}
		if (restriction == null) {
			logger.warning("Could not retrieve object " + objectURI);
		}
		return restriction;
	}
}