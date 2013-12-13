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
package org.openflexo.technologyadapter.diagram.fml.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.technologyadapter.diagram.model.DiagramConnector;
import org.openflexo.technologyadapter.diagram.model.DiagramElement;

public class DeclareConnectorInEditionPattern extends
		AbstractDeclareConnectorInEditionPattern<DiagramConnector, DiagramElement, DeclareConnectorInEditionPattern> {

	private static final Logger logger = Logger.getLogger(DeclareConnectorInEditionPattern.class.getPackage().getName());

	public static FlexoActionType<DeclareConnectorInEditionPattern, DiagramConnector, DiagramElement> actionType = new FlexoActionType<DeclareConnectorInEditionPattern, DiagramConnector, DiagramElement>(
			"declare_in_edition_pattern", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeclareConnectorInEditionPattern makeNewAction(DiagramConnector focusedObject, Vector<DiagramElement> globalSelection,
				FlexoEditor editor) {
			return new DeclareConnectorInEditionPattern(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(DiagramConnector connector, Vector<DiagramElement> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(DiagramConnector connector, Vector<DiagramElement> globalSelection) {
			return connector != null && connector.getDiagramSpecification().getEditionPatterns().size() > 0;
		}

	};

	static {
		FlexoModelObject.addActionForClass(DeclareConnectorInEditionPattern.actionType, DiagramConnector.class);
	}

	DeclareConnectorInEditionPattern(DiagramConnector focusedObject, Vector<DiagramElement> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

}
