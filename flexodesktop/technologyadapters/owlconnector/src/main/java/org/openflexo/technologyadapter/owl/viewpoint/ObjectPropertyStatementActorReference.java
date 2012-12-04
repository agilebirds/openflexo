package org.openflexo.technologyadapter.owl.viewpoint;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.ontology.EditionPatternReference;
import org.openflexo.foundation.ontology.EditionPatternReference.ActorReference;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.foundation.xml.FlexoWorkflowBuilder;
import org.openflexo.foundation.xml.VEShemaBuilder;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.technologyadapter.owl.model.OWLObject;
import org.openflexo.technologyadapter.owl.model.ObjectPropertyStatement;

public class ObjectPropertyStatementActorReference extends ActorReference {

	static final Logger logger = FlexoLogger.getLogger(ObjectPropertyStatementActorReference.class.getPackage().toString());

	public ObjectPropertyStatement statement;
	public String subjectURI;
	public String objectPropertyURI;
	public String objectURI;

	public ObjectPropertyStatementActorReference(ObjectPropertyStatement o, String aPatternRole, EditionPatternReference ref) {
		super(ref.getProject());
		setPatternReference(ref);
		patternRole = aPatternRole;
		statement = o;
		subjectURI = o.getSubject().getURI();
		objectURI = o.getStatementObject().getURI();
		objectPropertyURI = o.getProperty().getURI();
	}

	// Constructor used during deserialization
	public ObjectPropertyStatementActorReference(VEShemaBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	// Constructor used during deserialization
	public ObjectPropertyStatementActorReference(FlexoProcessBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	// Constructor used during deserialization
	public ObjectPropertyStatementActorReference(FlexoWorkflowBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	@Override
	public String getClassNameKey() {
		return "object_property_statement_actor_reference";
	}

	@Override
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectPropertyStatement retrieveObject() {
		if (statement == null) {
			OntologyObject subject = getProject().retrieveOntologyObject(subjectURI);
			if (subject == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not find subject with URI " + subjectURI);
				}
				return null;
			}
			if (subject instanceof OWLObject == false) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Statements aren't supported by non-owl ontologies, subject's URI: " + subjectURI);
				}
				return null;
			}

			OntologyObject object = getProject().retrieveOntologyObject(objectURI);
			if (object == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not find object with URI " + objectURI);
				}
				return null;
			}

			OntologyObjectProperty property = (OntologyObjectProperty) getProject().retrieveOntologyObject(objectPropertyURI);
			if (property != null) {
				// FIXED HUGE ISSUE HERE, with incorrect deserialization of statements
				statement = ((OWLObject) subject).getObjectPropertyStatement(property, object);
				// logger.info("Found statement: "+statement);
			}
		}
		if (statement == null) {
			logger.warning("Could not retrieve object " + objectURI);
		}
		return statement;
	}
}