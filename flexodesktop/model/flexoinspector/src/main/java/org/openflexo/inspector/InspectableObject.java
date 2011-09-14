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

import java.util.Vector;

import org.openflexo.inspector.model.TabModel;
import org.openflexo.kvc.KeyValueCoding;


/**
 * Interface implemented by inspectable objects
 * 
 * @author bmangez
 */
public interface InspectableObject extends KeyValueCoding
{

    public void deleteInspectorObserver(InspectorObserver obs);

    public void addInspectorObserver(InspectorObserver obs);

    /**
     * Return String uniquely identifying inspector template which must be
     * applied when trying to inspect this object
     * 
     * @return a String value
     */
    public String getInspectorName();

    public String getInspectorTitle();

	public boolean isDeleted();
	
	public Vector<TabModel> inspectionExtraTabs();
}
