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
import org.openflexo.fps.controller.FPSSelectionManager;
import org.openflexo.view.controller.ControllerActionInitializer;

/**
 * 
 * Action initializing for this module
 * 
 * @author yourname
 */
public class FPSControllerActionInitializer extends ControllerActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	private FPSController _fpsController;

	public FPSControllerActionInitializer(FPSController controller) {
		super(controller);
		_fpsController = controller;
	}

	protected FPSController getFPSController() {
		return _fpsController;
	}

	protected FPSSelectionManager getFPSSelectionManager() {
		return getFPSController().getFPSSelectionManager();
	}

	@Override
	public void initializeActions() {
		super.initializeActions();

		(new FPSSetPropertyInitializer(this)).init();

		// CVS repositories actions
		(new AddCVSRepositoryInitializer(this)).init();
		(new RemoveCVSRepositoryInitializer(this)).init();

		// Refreshing
		(new CVSRefreshInitializer(this)).init();

		// Obtaining projects
		(new CheckoutProjectInitializer(this)).init();
		(new OpenSharedProjectInitializer(this)).init();
		(new ShareProjectInitializer(this)).init();

		// CVS operations
		(new SynchronizeWithRepositoryInitializer(this)).init();
		(new RefreshProjectInitializer(this)).init();
		(new CommitFilesInitializer(this)).init();
		(new UpdateFilesInitializer(this)).init();
		(new MarkAsMergedFilesInitializer(this)).init();
		(new OverrideAndUpdateFilesInitializer(this)).init();
		(new OverrideAndCommitFilesInitializer(this)).init();

		// Edit operations
		(new EditCVSFileInitializer(this)).init();
		(new SaveCVSFileInitializer(this)).init();
		(new RevertToSavedCVSFileInitializer(this)).init();

		// CVSHistory
		(new RetrieveCVSHistoryInitializer(this)).init();

	}

	public boolean handleAuthenticationException(FlexoAuthentificationException exception) {
		return getFPSController().handleAuthenticationException(exception);
	}

	public boolean handleUnknownHostException(FlexoUnknownHostException exception) {
		return getFPSController().handleUnknownHostException(exception);
	}
}
