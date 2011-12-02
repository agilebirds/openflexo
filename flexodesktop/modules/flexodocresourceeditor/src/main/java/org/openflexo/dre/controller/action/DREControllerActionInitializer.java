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
package org.openflexo.dre.controller.action;

import java.util.logging.Logger;

import org.openflexo.dre.controller.DREController;
import org.openflexo.dre.controller.DRESelectionManager;
import org.openflexo.view.controller.ControllerActionInitializer;

/**
 * 
 * Action initializing for this module
 * 
 * @author yourname
 */
public class DREControllerActionInitializer extends ControllerActionInitializer {

	static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	private DREController _DREController;

	public DREControllerActionInitializer(DREController controller) {
		super(controller);
		_DREController = controller;
	}

	protected DREController getDREController() {
		return _DREController;
	}

	protected DRESelectionManager getDRESelectionManager() {
		return getDREController().getDRESelectionManager();
	}

	@Override
	public void initializeActions() {
		super.initializeActions();

		(new DRESetPropertyInitializer(this)).init();

		(new CreateDocItemInitializer(this)).init();
		(new CreateDocItemFolderInitializer(this)).init();

		(new DeleteDocItemFolderInitializer(this)).init();

		(new AddToInheritanceChildItemInitializer(this)).init();
		(new AddToEmbeddingChildItemInitializer(this)).init();
		(new AddToRelatedToItemInitializer(this)).init();
		(new RemoveInheritanceChildItemInitializer(this)).init();
		(new RemoveEmbeddingChildItemInitializer(this)).init();
		(new RemoveRelatedToItemInitializer(this)).init();

		(new SubmitVersionInitializer(this)).init();
		(new ApproveVersionInitializer(this)).init();
		(new RefuseVersionInitializer(this)).init();

		(new GenerateHelpSetInitializer(this)).init();
		(new ImportDocSubmissionReportInitializer(this)).init();
	}

}
