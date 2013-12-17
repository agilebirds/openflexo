package org.openflexo.foundation.view;

import java.util.logging.Logger;

import org.flexo.model.FlexoModelObject;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.logging.FlexoLogger;

public class ModelObjectActorReference<T extends FlexoModelObject> extends ActorReference<T> {

	private static final Logger logger = FlexoLogger.getLogger(ModelObjectActorReference.class.getPackage().toString());

	public T object;
	public FlexoModelObjectReference objectReference;

	public ModelObjectActorReference(T o, PatternRole aPatternRole, EditionPatternInstance epi) {
		super(epi.getProject());
		setEditionPatternInstance(epi);
		setPatternRole(aPatternRole);
		object = o;
		objectReference = new FlexoModelObjectReference(o);
	}

	// Constructor used during deserialization
	public ModelObjectActorReference(VirtualModelInstanceBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	@Override
	public String getClassNameKey() {
		return "model_object_actor_reference";
	}

	@Override
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T retrieveObject() {
		if (object == null) {
			object = (T) objectReference.getObject(true);
		}
		if (object == null) {
			logger.warning("Could not retrieve object " + objectReference);
		}
		return object;
	}
}