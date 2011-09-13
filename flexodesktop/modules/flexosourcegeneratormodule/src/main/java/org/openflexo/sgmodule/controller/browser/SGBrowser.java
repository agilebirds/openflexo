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
package org.openflexo.sgmodule.controller.browser;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.components.browser.BrowserConfiguration;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ConfigurableProjectBrowser;
import org.openflexo.components.browser.CustomBrowserFilter;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.sgmodule.controller.SGController;


/**
 * Browser for Code Generator module
 * 
 * @author sguerin
 * 
 */
public class SGBrowser extends ConfigurableProjectBrowser implements FlexoObserver
{

	private static final Logger logger = Logger.getLogger(SGBrowser.class.getPackage().getName());

	private SGController _controller;
	
	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public SGBrowser(SGController controller)
	{
		super(makeDefaultBrowserConfiguration(controller.getProject().getGeneratedSources()),controller.getSelectionManager());
		_controller = controller;
		update();
	}

	@Override
	public void update(FlexoObservable o, DataModification arg)
	{
		if (logger.isLoggable(Level.FINE))
			logger.fine("GeneratorBrowser update");
	}

	public static BrowserConfiguration makeDefaultBrowserConfiguration(final GeneratedOutput generatedCode)
	{
		BrowserConfiguration returned = new SGBrowserConfiguration(generatedCode);
		return returned;
	}

	public static BrowserConfiguration makeBrowserConfigurationForFileHistory(final CGFile file)
	{
		BrowserConfiguration returned = new SGBrowserConfiguration((file!=null?file.getGeneratedCode():null)) {
			@Override
			public CGFile getDefaultRootObject()
			{
				return file;
			}
			@Override
			public void configure(ProjectBrowser browser) 
			{
				browser.setFilterStatus(BrowserElementType.FILE_RELEASE_VERSION, BrowserFilterStatus.SHOW);
			}
		};
		return returned;
	}
	
	private CustomBrowserFilter allFilesAndDirectoryFilter;
	private CustomBrowserFilter upToDateFilesFilter;
	private CustomBrowserFilter needsGenerationFilter;
	private CustomBrowserFilter generationErrorFilter;
	private CustomBrowserFilter generationModifiedFilter;
	private CustomBrowserFilter diskModifiedFilter;
	private CustomBrowserFilter unresolvedConflictsFilter;
	private CustomBrowserFilter needsReinjectingFilter;
	private CustomBrowserFilter otherFilesFilter;

	public CustomBrowserFilter getAllFilesAndDirectoryFilter()	{
		return allFilesAndDirectoryFilter;
	}

	public void setAllFilesAndDirectoryFilter(
			CustomBrowserFilter allFilesAndDirectoryFilter) {
		this.allFilesAndDirectoryFilter = allFilesAndDirectoryFilter;
	}

	public CustomBrowserFilter getDiskModifiedFilter() {
		return diskModifiedFilter;
	}

	public void setDiskModifiedFilter(CustomBrowserFilter diskModifiedFilter) {
		this.diskModifiedFilter = diskModifiedFilter;
	}

	public CustomBrowserFilter getGenerationErrorFilter() {
		return generationErrorFilter;
	}

	public void setGenerationErrorFilter(CustomBrowserFilter generationErrorFilter) {
		this.generationErrorFilter = generationErrorFilter;
	}

	public CustomBrowserFilter getGenerationModifiedFilter() {
		return generationModifiedFilter;
	}

	public void setGenerationModifiedFilter(
			CustomBrowserFilter generationModifiedFilter) {
		this.generationModifiedFilter = generationModifiedFilter;
	}

	public CustomBrowserFilter getNeedsGenerationFilter() {
		return needsGenerationFilter;
	}

	public void setNeedsGenerationFilter(CustomBrowserFilter needsGenerationFilter) {
		this.needsGenerationFilter = needsGenerationFilter;
	}

	public CustomBrowserFilter getOtherFilesFilter() {
		return otherFilesFilter;
	}

	public void setOtherFilesFilter(CustomBrowserFilter otherFilesFilter) {
		this.otherFilesFilter = otherFilesFilter;
	}

	public CustomBrowserFilter getUnresolvedConflictsFilter() {
		return unresolvedConflictsFilter;
	}

	public void setUnresolvedConflictsFilter(
			CustomBrowserFilter unresolvedConflictsFilter) {
		this.unresolvedConflictsFilter = unresolvedConflictsFilter;
	}

	public CustomBrowserFilter getUpToDateFilesFilter() {
		return upToDateFilesFilter;
	}

	public void setUpToDateFilesFilter(CustomBrowserFilter upToDateFilesFilter) {
		this.upToDateFilesFilter = upToDateFilesFilter;
	}

	public CustomBrowserFilter getNeedsReinjectingFilter() 
	{
		return needsReinjectingFilter;
	}

	public void setNeedsReinjectingFilter(CustomBrowserFilter needsReinjectingFilter) 
	{
		this.needsReinjectingFilter = needsReinjectingFilter;
	}

	public SGController getController() {
		return _controller;
	}
	
}
