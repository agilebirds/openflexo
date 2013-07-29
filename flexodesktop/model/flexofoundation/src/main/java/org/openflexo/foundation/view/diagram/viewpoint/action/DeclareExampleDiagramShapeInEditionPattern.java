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
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramObject;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramShape;
import org.openflexo.foundation.viewpoint.EditionPattern;

public class DeclareExampleDiagramShapeInEditionPattern extends
		AbstractDeclareShapeInEditionPattern<ExampleDiagramShape, ExampleDiagramObject, DeclareExampleDiagramShapeInEditionPattern> {

	private static final Logger logger = Logger.getLogger(DeclareExampleDiagramShapeInEditionPattern.class.getPackage().getName());

	public static FlexoActionType<DeclareExampleDiagramShapeInEditionPattern, ExampleDiagramShape, ExampleDiagramObject> actionType = new FlexoActionType<DeclareExampleDiagramShapeInEditionPattern, ExampleDiagramShape, ExampleDiagramObject>(
			"declare_in_edition_pattern", FlexoActionType.editGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeclareExampleDiagramShapeInEditionPattern makeNewAction(ExampleDiagramShape focusedObject,
				Vector<ExampleDiagramObject> globalSelection, FlexoEditor editor) {
			return new DeclareExampleDiagramShapeInEditionPattern(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(ExampleDiagramShape shape, Vector<ExampleDiagramObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(ExampleDiagramShape shape, Vector<ExampleDiagramObject> globalSelection) {
			return shape != null && shape.getVirtualModel() != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(DeclareExampleDiagramShapeInEditionPattern.actionType, ExampleDiagramShape.class);
	}

	public boolean isTopLevel = true;
	public EditionPattern containerEditionPattern;

	DeclareExampleDiagramShapeInEditionPattern(ExampleDiagramShape focusedObject, Vector<ExampleDiagramObject> globalSelection,
			FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

}
