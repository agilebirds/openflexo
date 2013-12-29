package org.openflexo.foundation.view;

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoProjectObject;
import org.openflexo.foundation.utils.FlexoObjectReference;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.logging.FlexoLogger;

public class ModelObjectActorReference<T extends FlexoProjectObject> extends ActorReference<T> {

	private static final Logger logger = FlexoLogger.getLogger(ModelObjectActorReference.class.getPackage().toString());

	public T object;
	public FlexoObjectReference objectReference;

	public ModelObjectActorReference(T o, PatternRole aPatternRole, EditionPatternInstance epi) {
		super(epi.getProject());
		setEditionPatternInstance(epi);
		setPatternRole(aPatternRole);
		object = o;
		objectReference = new FlexoObjectReference(o, o.getProject());
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