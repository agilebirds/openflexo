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

import java.util.logging.Logger;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.view.controller.FlexoFIBController;
import org.openflexo.view.controller.InteractiveFlexoEditor;

public class FIBInspectorController extends FlexoFIBController {

	private static final Logger logger = FlexoLogger.getLogger(FIBInspectorController.class.getPackage().getName());

	private InteractiveFlexoEditor editor;
	
	public FIBInspectorController(FIBComponent component) 
	{
		super(component);
	}
	
	public boolean displayInspectorTabForContext(String context)
	{
		if (getEditor() != null 
				&& getEditor().getActiveModule() != null 
				&& getEditor().getActiveModule().getFlexoController() != null)
			return getEditor().getActiveModule().getFlexoController().displayInspectorTabForContext(context);
		logger.warning("No controller defined here !");
		return false;
	}

	public InteractiveFlexoEditor getEditor()
	{
		return editor;
	}

	public void setEditor(InteractiveFlexoEditor editor)
	{
		this.editor = editor;
	}

}
