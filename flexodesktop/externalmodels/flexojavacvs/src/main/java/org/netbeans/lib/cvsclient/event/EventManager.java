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

import java.io.File;

import org.netbeans.lib.cvsclient.ClientServices;

/**
 * This class is responsible for firing CVS events to registered listeners. It can either fire events as they are generated or wait until a
 * suitable checkpoint and fire many events at once. This can prevent event storms from degrading system performance.
 * 
 * @author Robert Greig
 */
public class EventManager {
	/**
	 * Registered listeners for events. This is an array for performance when firing events. We take the hit when adding or removing
	 * listeners - that should be a relatively rare occurrence.
	 */
	private CVSListener[] listeners;

	/**
	 * Holds value of property fireEnhancedEventSet. If true, the library fires the EnhancedMessageEvents. Default is true. Some builders
	 * might work badly, if set to false.
	 */
	private boolean fireEnhancedEventSet = true;

	private final ClientServices services;

	/**
	 * Construct a new EventManager
	 */
	public EventManager(ClientServices services) {
		this.services = services;
	}

	/**
	 * Returns Client services implementation tied to this event manager.
	 * 
	 * @return a ClientServices implementation
	 */
	public ClientServices getClientServices() {
		return services;
	}

	/**
	 * Add a listener to the list.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public synchronized void addCVSListener(CVSListener listener) {
		if (listeners == null || listeners.length == 0) {
			listeners = new CVSListener[1];
		} else {
			// allocate a new array and copy existing listeners
			CVSListener[] l = new CVSListener[listeners.length + 1];
			for (int i = 0; i < listeners.length; i++) {
				l[i] = listeners[i];
			}
			listeners = l;
		}
		listeners[listeners.length - 1] = listener;
	}

	/**
	 * Remove a listeners from the list
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public synchronized void removeCVSListener(CVSListener listener) {
		// TODO: test this method!!
		if (listeners.length == 1) {
			listeners = null;
		} else {
			CVSListener[] l = new CVSListener[listeners.length - 1];
			int i = 0;
			while (i < l.length) {
				if (listeners[i] == listener) {
					for (int j = i + 1; j < listeners.length; j++) {
						l[j - 1] = listeners[j];
					}
					break;
				} else {
					l[i] = listeners[i];
				}
				i++;
			}
			listeners = l;
		}
	}

	/**
	 * Fire a CVSEvent to all the listeners
	 * 
	 * @param e
	 *            the event to send
	 */
	public void fireCVSEvent(CVSEvent e) {
		// if we have no listeners, then there is nothing to do
		if (listeners == null || listeners.length == 0) {
			return;
		}
		if (e instanceof FileInfoEvent) {
			File file = ((FileInfoEvent) e).getInfoContainer().getFile();
			if (services.getGlobalOptions().isExcluded(file)) {
				return;
			}
		}
		CVSListener[] l = null;
		synchronized (listeners) {
			l = new CVSListener[listeners.length];
			System.arraycopy(listeners, 0, l, 0, l.length);
		}

		for (int i = 0; i < l.length; i++) {
			e.fireEvent(l[i]);
		}
	}

	/**
	 * Getter for property fireEnhancedEventSet.
	 * 
	 * @return Value of property fireEnhancedEventSet.
	 */
	public boolean isFireEnhancedEventSet() {
		return fireEnhancedEventSet;
	}

	/**
	 * Setter for property fireEnhancedEventSet.
	 * 
	 * @param fireEnhancedEventSet
	 *            New value of property fireEnhancedEventSet.
	 */
	public void setFireEnhancedEventSet(boolean fireEnhancedEventSet) {
		this.fireEnhancedEventSet = fireEnhancedEventSet;
	}

}
