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
package org.openflexo.rm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.components.AskForSaveResources;
import org.openflexo.components.ProgressWindow;
import org.openflexo.components.ProjectResourcesReviewer;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.rm.FlexoGeneratedResource;
import org.openflexo.foundation.rm.FlexoImportedResource;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.SaveResourceExceptionList;
import org.openflexo.foundation.rm.SaveResourcePermissionDeniedException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.FlexoDialog;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.FlexoController;

/**
 * Dialog displaying Resource Management Model
 * 
 * @author sguerin
 * 
 */
public class ResourceManagerWindow extends FlexoDialog implements ChangeListener {

	static final Logger logger = Logger.getLogger(ResourceManagerWindow.class.getPackage().getName());

	protected ResourceManagerPanel _storageResourcesPanel;
	protected ResourceManagerPanel _importedResourcesPanel;
	protected ResourceManagerPanel _generatedResourcesPanel;
	private JTabbedPane tabbedPane;
	protected FlexoProject _project;
	private JButton saveSelectedButton;
	private JButton loadSelectedButton;
	private JButton deleteSelectedButton;
	private JButton saveAllButton;
	private JButton rebuildDependenciesButton;
	private JButton refreshButton;
	private JButton closeButton;

	// private RMViewerController rmViewerController;

	public ResourceManagerWindow(FlexoProject project) {
		super(FlexoFrame.getActiveFrame(), false);
		setTitle(FlexoLocalization.localizedForKey("resource_manager"));
		getContentPane().setLayout(new BorderLayout());
		_storageResourcesPanel = new ResourceManagerPanel(project, new ResourceManagerModel.StorageResourceModel(project), this);
		_importedResourcesPanel = new ResourceManagerPanel(project, new ResourceManagerModel.ImportedResourceModel(project), this);
		_generatedResourcesPanel = new ResourceManagerPanel(project, new ResourceManagerModel.GeneratedResourceModel(project), this);
		_project = project;

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());

		saveSelectedButton = new JButton(FlexoLocalization.localizedForKey("save"));
		loadSelectedButton = new JButton(FlexoLocalization.localizedForKey("load"));
		deleteSelectedButton = new JButton(FlexoLocalization.localizedForKey("delete"));
		saveAllButton = new JButton(FlexoLocalization.localizedForKey("save_all"));
		rebuildDependenciesButton = new JButton(FlexoLocalization.localizedForKey("rebuild_dependancies"));
		refreshButton = new JButton(FlexoLocalization.localizedForKey("refresh"));
		closeButton = new JButton(FlexoLocalization.localizedForKey("close"));

		saveSelectedButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (getActivePanel() == _storageResourcesPanel) {
					if (_storageResourcesPanel.getSelectedResource() != null) {
						try {
							((FlexoStorageResource) _storageResourcesPanel.getSelectedResource()).saveResourceData();
						} catch (SaveResourceException e1) {
							// Warns about the exception
							if (logger.isLoggable(Level.WARNING)) {
								logger.warning("Exception raised: " + e1.getClass().getName() + ". See console for details.");
							}
							e1.printStackTrace();
						}
					}
				}
			}
		});

		loadSelectedButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (getActivePanel().getSelectedResource() != null) {
					try {
						FlexoResource resourceToLoad = getActivePanel().getSelectedResource();
						if (resourceToLoad instanceof FlexoStorageResource) {
							((FlexoStorageResource) resourceToLoad).getResourceData();
						} else if (resourceToLoad instanceof FlexoImportedResource) {
							((FlexoImportedResource) resourceToLoad).importResourceData();
						} else if (resourceToLoad instanceof FlexoGeneratedResource) {
							((FlexoGeneratedResource) resourceToLoad).getGeneratedResourceData();
						}
						getActivePanel().getRMModel().fireTableDataChanged();
					} catch (FlexoException exception) {
						// Warns about the exception
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Exception raised: " + exception.getClass().getName() + ". See console for details.");
						}
						exception.printStackTrace();
					}
				}
			}
		});

		deleteSelectedButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (getActivePanel().getSelectedResource() != null) {
					getActivePanel().getSelectedResource().delete();
					getActivePanel().getRMModel().fireTableDataChanged();
				}
			}
		});

		saveAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ProjectResourcesReviewer reviewer = new ProjectResourcesReviewer(_project);
				if (reviewer.getStatus() == AskForSaveResources.SAVE) {
					try {
						reviewer.saveSelection();
					} catch (SaveResourcePermissionDeniedException e1) {
						e1.printStackTrace();
						if (e1.getDeprecatedFileResource().getFile().isDirectory()) {
							FlexoController.showError(FlexoLocalization.localizedForKey("permission_denied"),
									FlexoLocalization.localizedForKey("project_was_not_properly_saved_permission_denied_directory") + "\n"
											+ e1.getDeprecatedFileResource().getFile().getAbsolutePath());
						} else {
							FlexoController.showError(FlexoLocalization.localizedForKey("permission_denied"),
									FlexoLocalization.localizedForKey("project_was_not_properly_saved_permission_denied_file") + "\n"
											+ e1.getDeprecatedFileResource().getFile().getAbsolutePath());
						}
					} catch (SaveResourceExceptionList e1) {
						e1.printStackTrace();
						FlexoController.showError(FlexoLocalization.localizedForKey("error_during_saving") + "\n" + e1.errorFilesList());
					}
				}
				_importedResourcesPanel.getRMModel().fireTableDataChanged();
			}
		});

		rebuildDependenciesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!ProgressWindow.hasInstance()) {
					ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("rebuild_dependancies"), _project.getResources()
							.size());
				}
				_project.rebuildDependencies(ProgressWindow.instance());
				ProgressWindow.hideProgressWindow();
				_importedResourcesPanel.getRMModel().fireTableDataChanged();
				_importedResourcesPanel.getRMModel().fireTableDataChanged();
				_generatedResourcesPanel.getRMModel().fireTableDataChanged();
			}
		});

		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_importedResourcesPanel.getRMModel().refresh();
				_importedResourcesPanel.getRMModel().refresh();
				_generatedResourcesPanel.getRMModel().refresh();
				_importedResourcesPanel.getRMModel().fireTableDataChanged();
				_importedResourcesPanel.getRMModel().fireTableDataChanged();
				_generatedResourcesPanel.getRMModel().fireTableDataChanged();
			}
		});

		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		controlPanel.add(saveSelectedButton);
		controlPanel.add(loadSelectedButton);
		controlPanel.add(deleteSelectedButton);
		controlPanel.add(saveAllButton);
		controlPanel.add(rebuildDependenciesButton);
		controlPanel.add(refreshButton);
		controlPanel.add(closeButton);

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BorderLayout());

		// rmViewerController = new RMViewerController(project);

		tabbedPane = new JTabbedPane();
		tabbedPane.addChangeListener(this);
		tabbedPane.add(FlexoLocalization.localizedForKey("storage_resources"), _storageResourcesPanel);
		tabbedPane.add(FlexoLocalization.localizedForKey("imported_resources"), _importedResourcesPanel);
		tabbedPane.add(FlexoLocalization.localizedForKey("generated_resources"), _generatedResourcesPanel);
		// tabbedPane.add(FlexoLocalization.localizedForKey("viewer"), rmViewerController.getMainView());

		contentPanel.add(tabbedPane, BorderLayout.CENTER);
		contentPanel.add(controlPanel, BorderLayout.SOUTH);

		getContentPane().add(contentPanel, BorderLayout.CENTER);

		update();

		setModal(false);
		setSize(1000, 200);
		validate();
		pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2);
		setVisible(true);
	}

	public FlexoProject getProject() {
		return _project;
	}

	@Override
	public void dispose() {
		super.dispose();
		_importedResourcesPanel.getRMModel().delete();
		_importedResourcesPanel.getRMModel().delete();
		_generatedResourcesPanel.getRMModel().delete();
	}

	protected void update() {
		saveSelectedButton.setEnabled(getActivePanel() != null && getActivePanel() == _storageResourcesPanel
				&& getActivePanel().getSelectedResource() != null);
		loadSelectedButton.setEnabled(getActivePanel() != null && getActivePanel().getSelectedResource() != null);
		deleteSelectedButton.setEnabled(getActivePanel() != null && getActivePanel().getSelectedResource() != null);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		update();
	}

	protected ResourceManagerPanel getActivePanel() {
		if (tabbedPane.getSelectedComponent() instanceof ResourceManagerPanel) {
			return (ResourceManagerPanel) tabbedPane.getSelectedComponent();
		}
		return null;
	}

}
