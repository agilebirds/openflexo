package org.openflexo.toolbox;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class PropertyChangeListenerRegistrationManager {

	private List<PropertyChangeListenerRegistration> registrations;

	public PropertyChangeListenerRegistrationManager() {
		registrations = new Vector<PropertyChangeListenerRegistrationManager.PropertyChangeListenerRegistration>();
	}

	public boolean hasListener(String propertyName, PropertyChangeListener listener, HasPropertyChangeSupport hasPropertyChangeSupport) {
		for (PropertyChangeListenerRegistration registration : registrations) {
			if (registration.hasPropertyChangeSupport == hasPropertyChangeSupport && registration.listener == listener
					&& registration.propertyName == null && propertyName == null || registration.propertyName != null
					&& registration.propertyName.equals(propertyName)) {
				return true;
			}
		}
		return false;
	}

	public void delete() {
		for (PropertyChangeListenerRegistration registration : new ArrayList<PropertyChangeListenerRegistration>(registrations)) {
			registration.removeListener();
		}
		// Just to be sure
		registrations.clear();
	}

	public class PropertyChangeListenerRegistration {
		private final String propertyName;
		private final PropertyChangeListener listener;
		private final HasPropertyChangeSupport hasPropertyChangeSupport;

		public PropertyChangeListenerRegistration(PropertyChangeListener listener, HasPropertyChangeSupport hasPropertyChangeSupport) {
			this(null, listener, hasPropertyChangeSupport);
		}

		public PropertyChangeListenerRegistration(String propertyName, PropertyChangeListener listener,
				HasPropertyChangeSupport hasPropertyChangeSupport) {
			this.propertyName = propertyName;
			this.listener = listener;
			this.hasPropertyChangeSupport = hasPropertyChangeSupport;
			if (propertyName != null) {
				hasPropertyChangeSupport.getPropertyChangeSupport().addPropertyChangeListener(propertyName, listener);
			} else {
				hasPropertyChangeSupport.getPropertyChangeSupport().addPropertyChangeListener(listener);
			}
			registrations.add(this);
		}

		public void removeListener() {
			if (propertyName != null) {
				hasPropertyChangeSupport.getPropertyChangeSupport().removePropertyChangeListener(propertyName, listener);
			} else {
				hasPropertyChangeSupport.getPropertyChangeSupport().removePropertyChangeListener(listener);
			}
			registrations.remove(this);
		}
	}

	public void addListener(PropertyChangeListener listener, HasPropertyChangeSupport hasPropertyChangeSupport) {
		addListener(null, listener, hasPropertyChangeSupport);
	}

	public void addListener(String propertyName, PropertyChangeListener listener, HasPropertyChangeSupport hasPropertyChangeSupport) {
		new PropertyChangeListenerRegistration(propertyName, listener, hasPropertyChangeSupport);
	}

	public void removeListener(PropertyChangeListener listener, HasPropertyChangeSupport hasPropertyChangeSupport) {
		removeListener(null, listener, hasPropertyChangeSupport);
	}

	public void removeListener(String propertyName, PropertyChangeListener listener, HasPropertyChangeSupport hasPropertyChangeSupport) {
		Iterator<PropertyChangeListenerRegistration> i = registrations.iterator();
		while (i.hasNext()) {
			PropertyChangeListenerRegistration r = i.next();
			if (r.hasPropertyChangeSupport == hasPropertyChangeSupport
					&& (r.propertyName == null && propertyName == null || propertyName != null && propertyName.equals(r.propertyName))
					&& r.listener == listener) {
				i.remove();
			}
		}
	}

}
