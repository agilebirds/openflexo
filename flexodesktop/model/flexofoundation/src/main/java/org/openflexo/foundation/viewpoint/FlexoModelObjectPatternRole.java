package org.openflexo.foundation.viewpoint;

import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.ProcessFolder;
import org.openflexo.foundation.wkf.Role;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.EventNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.localization.FlexoLocalization;

public class FlexoModelObjectPatternRole extends PatternRole {

	// TODO: unify this this FlexoObjectType in FlexoObjectParameter and FlexoObjectInspectorEntry
	public static enum FlexoModelObjectType {
		Process, ProcessFolder, Role, Activity, Operation, Action, Event, Screen
	}

	private FlexoModelObjectType flexoModelObjectType;

	public FlexoModelObjectPatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	@Override
	public PatternRoleType getType() {
		return PatternRoleType.FlexoModelObject;
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
		case Screen:
			return FlexoLocalization.localizedForKey("screen");
		default:
			return null;
		}
	}

	@Override
	public Class<?> getAccessedClass() {
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
		case Screen:
			return OperationComponentDefinition.class;
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

}
