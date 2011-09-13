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
package org.openflexo.fib.view.widget;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBFile;
import org.openflexo.fib.view.FIBWidgetView;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents a widget able to edit a File, or a String representing a File or a
 * StringConvertable object
 * 
 * @author sguerin
 */
public class FIBFileWidget extends FIBWidgetView<FIBFile,JTextField,File> {

    static final Logger logger = Logger.getLogger(FIBFileWidget.class.getPackage().getName());

    protected JPanel _mySmallPanel;

    protected JButton _chooseButton;

    protected JTextField _currentDirectoryLabel;

    protected File _file = null;
    
    protected FIBFile.FileMode mode;
    protected String filter;
    protected String title;
    protected Boolean isDirectory;
    protected File defaultDirectory;
    protected int columns;
   
	private static final int DEFAULT_COLUMNS = 10;

    public FIBFileWidget(FIBFile model, FIBController controller)
    {
        super(model,controller);

        mode = (model.mode != null ? model.mode : FIBFile.FileMode.OpenMode);
        filter = model.filter;
        title = model.title;
        isDirectory = model.isDirectory;
        defaultDirectory = (model.defaultDirectory != null ? model.defaultDirectory : new File(System.getProperty("user.dir")));
        
        	
       _mySmallPanel = new JPanel(new BorderLayout());
        _chooseButton = new JButton();
        _chooseButton.setText(FlexoLocalization.localizedForKey("choose", _chooseButton));
        addActionListenerToChooseButton();
        _currentDirectoryLabel = new JTextField("");
        _currentDirectoryLabel.setColumns(model.columns != null ? model.columns : DEFAULT_COLUMNS);
        _currentDirectoryLabel.setMinimumSize(MINIMUM_SIZE);
        _currentDirectoryLabel.setPreferredSize(MINIMUM_SIZE);
        _currentDirectoryLabel.setEditable(false);
        _currentDirectoryLabel.setEnabled(true);
        _currentDirectoryLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        _mySmallPanel.add(_currentDirectoryLabel, BorderLayout.CENTER);
        _mySmallPanel.add(_chooseButton, BorderLayout.EAST);
        _mySmallPanel.addFocusListener(this);
        setFile(null);

    }

    protected void configureFileChooser(FlexoFileChooser chooser)
    {
    	if (!isDirectory) {
    		//System.out.println("Looking for files");
    		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    		chooser.setDialogTitle(StringUtils.isEmpty(title)?FlexoLocalization.localizedForKey("select_a_file"):FlexoLocalization.localizedForKey(getController().getLocalizer(),title));
    		chooser.setFileFilterAsString(filter);
    		chooser.setDialogType(mode.getMode());
    	    System.setProperty("apple.awt.fileDialogForDirectories", "false");
    	}
    	else {
    		//System.out.println("Looking for directories");
    		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    		chooser.setDialogTitle(StringUtils.isEmpty(title)?FlexoLocalization.localizedForKey("select_directory"):FlexoLocalization.localizedForKey(getController().getLocalizer(),title));
    		chooser.setFileFilterAsString(filter);
    		chooser.setDialogType(mode.getMode());
    		System.setProperty("apple.awt.fileDialogForDirectories", "true");
    	}       
    }
    
    public void addActionListenerToChooseButton()
    {
        _chooseButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e)
            {
            	Window parent = SwingUtilities.getWindowAncestor(_chooseButton);
                // get destination directory
            	FlexoFileChooser chooser = new FlexoFileChooser(parent);
                if (_file != null) {
                    chooser.setCurrentDirectory(_file);
                    if(!_file.isDirectory()){
                    	chooser.setSelectedFile(_file);
                    }
                }
                configureFileChooser(chooser);
                
        		boolean parentWasAlwaysOnTop = false;
        		if (ToolBox.getPLATFORM()==ToolBox.MACOS) {
	        		if (parent != null && parent.isAlwaysOnTop()) {
	        			if (logger.isLoggable(Level.FINE))
	        				logger.fine("Found parent AlwaysOnTopState to true for "+parent);
	        			parentWasAlwaysOnTop = true;
	        			parent.setAlwaysOnTop(false);
	        		}
        		}
        		
        		switch (mode) {
				case OpenMode:
	               	if (chooser.showOpenDialog(_chooseButton) == JFileChooser.APPROVE_OPTION) {
                		// a dir has been picked...

                		try {
                			setFile(chooser.getSelectedFile());
                			updateModelFromWidget();
                		} catch (Exception e1) {
                			e1.printStackTrace();
                 		}
                	} else {
                		// cancelled, return.
                 	}					
					break;
					
				case SaveMode:
                	if (chooser.showSaveDialog(_chooseButton) == JFileChooser.APPROVE_OPTION) {
                		// a dir has been picked...
                		try {
                			setFile(chooser.getSelectedFile());
                			updateModelFromWidget();
                		} catch (Exception e1) {
                			e1.printStackTrace();
                		}
                	} else {
                		// cancelled, return.
                	}
					break;

				default:
					break;
				}
        		
                 if (parent!=null && parentWasAlwaysOnTop) {
                	parent.setAlwaysOnTop(true);
                }
            }
        });
    }

    public void performUpdate(Object newValue)
    {
        if (newValue instanceof File) {
            setFile((File) newValue);
        } else if (newValue instanceof String) {
            setFile(new File((String) newValue));
        }
    }

   @Override
public synchronized boolean updateWidgetFromModel()
   {
	   if (notEquals(getValue(), _file)) {
		   widgetUpdating = true;
		   if (getValue() instanceof File) setFile(getValue());
		   else if (getValue() == null) setFile(null);
		   widgetUpdating = false;
		   return true;
	   }
	   return false;
   }

    /**
     * Update the model given the actual state of the widget
     */
    @Override
	public synchronized boolean updateModelFromWidget()
    {
    	if (notEquals(getValue(), _file)) {
    		modelUpdating = true;
    		setValue(_file);
    		modelUpdating = false;
    		return true;
    	}
    	return false;
    }

    @Override
	public JPanel getJComponent()
    {
        return _mySmallPanel;
    }

	@Override
	public JTextField getDynamicJComponent()
	{
		return _currentDirectoryLabel;
	}

    protected void setFile(File aFile)
    {
        _file = aFile;
        if (_file != null) {
        	_currentDirectoryLabel.setEnabled(true);
            _currentDirectoryLabel.setText(_file.getAbsolutePath());
            _currentDirectoryLabel.setToolTipText(_file.getAbsolutePath());
        }
        else {
        	_currentDirectoryLabel.setEnabled(false);
            _currentDirectoryLabel.setText(FlexoLocalization.localizedForKey("no_file"));
        }
    }

}
