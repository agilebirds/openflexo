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
package org.openflexo.components.browser.ie;

import java.util.Enumeration;

import javax.naming.InvalidNameException;


import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.DuplicateFolderNameException;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.FlexoController;

/**
 * @author bmangez <B>Class Description</B>
 */
public class ComponentFolderElement extends IEElement
{

    /**
     * @param widget
     * @param browser
     */
    public ComponentFolderElement(FlexoComponentFolder folder, ProjectBrowser browser, BrowserElement parent)
    {
        super(folder, BrowserElementType.COMPONENT_FOLDER, browser,parent);
    }

    @Override
	protected void buildChildrenVector()
    {
        Enumeration<FlexoComponentFolder> en = getComponentFolder().getSortedSubFolders();
        while (en.hasMoreElements()) {
			FlexoComponentFolder f = en.nextElement();
			addToChilds(f);
        }
        Enumeration<ComponentDefinition> en1 = getComponentFolder().getSortedComponents();
        while (en1.hasMoreElements()) {
			ComponentDefinition cd = en1.nextElement();
			addToChilds(cd);
		}
	}

    /**
	 * Overrides isNameEditable
	 *
	 * @see org.openflexo.components.browser.BrowserElement#isNameEditable()
	 */
    @Override
	public boolean isNameEditable()
    {
        return true;
    }

    /**
     * Overrides setName
     *
     * @see org.openflexo.components.browser.BrowserElement#setName(java.lang.String)
     */
    @Override
	public void setName(String aName)
    {
        try {
            ((FlexoComponentFolder) getObject()).setName(aName);
        } catch (DuplicateFolderNameException e) {
            FlexoController.notify(FlexoLocalization.localizedForKey("there_is_already_a_folder_with that name"));
        } catch (InvalidNameException e) {
            FlexoLocalization.localizedForKey("folder_name_cannot_contain_:_\\_\"_:_*_?_<_>_/");
        }
    }

    @Override
	public String getName()
    {
        return getComponentFolder().getName();
    }

    protected FlexoComponentFolder getComponentFolder()
    {
        return (FlexoComponentFolder) getObject();
    }

}
