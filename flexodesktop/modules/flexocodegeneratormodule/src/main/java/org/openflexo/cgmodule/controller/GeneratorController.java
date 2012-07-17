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
package org.openflexo.cgmodule.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.openflexo.FlexoCst;
import org.openflexo.cgmodule.GeneratorPreferences;
import org.openflexo.cgmodule.controller.action.GeneratorControllerActionInitializer;
import org.openflexo.cgmodule.controller.browser.GeneratorBrowser;
import org.openflexo.cgmodule.menu.GeneratorMenuBar;
import org.openflexo.cgmodule.view.CGFileVersionPopup;
import org.openflexo.cgmodule.view.GeneratorBrowserView;
import org.openflexo.cgmodule.view.GeneratorMainPane;
import org.openflexo.components.AskParametersDialog;
import org.openflexo.components.ProgressWindow;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.action.AbstractGCAction;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.CGTemplateFile;
import org.openflexo.foundation.param.CheckboxParameter;
import org.openflexo.foundation.param.RadioButtonListParameter;
import org.openflexo.foundation.rm.FlexoGeneratedResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ResourceUpdateHandler.GeneratedResourceModifiedHook;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.rm.cg.ContentSource;
import org.openflexo.generator.AbstractProjectGenerator;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.action.AcceptDiskUpdate;
import org.openflexo.generator.action.AcceptDiskUpdateAndReinjectInModel;
import org.openflexo.generator.action.GCAction;
import org.openflexo.generator.action.ReinjectInModel;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.jedit.JEditTextArea;
import org.openflexo.jedit.JEditTextArea.CursorPositionListener;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.GeneratedResourceModifiedChoice;
import org.openflexo.selection.SelectionManager;
import org.openflexo.toolbox.FileCst;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.FlexoMainPane;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;
import org.openflexo.view.menu.FlexoMenuBar;

/**
 * Controller for Generator module
 * 
 * @author sguerin
 */
public class GeneratorController extends FlexoController implements GCAction.ProjectGeneratorFactory {

	protected static final Logger logger = Logger.getLogger(GeneratorController.class.getPackage().getName());

	public final FlexoPerspective CODE_GENERATOR_PERSPECTIVE;
	public final FlexoPerspective VERSIONNING_PERSPECTIVE;
	public final FlexoPerspective MODEL_REINJECTION_PERSPECTIVE;

	public static final FileResource flexoTemplatesDirectory = new FileResource(FileCst.GENERATOR_TEMPLATES_REL_PATH);

	protected Hashtable<GenerationRepository, ProjectGenerator> _projectGenerators;

	protected CGFooter _footer;

	private GeneratorBrowserView browserView;

	/**
	 * Default constructor
	 * 
	 * @param workflowFile
	 * @throws Exception
	 */
	public GeneratorController(FlexoModule module) {
		super(module);
		_CGGeneratedResourceModifiedHook = new CGGeneratedResourceModifiedHook();
		browser = new GeneratorBrowser(this);
		browserView = new GeneratorBrowserView(this, browser);
		createFooter();
		addToPerspectives(CODE_GENERATOR_PERSPECTIVE = new CodeGeneratorPerspective(this));
		addToPerspectives(VERSIONNING_PERSPECTIVE = new VersionningPerspective(this));
		addToPerspectives(MODEL_REINJECTION_PERSPECTIVE = new ModelReinjectionPerspective(this));
		_projectGenerators = new Hashtable<GenerationRepository, ProjectGenerator>();
	}

	public GeneratorBrowserView getBrowserView() {
		return browserView;
	}

	@Override
	protected SelectionManager createSelectionManager() {
		return new GeneratorSelectionManager(this);
	}

	@Override
	protected FlexoMainPane createMainPane() {
		return new GeneratorMainPane(this);
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
		browser.setRootObject(to != null && to.getProject() != null ? to.getProject().getGeneratedCode() : null);
	}

	@Override
	public FlexoModelObject getDefaultObjectToSelect(FlexoProject project) {
		return project.getGeneratedCode();
	}

	/**
	 * Creates a new instance of MenuBar for the module this controller refers to
	 * 
	 * @return
	 */
	@Override
	protected FlexoMenuBar createNewMenuBar() {
		return new GeneratorMenuBar(this);
	}

