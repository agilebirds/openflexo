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
package org.openflexo.sgmodule.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.action.FlexoActionSource;
import org.openflexo.foundation.cg.dm.CGRepositoryConnected;
import org.openflexo.foundation.cg.dm.CGRepositoryDisconnected;
import org.openflexo.foundation.cg.dm.LogAdded;
import org.openflexo.foundation.sg.SourceRepository;
import org.openflexo.generator.action.GenerateSourceCode;
import org.openflexo.generator.action.SynchronizeRepositoryCodeGeneration;
import org.openflexo.generator.action.WriteModifiedGeneratedFiles;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.sgmodule.SGCst;
import org.openflexo.sgmodule.controller.SGController;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.swing.JConsole;
import org.openflexo.toolbox.LogListener;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.listener.FlexoActionButton;


/**
 * 
 * @author sylvain
 */
public class SourceRepositoryModuleView extends JPanel implements ModuleView<SourceRepository>, FlexoObserver, LogListener, FlexoActionSource
{

    protected static final Logger logger = Logger.getLogger(SourceRepositoryModuleView.class.getPackage().getName());

    protected SGController controller;
    protected SourceRepository sourceRepository;
    
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
    				SourceRepositoryModuleView.this.console.clear();
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
    public SourceRepositoryModuleView(SourceRepository repository, SGController ctrl)
    {
        super(new BorderLayout());
        sourceRepository = repository;
        repository.addObserver(this);
        this.controller = ctrl;
        
       /* JPanel firstPanel = new JPanel(new FlowLayout());
        chooseFileButton = new GeneratorButton(new ImageIconResource("Resources/backup.gif"));
        chooseFileButton.setText(sourceRepository.getDirectory()!=null?sourceRepository.getDirectory().getAbsolutePath():FlexoLocalization.localizedForKey("undefined"));
        chooseFileButton.setPreferredSize(new Dimension(400,80));
        chooseFileButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                FlexoFileChooser fileChooser = new FlexoFileChooser(SwingUtilities.getWindowAncestor(chooseFileButton));
                fileChooser.setCurrentDirectory(sourceRepository.getDirectory());
                fileChooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                controller.dismountWindowsOnTop(null);
                int returnVal = fileChooser.showDialog(FlexoLocalization.localizedForKey("select"));
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                	sourceRepository.setDirectory(fileChooser.getSelectedFile());
                }
                controller.remountWindowsOnTop();
            }
        });
        firstPanel.add(chooseFileButton);
        generateButton = new GeneratorButton(new ImageIconResource("Resources/wolips.gif"));
        generateButton.setText(FlexoLocalization.localizedForKey("generateButton", generateButton));
        generateButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e){
                if (SynchronizeRepositoryCodeGeneration.actionType.isEnabled(sourceRepository, null,controller.getEditor())) {
                    SynchronizeRepositoryCodeGeneration action = SynchronizeRepositoryCodeGeneration.actionType.makeNewAction(sourceRepository, null,controller.getEditor());
                    action.doAction();
                } else
                    warButton.setEnabled(false);
        	}
        });
        firstPanel.add(generateButton);
         
	    chooseFileButton.setEnabled(repository.isEnabled());
	    
	    generateButton.setEnabled(SynchronizeRepositoryCodeGeneration.actionType.isEnabled(repository,null,controller.getEditor()));
	   */
        
        console = new JConsole();
        if(controller.getProjectGenerator(sourceRepository)!=null) {
        	controller.getProjectGenerator(sourceRepository).addToLogListeners(this);
        	listenerAdded = true;
        }
        if (sourceRepository.isEnabled()) {
        	//console.setText(controller.getProjectGenerator(codeRepository).getLogs().toString());
        }
        else if(console!=null){
        	console.clear();
        	console.log(FlexoLocalization.localizedForKey("repository_disconnected"),Color.BLUE);
        }
        
        console.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        console.setFont(FlexoCst.CODE_FONT);
        console.setForeground(Color.DARK_GRAY);
        console.setEditable(false);
       
        //add(firstPanel,BorderLayout.NORTH);
        //add(new JScrollPane(console),BorderLayout.CENTER);
        
        add(new ViewHeader(),BorderLayout.NORTH);
        
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
        return controller.CODE_GENERATION_PERSPECTIVE;
    }

 	@Override
	public void update(FlexoObservable observable, DataModification dataModification)
	{
		if (observable == sourceRepository /*|| observable == sourceRepository.getReaderRepository()*/) {
			if (dataModification.propertyName() != null && dataModification.propertyName().equals("warDirectory")){
				//chooseWarLocationButton.setText(sourceRepository.getWarDirectory()!=null?sourceRepository.getWarDirectory().getAbsolutePath():FlexoLocalization.localizedForKey("undefined"));
			} 
			else if (dataModification.propertyName() != null && dataModification.propertyName().equals("directory")){
		        chooseFileButton.setText(sourceRepository.getDirectory()!=null?sourceRepository.getDirectory().getAbsolutePath():FlexoLocalization.localizedForKey("undefined"));
			}
			else if (dataModification instanceof LogAdded) {
				//console.setText(controller.getProjectGenerator(codeRepository).getLogs().toString());
			}
			else if (dataModification instanceof CGRepositoryConnected) {
				if (sourceRepository.isEnabled()) {
					if(!listenerAdded) {
						controller.getProjectGenerator(sourceRepository).addToLogListeners(this);
						listenerAdded = true;
					}
					console.log(FlexoLocalization.localizedForKey("repository_connected"), Color.BLUE);
				}
			}
			else if (dataModification instanceof CGRepositoryDisconnected) {
				if (!sourceRepository.isEnabled()) {
					if(listenerAdded) {
						controller.getProjectGenerator(sourceRepository).removeFromLogListeners(this);
						listenerAdded = false;
					}
					console.log(FlexoLocalization.localizedForKey("repository_disconnected"), Color.BLUE);
				}
			}
		}
        //generateButton.setEnabled(SynchronizeRepositoryCodeGeneration.actionType.isEnabled(sourceRepository,null,controller.getEditor()));
        //warButton.setEnabled(GenerateWAR.actionType.isEnabled(codeRepository, null, controller.getEditor()));
	}
    
