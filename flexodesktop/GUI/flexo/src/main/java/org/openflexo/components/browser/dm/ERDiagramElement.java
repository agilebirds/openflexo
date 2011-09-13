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
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.ERDiagram;


/**
 * Please comment this class
 *
 * @author sguerin
 *
 */
public class ERDiagramElement extends DMElement
{

    public ERDiagramElement(ERDiagram diagram, ProjectBrowser browser, BrowserElement parent)
    {
        super(diagram, BrowserElementType.ER_DIAGRAM, browser,parent);
    }

    @Override
	public String getName()
    {
        return getDiagram().getName();
    }

    protected ERDiagram getDiagram()
    {
        return (ERDiagram) getObject();
    }

    @Override
	public boolean isNameEditable()
    {
        return true;
    }

    @Override
    public void setName(String aName)
    {
    	getDiagram().setName(aName);
    }

    @Override
	protected void buildChildrenVector()
    {

    	for (DMEntity entity : getDiagram().getEntities()) {
    		addToChilds(entity);
    	}

   }

}
