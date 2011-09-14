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
package org.openflexo.wkf.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.FlexoRelativeWindow;
import org.openflexo.wkf.WKFCst;
import org.openflexo.wkf.controller.WKFController;
import org.openflexo.wkf.processeditor.ProcessEditorController;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class ExternalProcessViewWindow extends FlexoRelativeWindow
{

    private static final Logger logger = Logger.getLogger(ExternalProcessViewWindow.class.getPackage().getName());

    private ProcessEditorController _processEditorController;

    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    public ExternalProcessViewWindow(WKFController controller, FlexoProcess process)
    {
        super(controller.getMainFrame());
        getContentPane().setLayout(new BorderLayout());
               
        _processEditorController = new ProcessEditorController(process,controller.getEditor(),null);
        
        JPanel mainView;
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(_processEditorController.getScalePanel(),BorderLayout.WEST);
		JPanel controlPanel = new JPanel(new FlowLayout());
		JButton closeButton = new JButton(FlexoLocalization.localizedForKey("close"));
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeWindow();
			}
		});
		controlPanel.add(closeButton);
		topPanel.add(controlPanel,BorderLayout.EAST);
		mainView = new JPanel(new BorderLayout());
		mainView.add(topPanel,BorderLayout.NORTH);
		mainView.add(new JScrollPane(_processEditorController.getDrawingView()),BorderLayout.CENTER);
		mainView.setPreferredSize(new Dimension(500,500));
		_processEditorController.setScale(0.5);
        
         getContentPane().add(mainView, BorderLayout.CENTER);

        setTitle(process.getName());
        setSize(WKFCst.DEFAULT_EXTERNAL_VIEW_WIDTH, WKFCst.DEFAULT_EXTERNAL_VIEW_HEIGHT);
        setLocation(100, 100);

        addWindowListener(new WindowAdapter() {
            @Override
			public void windowClosing(WindowEvent event)
            {
                closeWindow();
            }
        });

        validate();
        pack();
    }

    /**
     * Implements
     * 
     * @see org.openflexo.view.FlexoRelativeWindow#getName()
     */
    @Override
	public String getName()
    {
        if (getFlexoProcess() != null) {
            return getFlexoProcess().getName();
        } else {
            return FlexoLocalization.localizedForKey("unnamed");
        }
    }

    @Override
	public String getLocalizedName()
    {
        if (getFlexoProcess() != null) {
            return getFlexoProcess().getName();
        } else {
            return FlexoLocalization.localizedForKey(getName());
        }
    }

    public FlexoProcess getFlexoProcess()
    {
    	if (_processEditorController != null && _processEditorController.getDrawing() != null)
    		return _processEditorController.getDrawing().getFlexoProcess();
    	return null;
    }


    public void closeWindow()
    {
    	logger.info("Closing ExternalProcessViewWindow");
    	if (_processEditorController != null)
    		_processEditorController.delete();
    	_processEditorController = null;
    	dispose();
    }

 }
