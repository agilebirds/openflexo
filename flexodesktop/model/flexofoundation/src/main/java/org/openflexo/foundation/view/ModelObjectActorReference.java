package org.openflexo.foundation.view;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.foundation.xml.FlexoProcessBuilder;
import org.openflexo.foundation.xml.FlexoWorkflowBuilder;
import org.openflexo.foundation.xml.VEShemaBuilder;

public class ModelObjectActorReference<T extends FlexoModelObject> extends ActorReference<T> {
	public FlexoModelObject object;
	public FlexoModelObjectReference objectReference;

	public ModelObjectActorReference(FlexoModelObject o, PatternRole aPatternRole, EditionPatternReference ref) {
		super(o.getProject());
		setPatternReference(ref);
		setPatternRole(aPatternRole);
		object = o;
		objectReference = new FlexoModelObjectReference(o);
	}

	// Constructor used during deserialization
	public ModelObjectActorReference(VEShemaBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	// Constructor used during deserialization
	public ModelObjectActorReference(FlexoProcessBuilder builder) {
		super(builder.getProject());
		initializeDeserialization(builder);
	}

	// Constructor used during deserialization
	public ModelObjectActorReference(FlexoWorkflowBuilder builder) {
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
	public FlexoModelObject retrieveObject() {
		if (object == null) {
			object = objectReference.getObject(true);
		}
		if (object == null) {
			EditionPatternReference.logger.warning("Could not retrieve object " + objectReference);
		}
		return object;
	}
}