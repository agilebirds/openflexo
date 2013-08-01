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
import org.openflexo.foundation.view.diagram.model.DiagramElement;
import org.openflexo.foundation.view.diagram.model.DiagramShape;
import org.openflexo.foundation.viewpoint.EditionPattern;

/**
 * This class is an action that allows to create an edition pattern from a graphical representation which is a Diagram Shape
 * 
 * @author Vincent
 * 
 */
public class DeclareDiagramShapeInEditionPattern extends
		AbstractDeclareShapeInEditionPattern<DiagramShape, DiagramElement, DeclareDiagramShapeInEditionPattern> {
	private static final Logger logger = Logger.getLogger(DeclareDiagramShapeInEditionPattern.class.getPackage().getName());

	/**
	 * Create a new Flexo Action Type
	 */
	public static FlexoActionType<DeclareDiagramShapeInEditionPattern, DiagramShape, DiagramElement> actionType = new FlexoActionType<DeclareDiagramShapeInEditionPattern, DiagramShape, DiagramElement>(
			"declare_in_edition_pattern", FlexoActionType.editGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		@Override
		public DeclareDiagramShapeInEditionPattern makeNewAction(DiagramShape focusedObject, Vector<DiagramElement> globalSelection,
				FlexoEditor editor) {
			return new DeclareDiagramShapeInEditionPattern(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(DiagramShape shape, Vector<DiagramElement> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(DiagramShape shape, Vector<DiagramElement> globalSelection) {
			// TODO : implement the rights for modifying the viewpoint
			// ex: if(shape.getDiagramSpec.isEditable) ...

			return shape != null && shape.getDiagramSpecification() != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(DeclareDiagramShapeInEditionPattern.actionType, DiagramShape.class);
	}

	public boolean isTopLevel = true;
	public EditionPattern containerEditionPattern;

	DeclareDiagramShapeInEditionPattern(DiagramShape focusedObject, Vector<DiagramElement> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

}
