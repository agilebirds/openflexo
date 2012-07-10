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
package org.openflexo.sgmodule.controller;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.components.ProgressWindow;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.action.AbstractGCAction;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.CGTemplateFile;
import org.openflexo.foundation.param.CheckboxParameter;
import org.openflexo.foundation.param.RadioButtonListParameter;
import org.openflexo.foundation.rm.FlexoGeneratedResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ResourceUpdateHandler;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.rm.cg.ContentSource;
import org.openflexo.foundation.sg.GeneratedSources;
import org.openflexo.foundation.sg.SourceRepository;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.TechnologyModelObject;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleImplementation;
import org.openflexo.generator.AbstractProjectGenerator;
import org.openflexo.generator.action.AcceptDiskUpdate;
import org.openflexo.generator.action.GCAction.ProjectGeneratorFactory;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.GeneratedResourceModifiedChoice;
import org.openflexo.selection.SelectionManager;
import org.openflexo.sg.action.AcceptDiskUpdateAndReinjectInModel;
import org.openflexo.sg.action.ReinjectInModel;
import org.openflexo.sg.generator.ProjectGenerator;
import org.openflexo.sgmodule.SGPreferences;
import org.openflexo.sgmodule.controller.action.SGControllerActionInitializer;
import org.openflexo.sgmodule.controller.browser.SGBrowser;
import org.openflexo.sgmodule.view.CGFileModuleView;
import org.openflexo.sgmodule.view.SGBrowserView;
import org.openflexo.sgmodule.view.SGFooter;
import org.openflexo.sgmodule.view.SGMainPane;
import org.openflexo.sgmodule.view.menu.SGMenuBar;
import org.openflexo.sgmodule.view.popup.CGFileVersionPopup;
import org.openflexo.view.FlexoMainPane;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.menu.FlexoMenuBar;

/**
 * Controller for this module
 * 
 * @author sylvain
 */
public class SGController extends FlexoController implements ProjectGeneratorFactory {

	private static final Logger logger = Logger.getLogger(SGController.class.getPackage().getName());

	protected Hashtable<SourceRepository, ProjectGenerator> _projectGenerators;

	protected SGFooter _footer;

	public final CodeGenerationPerspective CODE_GENERATION_PERSPECTIVE;
	public final VersionningPerspective VERSIONNING_PERSPECTIVE;
	public final ModelReinjectionPerspective MODEL_REINJECTION_PERSPECTIVE;

	private SGBrowser _browser;
	private SGBrowserView _browserView;

	private SourceRepository _lastEditedCGRepository;
	protected Vector<SourceRepository> observedRepositories = new Vector<SourceRepository>();

	// ================================================
	// ================ Constructor ===================
	// ================================================

	/**
	 * Default constructor
	 */
	public SGController(FlexoModule module) {
		super(module);

		createFooter();

		addToPerspectives(CODE_GENERATION_PERSPECTIVE = new CodeGenerationPerspective(this));
		addToPerspectives(VERSIONNING_PERSPECTIVE = new VersionningPerspective(this));
		addToPerspectives(MODEL_REINJECTION_PERSPECTIVE = new ModelReinjectionPerspective(this));

		_browser = new SGBrowser(this);
		_browserView = new SGBrowserView(this, _browser) {
			@Override
			public void treeDoubleClick(FlexoModelObject object) {
				super.treeDoubleClick(object);
				if (object instanceof SourceRepository) {
					selectAndFocusObject(object);
				}
			}

		};
	}

	public SGBrowserView getBrowserView() {
		return _browserView;
	}

	@Override
	protected SelectionManager createSelectionManager() {
		return new SGSelectionManager(this);
	}

	@Override
	public ControllerActionInitializer createControllerActionInitializer() {
		return new SGControllerActionInitializer(this);
	}

	/**
	 * Creates a new instance of MenuBar for the module this controller refers to
	 * 
	 * @return
	 */
	@Override
	protected FlexoMenuBar createNewMenuBar() {
		return new SGMenuBar(this);
	}

	@Override
	protected FlexoMainPane createMainPane() {
		return new SGMainPane(this);
	}

	public void initProgressWindow(String msg, int steps) {
		ProgressWindow.showProgressWindow(msg, steps);
	}

