package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.view.EditionPatternReference;
import org.openflexo.foundation.view.ModelObjectActorReference;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.ProcessFolder;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.localization.FlexoLocalization;

public class FlexoModelObjectPatternRole extends PatternRole<FlexoModelObject> {

	// TODO: unify this this FlexoObjectType in FlexoObjectParameter and FlexoObjectInspectorEntry
	public static enum FlexoModelObjectType {
		Process, ProcessFolder, Role, Activity, Operation, Action, Event
	}

	private FlexoModelObjectType flexoModelObjectType;

	public FlexoModelObjectPatternRole(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	public FlexoModelObjectType getFlexoModelObjectType() {
		return flexoModelObjectType;
	}

	public void setFlexoModelObjectType(FlexoModelObjectType flexoModelObjectType) {
		this.flexoModelObjectType = flexoModelObjectType;
	}

	@Override
	public String getPreciseType() {
		if (flexoModelObjectType == null) {
			return null;
		}
		switch (flexoModelObjectType) {
		case Process:
			return FlexoLocalization.localizedForKey("process");
		case ProcessFolder:
			return FlexoLocalization.localizedForKey("process_folder");
		case Role:
			return FlexoLocalization.localizedForKey("role");
		case Activity:
			return FlexoLocalization.localizedForKey("activity");
		case Operation:
			return FlexoLocalization.localizedForKey("operation");
		case Action:
			return FlexoLocalization.localizedForKey("action");
		case Event:
			return FlexoLocalization.localizedForKey("event");
		default:
			return null;
		}
	}

	@Override
	public Type getType() {
		if (flexoModelObjectType == null) {
			return null;
		}
		switch (flexoModelObjectType) {
		case Process:
			return FlexoProcess.class;
		case ProcessFolder:
			return ProcessFolder.class;
		case Role:
			return Role.class;
		case Activity:
			return AbstractActivityNode.class;
		case Operation:
			return OperationNode.class;
		case Action:
			return ActionNode.class;
		case Event:
			return EventNode.class;
		default:
			return null;
		}
	}

	@Override
	public boolean getIsPrimaryRole() {
		return false;
	}

	@Override
	public void setIsPrimaryRole(boolean isPrimary) {
		// Not relevant
	}

	@Override
	public boolean defaultBehaviourIsToBeDeleted() {
		return false;
	}

	@Override
	public ModelObjectActorReference<FlexoModelObject> makeActorReference(FlexoModelObject object, EditionPatternReference epRef) {
		return new ModelObjectActorReference<FlexoModelObject>(object, this, epRef);
	}

}
