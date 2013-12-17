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
package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;

import org.flexo.model.FlexoModelObject;
import org.flexo.model.FlexoProcess;

public class FlexoObjectParameter extends EditionSchemeParameter {

	// TODO: unify this this FlexoObjectType in FlexoObjectInspectorEntry and FlexoModelObjectPatternRole
	public enum FlexoObjectType {
		Process, ProcessFolder, Role, Activity, Operation, Action
	}

	private FlexoObjectType flexoObjectType;

	public FlexoObjectParameter(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public Type getType() {
		if (getFlexoObjectType() == null) {
			return FlexoModelObject.class;
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
		default:
			return FlexoModelObject.class;
		}
	};

	@Override
	public WidgetType getWidget() {
		return WidgetType.FLEXO_OBJECT;
	}

	public FlexoObjectType getFlexoObjectType() {
		return flexoObjectType;
	}

	public void setFlexoObjectType(FlexoObjectType flexoObjectType) {
		this.flexoObjectType = flexoObjectType;
	}

}
