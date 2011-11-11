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
package org.openflexo.dg.action;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.zip.Deflater;
import java.util.zip.ZipOutputStream;

import org.openflexo.dg.docx.ProjectDocDocxGenerator;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.Format;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.generator.action.GCAction;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.IProgress;
import org.openflexo.toolbox.ZipUtils;

public class GenerateDocx extends GCAction<GenerateDocx, GenerationRepository> {

	public static final FlexoActionType<GenerateDocx, GenerationRepository, CGObject> actionType = new FlexoActionType<GenerateDocx, GenerationRepository, CGObject>(
			"generate_docx", GENERATE_MENU, WAR_GROUP, FlexoActionType.NORMAL_ACTION_TYPE) {

		@Override
		protected boolean isEnabledForSelection(GenerationRepository repository, Vector<CGObject> globalSelection) {
			if (repository.getFormat() != Format.DOCX || !(repository instanceof DGRepository))
				return false;
			ProjectDocDocxGenerator pg = (ProjectDocDocxGenerator) getProjectGenerator(repository);
			return pg != null && ((DGRepository) repository).getPostBuildDirectory() != null && pg.hasBeenInitialized();
		}

		@Override
		protected boolean isVisibleForSelection(GenerationRepository object, Vector<CGObject> globalSelection) {
			return object instanceof DGRepository && ((DGRepository) object).getFormat() == Format.DOCX;
		}

		@Override
		public GenerateDocx makeNewAction(GenerationRepository focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new GenerateDocx(focusedObject, globalSelection, editor);
		}

	};

	protected GenerateDocx(GenerationRepository focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	static {
		FlexoModelObject.addActionForClass(GenerateDocx.actionType, GenerationRepository.class);
	}

	protected class ZipFileFilter implements FileFilter {

		@Override
		public boolean accept(File pathname) {
			return !pathname.equals(((DGRepository) getFocusedObject()).getPostBuildFile()) && !pathname.getName().equals(".cvsignore")
					&& !pathname.getName().equals("DOCX") && !pathname.getName().equalsIgnoreCase(".dstore")
					&& !pathname.getName().equalsIgnoreCase(".DS_Store") && !pathname.getName().equals(".history")
					&& !pathname.getName().equals(".svn") && !pathname.getName().equals("CVS");
		}

	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		try {
			File zipOutput = ((DGRepository) getFocusedObject()).getPostBuildFile();

			IProgress progress = makeFlexoProgress(FlexoLocalization.localizedForKey("creating_docx_file") + " "
					+ ((DGRepository) getFocusedObject()).getPostProductName(), 1);
			if (progress != null)
				progress.resetSecondaryProgress(FileUtils.countFilesInDirectory(getFocusedObject().getDirectory(), true) + 1);
			FileUtils.createNewFile(zipOutput);
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipOutput));
			zos.setLevel(Deflater.DEFAULT_COMPRESSION);
			try {
				ZipUtils.zipDir(getFocusedObject().getDirectory().getAbsolutePath().length() + 1, getFocusedObject().getDirectory(), zos,
						progress, new ZipFileFilter());
			} finally {
				zos.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOFlexoException(e);
		}
	}

	public File getGeneratedDocxFile() {
		return ((DGRepository) getFocusedObject()).getPostBuildFile();
	}

}
