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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.action.CompareTemplatesInNewWindowInitializer;
import org.openflexo.foundation.action.FlexoActionizer;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.CGTemplateFile;
import org.openflexo.foundation.cg.templates.CGTemplateObject;
import org.openflexo.foundation.cg.templates.action.EditCustomTemplateFile;
import org.openflexo.foundation.cg.templates.action.OpenTemplateFileInNewWindow;
import org.openflexo.foundation.cg.templates.action.RedefineCustomTemplateFile;
import org.openflexo.foundation.sg.SourceRepository;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleDefinition;
import org.openflexo.generator.AbstractProjectGenerator;
import org.openflexo.generator.action.GCAction;
import org.openflexo.sgmodule.SGModule;
import org.openflexo.sgmodule.TechnologyModuleGUIFactory;
import org.openflexo.sgmodule.controller.SGController;
import org.openflexo.sgmodule.controller.SGSelectionManager;
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
    
    public SGControllerActionInitializer(SGController controller)
    {
        super(controller);
        _sgController = controller;
    }
    
    protected SGController getSGController()
    {
        return _sgController;
    }
    
    protected SGSelectionManager getSGSelectionManager()
    {
        return getSGController().getSGSelectionManager();
    }
    

    
    @Override
	public void initializeActions()
    {
        super.initializeActions();
 
		getSGController().getProject().getGeneratedSources().setFactory(new GCAction.ProjectGeneratorFactory() {
			@Override
			public AbstractProjectGenerator<? extends GenerationRepository> generatorForRepository(GenerationRepository repository) {
				if (repository instanceof SourceRepository)
					return getSGController().getProjectGenerator((SourceRepository) repository);
				else {
					if (logger.isLoggable(Level.SEVERE))
						logger.severe("Cannot create project generator for "+repository);
				}
				return null;
			}
		});

        (new SGSetPropertyInitializer(this)).init();
        
        // Implementation model management
        (new CreateImplementationModelInitializer(this)).init();
		(new CreateTechnologyModuleImplementationInitializer(this)).init();
        
		// Validate project
		(new ValidateProjectInitializer(this)).init();

		// Code generation
		(new SynchronizeRepositoryCodeGenerationInitializer(this)).init();
		(new GenerateSourceCodeInitializer(this)).init();
		new GenerateAndWriteCodeInitializer(this).init();
		(new ForceRegenerateSourceCodeInitializer(this)).init();
		(new RegenerateAndOverrideInitializer(this)).init();
        (new IncludeFromGenerationInitializer(this)).init();
        (new ExcludeFromGenerationInitializer(this)).init();

		(new WriteModifiedGeneratedFilesInitializer(this)).init();
		(new DismissUnchangedGeneratedFilesInitializer(this)).init();

		// Refreshing
		(new RefreshCGStructureInitializer(this)).init();

		// Edition
		(new EditGeneratedFileInitializer(this)).init();
		(new SaveGeneratedFileInitializer(this)).init();
		(new RevertToSavedGeneratedFileInitializer(this)).init();

		// Merge
		(new MarkAsMergedInitializer(this)).init();
		(new MarkAsUnmergedInitializer(this)).init();
		(new MarkAsMergedAllTrivialMergableFilesInitializer(this)).init();
		(new OverrideWithVersionInitializer(this)).init();
		(new CancelOverrideWithVersionInitializer(this)).init();

		// Accept disk version
		(new AcceptDiskUpdateInitializer(this)).init();
		(new AcceptDiskUpdateAndReinjectInModelnitializer(this)).init();

		// Model reinjection
		(new ReinjectInModelInitializer(this)).init();
		(new ImportInModelInitializer(this)).init();
		(new UpdateModelInitializer(this)).init();
		(new OpenDMEntityInitializer(this)).init();

		// Versionning
		(new RegisterNewCGReleaseInitializer(this)).init();
		(new RevertRepositoryToVersionInitializer(this)).init();
		(new RevertToHistoryVersionInitializer(this)).init();

		(new CleanIntermediateFilesInitializer(this)).init();
		(new RefreshHistoryInitializer(this)).init();

		(new ShowReleaseHistoryInitializer(this)).init();
		(new ShowFileHistoryInitializer(this)).init();
		(new ShowDifferencesInitializer(this)).init();

		// Repository management
		(new CreateSourceRepositoryInitializer(this)).init();
		(new RemoveGeneratedCodeRepositoryInitializer(this)).init();
		(new ConnectCGRepositoryInitializer(this)).init();
		(new DisconnectCGRepositoryInitializer(this)).init();

		// Show/view
		(new ShowFileVersionInitializer(this)).init();
		(new OpenDiffEditorInitializer(this)).init();
		(new OpenFileInExplorerInitializer(this)).init();
		(new CompareTemplatesInNewWindowInitializer(this)).init();

		// Templates management
		(new AddCustomTemplateRepositoryInitializer(this)).init();
		(new RemoveCustomTemplateRepositoryInitializer(this)).init();
		(new RedefineCustomTemplateFileInitializer(this)).init();
		(new EditCustomTemplateFileInitializer(this)).init();
		(new SaveCustomTemplateFileInitializer(this)).init();
		(new RefreshTemplatesInitializer(this)).init();
		(new OpenTemplateFileInNewWindowInitializer(this)).init();
		(new CancelEditionOfCustomTemplateFileInitializer(this)).init();
		(new RemoveTemplateFileInitializer(this)).init();
		(new RedefineAllTemplatesInitializer(this)).init();
		(new ImportTemplatesInitializer(this)).init();
		// Initialize actions available using inspector (template tab)

		// To be sure all Technology Module GUI Factories are recorded
		TechnologyModuleDefinition.getAllTechnologyModuleDefinitions();
		for (TechnologyModuleGUIFactory technologyModuleGUIFactory : SGModule.getAllTechnologyModuleGUIFactories()) {
			technologyModuleGUIFactory.initializeActions(this);
		}

		CGFile.showTemplateActionizer = new FlexoActionizer<OpenTemplateFileInNewWindow, CGTemplate, CGTemplateObject>(OpenTemplateFileInNewWindow.actionType, getSGController().getEditor());

		CGFile.editCustomTemplateActionizer = new FlexoActionizer<EditCustomTemplateFile, CGTemplateFile, CGTemplateObject>(EditCustomTemplateFile.actionType, getSGController().getEditor());

		CGFile.redefineTemplateActionizer = new FlexoActionizer<RedefineCustomTemplateFile, CGTemplate, CGTemplateObject>(RedefineCustomTemplateFile.actionType, getSGController().getEditor());

	}
  
}
