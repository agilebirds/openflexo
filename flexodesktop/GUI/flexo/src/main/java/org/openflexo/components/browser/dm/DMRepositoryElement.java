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

import javax.swing.Icon;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.ProjectBrowser.DMViewMode;
import org.openflexo.foundation.dm.ComponentRepository;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.foundation.dm.ExternalRepository;
import org.openflexo.foundation.dm.JDKRepository;
import org.openflexo.foundation.dm.ProcessBusinessDataRepository;
import org.openflexo.foundation.dm.ProcessInstanceRepository;
import org.openflexo.foundation.dm.WORepository;
import org.openflexo.foundation.dm.WSDLRepository;
import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.dm.eo.DMEORepository;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.icon.WSEIconLibrary;


/**
 * Please comment this class
 *
 * @author sguerin
 *
 */
public class DMRepositoryElement extends DMElement
{

    public DMRepositoryElement(DMRepository repository, ProjectBrowser browser, BrowserElement parent)
    {
        super(repository, BrowserElementType.DM_REPOSITORY, browser,parent);
    }

    public DMRepositoryElement(DMRepository repository, BrowserElementType elementType, ProjectBrowser browser, BrowserElement parent)
    {
        super(repository, elementType, browser,parent);
    }

    protected DMRepository getDMRepository()
    {
        return (DMRepository) getObject();
    }

    @Override
    public boolean isNameEditable()
    {
        return (!(getDMRepository() instanceof JDKRepository) && !(getDMRepository() instanceof WORepository)
                && !(getDMRepository() instanceof ComponentRepository) && !(getDMRepository().isReadOnly()));
    }

    @Override
    public Icon getIcon()
    {
        if (getDMRepository() instanceof JDKRepository) {
            return DMEIconLibrary.JDK_REPOSITORY_ICON;
        } else if (getDMRepository() instanceof WORepository) {
            return DMEIconLibrary.WO_REPOSITORY_ICON;
        } else if (getDMRepository() instanceof ComponentRepository) {
            return DMEIconLibrary.COMPONENT_REPOSITORY_ICON;
        } else if (getDMRepository() instanceof ProcessInstanceRepository) {
        	return DMEIconLibrary.PROCESS_INSTANCE_REPOSITORY_ICON;
        } else if (getDMRepository() instanceof ProcessBusinessDataRepository) {
        	return DMEIconLibrary.PROCESS_BUSINESS_DATA_REPOSITORY_ICON;
        } else if (getDMRepository() instanceof ExternalRepository) {
            return DMEIconLibrary.EXTERNAL_REPOSITORY_ICON;
        } else if (getDMRepository() instanceof WSDLRepository){
        		return WSEIconLibrary.WS_REPOSITORY_ICON;
        } else {
            return super.getIcon();
        }
    }

    @Override
	protected void buildChildrenVector()
    {

    	if (getProjectBrowser().getDMViewMode() == DMViewMode.Diagrams ) {
    		for (ERDiagram diagram : getDMRepository().getDMModel().getDiagrams()) {
    			if (diagram.getRepository() == getDMRepository()) addToChilds(diagram);
    		}
    		if (getDMRepository() instanceof DMEORepository) {
    			for (DMEOModel eoModel : ((DMEORepository)getDMRepository()).getDMEOModels().values()) {
    				addToChilds(eoModel);
    			}
    		}
    	}
    	else {
    		super.buildChildrenVector();
    	}
   }

}
