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
package org.openflexo.fps.controller;

/*
 * Created on <date> by <yourname>
 *
 * Flexo Application Suite
 * (c) Denali 2003-2006
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.openflexo.FlexoCst;
import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.param.CheckboxParameter;
import org.openflexo.foundation.param.ReadOnlyTextFieldParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.fps.CVSFile;
import org.openflexo.fps.CVSRepository;
import org.openflexo.fps.CVSRepository.ConnectionType;
import org.openflexo.fps.CVSRepositoryList;
import org.openflexo.fps.FlexoAuthentificationException;
import org.openflexo.fps.SharedProject;
import org.openflexo.fps.action.FlexoUnknownHostException;
import org.openflexo.fps.controller.action.FPSControllerActionInitializer;
import org.openflexo.fps.controller.browser.CVSRepositoriesBrowser;
import org.openflexo.fps.controller.browser.SharedProjectBrowser;
import org.openflexo.fps.view.CVSRepositoryBrowserView;
import org.openflexo.fps.view.ConsoleView;
import org.openflexo.fps.view.FPSMainPane;
import org.openflexo.fps.view.SharedProjectBrowserView;
import org.openflexo.fps.view.menu.FPSMenuBar;
import org.openflexo.icon.FPSIconLibrary;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.jedit.JEditTextArea;
import org.openflexo.jedit.JEditTextArea.CursorPositionListener;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.FlexoModule;
import org.openflexo.selection.SelectionManager;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.view.FlexoMainPane;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;
import org.openflexo.view.menu.FlexoMenuBar;

/**
 * Controller for this module
 * 
 * @author yourname
 */
public class FPSController extends FlexoController {
	static final Logger logger = Logger.getLogger(FPSController.class.getPackage().getName());

	public FlexoPerspective ALL_FILES_PERSPECTIVE = new AllFilesPerspective(this);
	public FlexoPerspective INTERESTING_FILES_PERSPECTIVE = new InterestingFilesPerspective(this);
	public FlexoPerspective LOCALLY_MODIFIED_PERSPECTIVE = new LocallyModifiedPerspective(this);
	public FlexoPerspective REMOTELY_MODIFIED_PERSPECTIVE = new RemotelyModifiedPerspective(this);
	public FlexoPerspective CONFLICTING_FILES_PERSPECTIVE = new ConflictingFilesPerspective(this);

	// ================================================
	// ============= Instance variables ===============
	// ================================================

	private CVSRepositoriesBrowser _repositoriesBrowser;
	private SharedProjectBrowser _sharedProjectBrowser;

	private CVSRepositoryList _repositories;
	private SharedProject _sharedProject;

	private ConsoleView _consoleView;

	FPSFooter _footer;

	// ================================================
	// ================ Constructor ===================
	// ================================================

	private static FPSController _instance = null;

	private SharedProjectBrowserView sharedProjectBrowserView;

	private CVSRepositoryBrowserView cvsRepositoryBrowserView;

	public static FPSController getInstance() {
		return _instance;
	}

	/**
	 * Default constructor
	 */
	public FPSController(FlexoModule module) {
		super(module);
		_instance = this;
	}

	@Override
	protected void initializePerspectives() {
		logger.info("Create CVSRepositoryList");
		_repositories = new CVSRepositoryList();
		_repositoriesBrowser = new CVSRepositoriesBrowser(this);
		_sharedProjectBrowser = new SharedProjectBrowser(this);
		cvsRepositoryBrowserView = new CVSRepositoryBrowserView(this);
		sharedProjectBrowserView = new SharedProjectBrowserView(this);

		createFooter();

		_consoleView = new ConsoleView();
		_repositories.loadStoredRepositoryLocation(FileUtils.getApplicationDataDirectory());
		_footer.refresh();

		addToPerspectives(ALL_FILES_PERSPECTIVE);
		addToPerspectives(INTERESTING_FILES_PERSPECTIVE);
		addToPerspectives(LOCALLY_MODIFIED_PERSPECTIVE);
		addToPerspectives(REMOTELY_MODIFIED_PERSPECTIVE);
		addToPerspectives(CONFLICTING_FILES_PERSPECTIVE);
		if (getCurrentPerspective() instanceof FPSPerspective) {
			((FPSPerspective) getCurrentPerspective()).setFilters();
			getSharedProjectBrowser().update();
		}
	}

