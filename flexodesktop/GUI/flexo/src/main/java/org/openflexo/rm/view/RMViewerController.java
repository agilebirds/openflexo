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
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.fge.swing.JDianaInteractiveViewer;
import org.openflexo.fge.swing.control.SwingToolFactory;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.model.exceptions.ModelDefinitionException;

public class RMViewerController extends JDianaInteractiveViewer<FlexoProject> {

	private static final Logger logger = Logger.getLogger(RMViewerController.class.getPackage().getName());

	/**
	 * This is the FGE model factory used in the context of RM_VIEWER
	 */
	public static FGEModelFactory RM_VIEWER_FACTORY = null;

	static {
		try {
			RM_VIEWER_FACTORY = new FGEModelFactoryImpl();
		} catch (ModelDefinitionException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	public RMViewerController(FlexoProject project) {
		super(new RMViewerRepresentation(project, RM_VIEWER_FACTORY), RM_VIEWER_FACTORY, SwingToolFactory.DEFAULT);
	}

	private JPanel mainView;

	public JPanel getMainView() {
		if (mainView == null) {
			JPanel topPanel = new JPanel(new BorderLayout());
			topPanel.add(getToolFactory().makeDianaScaleSelector(this).getComponent(), BorderLayout.WEST);
			mainView = new JPanel(new BorderLayout());
			mainView.add(topPanel, BorderLayout.NORTH);
			mainView.add(new JScrollPane(getDrawingView()), BorderLayout.CENTER);
			mainView.setPreferredSize(new Dimension(500, 500));
		}
		return mainView;
	}

}
