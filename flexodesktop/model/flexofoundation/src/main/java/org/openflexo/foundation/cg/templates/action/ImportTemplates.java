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
package org.openflexo.foundation.cg.templates.action;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.templates.CGTemplateObject;
import org.openflexo.foundation.cg.templates.CGTemplateRepository;
import org.openflexo.foundation.cg.templates.CustomCGTemplateRepository;
import org.openflexo.toolbox.FileUtils;

public class ImportTemplates extends FlexoAction<ImportTemplates, CGTemplateRepository, CGTemplateObject> {

	private static final Logger logger = Logger.getLogger(ImportTemplates.class.getPackage().getName());

	private CustomCGTemplateRepository _repository;
	private File externalTemplateDirectory;

	public static FlexoActionType<ImportTemplates, CGTemplateRepository, CGTemplateObject> actionType = new FlexoActionType<ImportTemplates, CGTemplateRepository, CGTemplateObject>(
			"import_templates", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public ImportTemplates makeNewAction(CGTemplateRepository focusedObject, Vector<CGTemplateObject> globalSelection,
				FlexoEditor editor) {
			return new ImportTemplates(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(CGTemplateRepository object, Vector<CGTemplateObject> globalSelection) {
			return object != null && !(object instanceof CustomCGTemplateRepository);
		}

		@Override
		public boolean isEnabledForSelection(CGTemplateRepository object, Vector<CGTemplateObject> globalSelection) {
			return object != null && !(object instanceof CustomCGTemplateRepository);
		}

	};

	static {
		FlexoModelObject.addActionForClass(ImportTemplates.actionType, CGTemplateRepository.class);
	}

	ImportTemplates(CGTemplateRepository focusedObject, Vector<CGTemplateObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws IOFlexoException {
		logger.info("ImportTemplates from " + getFocusedObject().getName() + " to " + getRepository().getName());
		File source = getExternalTemplateDirectory();
		CustomCGTemplateRepository dest = getRepository();
		try {
			FileUtils.copyContentDirToDir(source, dest.getDirectory());
		} catch (IOException e) {
			throw new IOFlexoException(e);
		}
		dest.refresh();
	}

	public CustomCGTemplateRepository getRepository() {
		return _repository;
	}

	public void setRepository(CustomCGTemplateRepository repository) {
		_repository = repository;
	}

	public File getExternalTemplateDirectory() {
		return externalTemplateDirectory;
	}

	public void setExternalTemplateDirectory(File externalTemplateDirectory) {
		this.externalTemplateDirectory = externalTemplateDirectory;
	}

}
