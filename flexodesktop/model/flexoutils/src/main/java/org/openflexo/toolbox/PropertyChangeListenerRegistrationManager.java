package org.openflexo.toolbox;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class PropertyChangeListenerRegistrationManager {

	private List<PropertyChangeListenerRegistration> registrations;

	public PropertyChangeListenerRegistrationManager() {
		registrations = new Vector<PropertyChangeListenerRegistrationManager.PropertyChangeListenerRegistration>();
	}

	public void delete() {
		for (PropertyChangeListenerRegistration registration : new ArrayList<PropertyChangeListenerRegistration>(registrations)) {
			registration.removeListener();
		}
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

	public void removeListener(String property, PropertyChangeListener listener, HasPropertyChangeSupport next) {
		for (PropertyChangeListenerRegistration r : registrations) {
			if (r.hasPropertyChangeSupport == next
					&& (r.propertyName == null && property == null || property != null && property.equals(r.propertyName))) {
				r.removeListener();
			}
		}
	}

}
