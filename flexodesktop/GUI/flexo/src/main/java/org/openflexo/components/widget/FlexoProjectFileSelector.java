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
package org.openflexo.components.widget;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.swing.TextFieldCustomPopup;

/**
 * Widget allowing to select a Project file
 * 
 * @author sguerin
 * 
 */
public class FlexoProjectFileSelector extends TextFieldCustomPopup<FlexoProjectFile> {

	public static final Font NORMAL_FONT = new Font("SansSerif", Font.PLAIN, 11);

	private static final String EMPTY_STRING = "";

	private FlexoProjectFile _oldValue;

	FileChooserPanel fileChooserPanel;

	private FileFilter _fileFilter;

	int fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES;

	int dialogType = JFileChooser.OPEN_DIALOG;

	private FlexoProject _project;

	public FlexoProjectFileSelector() {
		this((FlexoProjectFile) null);
	}

	public FlexoProjectFileSelector(FileFilter aFileFilter) {
		this(null, aFileFilter);
	}

	public FlexoProjectFileSelector(FlexoProjectFile aFile) {
		super(aFile);
		_oldValue = aFile;
	}

	public FlexoProjectFileSelector(FlexoProjectFile aFile, FileFilter aFileFilter) {
		this(aFile);
		setFileFilter(aFileFilter);
	}

	public FlexoProjectFileSelector(FlexoProjectFile aFile, FileFilter aFileFilter, int aFileSelectionMode) {
		this(aFile);
		setFileFilter(aFileFilter);
		fileSelectionMode = aFileSelectionMode;
	}

	public FlexoProjectFileSelector(FlexoProjectFile aFile, FileFilter aFileFilter, int aFileSelectionMode, int aDialogType) {
		this(aFile);
		setFileFilter(aFileFilter);
		fileSelectionMode = aFileSelectionMode;
		dialogType = aDialogType;
	}

	public FlexoProjectFile getEditedFile() {
		return getEditedObject();
	}

	public void setEditedFile(FlexoProjectFile aFile) {
		setEditedObject(aFile);
	}

	public void setProject(FlexoProject aProject) {
		_project = aProject;
	}

	public FlexoProject getProject() {
		return _project;
	}

	@Override
	protected ResizablePanel createCustomPanel(FlexoProjectFile editedObject) {
		fileChooserPanel = new FileChooserPanel((editedObject != null ? editedObject.getFile() : new File(System.getProperty("user.home"))));
		if (_fileFilter != null) {
			fileChooserPanel.setFileFilter(_fileFilter);
		}
		return fileChooserPanel;
	}

	@Override
	public void updateCustomPanel(FlexoProjectFile editedObject) {
		if (fileChooserPanel != null) {
			fileChooserPanel.update(editedObject.getFile());
		}
	}

	@Override
	public String renderedString(FlexoProjectFile editedObject) {
		if (editedObject != null) {
			return editedObject.toString();
		} else {
			return EMPTY_STRING;
		}
	}

	public void setFileFilter(FileFilter aFilter) {
		_fileFilter = aFilter;
		if (fileChooserPanel != null) {
			fileChooserPanel.setFileFilter(aFilter);
		}
	}

	@Override
	protected void openPopup() {
		super.openPopup();
		updateCustomPanel(getEditedObject());
	}

	protected class FileChooserPanel extends ResizablePanel implements ActionListener {
		private JFileChooser fileChooser;

		private Dimension defaultDimension = new Dimension(500, 400);

		void update(File aFile) {
			fileChooser.setSelectedFile(aFile);
		}

		protected FileChooserPanel(File aFile) {
			super();
			fileChooser = new JFileChooser(aFile);
			fileChooser.addActionListener(this);
			fileChooser.setFileSelectionMode(fileSelectionMode);
			fileChooser.setDialogType(dialogType);

			// Build the file chooser Panel
			setLayout(new BorderLayout());
			add(fileChooser, BorderLayout.CENTER);

			fileChooserPanel = this;

			fileChooser.setSize(defaultDimension);
			fileChooser.setMinimumSize(defaultDimension);
			fileChooser.setMaximumSize(defaultDimension);
		}

		public void setFileFilter(FileFilter aFilter) {
			if (aFilter != null) {
				fileChooser.setFileFilter(aFilter);
			}
		}

		@Override
		public Dimension getDefaultSize() {
			return defaultDimension;
		}

		@Override
		public void setPreferredSize(Dimension aDimension) {
			fileChooser.setPreferredSize(aDimension);
		}

		/**
		 * Overrides
		 * 
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
				if (getEditedFile() != null) {
					FlexoProjectFile newFile = (FlexoProjectFile) getEditedFile().clone();
					newFile.setFile(fileChooser.getSelectedFile());
					setEditedFile(newFile);
				} else {
					FlexoProjectFile newFile = new FlexoProjectFile(fileChooser.getSelectedFile(), getProject());
					setEditedFile(newFile);
				}
				apply();
			} else if (e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
				cancel();
			}
		}

	}

	@Override
	public void apply() {
		setRevertValue(getEditedObject());
		closePopup();
	}

	@Override
	public void cancel() {
		setEditedObject(_oldValue);
		closePopup();
	}

	@Override
	public void setRevertValue(FlexoProjectFile oldValue) {
		_oldValue = oldValue;
	}

	public FlexoProjectFile getRevertValue() {
		return _oldValue;
	}

	/*
	private FlexoProject _project;
	private FlexoProjectFile _projectFile;

	public FlexoProjectFileSelector(FlexoProject project, FlexoProjectFile aProjectFile)
	{
	    this(project);
	    _projectFile = aProjectFile;
	    if ((aProjectFile != null) && (aProjectFile.getFile() != null)) {
	        setEditedObject(aProjectFile.getFile());
	    }
	}

	public FlexoProjectFileSelector(FlexoProject project)
	{
	    this();
	    _project = project;
	}

	public void setProject(FlexoProject aProject)
	{
	    _project = aProject;
	}

	public FlexoProjectFileSelector()
	{
	    super();
	    _project = null;
	}

	public void setEditedObject(File aFile)
	{
	    super.setEditedObject(aFile);
	    if (_projectFile == null) {
	        _projectFile = new FlexoProjectFile(aFile,_project);
	    }
	    _projectFile.setFile(aFile);
	}

	public File getEditedFile()
	{
	    return getEditedObject();
	}

	public void setEditedFile(File aFile)
	{
	    setEditedObject(aFile);
	}

	 public void setEditedFlexoProjectFile(FlexoProjectFile editedObject)
	{
	    _projectFile = editedObject;
	    if (editedObject != null) {
	        _project = editedObject.getProject();
	        setEditedFile(editedObject.getFile());
	    }
	}

	public FlexoProjectFile getEditedFlexoProjectFile()
	{
	    if (_projectFile == null) {
	        _projectFile = new FlexoProjectFile(getEditedObject(),_project);
	    }
	   _projectFile.setProject(_project);
	    _projectFile.setFile(getEditedObject());
	    return _projectFile;
	 }

	public String renderedString(File editedObject)
	{
	    if (editedObject != null) {
	        return getEditedFlexoProjectFile().toString();
	    } else {
	        return super.renderedString(editedObject);
	    }
	}

	 */
}
