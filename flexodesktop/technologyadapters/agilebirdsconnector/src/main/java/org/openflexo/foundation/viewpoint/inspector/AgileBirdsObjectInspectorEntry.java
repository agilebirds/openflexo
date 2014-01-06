/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.foundation.viewpoint.inspector;

import org.flexo.model.TestModelObject;
import org.flexo.model.FlexoProcess;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.wkf.ProcessFolder;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.OperationNode;

/**
 * Represents an inspector entry for a flexo object
 * 
 * @author sylvain
 * 
 */
public class AgileBirdsObjectInspectorEntry extends InspectorEntry {

	// TODO: unify this this FlexoObjectType in FlexoObjectParameter and FlexoModelObjectPatternRole
	public enum FlexoObjectType {
		Process, ProcessFolder, Role, Activity, Operation, Action, Screen
	}

	public AgileBirdsObjectInspectorEntry() {
		super();
	}

	private FlexoObjectType flexoObjectType;

	@Override
	public Class getDefaultDataClass() {
		if (getFlexoObjectType() == null) {
			return TestModelObject.class;
		}
		switch (getFlexoObjectType()) {
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
		case Screen:
			return OperationComponentDefinition.class;
		default:
			return TestModelObject.class;
		}
	};

	public FlexoObjectType getFlexoObjectType() {
		return flexoObjectType;
	}

	public void setFlexoObjectType(FlexoObjectType flexoObjectType) {
		this.flexoObjectType = flexoObjectType;
	}

	@Override
	public String getWidgetName() {
		if (getFlexoObjectType() == null) {
			return "FlexoObjectSelector";
		}
		switch (getFlexoObjectType()) {
		case Process:
			return "ProcessSelector";
		case ProcessFolder:
			return "ProcessFolderSelector";
		case Role:
			return "RoleSelector";
		case Activity:
			return "ActivitySelector";
		case Operation:
			return "OperationSelector";
		case Action:
			return "ActionSelector";
		case Screen:
			return "ComponentSelector";
		default:
			return "FlexoObjectSelector";
		}
	}
}
