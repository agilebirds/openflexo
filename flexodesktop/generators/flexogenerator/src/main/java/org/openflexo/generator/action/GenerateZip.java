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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.Format;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.generator.AbstractProjectGenerator;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ZipUtils;

public class GenerateZip extends GenerateArtefact<GenerateZip, GenerationRepository> {

	public static final FlexoActionType<GenerateZip, GenerationRepository, CGObject> actionType = new FlexoActionType<GenerateZip, GenerationRepository, CGObject>(
			"generate_zip", GENERATE_MENU, WAR_GROUP, FlexoActionType.NORMAL_ACTION_TYPE) {

		@Override
		protected boolean isEnabledForSelection(GenerationRepository object, Vector<CGObject> globalSelection) {
			AbstractProjectGenerator<? extends GenerationRepository> pg = getProjectGenerator(object);
			return isVisibleForSelection(object, globalSelection) && pg != null && pg.hasBeenInitialized();
		}

		@Override
		protected boolean isVisibleForSelection(GenerationRepository object, Vector<CGObject> globalSelection) {
			return object instanceof DGRepository && ((DGRepository) object).getFormat() == Format.HTML;
		}

		@Override
		public GenerateZip makeNewAction(GenerationRepository focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new GenerateZip(focusedObject, globalSelection, editor);
		}

	};

	protected GenerateZip(GenerationRepository focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	static {
		FlexoModelObject.addActionForClass(GenerateZip.actionType, GenerationRepository.class);
	}

	protected class ZipFileFilter implements FileFilter {

		@Override
		public boolean accept(File pathname) {
			return !pathname.equals(((DGRepository) getFocusedObject()).getPostBuildFile()) && !pathname.getName().equals(".cvsignore")
					&& !pathname.getName().equals(".history") && !pathname.getName().equalsIgnoreCase(".dstore")
					&& !pathname.getName().equalsIgnoreCase(".DS_Store");
		}

	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		try {
			ZipUtils.makeZip(
					((DGRepository) getFocusedObject()).getPostBuildFile(),
					getFocusedObject().getDirectory(),
					makeFlexoProgress(
							FlexoLocalization.localizedForKey("creating_zip_file") + " "
									+ ((DGRepository) getFocusedObject()).getPostProductName(), 1), new ZipFileFilter());
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOFlexoException(e);
		}
	}

	@Override
	public File getArtefactFile() {
		return getGeneratedZipFile();
	}

	public File getGeneratedZipFile() {
		return ((DGRepository) getFocusedObject()).getPostBuildFile();
	}

}
