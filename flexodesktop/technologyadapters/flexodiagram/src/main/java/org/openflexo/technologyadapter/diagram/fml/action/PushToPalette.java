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
import org.openflexo.technologyadapter.diagram.fml.ExampleDiagramObject;
import org.openflexo.technologyadapter.diagram.fml.ExampleDiagramShape;

public class PushToPalette extends AbstractPushEditonPatternToPalette<PushToPalette, ExampleDiagramShape, ExampleDiagramObject> {

	private static final Logger logger = Logger.getLogger(PushToPalette.class.getPackage().getName());

	public static FlexoActionType<PushToPalette, ExampleDiagramShape, ExampleDiagramObject> actionType = new FlexoActionType<PushToPalette, ExampleDiagramShape, ExampleDiagramObject>(
			"push_to_palette", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public PushToPalette makeNewAction(ExampleDiagramShape focusedObject, Vector<ExampleDiagramObject> globalSelection,
				FlexoEditor editor) {
			return new PushToPalette(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(ExampleDiagramShape shape, Vector<ExampleDiagramObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(ExampleDiagramShape shape, Vector<ExampleDiagramObject> globalSelection) {
			return shape != null && shape.getVirtualModel().getPalettes().size() > 0;
		}

	};

	static {
		FlexoModelObject.addActionForClass(PushToPalette.actionType, ExampleDiagramShape.class);
	}

	PushToPalette(ExampleDiagramShape focusedObject, Vector<ExampleDiagramObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}
}
