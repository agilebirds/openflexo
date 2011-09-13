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
package org.openflexo.components.browser;

import java.util.logging.Logger;

import org.openflexo.components.browser.dm.DMBrowserElementFactory;
import org.openflexo.components.browser.ie.IEBrowserElementFactory;
import org.openflexo.components.browser.ontology.OEBrowserElementFactory;
import org.openflexo.components.browser.wkf.WKFBrowserElementFactory;
import org.openflexo.components.browser.ws.WSBrowserElementFactory;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;


public class DefaultBrowserElementFactory implements BrowserElementFactory {

    private static final Logger logger = Logger.getLogger(DefaultBrowserElementFactory.class.getPackage()
            .getName());

    public static final BrowserElementFactory DEFAULT_FACTORY = new DefaultBrowserElementFactory();

    private WKFBrowserElementFactory wkfFactory;
    private IEBrowserElementFactory ieFactory;
    private DMBrowserElementFactory dmFactory;
    private WSBrowserElementFactory wsFactory;
    private OEBrowserElementFactory oeFactory;

    protected DefaultBrowserElementFactory()
    {
        super();
        wkfFactory = new WKFBrowserElementFactory();
        ieFactory = new IEBrowserElementFactory();
        dmFactory = new DMBrowserElementFactory();
        wsFactory = new WSBrowserElementFactory();
        oeFactory = new OEBrowserElementFactory();
   }

    @Override
	public BrowserElement makeNewElement(FlexoModelObject object, ProjectBrowser browser, BrowserElement parent)
    {
        if (object instanceof FlexoProject) {
            return new ProjectElement((FlexoProject) object, browser,parent);
        }
        BrowserElement tryThis = null;
        tryThis = wkfFactory.makeNewElement(object,browser, parent);
        if (tryThis != null) return tryThis;
        tryThis = ieFactory.makeNewElement(object,browser, parent);
        if (tryThis != null) return tryThis;
        tryThis = dmFactory.makeNewElement(object,browser, parent);
        if (tryThis != null) return tryThis;
        tryThis = wsFactory.makeNewElement(object,browser, parent);
        if (tryThis != null) return tryThis;
        tryThis = oeFactory.makeNewElement(object,browser, parent);
        if (tryThis != null) return tryThis;

        logger.warning("Unexpected type " + object.getClass().getName() + " in browser");
        return null;

    }
}
