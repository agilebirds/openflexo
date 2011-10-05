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
import org.openflexo.foundation.viewpoint.ViewPointPaletteElement;


/**
 * Browser element representing the calc palette element
 *
 * @author sguerin
 *
 */
public class CalcPaletteElementBrowserElement extends BrowserElement
{

    protected CalcPaletteElementBrowserElement(ViewPointPaletteElement element, ProjectBrowser browser, BrowserElement parent)
    {
        super(element, BrowserElementType.ONTOLOGY_CALC_PALETTE_ELEMENT, browser, parent);
    }

    @Override
	protected void buildChildrenVector()
    {
    }

    @Override
	public String getName()
    {
        return getCalcElement().getName();
    }

    protected ViewPointPaletteElement getCalcElement()
    {
        return (ViewPointPaletteElement) getObject();
    }

}
