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
package org.openflexo.foundation.view.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.view.diagram.model.DiagramElement;
import org.openflexo.foundation.view.diagram.model.DiagramShape;
import org.openflexo.foundation.view.diagram.viewpoint.action.AbstractPushEditonPatternToPalette;

public class DiagramShapePushToPalette extends AbstractPushEditonPatternToPalette<DiagramShapePushToPalette, DiagramShape, DiagramElement> {

	private static final Logger logger = Logger.getLogger(DiagramShapePushToPalette.class.getPackage().getName());

	public static FlexoActionType<DiagramShapePushToPalette, DiagramShape, DiagramElement> actionType = new FlexoActionType<DiagramShapePushToPalette, DiagramShape, DiagramElement>(
			"push_to_palette", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DiagramShapePushToPalette makeNewAction(DiagramShape focusedObject, Vector<DiagramElement> globalSelection,
				FlexoEditor editor) {
			return new DiagramShapePushToPalette(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(DiagramShape shape, Vector<DiagramElement> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(DiagramShape shape, Vector<DiagramElement> globalSelection) {
			return shape != null && shape.getDiagramSpecification().getPalettes().size() > 0;
		}

	};

	static {
		FlexoModelObject.addActionForClass(DiagramShapePushToPalette.actionType, DiagramShape.class);
	}

	DiagramShapePushToPalette(DiagramShape focusedObject, Vector<DiagramElement> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

}
