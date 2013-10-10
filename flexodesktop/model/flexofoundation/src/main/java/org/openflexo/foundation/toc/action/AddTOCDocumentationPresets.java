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
package org.openflexo.foundation.toc.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.GeneratedDoc;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.toc.TOCData;
import org.openflexo.foundation.toc.TOCDocumentationPresets;
import org.openflexo.foundation.toc.TOCObject;

public class AddTOCDocumentationPresets extends FlexoAction<AddTOCDocumentationPresets, FlexoModelObject, TOCObject> {

	public static final FlexoActionType<AddTOCDocumentationPresets, FlexoModelObject, TOCObject> actionType = new FlexoActionType<AddTOCDocumentationPresets, FlexoModelObject, TOCObject>(
			"add_documentation_presets", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		@Override
		public boolean isEnabledForSelection(FlexoModelObject object, Vector<TOCObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isVisibleForSelection(FlexoModelObject object, Vector<TOCObject> globalSelection) {
			return true;
		}

		@Override
		public AddTOCDocumentationPresets makeNewAction(FlexoModelObject focusedObject, Vector<TOCObject> globalSelection,
				FlexoEditor editor) {
			return new AddTOCDocumentationPresets(focusedObject, globalSelection, editor);
		}

	};
	private TOCDocumentationPresets presets;

	static {
		FlexoModelObject.addActionForClass(actionType, TOCDocumentationPresets.class);
		FlexoModelObject.addActionForClass(actionType, TOCData.class);
		FlexoModelObject.addActionForClass(actionType, GeneratedDoc.class);
	}

	protected AddTOCDocumentationPresets(FlexoModelObject focusedObject, Vector<TOCObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		if (getData() != null) {
			presets = new TOCDocumentationPresets(getData());
			getData().addToPresets(presets);
		}
	}

	private TOCData getData() {
		return getProject().getTOCData();
	}

	private FlexoProject getProject() {
		return getFocusedObject().getProject();
	}

	public TOCDocumentationPresets getPresets() {
		return presets;
	}

}
