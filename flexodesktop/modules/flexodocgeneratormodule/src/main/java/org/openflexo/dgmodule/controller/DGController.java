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
package org.openflexo.dgmodule.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.openflexo.FlexoCst;
import org.openflexo.components.AskParametersDialog;
import org.openflexo.dg.ProjectDocGenerator;
import org.openflexo.dg.docx.ProjectDocDocxGenerator;
import org.openflexo.dg.html.ProjectDocHTMLGenerator;
import org.openflexo.dgmodule.DGPreferences;
import org.openflexo.dgmodule.controller.action.DGControllerActionInitializer;
import org.openflexo.dgmodule.controller.browser.DGBrowser;
import org.openflexo.dgmodule.menu.DGMenuBar;
import org.openflexo.dgmodule.view.DGBrowserView;
import org.openflexo.dgmodule.view.DGFileVersionPopup;
import org.openflexo.dgmodule.view.DGMainPane;
import org.openflexo.doceditor.controller.DEController;
import org.openflexo.doceditor.controller.DESelectionManager;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.ObjectDeleted;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.action.AbstractGCAction;
import org.openflexo.foundation.gen.GenerationProgressNotification;
import org.openflexo.foundation.param.CheckboxParameter;
import org.openflexo.foundation.param.RadioButtonListParameter;
import org.openflexo.foundation.rm.FlexoCopiedResource;
import org.openflexo.foundation.rm.FlexoGeneratedResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ResourceUpdateHandler.GeneratedResourceModifiedHook;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.rm.cg.ContentSource;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.generator.AbstractProjectGenerator;
import org.openflexo.generator.action.AcceptDiskUpdate;
import org.openflexo.generator.action.GCAction.ProjectGeneratorFactory;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.jedit.JEditTextArea;
import org.openflexo.jedit.JEditTextArea.CursorPositionListener;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.GeneratedResourceModifiedChoice;
import org.openflexo.toolbox.FileCst;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.FlexoMainPane;
import org.openflexo.view.menu.FlexoMenuBar;

/**
 * Controller for Generator module
 * 
 * @author gpolet
 */
public class DGController extends DEController implements FlexoObserver, ProjectGeneratorFactory {

	protected static final Logger logger = Logger.getLogger(DGController.class.getPackage().getName());

	public DocGeneratorPerspective DOCUMENTATION_GENERATOR_PERSPECTIVE;
	public TemplatesPerspective TEMPLATES_PERSPECTIVE;
	public VersionningPerspective VERSIONNING_PERSPECTIVE;

	@Override
	public boolean useNewInspectorScheme() {
		return true;
	}

	@Override
	public boolean useOldInspectorScheme() {
		return false;
	}

	public static final FileResource flexoTemplatesDirectory = new FileResource(FileCst.GENERATOR_TEMPLATES_REL_PATH);

	protected Map<DGRepository, ProjectDocGenerator> _projectGenerators;

	protected DGFooter _footer;

	private DGBrowserView dgBrowserView;

	/**
	 * Default constructor
	 * 
	 * @throws Exception
	 */
	public DGController(FlexoModule module) {
		super(module);
		_projectGenerators = new Hashtable<DGRepository, ProjectDocGenerator>();

	}

	@Override
	protected void initializePerspectives() {
		super.initializePerspectives();
		_CGGeneratedResourceModifiedHook = new DGGeneratedResourceModifiedHook();
		browser = new DGBrowser(this);
		dgBrowserView = new DGBrowserView(this, browser);
		createFooter();
		addToPerspectives(DOCUMENTATION_GENERATOR_PERSPECTIVE = new DocGeneratorPerspective(this));
		addToPerspectives(TEMPLATES_PERSPECTIVE = new TemplatesPerspective(this));
		addToPerspectives(VERSIONNING_PERSPECTIVE = new VersionningPerspective(this));
	}

	public DGBrowser getBrowser() {
		return browser;
	}

	public DGBrowserView getDgBrowserView() {
		return dgBrowserView;
	}

	@Override
	public void updateEditor(FlexoEditor from, FlexoEditor to) {
		super.updateEditor(from, to);
		if (from != null && from.getProject() != null) {
			from.getProject().getGeneratedDoc().setFactory(null);
		}
		if (to != null && to.getResourceUpdateHandler() != null) {
			to.getResourceUpdateHandler().setGeneratedResourceModifiedHook(_CGGeneratedResourceModifiedHook);
		}
		if (to != null && getEditor().getProject() != null) {
			to.getProject().getGeneratedDoc().setFactory(this);
		}
		browser.setRootObject(to != null && to.getProject() != null ? to.getProject().getGeneratedDoc() : null);
	}