	public void refreshProgressWindow(String msg) {
		ProgressWindow.setProgressInstance(msg);
	}

	public void refreshSecondaryProgressWindow(String msg) {
		ProgressWindow.setSecondaryProgressInstance(msg);
	}

	public void disposeProgressWindow() {
		ProgressWindow.hideProgressWindow();
	}

	public SGBrowser getBrowser() {
		return _browser;
	}

	/**
	 * Select the view representing supplied object, if this view exists. Try all to really display supplied object, even if required view
	 * is not the current displayed view
	 * 
	 * @param object
	 *            : the object to focus on
	 */
	@Override
	public void selectAndFocusObject(FlexoModelObject object) {
		if (object instanceof CGFile) {
			setCurrentEditedObjectAsModuleView(object);
		}
		getSelectionManager().setSelectedObject(object);
	}

	// ================================================
	// ============ Exception management ==============
	// ================================================

	@Override
	public boolean handleException(InspectableObject inspectable, String propertyName, Object value, Throwable exception) {
		// TODO: Handles here exceptions that may be thrown through the inspector
		return super.handleException(inspectable, propertyName, value, exception);
	}

	@Override
	public AbstractProjectGenerator<? extends GenerationRepository> generatorForRepository(GenerationRepository repository) {
		if (repository instanceof SourceRepository) {
			return getProjectGenerator((SourceRepository) repository);
		} else {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Cannot create project generator for " + repository);
			}
		}
		return null;
	}

	public ProjectGenerator getProjectGenerator(SourceRepository repository) {
		ProjectGenerator returned = _projectGenerators.get(repository);
		if (!repository.isConnected()) {
			return returned;
		}
		if (returned == null) {
			try {
				returned = new ProjectGenerator(getProject(), repository);
			} catch (GenerationException e) {
				showError(e.getLocalizedMessage());
				return null;
			}
			_projectGenerators.put(repository, returned);
		}
		return returned;
	}

	public Enumeration<ProjectGenerator> getProjectGenerators() {
		return _projectGenerators.elements();
	}

	@Override
	public void updateEditor(FlexoEditor from, FlexoEditor to) {
		super.updateEditor(from, to);
		if (from != null && from.getProject() != null) {
			from.getProject().getGeneratedCode().setFactory(null);
		}
		if (to != null && to.getResourceUpdateHandler() != null) {
			to.getResourceUpdateHandler().setGeneratedResourceModifiedHook(_CGGeneratedResourceModifiedHook);
		}
		if (to != null && to.getProject() != null) {
			to.getProject().getGeneratedCode().setFactory(this);
		}
	}

	@Override
	public FlexoModelObject getDefaultObjectToSelect(FlexoProject project) {
		// Implement this
		return null;
	}

	public SourceRepository getCurrentGeneratedCodeRepository() {
		FlexoModelObject object = getCurrentDisplayedObjectAsModuleView();
		if (object instanceof CGObject && AbstractGCAction.repositoryForObject((CGObject) object) instanceof SourceRepository) {
			return (SourceRepository) AbstractGCAction.repositoryForObject((CGObject) object);
		}
		return null;
	}

	public Vector<SourceRepository> getObservedRepositories() {
		return observedRepositories;
	}

	public void refreshFooter() {
		_footer.refresh();
	}

	private void createFooter() {
		_footer = new SGFooter(this);
	}

	public GenerationRepository getLastEditedCGRepository() {
		return _lastEditedCGRepository;
	}

	private Hashtable<CGFile, Hashtable<ContentSource, CGFileVersionPopup>> storedPopups = new Hashtable<CGFile, Hashtable<ContentSource, CGFileVersionPopup>>();

	public CGFileVersionPopup getPopupShowingFileVersion(CGFile aFile, ContentSource contentSource) {
		if (storedPopups.get(aFile) == null) {
			Hashtable<ContentSource, CGFileVersionPopup> newHashtable = new Hashtable<ContentSource, CGFileVersionPopup>();
			storedPopups.put(aFile, newHashtable);
		}
		if (storedPopups.get(aFile).get(contentSource) == null) {
			CGFileVersionPopup newPopup = new CGFileVersionPopup(aFile, contentSource, this);
			storedPopups.get(aFile).put(contentSource, newPopup);
		}
		return storedPopups.get(aFile).get(contentSource);
	}

	public void deletePopupShowingFileVersion(CGFile aFile, ContentSource contentSource) {
		storedPopups.get(aFile).remove(contentSource);
	}

	public SGFooter getFooter() {
		return _footer;
	}

	private CGGeneratedResourceModifiedHook _CGGeneratedResourceModifiedHook;

	public class CGGeneratedResourceModifiedHook implements ResourceUpdateHandler.GeneratedResourceModifiedHook {

		protected CGGeneratedResourceModifiedHook() {
		}

		@Override
		public void handleGeneratedResourceModified(FlexoGeneratedResource aGeneratedResource) {
			if (aGeneratedResource instanceof CGRepositoryFileResource) {
				CGRepositoryFileResource generatedResource = (CGRepositoryFileResource) aGeneratedResource;
				GeneratedResourceModifiedChoice choice = SGPreferences.getGeneratedResourceModifiedChoice();
				if (choice == GeneratedResourceModifiedChoice.ASK) {

					RadioButtonListParameter<String> whatToDo = new RadioButtonListParameter<String>("whatToDo",
							"what_would_you_like_to_do", GeneratedResourceModifiedChoice.IGNORE.getLocalizedName(),
							GeneratedResourceModifiedChoice.IGNORE.getLocalizedName(),
							GeneratedResourceModifiedChoice.REINJECT_IN_MODEL.getLocalizedName(),
							GeneratedResourceModifiedChoice.AUTOMATICALLY_REINJECT_IN_MODEL.getLocalizedName(),
							GeneratedResourceModifiedChoice.ACCEPT.getLocalizedName(),
							GeneratedResourceModifiedChoice.ACCEPT_AND_REINJECT.getLocalizedName(),
							GeneratedResourceModifiedChoice.ACCEPT_AND_AUTOMATICALLY_REINJECT.getLocalizedName());
					CheckboxParameter rememberMyChoice = new CheckboxParameter("rememberMyChoice", "remember_my_choice", false);
					AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
							FlexoLocalization.localizedForKey("resource_edited"), FlexoLocalization.localizedForKey("resource") + " "
									+ generatedResource.getFileName() + " " + FlexoLocalization.localizedForKey("has_been_edited"),
							whatToDo, rememberMyChoice);
					if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
						if (whatToDo.getValue().equals(GeneratedResourceModifiedChoice.IGNORE.getLocalizedName())) {
							choice = GeneratedResourceModifiedChoice.IGNORE;
						} else if (whatToDo.getValue().equals(GeneratedResourceModifiedChoice.REINJECT_IN_MODEL.getLocalizedName())) {
							choice = GeneratedResourceModifiedChoice.REINJECT_IN_MODEL;
						} else if (whatToDo.getValue().equals(
								GeneratedResourceModifiedChoice.AUTOMATICALLY_REINJECT_IN_MODEL.getLocalizedName())) {
							choice = GeneratedResourceModifiedChoice.AUTOMATICALLY_REINJECT_IN_MODEL;
						} else if (whatToDo.getValue().equals(GeneratedResourceModifiedChoice.ACCEPT.getLocalizedName())) {
							choice = GeneratedResourceModifiedChoice.ACCEPT;
						} else if (whatToDo.getValue().equals(GeneratedResourceModifiedChoice.ACCEPT_AND_REINJECT.getLocalizedName())) {
							choice = GeneratedResourceModifiedChoice.ACCEPT_AND_REINJECT;
						} else if (whatToDo.getValue().equals(
								GeneratedResourceModifiedChoice.ACCEPT_AND_AUTOMATICALLY_REINJECT.getLocalizedName())) {
							choice = GeneratedResourceModifiedChoice.ACCEPT_AND_AUTOMATICALLY_REINJECT;
						}
						if (rememberMyChoice.getValue()) {
							SGPreferences.setGeneratedResourceModifiedChoice(choice);
						}
					}
				}
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("I will perform " + choice.getLocalizedName());
				}
				if (choice == GeneratedResourceModifiedChoice.ASK) {
					choice = GeneratedResourceModifiedChoice.IGNORE;
				}
				if (generatedResource.getCGFile() != null) {
					// First of all, notify resource changed
					generatedResource.notifyResourceChangedOnDisk();
					Vector<CGObject> resourceChangedOnDisk = new Vector<CGObject>();
					resourceChangedOnDisk.add(generatedResource.getCGFile());
					if (choice == GeneratedResourceModifiedChoice.IGNORE) {
					} else if (choice == GeneratedResourceModifiedChoice.REINJECT_IN_MODEL) {
						ReinjectInModel.actionType.makeNewAction(generatedResource.getCGFile(), resourceChangedOnDisk, getEditor())
								.doAction();
					} else if (choice == GeneratedResourceModifiedChoice.AUTOMATICALLY_REINJECT_IN_MODEL) {
						ReinjectInModel reinjectInModelAction = ReinjectInModel.actionType.makeNewAction(generatedResource.getCGFile(),
								resourceChangedOnDisk, getEditor());
						reinjectInModelAction.setAskReinjectionContext(false);
						reinjectInModelAction.doAction();
					} else if (choice == GeneratedResourceModifiedChoice.ACCEPT) {
						AcceptDiskUpdate.actionType.makeNewAction(generatedResource.getCGFile(), null, getEditor()).doAction();
					} else if (choice == GeneratedResourceModifiedChoice.ACCEPT_AND_REINJECT) {
						AcceptDiskUpdateAndReinjectInModel.actionType.makeNewAction(generatedResource.getCGFile(), resourceChangedOnDisk,
								getEditor()).doAction();
					} else if (choice == GeneratedResourceModifiedChoice.ACCEPT_AND_AUTOMATICALLY_REINJECT) {
						AcceptDiskUpdateAndReinjectInModel acceptDiskUpdateAndReinjectInModelAction = AcceptDiskUpdateAndReinjectInModel.actionType
								.makeNewAction(generatedResource.getCGFile(), resourceChangedOnDisk, getEditor());
						acceptDiskUpdateAndReinjectInModelAction.setAskReinjectionContext(false);
						acceptDiskUpdateAndReinjectInModelAction.doAction();
					}
				}
			}
		}

	}

	public CGGeneratedResourceModifiedHook getCGGeneratedResourceModifiedHook() {
		return _CGGeneratedResourceModifiedHook;
	}

	public void setCGGeneratedResourceModifiedHook(CGGeneratedResourceModifiedHook generatedResourceModifiedHook) {
		_CGGeneratedResourceModifiedHook = generatedResourceModifiedHook;
	}

	@Override
	public String getWindowTitleforObject(FlexoModelObject object) {
		if (object instanceof ImplementationModel) {
			return FlexoLocalization.localizedForKey("implementation_model") + " " + object.getName();
		}
		if (object instanceof TechnologyModuleImplementation) {
			return ((TechnologyModuleImplementation) object).getTechnologyModuleDefinition().getName();
		}
		if (object instanceof TechnologyModelObject) {
			return ((TechnologyModelObject) object).getName();
		}
		if (object instanceof GeneratedSources) {
			return FlexoLocalization.localizedForKey("generated_sources");
		}
		if (object instanceof SourceRepository) {
			return ((SourceRepository) object).getName();
		}
		if (object instanceof CGFile) {
			CGFile cgFile = (CGFile) object;
			return cgFile.getResource().getFile().getName()
					+ (cgFile.isEdited() ? "[" + FlexoLocalization.localizedForKey("edited") + "]" : "");
		}
		if (object instanceof CGTemplate) {
			CGTemplate cgTemplateFile = (CGTemplate) object;
			return cgTemplateFile.getTemplateName()
					+ (cgTemplateFile instanceof CGTemplateFile && ((CGTemplateFile) cgTemplateFile).isEdited() ? "["
							+ FlexoLocalization.localizedForKey("edited") + "]" : "");
		}

		return null;
	}

	public void notifyModuleViewDisplayed(ModuleView<?> moduleView) {
		if (moduleView.getRepresentedObject() instanceof CGObject
				&& AbstractGCAction.repositoryForObject((CGObject) moduleView.getRepresentedObject()) instanceof SourceRepository) {
			_lastEditedCGRepository = (SourceRepository) AbstractGCAction.repositoryForObject((CGObject) moduleView.getRepresentedObject());
		}
		refreshFooter();
		if (moduleView instanceof CGFileModuleView) {
			((CGFileModuleView) moduleView).refresh();
		}
	}

}