	@Override
	public ControllerActionInitializer createControllerActionInitializer() {
		return new GeneratorControllerActionInitializer(this);
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

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {

	}

	@Override
	public AbstractProjectGenerator<? extends GenerationRepository> generatorForRepository(GenerationRepository repository) {
		if (repository instanceof CGRepository) {
			return getProjectGenerator((CGRepository) repository);
		} else {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Cannot create project generator for " + repository);
			}
		}
		return null;
	}

	public ProjectGenerator getProjectGenerator(CGRepository repository) {
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
	public void dispose() {
		super.dispose();
		getSelectionManager().deleteObserver(getSharedInspectorController());
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

	public GenerationRepository getCurrentGeneratedCodeRepository() {
		FlexoModelObject object = getCurrentDisplayedObjectAsModuleView();
		if (object instanceof CGObject) {
			return AbstractGCAction.repositoryForObject((CGObject) object);
		}
		return null;
	}

	public GenerationRepository _lastEditedCGRepository;

	private List<GenerationRepository> observedRepositories = new Vector<GenerationRepository>();

	public void refreshFooter() {
		_footer.refresh();
	}

	private void createFooter() {
		_footer = new CGFooter();
	}

	public GenerationRepository getLastEditedCGRepository() {
		return _lastEditedCGRepository;
	}

	private final Hashtable<CGFile, Hashtable<ContentSource, CGFileVersionPopup>> storedPopups = new Hashtable<CGFile, Hashtable<ContentSource, CGFileVersionPopup>>();

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

	public CGFooter getFooter() {
		return _footer;
	}

	public class CGFooter extends JPanel implements GraphicalFlexoObserver, FocusListener, CursorPositionListener {

		private final JLabel statusLabel;
		private final JPanel statusCountPanel;
		private final JPanel editorInfoPanel;

		private final JLabel generationModifiedLabel;
		private final JLabel diskModifiedLabel;
		private final JLabel conflictsLabel;
		private final JLabel needsMemoryGenerationLabel;
		private final JLabel needsReinjectionLabel;
		private final JLabel errorsLabel;

		private final JLabel cursorPositionLabel;
		private final JLabel editorStatusLabel;

		public CGFooter() {
			super(new GridLayout(1, 3));
			statusLabel = new JLabel("012345678901234567890123456789012345678901234567890123456789", SwingConstants.LEFT);
			statusLabel.setFont(FlexoCst.MEDIUM_FONT);
			statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
			add(statusLabel);

			statusCountPanel = new JPanel(new FlowLayout());
			generationModifiedLabel = new JLabel("0");
			generationModifiedLabel.setFont(FlexoCst.MEDIUM_FONT);
			statusCountPanel.add(generationModifiedLabel);
			statusCountPanel.add(new JLabel(UtilsIconLibrary.LEFT_MODIFICATION_ICON));
			statusCountPanel.add(Box.createRigidArea(new Dimension(3, 16)));
			diskModifiedLabel = new JLabel("5");
			diskModifiedLabel.setFont(FlexoCst.MEDIUM_FONT);
			statusCountPanel.add(diskModifiedLabel);
			statusCountPanel.add(new JLabel(UtilsIconLibrary.RIGHT_MODIFICATION_ICON));
			statusCountPanel.add(Box.createRigidArea(new Dimension(3, 16)));
			conflictsLabel = new JLabel("8");
			conflictsLabel.setFont(FlexoCst.MEDIUM_FONT);
			statusCountPanel.add(conflictsLabel);
			statusCountPanel.add(new JLabel(UtilsIconLibrary.CONFLICT_ICON));
			statusCountPanel.add(Box.createRigidArea(new Dimension(3, 16)));
			statusCountPanel.add(new JLabel(UtilsIconLibrary.SEPARATOR_ICON));
			statusCountPanel.add(Box.createRigidArea(new Dimension(3, 16)));
			needsMemoryGenerationLabel = new JLabel("1");
			needsMemoryGenerationLabel.setFont(FlexoCst.MEDIUM_FONT);
			statusCountPanel.add(needsMemoryGenerationLabel);
			statusCountPanel.add(new JLabel(GeneratorIconLibrary.GENERATE_CODE_ICON));
			statusCountPanel.add(Box.createRigidArea(new Dimension(3, 16)));
			needsReinjectionLabel = new JLabel("1");
			needsReinjectionLabel.setFont(FlexoCst.MEDIUM_FONT);
			statusCountPanel.add(needsReinjectionLabel);
			statusCountPanel.add(new JLabel(GeneratorIconLibrary.NEEDS_MODEL_REINJECTION_ICON));
			statusCountPanel.add(Box.createRigidArea(new Dimension(3, 16)));
			statusCountPanel.add(new JLabel(UtilsIconLibrary.SEPARATOR_ICON));
			statusCountPanel.add(Box.createRigidArea(new Dimension(3, 16)));
			errorsLabel = new JLabel("0");
			errorsLabel.setFont(FlexoCst.MEDIUM_FONT);
			statusCountPanel.add(errorsLabel);
			statusCountPanel.add(new JLabel(IconLibrary.UNFIXABLE_ERROR_ICON));
			add(statusCountPanel);

			editorInfoPanel = new JPanel(new FlowLayout());
			editorInfoPanel.add(new JLabel(UtilsIconLibrary.SEPARATOR_ICON));
			editorInfoPanel.add(Box.createRigidArea(new Dimension(3, 16)));
			cursorPositionLabel = new JLabel("-:-", SwingConstants.CENTER);
			cursorPositionLabel.setPreferredSize(new Dimension(50, 16));
			cursorPositionLabel.setFont(FlexoCst.MEDIUM_FONT);
			editorInfoPanel.add(cursorPositionLabel);
			editorInfoPanel.add(Box.createRigidArea(new Dimension(3, 16)));
			editorInfoPanel.add(new JLabel(UtilsIconLibrary.SEPARATOR_ICON));
			editorInfoPanel.add(Box.createRigidArea(new Dimension(3, 16)));
			editorStatusLabel = new JLabel("");
			editorStatusLabel.setFont(FlexoCst.MEDIUM_FONT);
			editorInfoPanel.add(editorStatusLabel);
			add(editorInfoPanel);
			refreshEditorInfoPanel();

		}

		public void refresh() {
			GenerationRepository repositoryToConsider = getCurrentGeneratedCodeRepository();
			// logger.info("Refresh footer with "+repositoryToConsider);
			boolean displayItemStatus;
			if (repositoryToConsider != null) {
				if (!observedRepositories.contains(repositoryToConsider)) {
					observedRepositories.add(repositoryToConsider);
					repositoryToConsider.addObserver(this);
				}
				String repName = "[" + repositoryToConsider.getName() + "] ";
				if (!repositoryToConsider.isConnected()) {
					statusLabel.setText(repName + FlexoLocalization.localizedForKey("repository_disconnected"));
					statusLabel.setForeground(Color.BLACK);
					displayItemStatus = false;
				} else {
					if (_projectGenerators.get(repositoryToConsider) == null
							|| !_projectGenerators.get(repositoryToConsider).hasBeenInitialized()) {
						statusLabel.setText(repName + FlexoLocalization.localizedForKey("code_generation_not_synchronized"));
						displayItemStatus = false;
					} else {
						statusLabel.setText(repName + FlexoLocalization.localizedForKey("code_generation_is_synchronized"));
						displayItemStatus = true;
					}
					statusLabel.setForeground(Color.BLACK);
				}
			} else {
				statusLabel.setText(FlexoLocalization.localizedForKey("no_repository_selected"));
				statusLabel.setForeground(Color.GRAY);
				displayItemStatus = false;
			}

			if (displayItemStatus) {
				generationModifiedLabel.setForeground(Color.BLACK);
				generationModifiedLabel.setText("" + repositoryToConsider.getGenerationModifiedCount());
				diskModifiedLabel.setForeground(Color.BLACK);
				diskModifiedLabel.setText("" + repositoryToConsider.getDiskModifiedCount());
				conflictsLabel.setForeground(Color.BLACK);
				conflictsLabel.setText("" + repositoryToConsider.getConflictsCount());
				needsMemoryGenerationLabel.setForeground(Color.BLACK);
				needsMemoryGenerationLabel.setText("" + repositoryToConsider.getNeedsMemoryGenerationCount());
				needsReinjectionLabel.setForeground(Color.BLACK);
				needsReinjectionLabel.setText("" + repositoryToConsider.getNeedsModelReinjectionCount());
				errorsLabel.setForeground(Color.BLACK);
				errorsLabel.setText("" + repositoryToConsider.getErrorsCount());
			} else {
				generationModifiedLabel.setForeground(Color.GRAY);
				generationModifiedLabel.setText("-");
				diskModifiedLabel.setForeground(Color.GRAY);
				diskModifiedLabel.setText("-");
				conflictsLabel.setForeground(Color.GRAY);
				conflictsLabel.setText("-");
				needsMemoryGenerationLabel.setForeground(Color.GRAY);
				needsMemoryGenerationLabel.setText("-");
				needsReinjectionLabel.setForeground(Color.GRAY);
				needsReinjectionLabel.setText("-");
				errorsLabel.setForeground(Color.GRAY);
				errorsLabel.setText("-");
			}

			refreshEditorInfoPanel();

			validate();
			repaint();
		}

		@Override
		public void update(FlexoObservable observable, DataModification dataModification) {
			refresh();
		}

		@Override
		public void focusGained(FocusEvent e) {
			if (e.getComponent() instanceof JEditTextArea) {
				((JEditTextArea) e.getComponent()).addToCursorPositionListener(this);
				_activeGenericCodeDisplayer = (JEditTextArea) e.getComponent();
				refresh();
			}
		}

		@Override
		public void focusLost(FocusEvent e) {
			if (e.getComponent() instanceof JEditTextArea) {
				((JEditTextArea) e.getComponent()).removeFromCursorPositionListener(this);
				if (_activeGenericCodeDisplayer == e.getComponent()) {
					_activeGenericCodeDisplayer = null;
				}
				refresh();
			}
		}

		private JEditTextArea _activeGenericCodeDisplayer;

		@Override
		public void positionChanged(int newPosX, int newPosY) {
			refreshEditorInfoPanel();
		}

		private void refreshEditorInfoPanel() {
			// logger.info("refreshEditorInfoPanel()");
			if (_activeGenericCodeDisplayer == null) {
				cursorPositionLabel.setText("-");
				editorStatusLabel.setText(FlexoLocalization.localizedForKey("no_edition"));
			} else {
				cursorPositionLabel.setText(_activeGenericCodeDisplayer.getCursorY() + ":" + _activeGenericCodeDisplayer.getCursorX());
				editorStatusLabel.setText(_activeGenericCodeDisplayer.isEditable() ? FlexoLocalization.localizedForKey("edition")
						: FlexoLocalization.localizedForKey("read_only"));
			}
		}

	}

	private CGGeneratedResourceModifiedHook _CGGeneratedResourceModifiedHook;

	private GeneratorBrowser browser;

	public class CGGeneratedResourceModifiedHook implements GeneratedResourceModifiedHook {

		protected CGGeneratedResourceModifiedHook() {
		}

		@Override
		public void handleGeneratedResourceModified(FlexoGeneratedResource aGeneratedResource) {
			if (aGeneratedResource instanceof CGRepositoryFileResource) {
				CGRepositoryFileResource generatedResource = (CGRepositoryFileResource) aGeneratedResource;
				GeneratedResourceModifiedChoice choice = GeneratorPreferences.getGeneratedResourceModifiedChoice();
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
							GeneratorPreferences.setGeneratedResourceModifiedChoice(choice);
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
		if (object instanceof GeneratedOutput) {
			return FlexoLocalization.localizedForKey("generated_code");
		} else if (object instanceof CGRepository) {
			return ((CGRepository) object).getName();
		} else if (object instanceof CGFile) {
			CGFile cgFile = (CGFile) object;
			return cgFile.getResource().getFile().getName()
					+ (cgFile.isEdited() ? "[" + FlexoLocalization.localizedForKey("edited") + "]" : "");
		} else if (object instanceof CGTemplate) {
			CGTemplate cgTemplateFile = (CGTemplate) object;
			return cgTemplateFile.getTemplateName()
					+ (cgTemplateFile instanceof CGTemplateFile && ((CGTemplateFile) cgTemplateFile).isEdited() ? "["
							+ FlexoLocalization.localizedForKey("edited") + "]" : "");
		}

		return null;
	}
}