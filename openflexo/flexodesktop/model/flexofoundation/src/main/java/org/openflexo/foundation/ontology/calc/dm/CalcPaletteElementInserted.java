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
package org.openflexo.foundation.ontology.calc.dm;

import org.openflexo.foundation.ontology.calc.CalcPalette;
import org.openflexo.foundation.ontology.calc.CalcPaletteElement;
import org.openflexo.foundation.ontology.dm.OEDataModification;

/**
 * Notify that a new element has been added to palette
 * 
 * @author sguerin
 * 
 */
public class CalcPaletteElementInserted extends OEDataModification
{

    private CalcPalette _parent;

    public CalcPaletteElementInserted(CalcPaletteElement element, CalcPalette parent)
    {
        super(null, element);
        _parent = parent;
    }

    @Override
    public CalcPaletteElement newValue()
    {
     	return (CalcPaletteElement)super.newValue();
    }
    

    public CalcPalette getParent()
    {
        return _parent;
    }

}
