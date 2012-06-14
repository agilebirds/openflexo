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
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.generator.AbstractProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.file.AbstractCGFile;

public class CancelOverrideWithVersion extends MultipleFileGCAction<CancelOverrideWithVersion> {

	private static final Logger logger = Logger.getLogger(CancelOverrideWithVersion.class.getPackage().getName());

	public static final MultipleFileGCActionType<CancelOverrideWithVersion> actionType = new MultipleFileGCActionType<CancelOverrideWithVersion>(
			"cancel_overriding", MERGE_MENU, OVERRIDE_GROUP, FlexoActionType.NORMAL_ACTION_TYPE) {
		/**
		 * Factory method
		 */
		@Override
		public CancelOverrideWithVersion makeNewAction(CGObject repository, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new CancelOverrideWithVersion(repository, globalSelection, editor);
		}

		@Override
		protected boolean accept(AbstractCGFile file) {
			return file.isOverrideScheduled();
		}

	};

	CancelOverrideWithVersion(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doImpl(Object context) throws GenerationException, SaveResourceException, FlexoException {
		logger.info("Cancel overriding");

		AbstractProjectGenerator<? extends GenerationRepository> pg = getProjectGenerator();
		pg.setAction(this);

		GenerationRepository repository = getRepository();

		for (AbstractCGFile file : getSelectedCGFilesOnWhyCurrentActionShouldApply()) {
			file.cancelOverriding();
		}
		repository.getProject().getFlexoRMResource().saveResourceData();

		// Refreshing repository
		if (repository instanceof CGRepository) {
			((CGRepository) repository).clearAllJavaParsingData();
		}

	}

	public boolean requiresThreadPool() {
		// TODO Auto-generated method stub
		return false;
	}

}