	@Override
	public FlexoModelObject getDefaultObjectToSelect(FlexoProject project) {
		return project.getGeneratedDoc();
	}

	@Override
	protected DESelectionManager createSelectionManager() {
		return new DGSelectionManager(this);
	}

	/**
	 * Creates a new instance of MenuBar for the module this controller refers to
	 * 
	 * @return
	 */
	@Override
	protected FlexoMenuBar createNewMenuBar() {
		return new DGMenuBar(this);
	}

	@Override
	protected FlexoMainPane createMainPane() {
		return new DGMainPane(this);
	}

	@Override
	public DGMainPane getMainPane() {
		return (DGMainPane) super.getMainPane();
	}

	@Override
	public DGControllerActionInitializer createControllerActionInitializer() {
		return new DGControllerActionInitializer(this);
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (dataModification instanceof GenerationProgressNotification) {
			if (((GenerationProgressNotification) dataModification).getIsFineMessage()) {
				refreshSecondaryProgressWindow(((GenerationProgressNotification) dataModification).getProgressMessage());
			} else {
				refreshProgressWindow(((GenerationProgressNotification) dataModification).getProgressMessage());
			}
		} else if (observable instanceof DGRepository && dataModification instanceof ObjectDeleted) {
			observedRepositories.remove(observable);
			observable.deleteObserver(this);
		}
	}

