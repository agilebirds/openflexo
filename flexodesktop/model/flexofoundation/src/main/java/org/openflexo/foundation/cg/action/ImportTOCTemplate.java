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
package org.openflexo.foundation.cg.action;

import java.io.File;
import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.GeneratedDoc;
import org.openflexo.foundation.toc.TOCData;
import org.openflexo.foundation.toc.TOCRepository;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;

public class ImportTOCTemplate extends FlexoAction<ImportTOCTemplate, FlexoModelObject, FlexoModelObject> {

	public static final FlexoActionType<ImportTOCTemplate, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<ImportTOCTemplate, FlexoModelObject, FlexoModelObject>(
			"import_toc_as_template") {

		@Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return true;
		}

		@Override
		public ImportTOCTemplate makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
			return new ImportTOCTemplate(focusedObject, globalSelection, editor);
		}

	};

	static {
		FlexoModelObject.addActionForClass(ImportTOCTemplate.actionType, GeneratedDoc.class);
		FlexoModelObject.addActionForClass(ImportTOCTemplate.actionType, TOCData.class);
	}

	private File sourceFile;

	public TOCRepository getSourceRepository() {
		return (TOCRepository) getFocusedObject();
	}

	/**
	 * @param actionType
	 * @param focusedObject
	 * @param globalSelection
	 */
	protected ImportTOCTemplate(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	/**
	 * Overrides doAction
	 * 
	 * @see org.openflexo.foundation.action.FlexoAction#doAction(java.lang.Object)
	 */
	@Override
	protected void doAction(Object context) throws FlexoException {
		try {
			File tocTemplateDirectory = new FileResource("Config/TOCTemplates");
			FileUtils.copyFileToDir(getSourceFile(), tocTemplateDirectory);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FlexoException(e.getMessage(), e);
		}
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}

}
