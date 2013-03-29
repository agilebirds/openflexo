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
import java.awt.Rectangle;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.WindowConstants;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.diagram.model.DiagramConnector;
import org.openflexo.foundation.view.diagram.model.DiagramShape;
import org.openflexo.inspector.ModuleInspectorController.InspectedObjectChanged;
import org.openflexo.swing.WindowSynchronizer;
import org.openflexo.utils.WindowBoundsSaver;
import org.openflexo.view.controller.FlexoController;

/**
 * Represent a JDialog showing inspector for the selection managed by an instance of ModuleInspectorController
 * 
 * @author sylvain
 * 
 */
public class FIBInspectorDialog extends JDialog implements Observer {

	static final Logger logger = Logger.getLogger(FIBInspectorDialog.class.getPackage().getName());

	private static final String INSPECTOR_TITLE = "Inspector";

	private static final WindowSynchronizer inspectorSync = new WindowSynchronizer();

	private FIBInspectorPanel inspectorPanel;

	private ModuleInspectorController inspectorController;

	public FIBInspectorDialog(ModuleInspectorController inspectorController) {
		super(inspectorController.getFlexoController().getFlexoFrame(), INSPECTOR_TITLE, false);
		this.inspectorController = inspectorController;
		inspectorSync.addToSynchronizedWindows(this);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		inspectorController.addObserver(this);
		inspectorPanel = new FIBInspectorPanel(inspectorController);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(inspectorPanel, BorderLayout.CENTER);
		setResizable(true);
		new WindowBoundsSaver(this, "FIBInspector", new Rectangle(800, 400, 400, 400));
	}

	public void delete() {
		setVisible(false);
		dispose();
		inspectorController.deleteObserver(this);
		inspectorSync.removeFromSynchronizedWindows(this);
		inspectorController = null;
		inspectorPanel = null;
	}

	/*public void inspectObject(Object object) {
		if (inspectorPanel.inspectObject(object)) {
			if (object instanceof FlexoModelObject && (object instanceof DiagramShape || object instanceof DiagramConnector)
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

	@Override
	public void update(Observable o, Object notification) {
		/*if (notification instanceof EmptySelectionActivated) {
			setTitle(INSPECTOR_TITLE);
		} else if (notification instanceof MultipleSelectionActivated) {
			setTitle(INSPECTOR_TITLE);
		} else*/if (notification instanceof InspectedObjectChanged) {
			Object object = ((InspectedObjectChanged) notification).getInspectedObject();
			if (object instanceof EditionPatternInstance) {
				String newTitle = ((EditionPatternInstance) object).getEditionPattern().getInspector().getInspectorTitle();
				setTitle(newTitle);
			} else if (object instanceof FlexoModelObject && (object instanceof DiagramShape || object instanceof DiagramConnector)
					&& ((FlexoModelObject) object).getEditionPatternReferences().size() > 0) {
				String newTitle = ((FlexoModelObject) object).getEditionPatternReferences().get(0).getObject().getEditionPattern()
						.getInspector().getInspectorTitle();
				setTitle(newTitle);
			} else if (getInspectorPanel() != null && getInspectorPanel().getCurrentlyDisplayedInspector() != null) {
				setTitle(getInspectorPanel().getCurrentlyDisplayedInspector().getParameter("title"));
			}
			if (object instanceof FlexoObject) {
				ImageIcon icon = FlexoController.statelessIconForObject(object);
				if (icon != null) {
					setIconImage(icon.getImage());
				}
				if (getInspectorPanel() != null && getInspectorPanel().getCurrentlyDisplayedInspector() != null
						&& object.getClass() != getInspectorPanel().getCurrentlyDisplayedInspector().getDataClass()
						&& !object.getClass().getSimpleName().contains("javassist")) {
					setTitle(getInspectorPanel().getCurrentlyDisplayedInspector().getParameter("title") + " : "
							+ object.getClass().getSimpleName());
				}
			}
		}
	}

	public FIBInspectorPanel getInspectorPanel() {
		return inspectorPanel;
	}
}
