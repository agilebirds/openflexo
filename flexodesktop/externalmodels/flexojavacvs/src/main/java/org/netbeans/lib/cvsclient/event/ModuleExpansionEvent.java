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

package org.netbeans.lib.cvsclient.event;

/**
 * This event is really intended only for the use in the Checkout command. During a checkout command, the client must ask the server to
 * expand modules to determine whether there are aliases defined for a particular module. The client must then use the expansion to
 * determine if a local directory exists and if so, send appropriate Modified requests etc.
 * 
 * @author Robert Greig
 */
public class ModuleExpansionEvent extends CVSEvent {
	/**
	 * The expanded module name
	 */
	private String module;

	/**
	 * Creates new ModuleExpansionEvent.
	 * 
	 * @param source
	 *            the source of the event
	 * @param theModule
	 *            the module name that the original request has been "expanded" to.
	 */
	public ModuleExpansionEvent(Object source, String module) {
		super(source);
		this.module = module;
	}

	/**
	 * Get the module name that the original module name has been expanded to.
	 * 
	 * @return the expanded name
	 */
	public String getModule() {
		return module;
	}

	/**
	 * Fire the event to the event listener. Subclasses should call the appropriate method on the listener to dispatch this event.
	 * 
	 * @param listener
	 *            the event listener
	 */
	@Override
	protected void fireEvent(CVSListener listener) {
		listener.moduleExpanded(this);
	}
}
