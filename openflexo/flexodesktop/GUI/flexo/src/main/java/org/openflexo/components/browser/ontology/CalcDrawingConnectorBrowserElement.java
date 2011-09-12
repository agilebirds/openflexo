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
package org.openflexo.components.browser.ontology;



import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.ontology.calc.CalcDrawingConnector;
import org.openflexo.foundation.ontology.calc.CalcDrawingObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;

/**
 * Browser element representing the calc palette element
 *
 * @author sguerin
 *
 */
public class CalcDrawingConnectorBrowserElement extends BrowserElement
{

    protected CalcDrawingConnectorBrowserElement(CalcDrawingConnector connector, ProjectBrowser browser, BrowserElement parent)
    {
        super(connector, BrowserElementType.ONTOLOGY_CALC_DRAWING_CONNECTOR, browser, parent);
    }

    @Override
	protected void buildChildrenVector()
    {
       	for (CalcDrawingObject element : getConnector().getChilds()) {
    		addToChilds(element);
    	}
     }

    @Override
	public String getName()
    {
    	if (StringUtils.isEmpty(getConnector().getName())) return "<"+FlexoLocalization.localizedForKey("unnamed")+">";
        return getConnector().getName();
    }

    protected CalcDrawingConnector getConnector()
    {
        return (CalcDrawingConnector) getObject();
    }

}