	public SharedProjectBrowserView getSharedProjectBrowserView() {
		return sharedProjectBrowserView;
	}

	public CVSRepositoryBrowserView getCvsRepositoryBrowserView() {
		return cvsRepositoryBrowserView;
	}

	@Override
	protected SelectionManager createSelectionManager() {
		return new FPSSelectionManager(this);
	}

	@Override
	public FlexoModelObject getDefaultObjectToSelect(FlexoProject project) {
		return getSharedProject();
	}

	@Override
	public void dispose() {
		super.dispose();
		if (_instance == this) {
			_instance = null;
		}
	}

	@Override
	public ControllerActionInitializer createControllerActionInitializer() {
		return new FPSControllerActionInitializer(this);
	}

	/**
	 * Creates a new instance of MenuBar for the module this controller refers to
	 * 
	 * @return
	 */
	@Override
	protected FlexoMenuBar createNewMenuBar() {
		return new FPSMenuBar(this);
	}

	@Override
	protected FlexoMainPane createMainPane() {
		return new FPSMainPane(this);
	}

	public CVSRepositoriesBrowser getCVSRepositoriesBrowser() {
		return _repositoriesBrowser;
	}

	public SharedProjectBrowser getSharedProjectBrowser() {
		return _sharedProjectBrowser;
	}

	@Override
	public boolean handleException(InspectableObject inspectable, String propertyName, Object value, Throwable exception) {
		// TODO: Handles here exceptions that may be thrown through the inspector
		return super.handleException(inspectable, propertyName, value, exception);
	}

	public CVSRepositoryList getRepositories() {
		return _repositories;
	}

	public SharedProject getSharedProject() {
		return _sharedProject;
	}

	public void setSharedProject(SharedProject project) {
		if (_sharedProject != null) {
			_sharedProject.deleteObserver(_footer);
		}
		if (project != null) {
			project.addObserver(_footer);
		}
		_sharedProject = project;
		_sharedProjectBrowser.update();
		_footer.refresh();
	}

	public ConsoleView getConsoleView() {
		return _consoleView;
	}

	public void refreshFooter() {
		_footer.refresh();
	}

	private void createFooter() {
		_footer = new FPSFooter();
	}

	public FPSFooter getFooter() {
		return _footer;
	}

	public class FPSFooter extends JPanel implements GraphicalFlexoObserver, FocusListener, CursorPositionListener {

		private final JLabel statusLabel;
		private final JPanel statusCountPanel;
		private final JPanel editorInfoPanel;

		private final JLabel locallyModifiedLabel;
		private final JLabel remoteModifiedLabel;
		private final JLabel conflictsLabel;
		private final JLabel resolvedConflictsLabel;

		private final JLabel cursorPositionLabel;
		private final JLabel editorStatusLabel;

