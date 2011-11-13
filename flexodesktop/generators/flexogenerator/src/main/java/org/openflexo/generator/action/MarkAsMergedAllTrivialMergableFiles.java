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
import org.openflexo.foundation.rm.cg.GenerationStatus;
import org.openflexo.generator.AbstractProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.file.AbstractCGFile;

public class MarkAsMergedAllTrivialMergableFiles extends MultipleFileGCAction<MarkAsMergedAllTrivialMergableFiles> {

	private static final Logger logger = Logger.getLogger(MarkAsMergedAllTrivialMergableFiles.class.getPackage().getName());

	public static final MultipleFileGCActionType<MarkAsMergedAllTrivialMergableFiles> actionType = new MultipleFileGCActionType<MarkAsMergedAllTrivialMergableFiles>(
			"mark_as_merged_all_trivially_mergable_files", MERGE_MENU, MARK_GROUP2, FlexoActionType.NORMAL_ACTION_TYPE) {
		/**
		 * Factory method
		 */
		@Override
		public MarkAsMergedAllTrivialMergableFiles makeNewAction(CGObject repository, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new MarkAsMergedAllTrivialMergableFiles(repository, globalSelection, editor);
		}

		@Override
		protected boolean accept(AbstractCGFile file) {
			return ((file.getResource() != null) && file.getGenerationStatus() == GenerationStatus.ConflictingUnMerged && (!file
					.needsMemoryGeneration()));
		}

	};

	static {
		FlexoModelObject.addActionForClass(MarkAsMergedAllTrivialMergableFiles.actionType, CGObject.class);
	}

	MarkAsMergedAllTrivialMergableFiles(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws GenerationException, SaveResourceException, FlexoException {
		logger.info("Mark files as merged");

		AbstractProjectGenerator<? extends GenerationRepository> pg = getProjectGenerator();
		pg.setAction(this);

		GenerationRepository repository = getRepository();

		for (AbstractCGFile file : getTrivialMergableFiles()) {
			file.setMarkedAsMerged(true);
		}

		// Refreshing repository
		repository.refresh();

	}

	private Vector<AbstractCGFile> _trivialMergableFiles;

	public Vector<AbstractCGFile> getTrivialMergableFiles() {
		if (_trivialMergableFiles == null) {
			_trivialMergableFiles = new Vector<AbstractCGFile>();
			for (AbstractCGFile file : getSelectedCGFilesOnWhyCurrentActionShouldApply()) {
				if (file.isTriviallyMergable())
					_trivialMergableFiles.add(file);
			}
		}
		return _trivialMergableFiles;
	}

	public void setTrivialMergableFiles(Vector<AbstractCGFile> trivialMergableFiles) {
		_trivialMergableFiles = trivialMergableFiles;
	}

	public boolean requiresThreadPool() {
		// TODO Auto-generated method stub
		return false;
	}

}
