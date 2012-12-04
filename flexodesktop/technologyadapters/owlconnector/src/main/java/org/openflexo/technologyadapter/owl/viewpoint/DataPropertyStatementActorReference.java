package org.openflexo.technologyadapter.owl.viewpoint;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.ontology.EditionPatternReference;
import org.openflexo.foundation.ontology.EditionPatternReference.ActorReference;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.foundation.xml.FlexoWorkflowBuilder;
import org.openflexo.foundation.xml.VEShemaBuilder;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.technologyadapter.owl.model.DataPropertyStatement;
import org.openflexo.technologyadapter.owl.model.OWLObject;

public class DataPropertyStatementActorReference extends ActorReference {

	static final Logger logger = FlexoLogger.getLogger(DataPropertyStatementActorReference.class.getPackage().toString());

	public DataPropertyStatement statement;
	public String subjectURI;
	public String dataPropertyURI;
	public String value;

	public DataPropertyStatementActorReference(DataPropertyStatement o, String aPatternRole, EditionPatternReference ref) {
		super(ref.getProject());
		setPatternReference(ref);
		patternRole = aPatternRole;
		statement = o;
		subjectURI = o.getSubject().getURI();
		value = o.getLiteral().toString();
		dataPropertyURI = o.getProperty().getURI();
	}

	// Constructor used during deserialization
	public DataPropertyStatementActorReference(VEShemaBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	// Constructor used during deserialization
	public DataPropertyStatementActorReference(FlexoProcessBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	// Constructor used during deserialization
	public DataPropertyStatementActorReference(FlexoWorkflowBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	@Override
	public String getClassNameKey() {
		return "data_property_statement_actor_reference";
	}

	@Override
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataPropertyStatement retrieveObject() {
		if (statement == null) {
			OntologyObject subject = getProject().retrieveOntologyObject(subjectURI);
			if (subject instanceof OWLObject == false) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Statements aren't supported by non-owl ontologies, subject's URI: " + subjectURI);
				}
				return null;
			}
			OntologyDataProperty property = (OntologyDataProperty) getProject().retrieveOntologyObject(dataPropertyURI);
			if (property != null) {
				statement = ((OWLObject<?>) subject).getDataPropertyStatement(property);
				// logger.info("Found statement: "+statement);
			}
		}
		if (statement == null) {
			logger.warning("Could not retrieve object " + value);
		}
		return statement;
	}
}