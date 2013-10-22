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
package org.openflexo.fge.control.tools;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.GRParameter;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.kvc.KVCObservableObject;
import org.openflexo.kvc.KeyValueCoding;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.factory.CloneableProxyObject;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * Abstraction of a graphical property container synchronized with a selection<br>
 * <ul>
 * <li>If selection is empty, then manage a default value (this value will be used to build new objects)</li>
 * <li>If selection is unique, manage value of unique selection</li>
 * <li>If selection is multiple, manage value of entire selection, by batch</li>
 * </ul>
 * 
 * @author sylvain
 * 
 * @param <S>
 */
public abstract class InspectedStyle<S extends KeyValueCoding> extends KVCObservableObject implements PropertyChangeListener {

	private static final Logger logger = FlexoLogger.getLogger(InspectedStyle.class.getPackage().getName());

	private DianaInteractiveViewer<?, ?, ?> controller;
	private S defaultValue;

	private PropertyChangeSupport pcSupport;

	protected InspectedStyle(DianaInteractiveViewer<?, ?, ?> controller, S defaultValue) {
		this.controller = controller;
		this.defaultValue = defaultValue;
		pcSupport = new PropertyChangeSupport(this);
	}

	public DianaInteractiveViewer<?, ?, ?> getController() {
		return controller;
	}

	private Map<GRParameter<?>, Object> storedPropertyValues = new HashMap<GRParameter<?>, Object>();

