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
import org.openflexo.foundation.ontology.calc.CalcPalette;


class CalcPaletteBrowser extends CEDBrowser
{
	private CalcPalette representedPalette = null;
	
	protected CalcPaletteBrowser(CEDController controller)
	{
		super(controller);
	}

	protected CalcPalette getRepresentedPalette() 
	{
		return representedPalette;
	}

	protected void setRepresentedPalette(CalcPalette representedPalette) 
	{
		this.representedPalette = representedPalette;
	}

    @Override
	public CalcPalette getDefaultRootObject()
    {
    	return representedPalette;
    }
    
	@Override
	public void configure()
	{
		super.configure();
		setFilterStatus(BrowserElementType.ONTOLOGY_CALC_PALETTE, BrowserFilterStatus.SHOW);
		setFilterStatus(BrowserElementType.ONTOLOGY_CALC_PALETTE_ELEMENT, BrowserFilterStatus.SHOW);
	}

}