	/**
	 * Overrides getRepresentedObject
	 * @see org.openflexo.view.ModuleView#getRepresentedObject()
	 */
	@Override
	public SourceRepository getRepresentedObject()
    {
        return sourceRepository;
    }

     /**
     * Overrides delete
     * @see org.openflexo.view.ModuleView#deleteModuleView()
     */
    @Override
	public void deleteModuleView()
    {
    	controller.removeModuleView(this);
    	sourceRepository.deleteObserver(this);
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

	@Override
	public FlexoModelObject getFocusedObject()
	{
		return getRepresentedObject();
	}

	@Override
	public Vector getGlobalSelection() 
	{
		return null;
	}

	@Override
	public FlexoEditor getEditor()
	{
		return controller.getEditor();
	}
	
	protected class ViewHeader extends JPanel
	{
		JButton chooseFileButton;
		JLabel title;
		JLabel subTitle;
		JLabel subTitle2;
		JPanel controlPanel;
		Vector<FlexoActionButton> actionButtons = new Vector<FlexoActionButton>();

		protected ViewHeader()
		{
			super(new BorderLayout());
			
	        chooseFileButton = new JButton(GeneratorIconLibrary.BACKUP_ICON);
	        chooseFileButton.setBorder(BorderFactory.createEmptyBorder());
	        chooseFileButton.addActionListener(new ActionListener() {
	            @Override
				public void actionPerformed(ActionEvent e)
	            {
	                FlexoFileChooser fileChooser = new FlexoFileChooser(SwingUtilities.getWindowAncestor(chooseFileButton));
	                fileChooser.setCurrentDirectory(sourceRepository.getDirectory());
	                fileChooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
	                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	                int returnVal = fileChooser.showDialog(FlexoLocalization.localizedForKey("select"));
	                if(returnVal == JFileChooser.APPROVE_OPTION) {
	                	sourceRepository.setDirectory(fileChooser.getSelectedFile());
	                }
	            }
	        });

			/*icon = new JLabel(IconLibrary.BIG_SOURCE_REPOSITORY_ICON);
			icon.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
			add(icon,BorderLayout.WEST);*/
	        add(chooseFileButton,BorderLayout.WEST);
			title = new JLabel(FlexoLocalization.localizedForKey("source_repository")+" "+getRepresentedObject().getName(),SwingConstants.LEFT);
			//title.setVerticalAlignment(JLabel.BOTTOM);
			title.setFont(SGCst.HEADER_FONT);
			title.setForeground(Color.BLACK);
			title.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
			subTitle = new JLabel(getRepresentedObject().getDirectory().getAbsolutePath(),SwingConstants.LEFT);
			//title.setVerticalAlignment(JLabel.BOTTOM);
			subTitle.setFont(SGCst.SUB_TITLE_FONT);
			subTitle.setForeground(Color.GRAY);
			subTitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
			subTitle2 = new JLabel(FlexoLocalization.localizedForKey("implementation_model")+": "+getRepresentedObject().getImplementationModel().getName(),SwingConstants.LEFT);
			//title.setVerticalAlignment(JLabel.BOTTOM);
			subTitle2.setFont(SGCst.NORMAL_FONT);
			subTitle2.setForeground(Color.BLACK);
			subTitle2.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));

			JPanel labelsPanel = new JPanel(new GridLayout(3,1));
			labelsPanel.add(title);
			labelsPanel.add(subTitle2);
			labelsPanel.add(subTitle);
			add(labelsPanel,BorderLayout.CENTER);			

			controlPanel = new JPanel(new FlowLayout());
			FlexoActionButton synchronizeAction = new FlexoActionButton(SynchronizeRepositoryCodeGeneration.actionType,"synchronize",SourceRepositoryModuleView.this,controller.getEditor());
			FlexoActionButton generateAction = new FlexoActionButton(GenerateSourceCode.actionType,"generate",SourceRepositoryModuleView.this,controller.getEditor());
			FlexoActionButton writeAction = new FlexoActionButton(WriteModifiedGeneratedFiles.actionType,"write_files",SourceRepositoryModuleView.this,controller.getEditor());
			actionButtons.add(synchronizeAction);
			actionButtons.add(generateAction);
			actionButtons.add(writeAction);
			controlPanel.add(synchronizeAction);
			controlPanel.add(generateAction);
			controlPanel.add(writeAction);
			controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
			add(controlPanel,BorderLayout.EAST);

			update();
		}

		protected void update()
		{
			title.setText(FlexoLocalization.localizedForKey("source_repository")+" "+getRepresentedObject().getName());
			subTitle.setText(getRepresentedObject().getDirectory().getAbsolutePath());
			subTitle2.setText(FlexoLocalization.localizedForKey("implementation_model")+" "+getRepresentedObject().getImplementationModel().getName());

			for (FlexoActionButton button : actionButtons) {
				button.update();
			}
		}
	}


}