	/**
	 * Return property value matching supplied parameter for current selection<br>
	 * <ul>
	 * <li>If selection is empty, then return default value (this value will be used to build new objects)</li>
	 * <li>If selection is unique, then return the right value</li>
	 * <li>If selection is multiple, return value of first selected object</li>
	 * </ul>
	 * Store the result for future comparison evaluations
	 * 
	 * @param parameter
	 * @return
	 */
	public <T> T getPropertyValue(GRParameter<T> parameter) {
		T returned = _getPropertyValue(parameter);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Requesting " + parameter + " for " + getSelection() + ", returning " + returned);
		}
		storedPropertyValues.put(parameter, returned);
		return returned;
	}

	/**
	 * Return property value matching supplied parameter for current selection<br>
	 * Do not store the result for future comparison evaluations
	 * 
	 * @param parameter
	 * @return
	 */
	private <T> T _getPropertyValue(GRParameter<T> parameter) {
		T returned;
		if (getSelection().size() == 0) {
			returned = (T) defaultValue.objectForKey(parameter.getName());
		} else {
			S style = getStyle(getSelection().get(0));
			returned = (T) style.objectForKey(parameter.getName());
		}
		return returned;
	}

	/**
	 * Sets property value matching supplied parameter for current selection, with supplied value<br>
	 * <ul>
	 * <li>If selection is empty, then sets default value (this value will be used to build new objects)</li>
	 * <li>If selection is unique, then sets the right value</li>
	 * <li>If selection is multiple, sets value of entire selection</li>
	 * </ul>
	 * Store the result for future comparison evaluations
	 * 
	 * @param parameter
	 * @param value
	 */
	public <T> void setPropertyValue(GRParameter<T> parameter, T value) {
		T oldValue = getPropertyValue(parameter);
		if (requireChange(oldValue, value)) {
			if (getSelection().size() == 0) {
				defaultValue.setObjectForKey(value, parameter.getName());
			} else {
				for (DrawingTreeNode<?, ?> n : getSelection()) {
					S style = getStyle(n);
					style.setObjectForKey(value, parameter.getName());
				}
			}
			storedPropertyValues.put(parameter, value);
			pcSupport.firePropertyChange(parameter.getName(), oldValue, value);
		}
	}

	/**
	 * Equals method allowing null values
	 * 
	 * @param oldObject
	 * @param newObject
	 * @return
	 */
	private boolean requireChange(Object oldObject, Object newObject) {
		if (oldObject == null) {
			if (newObject == null) {
				return false;
			} else {
				return true;
			}
		}
		return !oldObject.equals(newObject);
	}

	/**
	 * Abstract method returning sub-selection on which inspected style may apply (return all objects relevant for such style)
	 * 
	 * @return
	 */
	public abstract List<DrawingTreeNode<?, ?>> getSelection();

	/**
	 * Return relevant style for a given {@link DrawingTreeNode}
	 * 
	 * @param node
	 * @return
	 */
	public abstract S getStyle(DrawingTreeNode<?, ?> node);

	/**
	 * Generate new style using supplied factory and inspected property values
	 * 
	 * @param factory
	 * @return
	 */
	public S cloneStyle() {
		if (defaultValue instanceof CloneableProxyObject) {
			return (S) ((CloneableProxyObject) defaultValue).cloneObject();
		}
		logger.warning("Could not clone " + defaultValue);
		return defaultValue;
	}

	private List<S> inspectedStyles = new ArrayList<S>();

	public void fireSelectionUpdated() {
		Class<?> styleClass = TypeUtils.getBaseClass(TypeUtils.getTypeArgument(getClass(), InspectedStyle.class, 0));
		System.out.println("Selection changed for inspected style " + styleClass);
		for (GRParameter<?> p : GRParameter.getGRParameters(styleClass)) {
			Object storedValue = storedPropertyValues.get(p);
			Object newValue = _getPropertyValue(p);
			if (requireChange(storedValue, newValue)) {
				System.out.println("Notifying " + p.getName());
				pcSupport.firePropertyChange(p.getName(), storedValue, newValue);
			}
		}

		for (S s : inspectedStyles) {
			if (s instanceof HasPropertyChangeSupport) {
				((HasPropertyChangeSupport) s).getPropertyChangeSupport().removePropertyChangeListener(this);
			}/* else if (s instanceof Observable) {
				((Observable) s).deleteObserver(this);
				}*/
		}
		inspectedStyles.clear();
		for (DrawingTreeNode<?, ?> n : getSelection()) {
			S s = getStyle(n);
			if (s instanceof HasPropertyChangeSupport) {
				inspectedStyles.add(s);
				System.out.println("!!!!!!!!!!!!!!! Observing " + s + " for " + n);
				((HasPropertyChangeSupport) s).getPropertyChangeSupport().addPropertyChangeListener(this);
			}/* else if (s instanceof Observable) {
				inspectedStyles.add(s);
				((Observable) s).addObserver(this);
				}*/
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println("****************** PropertyChange with " + evt);
	}

	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	public String getDeletedProperty() {
		// Not relevant
		return null;
	}

	public FGEModelFactory getFactory() {
		// Not relevant
		return null;
	}

	public void setFactory(FGEModelFactory factory) {
		// Not relevant

	}

	public <T> void notifyChange(GRParameter<T> parameter, T oldValue, T newValue) {
		// Not relevant

	}

	public <T> void notifyChange(GRParameter<T> parameter) {
		// Not relevant

	}

	public <T> void notifyAttributeChange(GRParameter<T> parameter) {
		// Not relevant

	}

	public void notify(FGENotification notification) {
		// Not relevant

	}

	public boolean delete() {
		// Not relevant
		return false;
	}

	public boolean isDeleted() {
		// Not relevant
		return false;
	}

	public Object performSuperGetter(String propertyIdentifier) {
		// Not relevant
		return null;
	}

	public void performSuperSetter(String propertyIdentifier, Object value) {
		// Not relevant

	}

	public void performSuperAdder(String propertyIdentifier, Object value) {
		// Not relevant

	}

	public void performSuperRemover(String propertyIdentifier, Object value) {
		// Not relevant

	}

	public boolean performSuperDelete() {
		// Not relevant
		return false;
	}

	public boolean performSuperDelete(Object... context) {
		// Not relevant
		return false;
	}

	public Object performSuperGetter(String propertyIdentifier, Class<?> modelEntityInterface) {
		// Not relevant
		return null;
	}

	public void performSuperSetter(String propertyIdentifier, Object value, Class<?> modelEntityInterface) {
		// Not relevant

	}

	public void performSuperAdder(String propertyIdentifier, Object value, Class<?> modelEntityInterface) {
		// Not relevant

	}

	public void performSuperRemover(String propertyIdentifier, Object value, Class<?> modelEntityInterface) {
		// Not relevant

	}

	public void performSuperDelete(Class<?> modelEntityInterface) {
		// Not relevant

	}

	public void performSuperDelete(Class<?> modelEntityInterface, Object... context) {
		// Not relevant

	}

	public void performSuperSetModified(boolean modified) {
		// Not relevant

	}

	public Object performSuperFinder(String finderIdentifier, Object value) {
		// Not relevant
		return null;
	}

	public Object performSuperFinder(String finderIdentifier, Object value, Class<?> modelEntityInterface) {
		// Not relevant
		return null;
	}

	public boolean delete(Object... context) {
		// Not relevant
		return false;
	}

	public boolean isSerializing() {
		// Not relevant
		return false;
	}

	public boolean isDeserializing() {
		// Not relevant
		return false;
	}

	public boolean isModified() {
		// Not relevant
		return false;
	}

	public void setModified(boolean modified) {
		// Not relevant

	}

	public boolean equalsObject(Object obj) {
		// Not relevant
		return false;
	}

	public Object cloneObject() {
		// Not relevant
		return null;
	}

	public Object cloneObject(Object... context) {
		// Not relevant
		return null;
	}

	public boolean isCreatedByCloning() {
		// Not relevant
		return false;
	}

	public boolean isBeingCloned() {
		// Not relevant
		return false;
	}

	public Object clone() {
		// Not relevant
		return this;
	}
}
