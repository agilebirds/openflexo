/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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
package org.openflexo.fge.control.notifications;

/**
 * Such notification are thrown by tools
 * 
 * @author sylvain
 * 
 */
public class ControlNotification {

	public Object oldValue;
	public Object newValue;
	private String eventName;

	public ControlNotification(String eventName, Object oldValue, Object newValue) {
		super();
		this.eventName = eventName;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	@Override
	public String toString() {
		return "ControlNotification of " + getClass().getSimpleName() + " " + eventName() + " old: " + oldValue + " new: " + newValue;
	}

	public String eventName() {
		return eventName;
	}

}