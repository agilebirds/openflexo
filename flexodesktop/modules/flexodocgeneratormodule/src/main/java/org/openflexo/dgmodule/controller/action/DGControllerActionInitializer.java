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
package org.openflexo.dgmodule.controller.action;

import java.util.logging.Logger;

import org.openflexo.action.CompareTemplatesInNewWindowInitializer;
import org.openflexo.dgmodule.controller.DGController;
import org.openflexo.dgmodule.controller.DGSelectionManager;
import org.openflexo.doceditor.controller.action.DEControllerActionInitializer;
import org.openflexo.doceditor.controller.action.ImportDocumentationTemplateInitializer;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionizer;
import org.openflexo.foundation.action.OpenFileInExplorer;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.CGTemplateFile;
import org.openflexo.foundation.cg.templates.CGTemplateObject;
import org.openflexo.foundation.cg.templates.action.EditCustomTemplateFile;
import org.openflexo.foundation.cg.templates.action.OpenTemplateFileInNewWindow;
import org.openflexo.foundation.cg.templates.action.RedefineCustomTemplateFile;
import org.openflexo.generator.action.OverrideWithVersion;
import org.openflexo.generator.action.ShowFileVersion;
import org.openflexo.view.controller.InteractiveFlexoEditor;

public class DGControllerActionInitializer extends DEControllerActionInitializer {

	protected static final Logger logger = Logger.getLogger(DGControllerActionInitializer.class.getPackage().getName());

	static {
		FlexoModelObject.addActionForClass(OpenFileInExplorer.actionType, CGFile.class);
	}

	public DGControllerActionInitializer(InteractiveFlexoEditor editor, DGController controller) {
		super(editor, controller);
	}

	protected DGController getDGController() {
		return (DGController) getController();
	}

	@Override
	protected DGSelectionManager getDGSelectionManager() {
		return getDGController().getSelectionManager();
	}

	@Override
	public void initializeActions() {
		super.initializeActions();

		new AddGeneratedCodeRepositoryInitializer(this);
		new RemoveGeneratedCodeRepositoryInitializer(this);
		new ImportDocumentationTemplateInitializer(this);
		new DGSetPropertyInitializer(this);
		new OpenFileInExplorerInitializer(this);
		new CompareTemplatesInNewWindowInitializer(this);
		new GenerateProcessScreenshotInitializer(this);
		new GenerateActivityScreenshotInitializer(this);
		new GenerateComponentScreenshotInitializer(this);
		new GenerateOperationScreenshotInitializer(this);
		new SynchronizeRepositoryCodeGenerationInitializer(this);
		new GenerateSourceCodeInitializer(this);
		new GenerateAndWriteCodeInitializer(this);
		new ForceRegenerateSourceCodeInitializer(this);
		new RegenerateAndOverrideInitializer(this);
		new IncludeFromGenerationInitializer(this);
		new ExcludeFromGenerationInitializer(this);
		new ExportTOCAsTemplateInitializer(this);
		new WriteModifiedGeneratedFilesInitializer(this);
		new DismissUnchangedGeneratedFilesInitializer(this);

		// Refreshing
		new RefreshDGStructureInitializer(this);

		// Edition
		new EditGeneratedFileInitializer(this);
		new SaveGeneratedFileInitializer(this);
		new RevertToSavedGeneratedFileInitializer(this);

		// Merge
		new MarkAsMergedInitializer(this);
		new MarkAsUnmergedInitializer(this);
		new MarkAsMergedAllTrivialMergableFilesInitializer(this);
		new CancelOverrideWithVersionInitializer(this);
		new OverrideWithVersionInitializer(OverrideWithVersion.overrideWithPureGeneration, this);
		new OverrideWithVersionInitializer(OverrideWithVersion.overrideWithGeneratedMerge, this);
		new OverrideWithVersionInitializer(OverrideWithVersion.overrideWithLastGenerated, this);
		new OverrideWithVersionInitializer(OverrideWithVersion.overrideWithLastAccepted, this);

		// Accept disk version
		new AcceptDiskUpdateInitializer(this);

		// Versionning
		new RegisterNewCGReleaseInitializer(this);
		new RevertRepositoryToVersionInitializer(this);
		new RevertToHistoryVersionInitializer(this);

		new CleanIntermediateFilesInitializer(this);
		new RefreshHistoryInitializer(this);

		new ShowReleaseHistoryInitializer(this);
		new ShowFileHistoryInitializer(this);
		new ShowDifferencesInitializer(this);

		// PDF management
		new GeneratePDFInitializer(this);

		// DOCX management
		new GenerateDocxInitializer(this);
		new ReinjectDocxInitializer(this);

		// ZIP management
		new GenerateZipInitializer(this);

		// Repository management
		new ConnectCGRepositoryInitializer(this);
		new DisconnectCGRepositoryInitializer(this);

		// Show/view
		new OpenDiffEditorInitializer(this);
		new ShowFileVersionInitializer(ShowFileVersion.showPureGeneration, this);
		new ShowFileVersionInitializer(ShowFileVersion.showGeneratedMerge, this);
		new ShowFileVersionInitializer(ShowFileVersion.showContentOnDisk, this);
		new ShowFileVersionInitializer(ShowFileVersion.showResultFileMerge, this);
		new ShowFileVersionInitializer(ShowFileVersion.showLastGenerated, this);
		new ShowFileVersionInitializer(ShowFileVersion.showLastAccepted, this);
		new ShowFileVersionInitializer(ShowFileVersion.showHistoryVersion, this);

		// Templates management
		new AddCustomTemplateRepositoryInitializer(this);
		new RemoveCustomTemplateRepositoryInitializer(this);
		new RedefineCustomTemplateFileInitializer(this);
		new EditCustomTemplateFileInitializer(this);
		new SaveCustomTemplateFileInitializer(this);
		new RefreshTemplatesInitializer(this);
		new OpenTemplateFileInNewWindowInitializer(this);
		new CancelEditionOfCustomTemplateFileInitializer(this);
		new RemoveTemplateFileInitializer(this);
		// Initialize actions available using inspector (template tab)

		CGFile.showTemplateActionizer = new FlexoActionizer<OpenTemplateFileInNewWindow, CGTemplate, CGTemplateObject>(
				OpenTemplateFileInNewWindow.actionType, getDGController().getEditor());

		CGFile.editCustomTemplateActionizer = new FlexoActionizer<EditCustomTemplateFile, CGTemplateFile, CGTemplateObject>(
				EditCustomTemplateFile.actionType, getDGController().getEditor());

		CGFile.redefineTemplateActionizer = new FlexoActionizer<RedefineCustomTemplateFile, CGTemplate, CGTemplate>(
				RedefineCustomTemplateFile.actionType, getDGController().getEditor());

	}
}