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
package org.openflexo.view.popups;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.openflexo.diff.ComputeDiff;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.swing.diff.DiffPanel;
import org.openflexo.toolbox.TokenMarkerStyle;
import org.openflexo.view.FlexoDialog;
import org.openflexo.view.controller.FlexoController;


/**
 * @author gpolet
 */
public class FileDiffEditorPopup extends FlexoDialog
{
	private Logger logger = FlexoLogger.getLogger(FileDiffEditorPopup.class.getPackage().getName());

	private FlexoController controller;
	
	private String leftSource;
	
	private String rightSource;
	
	private DiffPanel diffPanel;
	
	public FileDiffEditorPopup (String leftTitle, String rightTitle, String leftSource, String rightSource, FlexoController controller)
	{
		super(controller.getFlexoFrame(),FlexoLocalization.localizedForKey("diff_editor"),false);
		this.leftSource = leftSource;
		this.rightSource = rightSource;
		this.controller = controller;
		
		diffPanel = new DiffPanel(ComputeDiff.diff(leftSource, rightSource),TokenMarkerStyle.None,leftTitle,rightTitle,null,true);
		getContentPane().setLayout(new BorderLayout());
	   	getContentPane().add(diffPanel,BorderLayout.CENTER);
    	JPanel controlPanel = new JPanel(new FlowLayout());
    	JButton button = new JButton();
    	button.setText(FlexoLocalization.localizedForKey("close",button));
    	button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}            		
    	});
    	controlPanel.add(button);
    	getContentPane().add(controlPanel,BorderLayout.SOUTH);
    	setPreferredSize(new Dimension(1000,800));
    	validate();
    	pack();
 	}
	
}
