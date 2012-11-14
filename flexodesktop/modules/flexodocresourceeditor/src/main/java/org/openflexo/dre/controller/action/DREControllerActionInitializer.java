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

	@Override
	public void initializeActions() {
		super.initializeActions();

		new DRESetPropertyInitializer(this);

		new CreateDocItemInitializer(this);
		new CreateDocItemFolderInitializer(this);

		new DeleteDocItemFolderInitializer(this);

		new AddToInheritanceChildItemInitializer(this);
		new AddToEmbeddingChildItemInitializer(this);
		new AddToRelatedToItemInitializer(this);
		new RemoveInheritanceChildItemInitializer(this);
		new RemoveEmbeddingChildItemInitializer(this);
		new RemoveRelatedToItemInitializer(this);

		new SubmitVersionInitializer(this);
		new ApproveVersionInitializer(this);
		new RefuseVersionInitializer(this);

		new GenerateHelpSetInitializer(this);
		new ImportDocSubmissionReportInitializer(this);

	}

}
