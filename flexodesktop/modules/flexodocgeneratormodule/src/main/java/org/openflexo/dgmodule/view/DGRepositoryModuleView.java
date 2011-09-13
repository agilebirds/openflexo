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
package org.openflexo.dgmodule.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.openflexo.dg.ProjectDocGenerator;
import org.openflexo.dg.action.GenerateDocx;
import org.openflexo.dg.action.GeneratePDF;
import org.openflexo.dg.action.ReinjectDocx;
import org.openflexo.dgmodule.DGCst;
import org.openflexo.dgmodule.DGPreferences;
import org.openflexo.dgmodule.controller.DGController;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.action.FlexoActionSource;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.dm.CGRepositoryConnected;
import org.openflexo.foundation.cg.dm.CGRepositoryDisconnected;
import org.openflexo.foundation.cg.dm.CGStructureRefreshed;
import org.openflexo.foundation.cg.dm.LogAdded;
import org.openflexo.foundation.cg.dm.PostBuildStart;
import org.openflexo.foundation.cg.dm.PostBuildStop;
import org.openflexo.generator.action.GenerateAndWrite;
import org.openflexo.generator.action.GenerateZip;
import org.openflexo.generator.action.SynchronizeRepositoryCodeGeneration;
import org.openflexo.icon.DGIconLibrary;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.prefs.FlexoPreferences;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.swing.JConsole;
import org.openflexo.toolbox.LogListener;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.listener.FlexoActionButton;

/**
 *
 * @author sylvain
 */
public class DGRepositoryModuleView extends JPanel implements ModuleView<DGRepository>, FlexoObserver, LogListener, FlexoActionSource
{

    protected static final Logger logger = Logger.getLogger(DGRepositoryModuleView.class.getPackage().getName());

    protected DGController controller;

    DGRepository codeRepository;

    protected JLabel generationPath;

    private final JPanel buttonPanel;

    private final JConsole console;
    
    private final ConsolePanel consolePanel;

    private final JButton chooseFileButton;

    private final JButton chooseWarLocationButton;

    private final FlexoActionButton generateButton;

    protected FlexoActionButton generateAndWriteButton;

    private FlexoActionButton postBuildButton;
    
    private FlexoActionButton postBuildButton2;

    private final JCheckBox openPostBuildFileCheckBox;

    public class ConsolePanel extends JPanel {
    	protected JButton clearButton;
    	
