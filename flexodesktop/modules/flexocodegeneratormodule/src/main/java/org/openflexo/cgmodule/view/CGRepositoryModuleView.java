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
package org.openflexo.cgmodule.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.openflexo.cgmodule.controller.GeneratorController;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.dm.CGRepositoryConnected;
import org.openflexo.foundation.cg.dm.CGRepositoryDisconnected;
import org.openflexo.foundation.cg.dm.LogAdded;
import org.openflexo.generator.action.GenerateWAR;
import org.openflexo.generator.action.SynchronizeRepositoryCodeGeneration;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.swing.JConsole;
import org.openflexo.swing.JConsoleOutputStream;
import org.openflexo.toolbox.LogListener;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;


/**
 * 
 * @author sylvain
 */
public class CGRepositoryModuleView extends JPanel implements ModuleView<CGRepository>, FlexoObserver, LogListener
{

    protected static final Logger logger = Logger.getLogger(CGRepositoryModuleView.class.getPackage().getName());

    protected GeneratorController controller;
    protected CGRepository codeRepository;
    
    protected JLabel generationPath;
    
    private ConsolePanel consolePanel;
    
    protected JConsole console;
    private boolean listenerAdded = false;
    
    protected JButton chooseFileButton;
    protected JButton chooseReaderFileButton;
    protected JButton chooseWarLocationButton;
    protected JButton generateButton;
    protected JButton warButton;

    public class ConsolePanel extends JPanel {
    	protected JButton clearButton;
    	
    	public ConsolePanel(JConsole console) {
    		super(new BorderLayout());
    		JPanel north = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    		clearButton = new JButton(GeneratorIconLibrary.CANCEL_ICON);
    		clearButton.setText(FlexoLocalization.localizedForKey("clear",clearButton));
    		clearButton.setToolTipText(FlexoLocalization.localizedForKey("clear_console"));
    		clearButton.addActionListener(new ActionListener(){
    			@Override
				public void actionPerformed(ActionEvent e) {
    				CGRepositoryModuleView.this.console.clear();
    			}
    		});
    		north.add(clearButton);
    		north.validate();
    		add(console, BorderLayout.CENTER);
    		add(north, BorderLayout.NORTH);
    		validate();
		}
    }
    
