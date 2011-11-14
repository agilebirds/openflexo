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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.file.AbstractCGFile;

public class GenerateAndWrite extends MultipleFileGCAction<GenerateAndWrite> {

	private static final Logger logger = Logger.getLogger(GenerateAndWrite.class.getPackage().getName());

	public static final MultipleFileGCActionType<GenerateAndWrite> actionType = new MultipleFileGCActionType<GenerateAndWrite>(
			"generate_and_write", GENERATE_MENU, GENERATION_GROUP, FlexoActionType.NORMAL_ACTION_TYPE) {
		/**
		 * Factory method
		 */
		@Override
		public GenerateAndWrite makeNewAction(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new GenerateAndWrite(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean accept(AbstractCGFile file) {
			return true;
		}

		/**
		 * Overrides isEnabled
		 * 
		 * @see org.openflexo.foundation.action.FlexoActionType#isEnabled(org.openflexo.foundation.FlexoModelObject, java.util.Vector,
		 *      org.openflexo.foundation.FlexoEditor)
		 */
		@Override
		public boolean isEnabled(CGObject object, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return SynchronizeRepositoryCodeGeneration.actionType
					.isEnabled(getRepository(object, globalSelection), globalSelection, editor);
		}

	};

	static {
		FlexoModelObject.addActionForClass(GenerateAndWrite.actionType, CGObject.class);
	}

	GenerateAndWrite(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws GenerationException, SaveResourceException {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Generating and writing " + getRepository().getName());
		}
		SynchronizeRepositoryCodeGeneration synch = SynchronizeRepositoryCodeGeneration.actionType.makeNewEmbeddedAction(getRepository(),
				getGlobalSelection(), this);
		synch.doAction();
		if (!getWriteUnchangedFiles()) {
			DismissUnchangedGeneratedFiles dismiss = DismissUnchangedGeneratedFiles.actionType.makeNewEmbeddedAction(getFocusedObject(),
					getGlobalSelection(), this);
			dismiss.doAction();
		}
		WriteModifiedGeneratedFiles write = WriteModifiedGeneratedFiles.actionType.makeNewEmbeddedAction(getFocusedObject(),
				getGlobalSelection(), this);
		if (write.getFilesToWrite().size() > 0) {
			write.doAction();
		}
	}

	private boolean writeUnchangedFiles = true;

	public boolean getWriteUnchangedFiles() {
		return writeUnchangedFiles;
	}

	public void setWriteUnchangedFiles(boolean writeUnchangedFiles) {
		this.writeUnchangedFiles = writeUnchangedFiles;
	}

}
