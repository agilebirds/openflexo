package org.openflexo.foundation.view;

import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.viewpoint.OntologicObjectPatternRole;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.foundation.xml.FlexoWorkflowBuilder;
import org.openflexo.foundation.xml.VEShemaBuilder;

public class ConceptActorReference<T extends OntologyObject> extends ActorReference<T> {
	private T object;
	private String objectURI;

	public ConceptActorReference(T o, OntologicObjectPatternRole<T> aPatternRole, EditionPatternReference ref) {
		super(ref.getProject());
		setPatternReference(ref);
		setPatternRole(aPatternRole);
		object = o;
		objectURI = o.getURI();
	}

	// Constructor used during deserialization
	public ConceptActorReference(VEShemaBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	// Constructor used during deserialization
	public ConceptActorReference(FlexoProcessBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	// Constructor used during deserialization
	public ConceptActorReference(FlexoWorkflowBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	@Override
	public String getClassNameKey() {
		return "concept_actor_reference";
	}

	@Override
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T retrieveObject() {
		if (object == null) {
			object = (T) getProject().getObject(objectURI);
		}
		if (object == null) {
			EditionPatternReference.logger.warning("Could not retrieve object " + objectURI);
		}
		return object;
	}

	public String _getObjectURI() {
		if (object != null) {
			return object.getURI();
		}
		return objectURI;
	}

	public void _setObjectURI(String objectURI) {
		this.objectURI = objectURI;
	}

	public OntologyObject getObject() {
		return object;
	}
}