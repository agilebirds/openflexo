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
package org.openflexo.foundation.viewpoint.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.ExampleDrawingObject;
import org.openflexo.foundation.viewpoint.ExampleDrawingShape;
import org.openflexo.foundation.viewpoint.ViewPointPalette;
import org.openflexo.foundation.viewpoint.ViewPointPaletteElement;
import org.openflexo.toolbox.StringUtils;

public class PushToPalette extends FlexoAction<PushToPalette, ExampleDrawingShape, ExampleDrawingObject> {

	private static final Logger logger = Logger.getLogger(PushToPalette.class.getPackage().getName());

	public static FlexoActionType<PushToPalette, ExampleDrawingShape, ExampleDrawingObject> actionType = new FlexoActionType<PushToPalette, ExampleDrawingShape, ExampleDrawingObject>(
			"push_to_palette", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public PushToPalette makeNewAction(ExampleDrawingShape focusedObject, Vector<ExampleDrawingObject> globalSelection,
				FlexoEditor editor) {
			return new PushToPalette(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(ExampleDrawingShape shape, Vector<ExampleDrawingObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(ExampleDrawingShape shape, Vector<ExampleDrawingObject> globalSelection) {
			return (shape != null && shape.getCalc().getPalettes().size() > 0);
		}

	};

	static {
		FlexoModelObject.addActionForClass(PushToPalette.actionType, ExampleDrawingShape.class);
	}

	public Object graphicalRepresentation;
	public ViewPointPalette palette;
	public EditionPattern editionPattern;
	public String newElementName;

	private ViewPointPaletteElement _newPaletteElement;

	PushToPalette(ExampleDrawingShape focusedObject, Vector<ExampleDrawingObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Push to palette");
		if (getFocusedObject() != null && palette != null) {

			_newPaletteElement = palette.addPaletteElement(newElementName, getFocusedObject().getGraphicalRepresentation());
			_newPaletteElement.setEditionPattern(editionPattern);
		} else {
			logger.warning("Focused role is null !");
		}
	}

	public ViewPointPaletteElement getNewPaletteElement() {
		return _newPaletteElement;
	}

	public boolean isValid() {
		return StringUtils.isNotEmpty(newElementName) && palette != null && editionPattern != null;
	}
}