		public FPSFooter() {
			super(new GridLayout(1, 3));
			statusLabel = new JLabel("012345678901234567890123456789012345678901234567890123456789", SwingConstants.LEFT);
			statusLabel.setFont(FlexoCst.MEDIUM_FONT);
			statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
			add(statusLabel);

			statusCountPanel = new JPanel(new FlowLayout());
			locallyModifiedLabel = new JLabel("0");
			locallyModifiedLabel.setFont(FlexoCst.MEDIUM_FONT);
			statusCountPanel.add(locallyModifiedLabel);
			statusCountPanel.add(new JLabel(UtilsIconLibrary.LEFT_MODIFICATION_ICON));
			statusCountPanel.add(Box.createRigidArea(new Dimension(3, 16)));
			remoteModifiedLabel = new JLabel("5");
			remoteModifiedLabel.setFont(FlexoCst.MEDIUM_FONT);
			statusCountPanel.add(remoteModifiedLabel);
			statusCountPanel.add(new JLabel(UtilsIconLibrary.RIGHT_MODIFICATION_ICON));
			statusCountPanel.add(Box.createRigidArea(new Dimension(3, 16)));
			conflictsLabel = new JLabel("8");
			conflictsLabel.setFont(FlexoCst.MEDIUM_FONT);
			statusCountPanel.add(conflictsLabel);
			statusCountPanel.add(new JLabel(UtilsIconLibrary.CONFLICT_ICON));
			statusCountPanel.add(Box.createRigidArea(new Dimension(3, 16)));
			resolvedConflictsLabel = new JLabel("8");
			resolvedConflictsLabel.setFont(FlexoCst.MEDIUM_FONT);
			statusCountPanel.add(resolvedConflictsLabel);
			statusCountPanel.add(new JLabel(FPSIconLibrary.RESOLVED_CONFLICT_ICON));
			statusCountPanel.add(Box.createRigidArea(new Dimension(3, 16)));
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
			if (getSharedProject() != null) {
				statusLabel.setText(getSharedProject().getCVSModule().getModuleName() + " - "
						+ getSharedProject().getLocalDirectory().getAbsolutePath());
				locallyModifiedLabel.setForeground(Color.BLACK);
				locallyModifiedLabel.setText("" + getSharedProject().getLocallyModifiedCount());
				remoteModifiedLabel.setForeground(Color.BLACK);
				remoteModifiedLabel.setText("" + getSharedProject().getRemotelyModifiedCount());
				conflictsLabel.setForeground(Color.BLACK);
				conflictsLabel.setText("" + getSharedProject().getConflictsCount());
				resolvedConflictsLabel.setForeground(Color.BLACK);
				resolvedConflictsLabel.setText("" + getSharedProject().getResolvedConflictsCount());
			} else {
				statusLabel.setText(FlexoLocalization.localizedForKey("no_project_loaded"));
				locallyModifiedLabel.setForeground(Color.GRAY);
				locallyModifiedLabel.setText("-");
				remoteModifiedLabel.setForeground(Color.GRAY);
				remoteModifiedLabel.setText("-");
				conflictsLabel.setForeground(Color.GRAY);
				conflictsLabel.setText("-");
				resolvedConflictsLabel.setForeground(Color.GRAY);
				resolvedConflictsLabel.setText("-");
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

	@Override
	public void switchToPerspective(FlexoPerspective perspective) {
		// logger.info("Selection="+getSelectionManager().getSelection());
		Vector<FlexoModelObject> selection = (Vector<FlexoModelObject>) getSelectionManager().getSelection().clone();
		super.switchToPerspective(perspective);
		if (perspective instanceof FPSPerspective) {
			((FPSPerspective) perspective).setFilters();
			getSharedProjectBrowser().update();
		}
		getSelectionManager().setSelectedObjects(selection);
		// logger.info("Selection="+getSelectionManager().getSelection());
	}

	@Override
	public String getWindowTitleforObject(FlexoObject object) {
		if (object instanceof CVSFile) {
			CVSFile cvsFile = (CVSFile) object;
			return cvsFile.getFileName() + (cvsFile.isEdited() ? "[" + FlexoLocalization.localizedForKey("edited") + "]" : "");
		}
		return null;
	}

	public synchronized boolean handleAuthenticationException(FlexoAuthentificationException exception) {
		CVSRepository repository = exception.getRepository();
		if (repository.isEnabled()) {
			return true;
		}
		ReadOnlyTextFieldParameter userName = new ReadOnlyTextFieldParameter("userName", "user_name", repository.getUserName() == null ? ""
				: repository.getUserName(), 20);
		TextFieldParameter passwd = new TextFieldParameter("passwd", "password", repository.getPassword() == null ? ""
				: repository.getPassword());
		passwd.setIsPassword(true);
		CheckboxParameter storePasswd = new CheckboxParameter("storePasswd", "store_password", repository.getStorePassword());

		AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null, repository.getName() + " : "
				+ FlexoLocalization.localizedForKey("authentification_failed"), FlexoLocalization.localizedForKey("reenter_password"),
				userName, passwd, storePasswd);
		if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
			repository.setUserName(userName.getValue());
			repository.setPassword(passwd.getValue(), repository.getConnectionType() == ConnectionType.PServer);
			repository.setStorePassword(storePasswd.getValue());
			return true;
		} else {
			repository.disconnect();
		}
		return false;
	}

	public synchronized boolean handleUnknownHostException(FlexoUnknownHostException exception) {
		CVSRepository repository = exception.getRepository();
		if (repository.isEnabled()) {
			return true;
		}
		TextFieldParameter host = new TextFieldParameter("host", "host", repository.getHostName() == null ? "" : repository.getHostName());

		AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null, repository.getName() + " : "
				+ FlexoLocalization.localizedForKey("unknown_host"), FlexoLocalization.localizedForKey("reenter_host"), host);
		if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
			repository.setHostName(host.getValue());
			return true;
		} else {
			repository.disconnect();
		}
		return false;
	}

}
