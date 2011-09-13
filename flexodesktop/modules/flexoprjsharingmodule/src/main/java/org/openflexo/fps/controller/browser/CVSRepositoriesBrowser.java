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
package org.openflexo.fps.controller.browser;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.components.browser.BrowserConfiguration;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.fps.CVSRepositoryList;
import org.openflexo.fps.controller.FPSController;


/**
 * Browser for Code Generator module
 * 
 * @author sguerin
 * 
 */
public class CVSRepositoriesBrowser extends FPSBrowser implements FlexoObserver
{

	private static final Logger logger = Logger.getLogger(CVSRepositoriesBrowser.class.getPackage().getName());

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public CVSRepositoriesBrowser(FPSController controller)
	{
		super(makeDefaultBrowserConfiguration(controller.getRepositories()),controller);
		update();
	}

	public CVSRepositoriesBrowser(CVSRepositoryList repositories)
	{
		super(makeDefaultBrowserConfiguration(repositories));
		update();
	}

	@Override
	public void update(FlexoObservable o, DataModification arg)
	{
		if (logger.isLoggable(Level.FINE))
			logger.fine("CVSRepositoriesBrowser update");
	}

	public static BrowserConfiguration makeDefaultBrowserConfiguration(CVSRepositoryList repositories)
	{
		BrowserConfiguration returned = new CVSRepositoriesBrowserConfiguration(repositories);
		return returned;
	}

}