    	public ConsolePanel(JConsole console) {
    		super(new BorderLayout());
    		JPanel north = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    		clearButton = new JButton(GeneratorIconLibrary.CANCEL_ICON);
    		clearButton.setToolTipText(FlexoLocalization.localizedForKey("clear_console"));
    		clearButton.setText(FlexoLocalization.localizedForKey("clear",clearButton));
    		clearButton.addActionListener(new ActionListener(){
    			@Override
				public void actionPerformed(ActionEvent e) {
    				DGRepositoryModuleView.this.console.clear();
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
    public DGRepositoryModuleView(DGRepository repository, DGController ctrl)
    {
        super(new BorderLayout());
        codeRepository = repository;
        repository.addObserver(this);
        this.controller = ctrl;

        console = new JConsole();
        consolePanel = new ConsolePanel(console);
        add(consolePanel, BorderLayout.CENTER);
        JPanel bigButtonsPanel = new JPanel(new BorderLayout());

        JPanel northPanel = new JPanel(new FlowLayout());
        chooseFileButton = new GeneratorButton(GeneratorIconLibrary.BACKUP_ICON);
        chooseFileButton.setText(codeRepository.getDirectory() != null ? codeRepository.getDirectory().getAbsolutePath()
                : FlexoLocalization.localizedForKey("undefined"));
        chooseFileButton.setPreferredSize(new Dimension(350, 80));
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
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    codeRepository.setDirectory(fileChooser.getSelectedFile());
                }
                controller.remountWindowsOnTop();
            }
        });
        northPanel.add(chooseFileButton);
        generateButton = new FlexoActionButton(SynchronizeRepositoryCodeGeneration.actionType,this,controller.getEditor());
        generateButton.setIcon(DGIconLibrary.GENERATE_DOC_BUTTON);
        generateButton.setDisabledIcon(null);
        generateButton.setHorizontalTextPosition(SwingConstants.CENTER);
        generateButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        generateButton.setPreferredSize(new Dimension(120, 80));
//        generateButton.setText(FlexoLocalization.localizedForKey("synchronize", generateButton));
//        generateButton.setToolTipText(FlexoLocalization.localizedForKey("synchronize", generateButton));
//        generateButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e)
//            {
//                SynchronizeRepositoryCodeGeneration action = SynchronizeRepositoryCodeGeneration.actionType.makeNewAction(codeRepository,
//                        null, controller.getEditor());
//                action.doAction();
//            }
//        });
        northPanel.add(generateButton);
        generateAndWriteButton = new FlexoActionButton(GenerateAndWrite.actionType,this,controller.getEditor());
        generateAndWriteButton.setIcon(DGIconLibrary.GENERATE_DOC_AND_WRITE_BUTTON);
        generateAndWriteButton.setDisabledIcon(null);
        generateAndWriteButton.setHorizontalTextPosition(SwingConstants.CENTER);
        generateAndWriteButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        generateAndWriteButton.setPreferredSize(new Dimension(120, 80));
//        generateAndWriteButton.setText(FlexoLocalization.localizedForKey("synchronize_and_write", generateAndWriteButton));
//        generateAndWriteButton.setToolTipText(FlexoLocalization.localizedForKey("synchronize_and_write", generateAndWriteButton));
//        generateAndWriteButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e)
//            {
//                GenerateAndWrite action = GenerateAndWrite.actionType.makeNewAction(codeRepository,
//                        null, controller.getEditor());
//                action.doAction();
//            }
//        });
        northPanel.add(generateAndWriteButton);
        bigButtonsPanel.add(northPanel, BorderLayout.NORTH);
        buttonPanel = new JPanel(new FlowLayout());
        chooseWarLocationButton = new GeneratorButton(GeneratorIconLibrary.BACKUP_ICON);
        chooseWarLocationButton.setText(codeRepository.getPostBuildDirectory() != null ? codeRepository.getPostBuildDirectory().getAbsolutePath()
                : FlexoLocalization.localizedForKey("undefined"));
        chooseWarLocationButton.setPreferredSize(new Dimension(350, 80));
        chooseWarLocationButton.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
            	FlexoFileChooser fileChooser = new FlexoFileChooser(SwingUtilities.getWindowAncestor(postBuildButton));
                fileChooser.setCurrentDirectory(codeRepository.getPostBuildDirectory());
                fileChooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                controller.dismountWindowsOnTop(null);
                int returnVal = fileChooser.showDialog(FlexoLocalization.localizedForKey("select"));
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    codeRepository.setPostBuildDirectory(fileChooser.getSelectedFile());
                    chooseWarLocationButton.setText(codeRepository.getPostBuildDirectory() != null ? codeRepository.getPostBuildDirectory().getAbsolutePath()
                            : FlexoLocalization.localizedForKey("undefined"));
                }
                controller.remountWindowsOnTop();
            }
        });
        buttonPanel.add(chooseWarLocationButton);
        switch(repository.getFormat())
        {
        	case LATEX:
        		postBuildButton = new FlexoActionButton(GeneratePDF.actionType,this,controller.getEditor());
    	        postBuildButton.setIcon(DGIconLibrary.GENERATE_PDF);
    	        postBuildButton.setText(FlexoLocalization.localizedForKey("generate_PDF", postBuildButton));
    	        postBuildButton.setToolTipText(FlexoLocalization.localizedForKey("generate_PDF", postBuildButton));
        		break;
        	case DOCX:
        		postBuildButton = new FlexoActionButton(GenerateDocx.actionType, this, controller.getEditor());
    	        postBuildButton.setIcon(DGIconLibrary.GENERATE_DOCX);
    	        postBuildButton.setText(FlexoLocalization.localizedForKey("generate_docx", postBuildButton));
    	        postBuildButton.setToolTipText(FlexoLocalization.localizedForKey("generate_docx", postBuildButton));
    	        
    	        postBuildButton2 = new FlexoActionButton(ReinjectDocx.actionType, this, controller.getEditor());
    	        postBuildButton2.setIcon(DGIconLibrary.REINJECT_DOCX);
    	        postBuildButton2.setText(FlexoLocalization.localizedForKey("reinject_docx", postBuildButton2));
    	        postBuildButton2.setToolTipText(FlexoLocalization.localizedForKey("reinject_docx", postBuildButton2));
    	        
        		break;
        	case HTML:
        		postBuildButton = new FlexoActionButton(GenerateZip.actionType,this,controller.getEditor());
    	        postBuildButton.setIcon(DGIconLibrary.GENERATE_ZIP);
    	        postBuildButton.setText(FlexoLocalization.localizedForKey("generate_zip", postBuildButton));
    	        postBuildButton.setToolTipText(FlexoLocalization.localizedForKey("generate_zip", postBuildButton));
        		break;
        }

        postBuildButton.setDisabledIcon(null);
        postBuildButton.setHorizontalTextPosition(SwingConstants.CENTER);
        postBuildButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        postBuildButton.setPreferredSize(new Dimension(120, 80));
        buttonPanel.add(postBuildButton);
        
        if(postBuildButton2 != null)
        {
        	postBuildButton2.setDisabledIcon(null);
        	postBuildButton2.setHorizontalTextPosition(SwingConstants.CENTER);
        	postBuildButton2.setVerticalTextPosition(SwingConstants.BOTTOM);
        	postBuildButton2.setPreferredSize(new Dimension(120, 80));
            buttonPanel.add(postBuildButton2);
        }
        else
        {
	        JPanel fakeThirdButton = new JPanel() {
	            /**
	             * Overrides getPreferredSize
	             * @see javax.swing.JComponent#getPreferredSize()
	             */
	            @Override
	            public Dimension getPreferredSize()
	            {
	                return generateAndWriteButton.getPreferredSize();
	            }
	        };
	        fakeThirdButton.setOpaque(false);
	        buttonPanel.add(fakeThirdButton);
        }
        
        bigButtonsPanel.add(buttonPanel, BorderLayout.SOUTH);
        /*generateButton.setEnabled(repository.isEnabled());
        generateAndWriteButton.setEnabled(repository.isEnabled());
        pdfButton.setEnabled(repository.isEnabled());*/
        JPanel northPanel2 = new JPanel(new BorderLayout());
        JPanel checkBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        openPostBuildFileCheckBox = new JCheckBox();
        
        switch(repository.getFormat())
        {
        	case LATEX:
        		openPostBuildFileCheckBox.setText(FlexoLocalization.localizedForKey("automatically_open_PDF", openPostBuildFileCheckBox));
    			openPostBuildFileCheckBox.setSelected(DGPreferences.getOpenPDF());
    			openPostBuildFileCheckBox.addActionListener(new ActionListener() {
    				/**
    				 * Overrides actionPerformed
    				 * 
    				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    				 */
    				@Override
					public void actionPerformed(ActionEvent e) {
    					DGPreferences.setOpenPDF(openPostBuildFileCheckBox.isSelected());
    					FlexoPreferences.savePreferences(true);
    				}
    			});
        		break;
        	case DOCX:
        		openPostBuildFileCheckBox.setText(FlexoLocalization.localizedForKey("automatically_open_docx", openPostBuildFileCheckBox));
    			openPostBuildFileCheckBox.setSelected(DGPreferences.getOpenDocx());
    			openPostBuildFileCheckBox.addActionListener(new ActionListener() {
    				/**
    				 * Overrides actionPerformed
    				 * 
    				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    				 */
    				@Override
					public void actionPerformed(ActionEvent e) {
    					DGPreferences.setOpenDocx(openPostBuildFileCheckBox.isSelected());
    					FlexoPreferences.savePreferences(true);
    				}
    			});
        		break;
        	case HTML:
        		openPostBuildFileCheckBox.setText(FlexoLocalization.localizedForKey("automatically_show_zip", openPostBuildFileCheckBox));
    			openPostBuildFileCheckBox.setSelected(DGPreferences.getShowZIP());
    			openPostBuildFileCheckBox.addActionListener(new ActionListener() {
    				/**
    				 * Overrides actionPerformed
    				 * 
    				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    				 */
    				@Override
					public void actionPerformed(ActionEvent e) {
    					DGPreferences.setShowZIP(openPostBuildFileCheckBox.isSelected());
    					FlexoPreferences.savePreferences(true);
    				}
    			});
        		break;
        }
        
        checkBoxPanel.add(openPostBuildFileCheckBox);
        checkBoxPanel.validate();
        northPanel2.add(bigButtonsPanel, BorderLayout.NORTH);
        northPanel2.add(checkBoxPanel,BorderLayout.SOUTH);
        northPanel2.validate();
        add(northPanel2,BorderLayout.NORTH);
        updateButtons();
        validate();
    }

    private class GeneratorButton extends JButton
    {
        public GeneratorButton(Icon icon)
        {
            super(icon);
            setHorizontalTextPosition(SwingConstants.CENTER);
            setVerticalTextPosition(SwingConstants.BOTTOM);
            setPreferredSize(new Dimension(120, 80));
        }

        @Override
        public void setText(String text)
        {
            if (text == null) {
				super.setText("Select a file");
			} else {
				super.setText(text);
			}
        }

    }

    /**
     * Overrides getPerspective
     *
     * @see org.openflexo.view.ModuleView#getPerspective()
     */
	@Override
	public FlexoPerspective<FlexoModelObject> getPerspective() 
	{
		return controller.CODE_GENERATOR_PERSPECTIVE;
	}


    public JPanel getButtonPanel()
    {
        return buttonPanel;
    }

    protected boolean willUpdateButtons = false;
    
    private synchronized void updateButtonsWhenPossible() {
    	if (willUpdateButtons) {
			return;
		}
    	SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				updateButtons();
				willUpdateButtons = false;
			}
		});
    }
    
    protected void updateButtons() 
    {
    	generateButton.update();
        generateAndWriteButton.update();
        postBuildButton.update();
    }
    
    @Override
	public void update(FlexoObservable observable, DataModification dataModification)
    {
        if (observable == codeRepository) {
            if ((dataModification.propertyName() != null) && dataModification.propertyName().equals("pdfDirectory")) {
                chooseWarLocationButton.setText(codeRepository.getPostBuildDirectory() != null ? codeRepository.getPostBuildDirectory()
                        .getAbsolutePath() : FlexoLocalization.localizedForKey("undefined"));
            } else if ((dataModification.propertyName() != null) && dataModification.propertyName().equals("directory")) {
                chooseFileButton.setText(codeRepository.getDirectory() != null ? codeRepository.getDirectory().getAbsolutePath()
                        : FlexoLocalization.localizedForKey("undefined"));
            } else if (dataModification instanceof LogAdded) {
            } else if (dataModification instanceof CGRepositoryConnected) {
            	updateButtonsWhenPossible();
            } else if (dataModification instanceof CGRepositoryDisconnected) {
            	updateButtonsWhenPossible();
            } else if (dataModification instanceof CGStructureRefreshed) {
            	updateButtonsWhenPossible();
            } else if (dataModification instanceof PostBuildStart) {
                addConsoleListener();
                console.setRefreshOnlyInSwingEventDispatchingThread(false);
            }
            else if (dataModification instanceof PostBuildStop) {
                removeConsoleListener();
                console.setRefreshOnlyInSwingEventDispatchingThread(true);
            }
        }
    }

    /**
     * Overrides getRepresentedObject
     *
     * @see org.openflexo.view.ModuleView#getRepresentedObject()
     */
    @Override
	public DGRepository getRepresentedObject()
    {
        return codeRepository;
    }

    /**
     * Overrides delete
     *
     * @see org.openflexo.view.ModuleView#deleteModuleView()
     */
    @Override
	public void deleteModuleView()
    {
    	controller.removeModuleView(this);
    	codeRepository.deleteObserver(this);
        removeConsoleListener();
        projectGenerator = null;
    }

    private boolean isListening = false;

    private ProjectDocGenerator projectGenerator = null;

    private void addConsoleListener()
    {
        if (!isListening) {
            if (projectGenerator == null) {
                Enumeration<ProjectDocGenerator> en = controller.getProjectGenerators();
                while (en.hasMoreElements() && (projectGenerator == null)) {
                    ProjectDocGenerator pdg = en.nextElement();
                    if (pdg.getRepository() == codeRepository) {
						projectGenerator = pdg;
					}
                }
            }
            if (projectGenerator != null) {
                projectGenerator.addToLogListeners(this);
                isListening = true;
            }
        }
    }

    private void removeConsoleListener()
    {
        if (isListening) {
            projectGenerator.removeFromLogListeners(this);
            isListening = false;
        }
    }

    /**
     * Overrides willShow
     *
     * @see org.openflexo.view.ModuleView#willShow()
     */
    @Override
	public void willShow()
    {
    	updateButtons();
        addConsoleListener();
    }

    /**
     * Overrides willHide
     *
     * @see org.openflexo.view.ModuleView#willHide()
     */
    @Override
	public void willHide()
    {
        removeConsoleListener();
    }

    /**
     * Returns flag indicating if this view is itself responsible for scroll
     * management When not, Flexo will manage it's own scrollbar for you
     *
     * @return
     */
    @Override
	public boolean isAutoscrolled()
    {
        return true;
    }

    /**
     * Overrides log
     *
     * @see org.openflexo.toolbox.LogListener#log(java.lang.String)
     */
    @Override
	public void log(String line)
    {
        console.log(line, DGCst.DEFAULT_CONSOLE_COLOR);
    }

	@Override
	public void warn(String line) {
		console.log(line, Color.ORANGE);
	}

    /**
     * Overrides err
     * @see org.openflexo.toolbox.LogListener#err(java.lang.String)
     */
    @Override
	public void err(String line)
    {
        console.log(line, Color.RED);
    }

	@Override
	public FlexoEditor getEditor() {
		return controller.getEditor();
	}

	@Override
	public FlexoModelObject getFocusedObject() {
		return codeRepository;
	}

	@Override
	public Vector getGlobalSelection() {
		return null;
	}

}
