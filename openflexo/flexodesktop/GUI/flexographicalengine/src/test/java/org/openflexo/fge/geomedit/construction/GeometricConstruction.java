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
package org.openflexo.fge.geomedit.construction;

import java.util.Vector;

import org.openflexo.inspector.DefaultInspectableObject;
import org.openflexo.xmlcode.XMLSerializable;


public abstract class GeometricConstruction<O extends Object> extends DefaultInspectableObject implements XMLSerializable {

	private O computedData;
	
	protected abstract O computeData();

	public final O getData()
	{		
		//System.out.println("getData() for "+this.getClass().getSimpleName());
		
		 ensureUpToDate();

		 if (computedData == null) {
			computedData = computeData();
		}
		
		
		return computedData;
	}
	
	private void ensureUpToDate()
	{
		ensureUpToDate(new Vector<GeometricConstruction>());
	}
	
	private void ensureUpToDate(Vector<GeometricConstruction> updatedConstructions)
	{
		// Recursively called ensureUpToDate() on dependancies
		if (getDepends() != null) 
			for (GeometricConstruction<?> c : getDepends()) {
				if (!c._altered.contains(this)) c._altered.add(this);
				c.ensureUpToDate(updatedConstructions);
			}
			
		if (modified || updatedConstructions.size() > 0) {
			//System.out.println("Recompute data for "+this.getClass().getSimpleName());
			computedData = computeData();
			updatedConstructions.add(this);
			modified = false;
		}
	}
	
	public final void refresh()
	{
		//System.out.println("Refresh for "+this.getClass().getSimpleName());
		if (getDepends() != null) for (GeometricConstruction c : getDepends()) c.refresh();
		computedData = computeData();
	}
	
	@Override
	public abstract String toString();
	
	public abstract GeometricConstruction[] getDepends();
	
	private boolean modified = true;
	
	protected void setModified()
	{
		modified = true;
		for (GeometricConstruction c : _altered) {
			c.computedData = null;
			c.setModified();
		}
	}
	
	private Vector<GeometricConstruction> _altered = new Vector<GeometricConstruction>();
	
	@Override
	public String getInspectorName()
	{
		// Never inspected alone
		return null;
	}
	
}
