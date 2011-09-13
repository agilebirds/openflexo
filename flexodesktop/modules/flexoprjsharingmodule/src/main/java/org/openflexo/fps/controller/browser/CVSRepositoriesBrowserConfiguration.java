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

import org.openflexo.components.browser.BrowserConfiguration;
import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementFactory;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.fps.CVSDirectory;
import org.openflexo.fps.CVSExplorer;
import org.openflexo.fps.CVSFile;
import org.openflexo.fps.CVSModule;
import org.openflexo.fps.CVSRepository;
import org.openflexo.fps.CVSRepositoryList;
import org.openflexo.fps.SharedProject;


class CVSRepositoriesBrowserConfiguration implements BrowserConfiguration
{
	private CVSRepositoryList _repositories;
	private CVSRepositoriesBrowserConfigurationElementFactory _factory;

	protected CVSRepositoriesBrowserConfiguration(CVSRepositoryList repositories)
	{
		super();
		_repositories = repositories;
		_factory = new CVSRepositoriesBrowserConfigurationElementFactory();
	}

	@Override
	public FlexoProject getProject() 
	{
		return null;
	}
	
    @Override
	public void configure(ProjectBrowser aBrowser) 
	{
 	}

	@Override
	public FlexoModelObject getDefaultRootObject()
	{
		return _repositories;
	}

	@Override
	public BrowserElementFactory getBrowserElementFactory()
	{
		return _factory; 
	}

	class CVSRepositoriesBrowserConfigurationElementFactory implements BrowserElementFactory
	{

		CVSRepositoriesBrowserConfigurationElementFactory() {
			super();
		}

		@Override
		public BrowserElement makeNewElement(FlexoModelObject object, ProjectBrowser browser, BrowserElement parent)
		{
			if (object instanceof CVSRepositoryList) {
				return new CVSRepositoryListElement((CVSRepositoryList)object,browser,parent);
			}
			else if (object instanceof CVSRepository) {
				return new CVSRepositoryElement((CVSRepository)object,browser,parent);
			}
			else if (object instanceof CVSModule) {
				return new CVSModuleElement((CVSModule)object,browser,parent);
			}
			else if (object instanceof CVSExplorer) {
				return new CVSExplorerElement((CVSExplorer)object,browser,parent);
			}
			else if (object instanceof SharedProject) {
				return new SharedProjectElement((SharedProject)object,browser,parent);
			}
			else if (object instanceof CVSDirectory) {
				return new CVSDirectoryElement((CVSDirectory)object,browser,parent);
			}
			else if (object instanceof CVSFile) {
				return new CVSFileElement((CVSFile)object,browser,parent);
			}
			return null;
		}
		
	}
}