    /**
     * @param _process
     * 
     */
    public CGRepositoryModuleView(CGRepository repository, GeneratorController ctrl)
    {
        super(new BorderLayout());
        codeRepository = repository;
        repository.addObserver(this);
        this.controller = ctrl;
        
        JPanel bigButtonsPanel = new JPanel(new GridLayout(repository.includeReader()?3:2,1));
        
        JPanel firstPanel = new JPanel(new FlowLayout());
        chooseFileButton = new GeneratorButton(GeneratorIconLibrary.BACKUP_ICON);
        chooseFileButton.setText(codeRepository.getDirectory()!=null?codeRepository.getDirectory().getAbsolutePath():FlexoLocalization.localizedForKey("undefined"));
        chooseFileButton.setPreferredSize(new Dimension(400,80));
        chooseFileButton.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                FlexoFileChooser fileChooser = new FlexoFileChooser(SwingUtilities.getWindowAncestor(chooseFileButton));
                fileChooser.setCurrentDirectory(codeRepository.getDirectory());
                fileChooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                controller.dismountWindowsOnTop(null);
                int returnVal = fileChooser.showDialog(FlexoLocalization.localizedForKey("select"));
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                	codeRepository.setDirectory(fileChooser.getSelectedFile());
                }
                controller.remountWindowsOnTop();
            }
        });
        firstPanel.add(chooseFileButton);
        generateButton = new GeneratorButton(GeneratorIconLibrary.WOLIPS_ICON);
        generateButton.setText(FlexoLocalization.localizedForKey("generateButton", generateButton));
        generateButton.addActionListener(new ActionListener() {
        	@Override
			public void actionPerformed(ActionEvent e){
                if (SynchronizeRepositoryCodeGeneration.actionType.isEnabled(codeRepository, null,controller.getEditor())) {
                    SynchronizeRepositoryCodeGeneration action = SynchronizeRepositoryCodeGeneration.actionType.makeNewAction(codeRepository, null,controller.getEditor());
                    action.doAction();
                } else {
					warButton.setEnabled(false);
				}
        	}
        });
        firstPanel.add(generateButton);
        bigButtonsPanel.add(firstPanel);
        JPanel secondPanel = new JPanel(new FlowLayout());
        chooseWarLocationButton = new GeneratorButton(GeneratorIconLibrary.BACKUP_ICON);
        chooseWarLocationButton.setText(codeRepository.getWarDirectory()!=null?codeRepository.getWarDirectory().getAbsolutePath():FlexoLocalization.localizedForKey("undefined"));
        chooseWarLocationButton.setPreferredSize(new Dimension(400,80));
        chooseWarLocationButton.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
            	FlexoFileChooser fileChooser = new FlexoFileChooser(SwingUtilities.getWindowAncestor(warButton));
                fileChooser.setCurrentDirectory(codeRepository.getWarDirectory());
                fileChooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                controller.dismountWindowsOnTop(null);
                int returnVal = fileChooser.showDialog(FlexoLocalization.localizedForKey("select"));
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                	codeRepository.setWarDirectory(fileChooser.getSelectedFile());
                }
                controller.remountWindowsOnTop();
            }
        });
        secondPanel.add(chooseWarLocationButton);
        warButton = new GeneratorButton(GeneratorIconLibrary.TOMCAT_ICON);
        warButton.setText(FlexoLocalization.localizedForKey("generateWarButton", warButton));
        warButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e){
                if (GenerateWAR.actionType.isEnabled(codeRepository, null,controller.getEditor())) {
                    GenerateWAR action = GenerateWAR.actionType.makeNewAction(codeRepository, null,controller.getEditor());
                    action.setCustomErrStream(new JConsoleOutputStream(console,Color.RED));
                    action.setCustomOutStream(new JConsoleOutputStream(console,Color.BLACK));
                    
                    action.doAction();
                } else {
					warButton.setEnabled(false);
				}
            }
        });
        warButton.setEnabled(GenerateWAR.actionType.isEnabled(codeRepository, null, controller.getEditor()));
        secondPanel.add(warButton);
        bigButtonsPanel.add(secondPanel);
        if (repository.includeReader()) {
        	chooseReaderFileButton = new GeneratorButton(GeneratorIconLibrary.BACKUP_ICON);
        	chooseReaderFileButton.setText(codeRepository.getReaderRepository().getDirectory()!=null?codeRepository.getReaderRepository().getDirectory().getAbsolutePath():FlexoLocalization.localizedForKey("undefined"));
        	chooseReaderFileButton.setPreferredSize(new Dimension(400,80));
        	chooseReaderFileButton.addActionListener(new ActionListener() {

                @Override
				public void actionPerformed(ActionEvent e)
                {
                    FlexoFileChooser fileChooser = new FlexoFileChooser(SwingUtilities.getWindowAncestor(chooseReaderFileButton));
                    fileChooser.setCurrentDirectory(codeRepository.getReaderRepository().getDirectory());
                    fileChooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    controller.dismountWindowsOnTop(null);
                    int returnVal = fileChooser.showDialog(FlexoLocalization.localizedForKey("select"));
                    if(returnVal == JFileChooser.APPROVE_OPTION) {
                    	codeRepository.getReaderRepository().setDirectory(fileChooser.getSelectedFile());
                    }
                    controller.remountWindowsOnTop();
                }
            });
        }
        
	    //chooseFileButton.setEnabled(repository.isEnabled());
	    //chooseWarLocationButton.setEnabled(repository.isEnabled());
	    generateButton.setEnabled(SynchronizeRepositoryCodeGeneration.actionType.isEnabled(repository,null,controller.getEditor()));
	    console = new JConsole();
        if(controller.getProjectGenerator(codeRepository)!=null) {
        	controller.getProjectGenerator(codeRepository).addToLogListeners(this);
        	listenerAdded = true;
        }
        if (codeRepository.isEnabled()) {
        	//console.setText(controller.getProjectGenerator(codeRepository).getLogs().toString());
        }
        else if(console!=null){
        	console.clear();
        	console.log(FlexoLocalization.localizedForKey("repository_disconnected"),Color.BLUE);
        }
        /*console.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        console.setFont(FlexoCst.CODE_FONT);
        console.setForeground(Color.DARK_GRAY);
        console.setEditable(false);*/
       
        add(bigButtonsPanel,BorderLayout.NORTH);
        //add(new JScrollPane(console),BorderLayout.CENTER);
        if(console!=null) {
        	consolePanel = new ConsolePanel(console);
        	add(consolePanel,BorderLayout.CENTER);
        }
               
        validate();
        //repaint();
    }

    private class GeneratorButton extends JButton
    {
    	public GeneratorButton(Icon icon){
    		super(icon);
            if (ToolBox.getPLATFORM()==ToolBox.MACOS) {
                setBorder(BorderFactory.createEtchedBorder());
                setBackground(Color.WHITE);
            }
    		setHorizontalTextPosition(SwingConstants.CENTER);
            setVerticalTextPosition(SwingConstants.BOTTOM);
            setPreferredSize(new Dimension(100,80));
    	}

		@Override
		public void setText(String text) {
			if(text==null) {
				super.setText("Select a file");
			} else {
				super.setText(text);
			}
		}
    	
		/*public void setEnabled(boolean aBoolean)
		{
			super.setEnabled(aBoolean);
			setForeground(aBoolean?Color.BLACK:Color.GRAY);
		}*/
    }
    
    /**
     * Overrides getPerspective
     * @see org.openflexo.view.ModuleView#getPerspective()
     */
    @Override
	public FlexoPerspective<FlexoModelObject> getPerspective()
    {
        return controller.CODE_GENERATOR_PERSPECTIVE;
    }

 	@Override
	public void update(FlexoObservable observable, DataModification dataModification)
	{
		if ((observable == codeRepository) || (observable == codeRepository.getReaderRepository())) {
			if ((dataModification.propertyName() != null) && dataModification.propertyName().equals("warDirectory")){
				chooseWarLocationButton.setText(codeRepository.getWarDirectory()!=null?codeRepository.getWarDirectory().getAbsolutePath():FlexoLocalization.localizedForKey("undefined"));
			} 
			else if ((dataModification.propertyName() != null) && dataModification.propertyName().equals("directory")){
		        chooseFileButton.setText(codeRepository.getDirectory()!=null?codeRepository.getDirectory().getAbsolutePath():FlexoLocalization.localizedForKey("undefined"));
			}
			else if (dataModification instanceof LogAdded) {
				//console.setText(controller.getProjectGenerator(codeRepository).getLogs().toString());
			}
			else if (dataModification instanceof CGRepositoryConnected) {
				if (codeRepository.isEnabled()) {
					if(!listenerAdded) {
						controller.getProjectGenerator(codeRepository).addToLogListeners(this);
						listenerAdded = true;
					}
					console.log(FlexoLocalization.localizedForKey("repository_connected"), Color.BLUE);
				}
			}
			else if (dataModification instanceof CGRepositoryDisconnected) {
				if (!codeRepository.isEnabled()) {
					if(listenerAdded) {
						controller.getProjectGenerator(codeRepository).removeFromLogListeners(this);
						listenerAdded = false;
					}
					console.log(FlexoLocalization.localizedForKey("repository_disconnected"), Color.BLUE);
				}
			}
		}
        generateButton.setEnabled(SynchronizeRepositoryCodeGeneration.actionType.isEnabled(codeRepository,null,controller.getEditor()));
        warButton.setEnabled(GenerateWAR.actionType.isEnabled(codeRepository, null, controller.getEditor()));
	}
    
	/**
	 * Overrides getRepresentedObject
	 * @see org.openflexo.view.ModuleView#getRepresentedObject()
	 */
	@Override
	public CGRepository getRepresentedObject()
    {
        return codeRepository;
    }

     /**
     * Overrides delete
     * @see org.openflexo.view.ModuleView#deleteModuleView()
     */
    @Override
	public void deleteModuleView()
    {
    	controller.removeModuleView(this);
    	codeRepository.deleteObserver(this);
    }

    /**
     * Overrides willShow
     * @see org.openflexo.view.ModuleView#willShow()
     */
    @Override
	public void willShow()
    {
    }

    /**
     * Overrides willHide
     * @see org.openflexo.view.ModuleView#willHide()
     */
    @Override
	public void willHide()
    {
    }

	/**
	 * Returns flag indicating if this view is itself responsible for scroll management
	 * When not, Flexo will manage it's own scrollbar for you
	 * 
	 * @return
	 */
	@Override
	public boolean isAutoscrolled() 
	{
		return true;
	}

	public JConsole getConsole() {
		return console;
	}

	@Override
	public void err(String line) {
		console.log(line, Color.RED);
	}
	
	@Override
	public void warn(String line) {
		console.log(line, Color.ORANGE);
	}

	@Override
	public void log(String line) {
		console.log(line, Color.BLACK);
	}

}