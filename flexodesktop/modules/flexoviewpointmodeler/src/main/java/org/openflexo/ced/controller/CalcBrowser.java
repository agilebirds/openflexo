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
package org.openflexo.ced.controller;

import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.foundation.viewpoint.OntologyCalc;


class CalcBrowser extends CEDBrowser
{
	private OntologyCalc representedCalc = null;
	
	protected CalcBrowser(CEDController controller)
	{
		super(controller);
	}

	protected OntologyCalc getRepresentedObject() 
	{
		return representedCalc;
	}

	protected void setRepresentedObject(OntologyCalc representedCalc) 
	{
		this.representedCalc = representedCalc;
	}

    @Override
	public OntologyCalc getDefaultRootObject()
    {
    	return representedCalc;
    }
    
	@Override
	public void configure()
	{
		super.configure();
		setFilterStatus(BrowserElementType.PROJECT_ONTOLOGY, BrowserFilterStatus.SHOW);
		setFilterStatus(BrowserElementType.IMPORTED_ONTOLOGY, BrowserFilterStatus.SHOW);
		setFilterStatus(BrowserElementType.ONTOLOGY_CLASS, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.ONTOLOGY_INDIVIDUAL, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.ONTOLOGY_DATA_PROPERTY, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.ONTOLOGY_OBJECT_PROPERTY, BrowserFilterStatus.HIDE);
		setFilterStatus(BrowserElementType.ONTOLOGY_CALC, BrowserFilterStatus.SHOW);
		setFilterStatus(BrowserElementType.EDITION_PATTERN, BrowserFilterStatus.SHOW);
		setFilterStatus(BrowserElementType.ONTOLOGY_CALC_PALETTE, BrowserFilterStatus.SHOW);
		setFilterStatus(BrowserElementType.ONTOLOGY_CALC_DRAWING_SHEMA, BrowserFilterStatus.SHOW);
		setFilterStatus(BrowserElementType.ONTOLOGY_CALC_PALETTE_ELEMENT, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
		setFilterStatus(BrowserElementType.ONTOLOGY_CALC_DRAWING_SHAPE, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
		setFilterStatus(BrowserElementType.ONTOLOGY_CALC_DRAWING_CONNECTOR, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN);
	}

}