	@Override
	public AbstractProjectGenerator<? extends GenerationRepository> generatorForRepository(GenerationRepository repository) {
		if (repository instanceof DGRepository) {
			return getProjectGenerator((DGRepository) repository);
		} else {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Cannot create project generator for " + repository);
			}
		}
		return null;
	}

	public ProjectDocGenerator getProjectGenerator(DGRepository repository) {
		if (!repository.isConnected()) {
			return null;
		}
		ProjectDocGenerator returned = _projectGenerators.get(repository);
		if (returned == null) {
			try {
				switch (repository.getFormat()) {
				/*case LATEX:
					returned = new ProjectDocLatexGenerator(getProject(), repository);
					break;*/
				case HTML:
					returned = new ProjectDocHTMLGenerator(getProject(), repository);
					break;
				case DOCX:
					returned = new ProjectDocDocxGenerator(getProject(), repository);
					break;
				}
			} catch (GenerationException e) {
				showError(e.getLocalizedMessage());
				return null;
			}
			_projectGenerators.put(repository, returned);
		}
		return returned;
	}

	public Collection<ProjectDocGenerator> getProjectGenerators() {
		return _projectGenerators.values();
	}

	/**
	 * Select the view representing supplied object, if this view exists. Try all to really display supplied object, even if required view
	 * is not the current displayed view
	 * 
	 * @param object
	 *            : the object to focus on
	 */
	@Override
	public void selectAndFocusObject(FlexoObject object) {
		if (object instanceof CGFile) {
			setCurrentEditedObjectAsModuleView(object);
		}
		if (object instanceof TOCEntry) {
			setCurrentEditedObjectAsModuleView(object);
		}
		getSelectionManager().setSelectedObject(object);
	}

	public DGRepository getCurrentGeneratedCodeRepository() {
		FlexoObject object = getCurrentDisplayedObjectAsModuleView();
		if (object instanceof CGObject) {
			return (DGRepository) AbstractGCAction.repositoryForObject((CGObject) object);
		}
		return null;
	}

	GenerationRepository _lastEditedCGRepository;

	protected Vector<DGRepository> observedRepositories = new Vector<DGRepository>();

	public void refreshFooter() {
		_footer.refresh();
	}

	private void createFooter() {
		_footer = new DGFooter();
	}

	public GenerationRepository getLastEditedCGRepository() {
		return _lastEditedCGRepository;
	}

	private final Hashtable<CGFile, Hashtable<ContentSource, DGFileVersionPopup>> storedPopups = new Hashtable<CGFile, Hashtable<ContentSource, DGFileVersionPopup>>();

	public DGFileVersionPopup getPopupShowingFileVersion(CGFile aFile, ContentSource contentSource) {
		if (storedPopups.get(aFile) == null) {
			Hashtable<ContentSource, DGFileVersionPopup> newHashtable = new Hashtable<ContentSource, DGFileVersionPopup>();
			storedPopups.put(aFile, newHashtable);
		}
		if (storedPopups.get(aFile).get(contentSource) == null) {
			DGFileVersionPopup newPopup = new DGFileVersionPopup(aFile, contentSource, this);
			storedPopups.get(aFile).put(contentSource, newPopup);
		}
		return storedPopups.get(aFile).get(contentSource);
	}

	public void deletePopupShowingFileVersion(CGFile aFile, ContentSource contentSource) {
		storedPopups.get(aFile).remove(contentSource);
	}

	public DGFooter getFooter() {
		return _footer;
	}

	public class DGFooter extends JPanel implements GraphicalFlexoObserver, FocusListener, CursorPositionListener {

		private final JLabel statusLabel;

		private final JPanel statusCountPanel;

		private final JPanel editorInfoPanel;

		private final JLabel generationModifiedLabel;

		private final JLabel diskModifiedLabel;

		private final JLabel conflictsLabel;

		private final JLabel needsMemoryGenerationLabel;

		private final JLabel errorsLabel;

		private final JLabel cursorPositionLabel;

		private final JLabel editorStatusLabel;

		public DGFooter() {
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
			DGRepository repositoryToConsider = getCurrentGeneratedCodeRepository();
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
					if (_projectGenerators.get(repositoryToConsider) == null) {
						statusLabel.setText(repName + FlexoLocalization.localizedForKey("doc_generation_not_synchronized"));
						displayItemStatus = false;
					} else {
						statusLabel.setText(repName + FlexoLocalization.localizedForKey("doc_generation_is_synchronized"));
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

	private DGGeneratedResourceModifiedHook _CGGeneratedResourceModifiedHook;

	private DGBrowser browser;

	public class DGGeneratedResourceModifiedHook implements GeneratedResourceModifiedHook {

		protected DGGeneratedResourceModifiedHook() {
		}

		@Override
		public void handleGeneratedResourceModified(FlexoGeneratedResource aGeneratedResource) {
			if (aGeneratedResource instanceof CGRepositoryFileResource && !(aGeneratedResource instanceof FlexoCopiedResource)) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Resource " + aGeneratedResource + " has been modified on the disk.");
				}
				CGRepositoryFileResource generatedResource = (CGRepositoryFileResource) aGeneratedResource;
				GeneratedResourceModifiedChoice choice = DGPreferences.getGeneratedResourceModifiedChoice();
				if (choice == GeneratedResourceModifiedChoice.ASK) {

					RadioButtonListParameter<String> whatToDo = new RadioButtonListParameter<String>("whatToDo",
							"what_would_you_like_to_do", GeneratedResourceModifiedChoice.IGNORE.getLocalizedName(),
							GeneratedResourceModifiedChoice.IGNORE.getLocalizedName(),
							GeneratedResourceModifiedChoice.ACCEPT.getLocalizedName());
					CheckboxParameter rememberMyChoice = new CheckboxParameter("rememberMyChoice", "remember_my_choice", false);
					AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
							FlexoLocalization.localizedForKey("resource_edited"), FlexoLocalization.localizedForKey("resource") + " "
									+ generatedResource.getFileName() + " " + FlexoLocalization.localizedForKey("has_been_edited"),
							whatToDo, rememberMyChoice);
					if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
						if (whatToDo.getValue().equals(GeneratedResourceModifiedChoice.IGNORE.getLocalizedName())) {
							choice = GeneratedResourceModifiedChoice.IGNORE;
						} else if (whatToDo.getValue().equals(GeneratedResourceModifiedChoice.ACCEPT.getLocalizedName())) {
							choice = GeneratedResourceModifiedChoice.ACCEPT;
						}
						if (rememberMyChoice.getValue()) {
							DGPreferences.setGeneratedResourceModifiedChoice(choice);
						}
					}
				}
				if (choice == GeneratedResourceModifiedChoice.ASK) {
					choice = GeneratedResourceModifiedChoice.IGNORE;
				}

				// First of all, notify resource changed
				generatedResource.notifyResourceChangedOnDisk();

				Vector<CGObject> resourceChangedOnDisk = new Vector<CGObject>();
				resourceChangedOnDisk.add(generatedResource.getCGFile());

				if (choice == GeneratedResourceModifiedChoice.IGNORE) {
				} else if (choice == GeneratedResourceModifiedChoice.ACCEPT) {
					AcceptDiskUpdate.actionType.makeNewAction(generatedResource.getCGFile(), resourceChangedOnDisk, getEditor()).doAction();
				}
			}
		}

	}

	public DGGeneratedResourceModifiedHook getCGGeneratedResourceModifiedHook() {
		return _CGGeneratedResourceModifiedHook;
	}

	public void setCGGeneratedResourceModifiedHook(DGGeneratedResourceModifiedHook generatedResourceModifiedHook) {
		_CGGeneratedResourceModifiedHook = generatedResourceModifiedHook;
	}

}
