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
package org.openflexo.foundation;

import java.util.Vector;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.localization.FlexoLocalization;

/**
 * @author gpolet
 *
 */
public abstract class TargetType extends FlexoModelObject
{
    public TargetType(FlexoProject project) {
		super(project);
	}

    @Override
	public abstract String getName();

    public abstract String getTemplateFolderName();

    public abstract Vector<Format> getAvailableFormats();
    
    @Override
    public String getClassNameKey()
    {
    	return "target_type";
    }
    
    /**
     * @return
     */
    public String getLocalizedName()
    {
        return FlexoLocalization.localizedForKey(getName());
    }

    /**
     * Overrides toString
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getName();
    }
    
}
