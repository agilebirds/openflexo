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
package org.openflexo.components.browser.dm;



import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.ProjectBrowser.DMViewMode;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.localization.FlexoLocalization;

/**
 * Please comment this class
 *
 * @author sguerin
 *
 */
public class DMModelElement extends DMElement
{

    public DMModelElement(DMModel object, ProjectBrowser browser, BrowserElement parent)
    {
        super(object, BrowserElementType.DM_MODEL, browser,parent);
    }

    @Override
	protected void buildChildrenVector()
    {

    	if (getProjectBrowser().getDMViewMode() == DMViewMode.Repositories) {
    		addToChilds(getDMModel().getInternalRepositoryFolder());
    		if (getDMModel().getNonPersistantDataRepositoryFolder().getRepositoriesCount() > 0)
    			addToChilds(getDMModel().getNonPersistantDataRepositoryFolder());
    		if (getDMModel().getPersistantDataRepositoryFolder().getRepositoriesCount() > 0)
    			addToChilds(getDMModel().getPersistantDataRepositoryFolder());
    		if (getDMModel().getLibraryRepositoryFolder().getRepositoriesCount() > 0)
    			addToChilds(getDMModel().getLibraryRepositoryFolder());
    	}
    	else if (getProjectBrowser().getDMViewMode() == DMViewMode.Packages) {
    		for (DMPackage p : getDMModel().getAllOrderedPackages()) {
    			addToChilds(p);
    		}
    	}
    	else if (getProjectBrowser().getDMViewMode() == DMViewMode.Diagrams) {
    		for (ERDiagram diagram : getDMModel().getDiagrams()) {
    			//if (diagram.getRepository() == null)
    				addToChilds(diagram);
    		}
    		/*for (DMRepository repository : getDMModel().getRepositories().values()) {
    			if (repository.hasDiagrams()) {
        			addToChilds(repository);
    			}
    		}*/
    	}
   }

    protected DMModel getDMModel()
    {
        return (DMModel) getObject();
    }

    @Override
	public boolean isNameEditable()
    {
        return false;
    }

    @Override
	public String getName()
    {
        return FlexoLocalization.localizedForKey(getDMModel().getName());
    }

}
