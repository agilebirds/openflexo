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
package org.openflexo.fib.model;



public class FIBTab extends FIBPanel {

	private String title;	
	private int index;

	public static enum Parameters implements FIBModelAttribute
	{
		title,
		index
	}
		

	public int getIndex()
	{
		return index;
	}

	public void setIndex(int index)
	{
		FIBAttributeNotification<Integer> notification = requireChange(
				Parameters.index, index);
		if (notification != null) {
			this.index = index;
			hasChanged(notification);
		}
	}
	
	@Override
	public String getIdentifier()
	{
		return getTitle();
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		FIBAttributeNotification<String> notification = requireChange(
				Parameters.title, title);
		if (notification != null) {
			this.title = title;
			hasChanged(notification);
		}
	}

}
