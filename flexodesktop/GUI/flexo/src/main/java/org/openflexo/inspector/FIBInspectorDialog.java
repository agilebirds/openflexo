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
package org.openflexo.inspector;

import java.awt.BorderLayout;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import org.openflexo.swing.WindowSynchronizer;

/**
 * Represent a JDialog showing inspector for the selection managed by an instance of ModuleInspectorController
 * 
 * @author sylvain
 * 
 */
public class FIBInspectorDialog extends JDialog {

	static final Logger logger = Logger.getLogger(FIBInspectorDialog.class.getPackage().getName());

	private final FIBInspectorPanel inspectorPanel;

	private final ModuleInspectorController inspectorController;

	private static final WindowSynchronizer inspectorSync = new WindowSynchronizer();

	public FIBInspectorDialog(ModuleInspectorController inspectorController) {
		super(inspectorController.getFlexoController().getFlexoFrame(), "Inspector", false);

		this.inspectorController = inspectorController;

		inspectorSync.addToSynchronizedWindows(this);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		inspectorPanel = new FIBInspectorPanel(inspectorController);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(inspectorPanel, BorderLayout.CENTER);

		setResizable(true);
		setLocation(1210, 360);
		pack();
		setVisible(true);
	}

	/*public void inspectObject(Object object) {
		if (inspectorPanel.inspectObject(object)) {
			if (object instanceof FlexoModelObject && (object instanceof ViewShape || object instanceof ViewConnector)
					&& ((FlexoModelObject) object).getEditionPatternReferences().size() > 0) {
				String newTitle = ((FlexoModelObject) object).getEditionPatternReferences().firstElement().getEditionPattern()
						.getInspector().getInspectorTitle();
				setTitle(newTitle);
			} else {
				FIBInspector newInspector = inspectorController.inspectorForObject(object);
				setTitle(newInspector.getParameter("title"));
			}
		}
	}*/

	public FIBInspectorPanel getInspectorPanel() {
		return inspectorPanel;
	}
}
