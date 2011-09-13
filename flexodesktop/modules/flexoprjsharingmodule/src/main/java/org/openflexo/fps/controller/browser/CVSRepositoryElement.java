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

import java.util.Vector;
import java.util.logging.Level;

import javax.swing.tree.TreePath;


import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ExpansionSynchronizedElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.fps.CVSExplorer;
import org.openflexo.fps.CVSModule;
import org.openflexo.fps.CVSRepository;
import org.openflexo.fps.dm.CVSModuleDiscovered;
import org.openflexo.localization.FlexoLocalization;

public class CVSRepositoryElement extends FPSBrowserElement implements ExpansionSynchronizedElement
{
	private FlexoEditor _editor;

	public CVSRepositoryElement(CVSRepository repository, ProjectBrowser browser, BrowserElement parent)
	{
		super(repository, BrowserElementType.CVS_REPOSITORY, browser,parent);
	}

	@Override
	public TreePath getTreePath() {
		return super.getTreePath();
	}

	@Override
	protected void buildChildrenVector()
	{
		if (logger.isLoggable(Level.FINE))
			logger.fine("buildChildrenVector() for CVSRepositoryElement, explorer = "+getCVSExplorer()+ " isExplored="+getCVSExplorer().isExplored());
		if (getCVSExplorer().isExplored()) {
			for (CVSModule module : new Vector<CVSModule>(getCVSRepository().getCVSModules())) {// The clone is necessary, because FPS is M-T and new modules can be added after this call.
				addToChilds(module);
			}
		}
		else if (!getCVSExplorer().isError()) {
			addToChilds(getCVSExplorer());
		}
	}

	@Override
	public String getName()
	{
		return getCVSRepository().getName()+(getCVSRepository().isConnected()?"":" <"+FlexoLocalization.localizedForKey("unreachable")+">");
	}

	public CVSRepository getCVSRepository()
	{
		return (CVSRepository)getObject();
	}

	public CVSExplorer getCVSExplorer()
	{
		return getCVSRepository().getCVSExplorer(getProjectBrowser());
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
