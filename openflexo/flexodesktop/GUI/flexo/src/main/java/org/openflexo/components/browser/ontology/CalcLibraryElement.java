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
import org.openflexo.foundation.ontology.calc.CalcFolder;
import org.openflexo.foundation.ontology.calc.CalcLibrary;
import org.openflexo.foundation.ontology.calc.OntologyCalc;
import org.openflexo.localization.FlexoLocalization;

/**
 * Browser element representing the calc library
 *
 * @author sguerin
 *
 */
public class CalcLibraryElement extends BrowserElement
{

    protected CalcLibraryElement(CalcLibrary library, ProjectBrowser browser, BrowserElement parent)
    {
        super(library, BrowserElementType.CALC_LIBRARY, browser, parent);
    }

    @Override
	protected void buildChildrenVector()
    {
      	for (CalcFolder subFolder : getCalcLibrary().getRootFolder().getChildren()) {
       		addToChilds(subFolder);
       	}
       	for (OntologyCalc calc : getCalcLibrary().getRootFolder().getCalcs()) {
       		addToChilds(calc);
       	}
    }

    @Override
	public String getName()
    {
        return FlexoLocalization.localizedForKey("calc_library");
    }

    protected CalcLibrary getCalcLibrary()
    {
        return (CalcLibrary) getObject();
    }

}
