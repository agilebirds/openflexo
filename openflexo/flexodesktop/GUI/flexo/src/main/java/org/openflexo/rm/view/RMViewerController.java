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
package org.openflexo.rm.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


import org.openflexo.fge.controller.DrawingController;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.localization.FlexoLocalization;

public class RMViewerController extends DrawingController<RMViewerRepresentation> {

	public RMViewerController(FlexoProject project)
	{
		super(new RMViewerRepresentation(project));
	}
	
	private JPanel mainView;
	
	public JPanel getMainView()
	{
		if (mainView == null) {
			JPanel topPanel = new JPanel(new BorderLayout());
			topPanel.add(getScalePanel(),BorderLayout.WEST);
			JPanel controlPanel = new JPanel(new FlowLayout());
			JButton randomLayoutButton = new JButton(FlexoLocalization.localizedForKey("random_layout"));
			randomLayoutButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getDrawingGraphicalRepresentation().performRandomLayout();
				}
			});
			controlPanel.add(randomLayoutButton);
			JButton autoLayoutButton = new JButton(FlexoLocalization.localizedForKey("auto_layout"));
			autoLayoutButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getDrawingGraphicalRepresentation().performAutoLayout();
				}
			});
			controlPanel.add(autoLayoutButton);
			topPanel.add(controlPanel,BorderLayout.EAST);
			mainView = new JPanel(new BorderLayout());
			mainView.add(topPanel,BorderLayout.NORTH);
			mainView.add(new JScrollPane(getDrawingView()),BorderLayout.CENTER);
			mainView.setPreferredSize(new Dimension(500,500));
		}
		return mainView;
	}

}
