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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.toc.TOCRepository;
import org.openflexo.toolbox.FileResource;
import org.openflexo.xmlcode.XMLCoder;

public class ExportTOCAsTemplate extends FlexoAction<ExportTOCAsTemplate, AgileBirdsObject, AgileBirdsObject> {

	public static final FlexoActionType<ExportTOCAsTemplate, AgileBirdsObject, AgileBirdsObject> actionType = new FlexoActionType<ExportTOCAsTemplate, AgileBirdsObject, AgileBirdsObject>(
			"export_toc_as_template") {

		@Override
		public boolean isEnabledForSelection(AgileBirdsObject object, Vector<AgileBirdsObject> globalSelection) {
			return object instanceof TOCRepository;
		}

		@Override
		public boolean isVisibleForSelection(AgileBirdsObject object, Vector<AgileBirdsObject> globalSelection) {
			return object instanceof TOCRepository;
		}

		@Override
		public ExportTOCAsTemplate makeNewAction(AgileBirdsObject focusedObject, Vector<AgileBirdsObject> globalSelection,
				FlexoEditor editor) {
			return new ExportTOCAsTemplate(focusedObject, globalSelection, editor);
		}

	};

	static {
		AgileBirdsObject.addActionForClass(ExportTOCAsTemplate.actionType, TOCRepository.class);
	}

	private File destinationFile;

	public TOCRepository getSourceRepository() {
		return (TOCRepository) getFocusedObject();
	}

	/**
	 * @param actionType
	 * @param focusedObject
	 * @param globalSelection
	 */
	protected ExportTOCAsTemplate(AgileBirdsObject focusedObject, Vector<AgileBirdsObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	/**
	 * Overrides doAction
	 * 
	 * @see org.openflexo.foundation.action.FlexoAction#doAction(java.lang.Object)
	 */
	@Override
	protected void doAction(Object context) throws FlexoException {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(getDestinationFile());
			XMLCoder.encodeObjectWithMappingFile(getSourceRepository(), new FileResource("Models/TOCModel/toc_template_0.1.xml"), out);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FlexoException(e.getMessage(), e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public File getDestinationFile() {
		return destinationFile;
	}

	public void setDestinationFile(File destinationFile) {
		this.destinationFile = destinationFile;
	}

}
