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
package org.openflexo.generator.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.generator.file.AbstractCGFile;

/**
 * @author gpolet
 * 
 */
public class IncludeInGeneration extends MultipleFileGCAction<IncludeInGeneration> {

	public static final MultipleFileGCActionType<IncludeInGeneration> actionType = new MultipleFileGCActionType<IncludeInGeneration>(
			"include_in_generation", GENERATE_MENU, GENERATION_GROUP, FlexoActionType.NORMAL_ACTION_TYPE) {

		@Override
		protected boolean accept(AbstractCGFile aFile) {
			return aFile.getMarkedAsDoNotGenerate();
		}

		@Override
		public IncludeInGeneration makeNewAction(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new IncludeInGeneration(focusedObject, globalSelection, editor);
		}

		/**
		 * Overrides getSelectedCGFilesOnWhyCurrentActionShouldApply
		 * 
		 * @see org.openflexo.generator.action.MultipleFileGCAction.MultipleFileGCActionType#getSelectedCGFilesOnWhyCurrentActionShouldApply(org.openflexo.foundation.cg.CGObject,
		 *      java.util.Vector)
		 */
		@Override
		protected Vector<AbstractCGFile> getSelectedCGFilesOnWhyCurrentActionShouldApply(CGObject focusedObject,
				Vector<CGObject> globalSelection) {
			Vector<AbstractCGFile> selectedFiles = getSelectedCGFiles(focusedObject, globalSelection);
			Vector<AbstractCGFile> returned = new Vector<AbstractCGFile>();
			for (AbstractCGFile file : selectedFiles) {
				if (accept(file)) {
					returned.add(file);
				}
			}
			return returned;
		}

	};

	static {
		TestModelObject.addActionForClass(IncludeInGeneration.actionType, CGObject.class);
	}

	/**
	 * @param actionType
	 * @param focusedObject
	 * @param globalSelection
	 * @param editor
	 */
	protected IncludeInGeneration(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	/**
	 * Overrides doAction
	 * 
	 * @see org.openflexo.foundation.action.FlexoAction#doAction(java.lang.Object)
	 */
	@Override
	protected void doImpl(Object context) throws FlexoException {
		for (AbstractCGFile file : getSelectedCGFilesOnWhyCurrentActionShouldApply()) {
			file.setMarkedAsDoNotGenerate(false);
		}
		getRepository().refresh();
	}

	public boolean requiresThreadPool() {
		// TODO Auto-generated method stub
		return false;
	}

}
