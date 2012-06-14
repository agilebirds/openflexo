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
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.generator.AbstractProjectGenerator;
import org.openflexo.generator.file.AbstractCGFile;

public class RegenerateAndOverride extends MultipleFileGCAction<RegenerateAndOverride> {

	private static final Logger logger = Logger.getLogger(RegenerateAndOverride.class.getPackage().getName());

	public static final MultipleFileGCActionType<RegenerateAndOverride> actionType = new MultipleFileGCActionType<RegenerateAndOverride>(
			"regenerate_and_override", GENERATE_MENU, GENERATION_GROUP, FlexoActionType.NORMAL_ACTION_TYPE) {
		/**
		 * Factory method
		 */
		@Override
		public RegenerateAndOverride makeNewAction(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new RegenerateAndOverride(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean accept(AbstractCGFile file) {
			return file.getResource() != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(RegenerateAndOverride.actionType, CGObject.class);
	}

	RegenerateAndOverride(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doImpl(Object context) throws SaveResourceException, FlexoException {
		logger.info("Regenerate and override");
		AbstractProjectGenerator<? extends GenerationRepository> pg = getProjectGenerator();
		pg.setAction(this);

		ForceRegenerateSourceCode force = ForceRegenerateSourceCode.actionType.makeNewEmbeddedAction(getFocusedObject(),
				getGlobalSelection(), this);
		force.doAction();
		WriteModifiedGeneratedFiles write = WriteModifiedGeneratedFiles.actionType.makeNewEmbeddedAction(getFocusedObject(),
				getGlobalSelection(), this);
		if (write.getFilesToWrite().size() > 0) {
			write.doAction();
		}
		hideFlexoProgress();
	}

	public boolean requiresThreadPool() {
		return false;
	}

}
