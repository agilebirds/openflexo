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
package org.openflexo.drm.action;

import java.io.File;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.drm.DocItemFolder;
import org.openflexo.drm.DocResourceCenter;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.drm.DocSubmissionReport;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;

public class ImportDocSubmissionReport extends FlexoAction {

	private static final Logger logger = Logger.getLogger(ImportDocSubmissionReport.class.getPackage().getName());

	public static FlexoActionType actionType = new FlexoActionType("import_doc_submission_report", FlexoActionType.defaultGroup,
			FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public FlexoAction makeNewAction(FlexoObject focusedObject, Vector globalSelection, FlexoEditor editor) {
			return new ImportDocSubmissionReport(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoObject object, Vector globalSelection) {
			return isEnabledForSelection(object, globalSelection);
		}

		@Override
		public boolean isEnabledForSelection(FlexoObject object, Vector globalSelection) {
			return object != null && object instanceof DocItemFolder && ((DocItemFolder) object).isRootFolder();
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(actionType, DocItemFolder.class);
	}

	ImportDocSubmissionReport(FlexoObject focusedObject, Vector globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	private File _docSubmissionReportFile;
	private Vector _actionsToImport; // null if all actions must be imported

	@Override
	protected void doAction(Object context) {
		logger.info("ImportDocSubmissionReport");
		if (getDocSubmissionReport() != null) {
			DocResourceManager.instance().importDocSubmissionReport(getDocSubmissionReport(), getActionsToImport());
		}
	}

	public File getDocSubmissionReportFile() {
		return _docSubmissionReportFile;
	}

	public void setDocSubmissionReportFile(File docSubmissionReportFile) {
		_docSubmissionReportFile = docSubmissionReportFile;
	}

	private DocSubmissionReport _docSubmissionReport;

	public DocSubmissionReport getDocSubmissionReport() {
		if (_docSubmissionReport == null) {
			if (getDocSubmissionReportFile() != null) {
				DocResourceCenter drc = DocResourceManager.instance().getDocResourceCenter();
				_docSubmissionReport = DocSubmissionReport.load(drc, getDocSubmissionReportFile());
			}
		}
		return _docSubmissionReport;
	}

	public Vector getActionsToImport() {
		return _actionsToImport;
	}

	public void setActionsToImport(Vector actionsToImport) {
		_actionsToImport = actionsToImport;
	}

}
