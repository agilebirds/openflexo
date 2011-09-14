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

import javax.swing.tree.TreePath;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ExpansionSynchronizedElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.fps.CVSExplorer;
import org.openflexo.fps.CVSFile;
import org.openflexo.fps.CVSModule;
import org.openflexo.fps.dm.CVSModuleDiscovered;


public class CVSModuleElement extends FPSBrowserElement implements ExpansionSynchronizedElement
{
	public CVSModuleElement(CVSModule module, ProjectBrowser browser, BrowserElement parent)
	{
		super(module, BrowserElementType.CVS_MODULE, browser,parent);
	}

	@Override
	public TreePath getTreePath() {
		return super.getTreePath();
	}

	@Override
	protected void buildChildrenVector()
	{
		if (logger.isLoggable(Level.FINE))
			logger.fine("buildChildrenVector() for CVSModuleElement, explorer = "+getCVSExplorer()+ " isExplored="+getCVSExplorer().isExplored());
		if (getCVSExplorer().isExplored()) {
			for (CVSModule module : getModule().getCVSModules()) {
				addToChilds(module);
			}
			for (CVSFile file : getModule().getCVSFiles()) {
				addToChilds(file);
			}
		}
		else if (!getCVSExplorer().isError()) {
			addToChilds(getCVSExplorer());
		}
	}

	@Override
	public String getName()
	{
		return getModule().getModuleName();
	}

	public CVSModule getModule()
	{
		return (CVSModule)getObject();
	}

	public CVSExplorer getCVSExplorer()
	{
		return getModule().getCVSExplorer(getProjectBrowser());
	}

	@Override
	public boolean isExpansionSynchronizedWithData()
	{
		return true;
	}

	@Override
	public boolean isExpanded()
	{
		return getCVSExplorer().wasExploringRequested();
	}

	@Override
	public void expand()
	{
		if (!getCVSExplorer().wasExploringRequested())
			getCVSExplorer().explore();
	}

	@Override
	public void collapse()
	{
		// Nothing to do
	}

	@Override
	public boolean requiresExpansionFor(BrowserElement next)
	{
		return true;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification)
	{
		//logger.info("observable="+observable+" dataModification="+dataModification);
		if (dataModification instanceof CVSModuleDiscovered && getCVSExplorer().isExploring()) {
			// Dont notify yet, wait for CVSExplored notification
		}
		else {
			super.update(observable, dataModification);
		}
	}


}
