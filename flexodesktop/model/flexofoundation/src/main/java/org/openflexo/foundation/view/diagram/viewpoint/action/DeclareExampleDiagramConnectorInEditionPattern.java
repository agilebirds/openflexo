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
package org.openflexo.foundation.view.diagram.viewpoint.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramConnector;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramObject;

public class DeclareExampleDiagramConnectorInEditionPattern
		extends
		AbstractDeclareConnectorInEditionPattern<ExampleDiagramConnector, ExampleDiagramObject, DeclareExampleDiagramConnectorInEditionPattern> {

	private static final Logger logger = Logger.getLogger(DeclareExampleDiagramConnectorInEditionPattern.class.getPackage().getName());

	public static FlexoActionType<DeclareExampleDiagramConnectorInEditionPattern, ExampleDiagramConnector, ExampleDiagramObject> actionType = new FlexoActionType<DeclareExampleDiagramConnectorInEditionPattern, ExampleDiagramConnector, ExampleDiagramObject>(
			"declare_in_edition_pattern", FlexoActionType.editGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeclareExampleDiagramConnectorInEditionPattern makeNewAction(ExampleDiagramConnector focusedObject,
				Vector<ExampleDiagramObject> globalSelection, FlexoEditor editor) {
			return new DeclareExampleDiagramConnectorInEditionPattern(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(ExampleDiagramConnector shape, Vector<ExampleDiagramObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(ExampleDiagramConnector shape, Vector<ExampleDiagramObject> globalSelection) {
			return shape != null && shape.getVirtualModel() != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(DeclareExampleDiagramConnectorInEditionPattern.actionType, ExampleDiagramConnector.class);
	}

	DeclareExampleDiagramConnectorInEditionPattern(ExampleDiagramConnector focusedObject, Vector<ExampleDiagramObject> globalSelection,
			FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	public ExampleDiagramConnector getFocusedObject() {
		return super.getFocusedObject();
	}

}
