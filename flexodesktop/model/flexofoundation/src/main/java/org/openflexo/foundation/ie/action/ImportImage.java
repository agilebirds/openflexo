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
package org.openflexo.foundation.ie.action;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoWebServerFileResource;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.toolbox.FileUtils;

public class ImportImage extends FlexoAction<ImportImage, FlexoModelObject, FlexoModelObject> {
	private FlexoWebServerFileResource _res = null;
	private File _fileToImport;

	public static FlexoActionType<ImportImage, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<ImportImage, FlexoModelObject, FlexoModelObject>(
			"import_image", FlexoActionType.helpGroup) {

		/**
		 * Factory method
		 */
		@Override
		public ImportImage makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
			return new ImportImage(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return true;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, FlexoProject.class);
	}

	public static boolean isValidImageFile(File file) {
		String ext = file.getName().toLowerCase();
		return ext.endsWith(".gif") || ext.endsWith(".png") || ext.endsWith(".jpg");
	}

	private boolean overwrite = false;
	private String imageName;

	private String targetImageName;

	ImportImage(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateResourceException {
		if (getFileToImport() != null) {
			try {
				/*
				 * To investigate: it causes issues with the progress window when creating a new project (commenting this for now)
				 * makeFlexoProgress(FlexoLocalization.localizedForKey("import_image"), 2);
				 * setProgress(FlexoLocalization.localizedForKey("copying_file_to_project"));
				 */
				FlexoResource res = getEditor().getProject().resourceForKey(ResourceType.WEBSERVER, getTargetImageName());
				if (res != null) {
					if (!overwrite) {
						throw new DuplicateResourceException(res);
					} else {
						res.delete();
					}
				}
				File newFile = new File(getEditor().getProject().getImportedImagesDir(), getTargetImageName());
				res = getEditor().getProject().resourceForFile(newFile);
				if (res != null) {
					throw new DuplicateResourceException(res);
				}
				FileUtils.copyFileToFile(getFileToImport(), newFile);
				_res = FlexoWebServerFileResource.createNewWebServerFileResource(newFile, getEditor().getProject());
				// setProgress(FlexoLocalization.localizedForKey("reloading_palette"));
				getEditor().getProject().getCustomImagePalette().refresh();
				getEditor().getProject().resetAvailableImages();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String getTargetImageName() {
		if (targetImageName == null) {
			targetImageName = FileUtils.lowerCaseExtension(FileUtils.removeNonASCIIAndPonctuationAndBadFileNameChars(
					getImageName() != null ? getImageName() : getFileToImport().getName()).replace('_', '-'));
		}
		return targetImageName;
	}

	public void setFileToImport(File w) {
		_fileToImport = w;
	}

	public File getFileToImport() {
		return _fileToImport;
	}

	public FlexoWebServerFileResource getCreatedResource() {
		return _res;
	}

	public void setCreatedResource(FlexoWebServerFileResource res) {
		_res = res;
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
		targetImageName = null;
	}
}
