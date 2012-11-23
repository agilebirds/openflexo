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
package org.openflexo.sgmodule.controller.action;

import java.util.logging.Logger;

import org.openflexo.action.CompareFilesInitializer;
import org.openflexo.action.CompareTemplatesInNewWindowInitializer;
import org.openflexo.foundation.action.FlexoActionizer;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.CGTemplateFile;
import org.openflexo.foundation.cg.templates.CGTemplateObject;
import org.openflexo.foundation.cg.templates.action.EditCustomTemplateFile;
import org.openflexo.foundation.cg.templates.action.OpenTemplateFileInNewWindow;
import org.openflexo.foundation.cg.templates.action.RedefineCustomTemplateFile;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleDefinition;
import org.openflexo.generator.action.OverrideWithVersion;
import org.openflexo.generator.action.ShowFileVersion;
import org.openflexo.sgmodule.SGModule;
import org.openflexo.sgmodule.TechnologyModuleGUIFactory;
import org.openflexo.sgmodule.controller.SGController;
import org.openflexo.view.controller.ControllerActionInitializer;

/**
 * 
 * Action initializing for this module
 * 
 * @author yourname
 */
public class SGControllerActionInitializer extends ControllerActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	private SGController _sgController;

	public SGControllerActionInitializer(SGController controller) {
		super(controller);
		_sgController = controller;
	}

	protected SGController getSGController() {
		return _sgController;
	}

	@Override
	public void initializeActions() {
		super.initializeActions();
		new SGSetPropertyInitializer(this);

		// Implementation model management
		new CreateImplementationModelInitializer(this);
		new CreateTechnologyModuleImplementationInitializer(this);

		// Validate project
		new ValidateProjectInitializer(this);

		// Code generation
		new SynchronizeRepositoryCodeGenerationInitializer(this);
		new GenerateSourceCodeInitializer(this);
		new GenerateAndWriteCodeInitializer(this);
		new ForceRegenerateSourceCodeInitializer(this);
		new RegenerateAndOverrideInitializer(this);
		new IncludeFromGenerationInitializer(this);
		new ExcludeFromGenerationInitializer(this);

		new WriteModifiedGeneratedFilesInitializer(this);
		new DismissUnchangedGeneratedFilesInitializer(this);

		// Refreshing
		new RefreshCGStructureInitializer(this);

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
		new AcceptDiskUpdateAndReinjectInModelnitializer(this);

		// Model reinjection
		new ReinjectInModelInitializer(this);
		new ImportInModelInitializer(this);
		new UpdateModelInitializer(this);
		new OpenDMEntityInitializer(this);

		// Versionning
		new RegisterNewCGReleaseInitializer(this);
		new RevertRepositoryToVersionInitializer(this);
		new RevertToHistoryVersionInitializer(this);

		new CleanIntermediateFilesInitializer(this);
		new RefreshHistoryInitializer(this);

		new ShowReleaseHistoryInitializer(this);
		new ShowFileHistoryInitializer(this);
		new ShowDifferencesInitializer(this);

		// Repository management
		new CreateSourceRepositoryInitializer(this);
		new RemoveGeneratedCodeRepositoryInitializer(this);
		new ConnectCGRepositoryInitializer(this);
		new DisconnectCGRepositoryInitializer(this);

		// Show/view
		new ShowFileVersionInitializer(ShowFileVersion.showPureGeneration, this);
		new ShowFileVersionInitializer(ShowFileVersion.showGeneratedMerge, this);
		new ShowFileVersionInitializer(ShowFileVersion.showContentOnDisk, this);
		new ShowFileVersionInitializer(ShowFileVersion.showResultFileMerge, this);
		new ShowFileVersionInitializer(ShowFileVersion.showLastGenerated, this);
		new ShowFileVersionInitializer(ShowFileVersion.showLastAccepted, this);
		new ShowFileVersionInitializer(ShowFileVersion.showHistoryVersion, this);
		new OpenDiffEditorInitializer(this);
		new OpenFileInExplorerInitializer(this);
		new CompareTemplatesInNewWindowInitializer(this);
		new CompareFilesInitializer(this);

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
		new RedefineAllTemplatesInitializer(this);
		new ImportTemplatesInitializer(this);
		// Initialize actions available using inspector (template tab)

		// To be sure all Technology Module GUI Factories are recorded
		TechnologyModuleDefinition.getAllTechnologyModuleDefinitions();
		for (TechnologyModuleGUIFactory technologyModuleGUIFactory : SGModule.getAllTechnologyModuleGUIFactories()) {
			technologyModuleGUIFactory.initializeActions(this);
		}

		CGFile.showTemplateActionizer = new FlexoActionizer<OpenTemplateFileInNewWindow, CGTemplate, CGTemplateObject>(
				OpenTemplateFileInNewWindow.actionType, this);

		CGFile.editCustomTemplateActionizer = new FlexoActionizer<EditCustomTemplateFile, CGTemplateFile, CGTemplateObject>(
				EditCustomTemplateFile.actionType, this);

		CGFile.redefineTemplateActionizer = new FlexoActionizer<RedefineCustomTemplateFile, CGTemplate, CGTemplate>(
				RedefineCustomTemplateFile.actionType, this);

	}

}
