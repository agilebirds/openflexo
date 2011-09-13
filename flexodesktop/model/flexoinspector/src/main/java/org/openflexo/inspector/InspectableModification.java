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
package org.openflexo.inspector;

/**
 * @author bmangez
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public interface InspectableModification
{

    public String propertyName();

    public Object newValue();

    /**
     * Returns a flag indicating if this notification should be propagated to the widget
     * forcing widget to be updating from the model, even if the source of modification
     * is the inspected widget itself. Typically this value is false, except when a change
     * will modify the same property itself that should be refreshed.
     * 
     * @return
     */
    public boolean isReentrant();
    
}
