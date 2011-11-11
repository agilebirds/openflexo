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
import org.openflexo.components.browser.CustomBrowserFilter;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.fps.SharedProject;
import org.openflexo.fps.controller.FPSController;

/**
 * Browser for Code Generator module
 * 
 * @author sguerin
 * 
 */
public class SharedProjectBrowser extends FPSBrowser implements FlexoObserver {

	private static final Logger logger = Logger.getLogger(SharedProjectBrowser.class.getPackage().getName());

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public SharedProjectBrowser(FPSController controller) {
		super(makeDefaultBrowserConfiguration(controller.getSharedProject()), controller);
		update();
	}

	@Override
	public void update(FlexoObservable o, DataModification arg) {
		if (logger.isLoggable(Level.FINE))
			logger.fine("CVSRepositoriesBrowser update");
	}

	public static BrowserConfiguration makeDefaultBrowserConfiguration(SharedProject project) {
		BrowserConfiguration returned = new SharedProjectBrowserConfiguration(project);
		return returned;
	}

	@Override
	public FlexoModelObject getDefaultRootObject() {
		if (getController() != null)
			return getController().getSharedProject();
		return null;
	}

	private CustomBrowserFilter allFilesAndDirectoryFilter;
	private CustomBrowserFilter upToDateFilesFilter;
	private CustomBrowserFilter locallyModifiedFilter;
	private CustomBrowserFilter remotelyModifiedFilter;
	private CustomBrowserFilter conflictingFileFilter;
	private CustomBrowserFilter ignoredFileFilter;

	public CustomBrowserFilter getAllFilesAndDirectoryFilter() {
		return allFilesAndDirectoryFilter;
	}

	public void setAllFilesAndDirectoryFilter(CustomBrowserFilter allFilesAndDirectoryFilter) {
		this.allFilesAndDirectoryFilter = allFilesAndDirectoryFilter;
	}

	public CustomBrowserFilter getConflictingFileFilter() {
		return conflictingFileFilter;
	}

	public void setConflictingFileFilter(CustomBrowserFilter conflictingFileFilter) {
		this.conflictingFileFilter = conflictingFileFilter;
	}

	public CustomBrowserFilter getIgnoredFileFilter() {
		return ignoredFileFilter;
	}

	public void setIgnoredFileFilter(CustomBrowserFilter ignoredFileFilter) {
		this.ignoredFileFilter = ignoredFileFilter;
	}

	public CustomBrowserFilter getLocallyModifiedFilter() {
		return locallyModifiedFilter;
	}

	public void setLocallyModifiedFilter(CustomBrowserFilter locallyModifiedFilter) {
		this.locallyModifiedFilter = locallyModifiedFilter;
	}

	public CustomBrowserFilter getRemotelyModifiedFilter() {
		return remotelyModifiedFilter;
	}

	public void setRemotelyModifiedFilter(CustomBrowserFilter remotelyModifiedFilter) {
		this.remotelyModifiedFilter = remotelyModifiedFilter;
	}

	public CustomBrowserFilter getUpToDateFilesFilter() {
		return upToDateFilesFilter;
	}

	public void setUpToDateFilesFilter(CustomBrowserFilter upToDateFilesFilter) {
		this.upToDateFilesFilter = upToDateFilesFilter;
	}

}
