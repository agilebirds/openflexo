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
package org.openflexo.fps.controller.action;

import java.util.logging.Logger;

import org.openflexo.fps.FlexoAuthentificationException;
import org.openflexo.fps.action.FlexoUnknownHostException;
import org.openflexo.fps.controller.FPSController;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.InteractiveFlexoEditor;

/**
 * 
 * Action initializing for this module
 * 
 * @author yourname
 */
public class FPSControllerActionInitializer extends ControllerActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	private FPSController _fpsController;

	public FPSControllerActionInitializer(InteractiveFlexoEditor editor, FPSController controller) {
		super(editor, controller);
		_fpsController = controller;
	}

	protected FPSController getFPSController() {
		return _fpsController;
	}

	@Override
	public void initializeActions() {
		super.initializeActions();

		new FPSSetPropertyInitializer(this);

		// CVS repositories actions
		new AddCVSRepositoryInitializer(this);
		new RemoveCVSRepositoryInitializer(this);

		// Refreshing
		new CVSRefreshInitializer(this);

		// Obtaining projects
		new CheckoutProjectInitializer(this);
		new OpenSharedProjectInitializer(this);
		new ShareProjectInitializer(this);

		// CVS operations
		new SynchronizeWithRepositoryInitializer(this);
		new RefreshProjectInitializer(this);
		new CommitFilesInitializer(this);
		new UpdateFilesInitializer(this);
		new MarkAsMergedFilesInitializer(this);
		new OverrideAndUpdateFilesInitializer(this);
		new OverrideAndCommitFilesInitializer(this);

		// Edit operations
		new EditCVSFileInitializer(this);
		new SaveCVSFileInitializer(this);
		new RevertToSavedCVSFileInitializer(this);

		// CVSHistory
		new RetrieveCVSHistoryInitializer(this);

	}

	public boolean handleAuthenticationException(FlexoAuthentificationException exception) {
		return getFPSController().handleAuthenticationException(exception);
	}

	public boolean handleUnknownHostException(FlexoUnknownHostException exception) {
		return getFPSController().handleUnknownHostException(exception);
	}
}
