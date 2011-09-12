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
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.toolbox.ToolBox;


import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;

/**
 * Abstract component allowing to choose a Flexo Project
 * 
 * @author sguerin
 */
public abstract class ProjectChooserComponent
{
    private static final Logger logger = FlexoLogger.getLogger(ProjectChooserComponent.class.getPackage().getName());
    
	private enum ImplementationType
	{
		JFileChooserImplementation,
		FileDialogImplementation
	}
	
	private static ImplementationType getImplementationType()
	{
		return (ToolBox.getPLATFORM() == ToolBox.MACOS?ImplementationType.FileDialogImplementation:ImplementationType.JFileChooserImplementation);
		//return ImplementationType.JFileChooserImplementation;
		//return ImplementationType.FileDialogImplementation;
	}

	private FileDialog _fileDialog;
	private JFileChooser _fileChooser;
	private Window _owner;
	private String approveButtonText;
	
    public ProjectChooserComponent(Window owner)
    {
        super();
        _owner = owner;
        if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
        	buildAsJFileChooser();
        }
        else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
        	buildAsFileDialog();
        }
    }
    
    private Component buildAsJFileChooser()
    {
		_fileChooser = FlexoFileChooser.getFileChooser(null);
    	FileFilter[] ff = _fileChooser.getChoosableFileFilters();
        for (int i = 0; i < ff.length; i++) {
			FileFilter filter = ff[i];
			_fileChooser.removeChoosableFileFilter(filter);
		}
        _fileChooser.setCurrentDirectory(AdvancedPrefs.getLastVisitedDirectory());
        _fileChooser.setDialogTitle(ToolBox.getPLATFORM()==ToolBox.MACOS?FlexoLocalization.localizedForKey("select_a_prj_file"):FlexoLocalization.localizedForKey("select_a_prj_directory"));
        _fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);       
        _fileChooser.setFileFilter(FlexoProject.getFileFilter());
        _fileChooser.setFileView(FlexoProject.getFileView());
        return _fileChooser;
    }
    
    private Component buildAsFileDialog()
    {
       	if (_owner == null) _owner = new JFrame();
        if (_owner instanceof Frame)
            _fileDialog = new FileDialog((Frame)_owner);
        else if (_owner instanceof Dialog)
            _fileDialog = new FileDialog((Dialog)_owner);
        else {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Owner of the project chooser is not a Dialog nor a Frame");
            _fileDialog = new FileDialog(Frame.getFrames()[0]);
        }
		try {
			_fileDialog.setDirectory(AdvancedPrefs.getLastVisitedDirectory().getCanonicalPath());
		} catch (Throwable t) {
		}
		_fileDialog.setTitle(ToolBox.getPLATFORM()==ToolBox.MACOS?FlexoLocalization.localizedForKey("select_a_prj_file"):FlexoLocalization.localizedForKey("select_a_prj_directory"));
		_fileDialog.setFilenameFilter(FlexoProject.getFilenameFilter());
		return _fileDialog;
    }
    
    protected void setApproveButtonText(String text) {
    	approveButtonText = text;
    }
    
    public Component getComponent()
    {
        if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
        	return _fileChooser;
        }
        else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
        	return _fileDialog;
        }
        return null;
   }
    
    public int showOpenDialog() throws HeadlessException
    {
        if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
        	_fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        	if (approveButtonText!=null)
        		_fileChooser.setApproveButtonText(approveButtonText);
        	return _fileChooser.showDialog(_owner, null);
        }
        else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
        	_fileDialog.setMode(FileDialog.LOAD);
        	_fileDialog.setVisible(true);
        	if (_fileDialog.getFile() == null) {
        		return JFileChooser.CANCEL_OPTION;
        	}
        	else {
        		return JFileChooser.APPROVE_OPTION;
        	}
        }
        return JFileChooser.ERROR_OPTION;
    }

    public int showSaveDialog() throws HeadlessException
    {
        if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
        	_fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        	_fileChooser.setDialogTitle(FlexoLocalization.localizedForKey("set_name_for_new_prj_in_selected_directory"));
        	if (approveButtonText!=null)
        		_fileChooser.setApproveButtonText(approveButtonText);
        	return _fileChooser.showDialog(_owner, null);
        }
        else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
        	_fileDialog.setMode(FileDialog.SAVE);
         	_fileDialog.setTitle(FlexoLocalization.localizedForKey("set_name_for_new_prj_in_selected_directory"));
        	_fileDialog.setVisible(true);
        	if (_fileDialog.getFile() == null) {
        		return JFileChooser.CANCEL_OPTION;
        	}
        	else {
        		return JFileChooser.APPROVE_OPTION;
        	}
        }
        return JFileChooser.ERROR_OPTION;
    }

   public File getSelectedFile()
    {
        if (getImplementationType() == ImplementationType.JFileChooserImplementation) {
         	return _fileChooser.getSelectedFile();
        }
        else if (getImplementationType() == ImplementationType.FileDialogImplementation) {
        	if (_fileDialog.getFile() != null) return new File(_fileDialog.getDirectory(),_fileDialog.getFile());
       }
        return null;
   }
}
