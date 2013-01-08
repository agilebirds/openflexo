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
package org.openflexo.components;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import org.openflexo.AdvancedPrefs;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.utils.FlexoFileChooserUtils;
import org.openflexo.view.FlexoFrame;

/**
 * Abstract component allowing to choose a Flexo Project
 * 
 * @author sguerin
 */
public abstract class ProjectChooserComponent {
	private static final Logger logger = FlexoLogger.getLogger(ProjectChooserComponent.class.getPackage().getName());

	private enum ImplementationType {
		JFileChooserImplementation, FileDialogImplementation
	}

	private static ImplementationType getImplementationType() {
		return ToolBox.getPLATFORM() == ToolBox.MACOS ? ImplementationType.FileDialogImplementation
				: ImplementationType.JFileChooserImplementation;
		// return ImplementationType.JFileChooserImplementation;
		// return ImplementationType.FileDialogImplementation;
	}

	private FileDialog fileDialog;
	private JFileChooser fileChooser;
	private Window owner;
	private String approveButtonText;

	public ProjectChooserComponent(Window owner) {
		super();
		this.owner = owner;
		if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
			buildAsJFileChooser();
		} else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			buildAsFileDialog();
		}
	}

	private Component buildAsJFileChooser() {
		fileChooser = FlexoFileChooser.getFileChooser(null);
		FileFilter[] ff = fileChooser.getChoosableFileFilters();
		for (int i = 0; i < ff.length; i++) {
			FileFilter filter = ff[i];
			fileChooser.removeChoosableFileFilter(filter);
		}
		fileChooser.setCurrentDirectory(AdvancedPrefs.getLastVisitedDirectory());
		fileChooser.setDialogTitle(ToolBox.getPLATFORM() == ToolBox.MACOS ? FlexoLocalization.localizedForKey("select_a_prj_file")
				: FlexoLocalization.localizedForKey("select_a_prj_directory"));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setFileFilter(FlexoFileChooserUtils.PROJECT_FILE_FILTER);
		fileChooser.setFileView(FlexoFileChooserUtils.PROJECT_FILE_VIEW);
		return fileChooser;
	}

	private Component buildAsFileDialog() {
		if (owner == null) {
			owner = new JFrame();
		}
		if (owner instanceof Frame) {
			fileDialog = new FileDialog((Frame) owner);
		} else if (owner instanceof Dialog) {
			fileDialog = new FileDialog((Dialog) owner);
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Owner of the project chooser is not a Dialog nor a Frame");
			}
			fileDialog = new FileDialog(FlexoFrame.getActiveFrame());
		}
		try {
			fileDialog.setDirectory(AdvancedPrefs.getLastVisitedDirectory().getCanonicalPath());
		} catch (Throwable t) {
			t.printStackTrace();
		}
		fileDialog.setTitle(ToolBox.getPLATFORM() == ToolBox.MACOS ? FlexoLocalization.localizedForKey("select_a_prj_file")
				: FlexoLocalization.localizedForKey("select_a_prj_directory"));
		fileDialog.setFilenameFilter(FlexoFileChooserUtils.PROJECT_FILE_NAME_FILTER);
		return fileDialog;
	}

	protected void setApproveButtonText(String text) {
		approveButtonText = text;
	}

	public Container getComponent() {
		if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			return fileDialog;
		} else {
			return fileChooser;
		}
	}

	public void setTitle(String title) {
		if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			fileDialog.setTitle(title);
		} else {
			fileChooser.setDialogTitle(title);
		}
	}

	public void setOpenMode() {
		if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			fileDialog.setMode(FileDialog.LOAD);
		} else {
			fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		}
	}

	public void setSaveMode() {
		if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			fileDialog.setMode(FileDialog.SAVE);
		} else {
			fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		}
	}

	public int showOpenDialog() throws HeadlessException {
		if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			fileDialog.setMode(FileDialog.LOAD);
			fileDialog.setVisible(true);
			if (fileDialog.getFile() == null) {
				return JFileChooser.CANCEL_OPTION;
			} else {
				return JFileChooser.APPROVE_OPTION;
			}
		}
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		if (approveButtonText != null) {
			fileChooser.setApproveButtonText(approveButtonText);
		}
		return fileChooser.showDialog(owner, null);
	}

	public int showSaveDialog() throws HeadlessException {
		if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			fileDialog.setMode(FileDialog.SAVE);
			fileDialog.setTitle(FlexoLocalization.localizedForKey("set_name_for_new_prj_in_selected_directory"));
			fileDialog.setVisible(true);
			if (fileDialog.getFile() == null) {
				return JFileChooser.CANCEL_OPTION;
			} else {
				return JFileChooser.APPROVE_OPTION;
			}
		}
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		fileChooser.setDialogTitle(FlexoLocalization.localizedForKey("set_name_for_new_prj_in_selected_directory"));
		if (approveButtonText != null) {
			fileChooser.setApproveButtonText(approveButtonText);
		}
		return fileChooser.showDialog(owner, null);
	}

	public File getSelectedFile() {
		if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			if (fileDialog.getFile() != null) {
				return new File(fileDialog.getDirectory(), fileDialog.getFile());
			} else {
				return null;
			}
		} else {
			return fileChooser.getSelectedFile();
		}
	}

	public void setSelectedFile(File selectedFile) {
		if (getImplementationType() == ImplementationType.FileDialogImplementation) {
			if (selectedFile != null) {
				fileDialog.setDirectory(selectedFile.getParentFile().getAbsolutePath());
				fileDialog.setFile(selectedFile.getName());
			} else {
				fileDialog.setDirectory(null);
				fileDialog.setFile(null);
			}
		} else {
			fileChooser.setSelectedFile(selectedFile);
		}
	}
}
