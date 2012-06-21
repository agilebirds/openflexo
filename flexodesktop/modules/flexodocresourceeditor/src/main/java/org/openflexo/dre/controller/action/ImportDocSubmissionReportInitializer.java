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
package org.openflexo.dre.controller.action;

import java.io.File;
import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.openflexo.dre.DocSubmissionReportDialog;
import org.openflexo.drm.action.ImportDocSubmissionReport;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class ImportDocSubmissionReportInitializer extends ActionInitializer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	ImportDocSubmissionReportInitializer(DREControllerActionInitializer actionInitializer) {
		super(ImportDocSubmissionReport.actionType, actionInitializer);
	}

	@Override
	protected DREControllerActionInitializer getControllerActionInitializer() {
		return (DREControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<ImportDocSubmissionReport> getDefaultInitializer() {
		return new FlexoActionInitializer<ImportDocSubmissionReport>() {
			@Override
			public boolean run(EventObject e, ImportDocSubmissionReport action) {
				if (action.getDocSubmissionReportFile() == null) {
					JFileChooser chooser = new JFileChooser();
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					chooser.setDialogTitle(FlexoLocalization.localizedForKey("please_select_a_file"));
					chooser.setFileFilter(new FileFilter() {
						@Override
						public boolean accept(File f) {
							return f.getName().endsWith(".dsr");
						}

						@Override
						public String getDescription() {
							return FlexoLocalization.localizedForKey("doc_submission_report_files");
						}

					});
					if ((chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) && (chooser.getSelectedFile() != null)) {
						action.setDocSubmissionReportFile(chooser.getSelectedFile());
					} else {
						return false;
					}
				}
				if (action.getDocSubmissionReport() == null) {
					FlexoController.showError(FlexoLocalization.localizedForKey("could_not_open_file"));
					return false;
				}
				DocSubmissionReportDialog dialog = new DocSubmissionReportDialog(action.getDocSubmissionReport());
				action.setActionsToImport(dialog.getSelectedActions());
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<ImportDocSubmissionReport> getDefaultFinalizer() {
		return new FlexoActionFinalizer<ImportDocSubmissionReport>() {
			@Override
			public boolean run(EventObject e, ImportDocSubmissionReport action) {
				FlexoController.notify(FlexoLocalization.localizedForKey("import_completed"));
				return true;
			}
		};
	}

}
