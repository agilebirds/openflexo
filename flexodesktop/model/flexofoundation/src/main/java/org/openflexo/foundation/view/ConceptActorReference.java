package org.openflexo.foundation.view;

import java.util.logging.Logger;

import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.viewpoint.OntologicObjectPatternRole;
import org.openflexo.foundation.xml.VirtualModelInstanceBuilder;
import org.openflexo.logging.FlexoLogger;

public class ConceptActorReference<T extends IFlexoOntologyObject> extends ActorReference<T> {

	private static final Logger logger = FlexoLogger.getLogger(ConceptActorReference.class.getPackage().toString());

	private T object;
	private String objectURI;

	public ConceptActorReference(T o, OntologicObjectPatternRole<T> aPatternRole, EditionPatternInstance epi) {
		super(epi.getProject());
		setEditionPatternInstance(epi);
		setPatternRole(aPatternRole);
		object = o;

		ModelSlotInstance msInstance = getModelSlotInstance();
		/** Model Slot is responsible for URI mapping */
	    objectURI = msInstance.getModelSlot().getURIForObject(msInstance, o);
	}

	// Constructor used during deserialization
	public ConceptActorReference(VirtualModelInstanceBuilder builder) {
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
			ModelSlotInstance msInstance = getModelSlotInstance();
			if (msInstance.getResourceData() != null) {
				// object = (T) getProject().getObject(objectURI); 
				/** Model Slot is responsible for URI mapping */
				 object = (T) msInstance.getModelSlot().retrieveObjectWithURI(msInstance, objectURI);
			} else {
				logger.warning("Could not access to model in model slot " + getModelSlotInstance());
				logger.warning("Searched " + getModelSlotInstance().getModelURI());
			}
		}
		if (object == null) {
			logger.warning("Could not retrieve object " + objectURI);
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

	public IFlexoOntologyObject getObject() {
		return object;
	}
}