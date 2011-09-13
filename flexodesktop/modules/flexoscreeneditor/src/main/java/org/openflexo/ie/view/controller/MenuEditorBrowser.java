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
package org.openflexo.ie.view.controller;


import java.util.logging.Logger;

import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.ie.dm.TreeStructureChanged;


public class MenuEditorBrowser extends ProjectBrowser implements FlexoObserver
{

    @Override
	public void update(FlexoObservable o, DataModification arg)
    {
        if (o.equals(getProject().getFlexoComponentLibrary())) {
            if (arg instanceof TreeStructureChanged) {
                update();
            }
        }

    }

    protected static final Logger logger = Logger.getLogger(ComponentLibraryBrowser.class.getPackage().getName());

    protected IEController _controller;

    public MenuEditorBrowser(IEController controller)
    {
        super(controller.getEditor(), controller.getIESelectionManager());
        _controller = controller;
        update();
        getProject().getFlexoComponentLibrary().addObserver(this);
    }

    @Override
	public void configure()
    {
    }

    @Override
	public FlexoModelObject getDefaultRootObject()
    {
        if (getProject() == null)
            logger.severe("project is null");
        return getProject().getFlexoNavigationMenu().getRootMenu();
    }

}
