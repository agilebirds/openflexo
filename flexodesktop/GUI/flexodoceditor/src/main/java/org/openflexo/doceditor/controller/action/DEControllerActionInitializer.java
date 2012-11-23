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
package org.openflexo.doceditor.controller.action;

import java.util.logging.Logger;

import org.openflexo.action.ImportImageInitializer;
import org.openflexo.doceditor.controller.DEController;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.OpenFileInExplorer;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.selection.SelectionManager;
import org.openflexo.view.controller.ControllerActionInitializer;

public class DEControllerActionInitializer extends ControllerActionInitializer {

	protected static final Logger logger = Logger.getLogger(DEControllerActionInitializer.class.getPackage().getName());

	static {
		FlexoModelObject.addActionForClass(OpenFileInExplorer.actionType, CGFile.class);
	}

	public DEControllerActionInitializer(DEController controller) {
		super(controller);
	}

	protected DEController getDEController() {
		return (DEController) getController();
	}

	protected SelectionManager getSelectionManager() {
		return getDEController().getSelectionManager();
	}

	@Override
	public void initializeActions() {
		super.initializeActions();
		new DESetPropertyInitializer(this);
		new AddTOCRepositoryInitializer(this);
		// new DeprecatedAddTOCEntryInitializer(this);
		new AddTOCEntryInitializer(this);
		new MoveTOCEntryInitializer(this);
		new AddDocTypeInitializer(this);
		// Desactivate ImportTOCTemplate
		// new ImportTOCTemplateInitializer(this);
		new RemoveTOCRepositoryInitializer(this);
		new RemoveTOCEntryInitializer(this);
		new RemoveDocTypeInitializer(this);
		new RepairTocEntryInitializer(this);
		new ImportImageInitializer(this);

	}

}
