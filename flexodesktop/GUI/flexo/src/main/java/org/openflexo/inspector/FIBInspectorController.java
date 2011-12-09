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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.viewpoint.binding.EditionPatternInstancePathElement;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.view.controller.FlexoFIBController;
import org.openflexo.view.controller.InteractiveFlexoEditor;

public class FIBInspectorController extends FlexoFIBController {

	private static final Logger logger = FlexoLogger
			.getLogger(FIBInspectorController.class.getPackage().getName());

	private InteractiveFlexoEditor editor;

	public FIBInspectorController(FIBComponent component) {
		super(component);
	}

	public boolean displayInspectorTabForContext(String context) {
		logger.info("What about context " + context + " ?");
		if (getEditor() != null && getEditor().getActiveModule() != null
				&& getEditor().getActiveModule().getFlexoController() != null) {
			return getEditor().getActiveModule().getFlexoController()
					.displayInspectorTabForContext(context);
		}
		logger.warning("No controller defined here !");
		return false;
	}

	@Override
	public InteractiveFlexoEditor getEditor() {
		return editor;
	}

	public void setEditor(InteractiveFlexoEditor editor) {
		this.editor = editor;
	}

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable instanceof EditionPatternInstancePathElement) {
			if (getDataObject() instanceof FlexoModelObject) {
				return ((FlexoModelObject) getDataObject())
						.getEditionPatternReferences()
						.get(((EditionPatternInstancePathElement) variable)
								.getIndex()).getEditionPatternInstance();
			}
		}
		return super.getValue(variable);
	}

	@Override
	protected void openFIBEditor(FIBComponent component, final MouseEvent event) {
		if (component instanceof FIBInspector) {
			JPopupMenu popup = new JPopupMenu();
			FIBInspector current = (FIBInspector) component;
			while (current != null) {
				File inspectorFile = new File(current.getDefinitionFile());
				System.out.println("> " + inspectorFile);
				if (inspectorFile.exists()) {
					JMenuItem menuItem = new JMenuItem(inspectorFile.getName());
					// We dont use existing inspector which is already
					// aggregated !!!
					final FIBInspector inspectorToOpen = (FIBInspector) FIBLibrary
							.instance().retrieveFIBComponent(inspectorFile,
									false);
					menuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							FIBInspectorController.super.openFIBEditor(
									inspectorToOpen, event);
						}
					});
					popup.add(menuItem);
				}
				current = current.getSuperInspector();
			}
			popup.show(event.getComponent(), event.getX(), event.getY());
		}
	}

}
