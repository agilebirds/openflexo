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
package org.openflexo.foundation.action;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.logging.FlexoLogger;

public class ExecutionContext {

	static final Logger logger = FlexoLogger.getLogger(FlexoAction.class.getPackage().getName());

	FlexoAction<?, ?, ?> _action;
	private Hashtable<String, FlexoModelObject> _objectsCreatedWhileExecutingAction;
	private Hashtable<String, FlexoModelObject> _objectsDeletedWhileExecutingAction;

	public ExecutionContext(FlexoAction<?, ?, ?> action) {
		_action = action;
		_objectsCreatedWhileExecutingAction = new Hashtable<String, FlexoModelObject>();
		_objectsDeletedWhileExecutingAction = new Hashtable<String, FlexoModelObject>();
		_singleProperties = new Vector<SingleProperty<?>>();
		_vectorProperties = new Vector<VectorProperty<?>>();
	}

	public void objectCreated(String key, FlexoModelObject object) {
		_objectsCreatedWhileExecutingAction.put(key, object);
	}

	public void objectDeleted(String key, FlexoModelObject object) {
		_objectsDeletedWhileExecutingAction.put(key, object);
	}

	public Hashtable<String, FlexoModelObject> getObjectsCreatedWhileExecutingAction() {
		return _objectsCreatedWhileExecutingAction;
	}

	public Hashtable<String, FlexoModelObject> getObjectsDeletedWhileExecutingAction() {
		return _objectsDeletedWhileExecutingAction;
	}

	protected class SingleProperty<T> {
		protected String _propertyKey;
		protected T _originalValue;
		protected T _value;
		protected FlexoAction<?, ?, ?> _actionWhereThisValueWasInstanciated;
		protected String _keyOfActionWhereThisValueWasInstanciated;

		protected SingleProperty(String propertyKey, List<FlexoAction<?, ?, ?>> previouslyExecutedActions) {
			_propertyKey = propertyKey;
			_value = (T) _action.objectForKey(propertyKey);
			_originalValue = _value;
			for (FlexoAction<?, ?, ?> a : previouslyExecutedActions) {
				Hashtable<String, FlexoModelObject> objectsCreatedByAction = a.getExecutionContext()
						.getObjectsCreatedWhileExecutingAction();
				for (String k : objectsCreatedByAction.keySet()) {
					if (objectsCreatedByAction.get(k).equals(_value)) {
						_keyOfActionWhereThisValueWasInstanciated = k;
						_actionWhereThisValueWasInstanciated = a;
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("SingleProperty: " + propertyKey + ": Action " + _action + " property " + propertyKey
									+ " is a value created when executing action " + a.toSimpleString() + " for key " + k);
						}
					}
				}
			}
		}

		protected T getValue() {
			return _value;
		}

		protected void setValue(T newValue) {
			_action.setObjectForKey(newValue, _propertyKey);
			_value = newValue;
		}

		public void notifyExternalObjectCreatedByAction(T object, FlexoAction<?, ?, ?> action, String key, boolean considerAsOriginal) {
			if (action == _actionWhereThisValueWasInstanciated && key.equals(_keyOfActionWhereThisValueWasInstanciated)) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Object " + object + " was created with key " + key + " and this object is also referenced in " + _action
							+ " with property " + _propertyKey);
				}
				T oldValue = getValue();
				setValue(object);
				_action.replacedSinglePropertyValue(_propertyKey, object, oldValue, _originalValue);
				_originalValue = object;
			}
		}

		public void notifyExternalObjectDeletedByAction(T object, FlexoAction<?, ?, ?> action, String key, boolean considerAsOriginal) {
			if (action == _actionWhereThisValueWasInstanciated && key.equals(_keyOfActionWhereThisValueWasInstanciated)) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Object " + object + " was deleted with key " + key + " and this object is also referenced in " + _action
							+ " with property " + _propertyKey);
				}
				T oldValue = getValue();
				setValue(null);
				_action.replacedSinglePropertyValue(_propertyKey, null, oldValue, _originalValue);
			}
		}

	}

	protected class VectorProperty<T> {
		protected String _propertyKey;
		protected Vector<T> _values;
		protected Vector<PersistentValue> _persistentValues;

		protected class PersistentValue {
			protected T _value;
			protected FlexoAction<?, ?, ?> _actionWhereThisValueWasInstanciated;
			protected String _keyOfActionWhereThisValueWasInstanciated;
			protected int index;
			protected T _originalValue;
		}

		protected VectorProperty(String propertyKey, List<FlexoAction<?, ?, ?>> previouslyExecutedActions) {
			_propertyKey = propertyKey;
			_values = (Vector<T>) _action.objectForKey(propertyKey);

			_persistentValues = new Vector<PersistentValue>();

			// If there is no values, just return
			if (_values == null) {
				return;
			}

			int index = 0;
			for (T value : _values) {
				for (FlexoAction<?, ?, ?> a : previouslyExecutedActions) {
					Hashtable<String, FlexoModelObject> objectsCreatedByAction = a.getExecutionContext()
							.getObjectsCreatedWhileExecutingAction();
					for (String k : objectsCreatedByAction.keySet()) {
						if (objectsCreatedByAction.get(k).equals(value)) {
							PersistentValue newPersistentValue = new PersistentValue();
							newPersistentValue._value = value;
							newPersistentValue._originalValue = value;
							newPersistentValue._keyOfActionWhereThisValueWasInstanciated = k;
							newPersistentValue._actionWhereThisValueWasInstanciated = a;
							newPersistentValue.index = index;
							_persistentValues.add(newPersistentValue);
							if (logger.isLoggable(Level.INFO)) {
								logger.info("VectorProperty: " + propertyKey + " at index " + index + ": Action " + _action + " property "
										+ propertyKey + " is a value created when executing action " + a.toSimpleString() + " for key " + k);
							}
						}
					}
				}
				index++;
			}

		}

		protected Vector<T> getValues() {
			return _values;
		}

		protected T getValueAtIndex(int index) {
			if (_values != null && index < _values.size()) {
				return _values.get(index);
			}
			return null;
		}

		protected void setValueAtIndex(T value, int index) {
			if (_values != null && index < _values.size()) {
				_values.setElementAt(value, index);
			}
		}

		public void notifyExternalObjectCreatedByAction(T object, FlexoAction<?, ?, ?> action, String key, boolean considerAsOriginal) {
			for (PersistentValue persistentValue : _persistentValues) {
				if (logger.isLoggable(Level.FINEST)) {
					logger.finest("persistentValue action=" + persistentValue._actionWhereThisValueWasInstanciated + " key="
							+ persistentValue._keyOfActionWhereThisValueWasInstanciated + " index=" + persistentValue.index);
				}
				if (action == persistentValue._actionWhereThisValueWasInstanciated
						&& key.equals(persistentValue._keyOfActionWhereThisValueWasInstanciated)) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Object " + object + " was created with key " + key + " and this object is also referenced in "
								+ _action + " with property " + _propertyKey + " at index " + persistentValue.index);
					}
					T oldValue = getValueAtIndex(persistentValue.index);
					setValueAtIndex(object, persistentValue.index);
					_action.replacedVectorPropertyValue(_propertyKey, persistentValue.index, object, oldValue,
							persistentValue._originalValue);
					persistentValue._originalValue = object;
				}
			}
		}

		public void notifyExternalObjectDeletedByAction(T object, FlexoAction<?, ?, ?> action, String key, boolean considerAsOriginal) {
			for (PersistentValue persistentValue : _persistentValues) {
				if (action == persistentValue._actionWhereThisValueWasInstanciated
						&& key.equals(persistentValue._keyOfActionWhereThisValueWasInstanciated)) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Object " + object + " was deleted with key " + key + " and this object is also referenced in "
								+ _action + " with property " + _propertyKey + " at index " + persistentValue.index);
					}
					T oldValue = getValueAtIndex(persistentValue.index);
					setValueAtIndex(null, persistentValue.index);
					_action.replacedVectorPropertyValue(_propertyKey, persistentValue.index, null, oldValue, persistentValue._originalValue);
				}
			}
		}
	}

	private Vector<SingleProperty<?>> _singleProperties;
	private Vector<VectorProperty<?>> _vectorProperties;

	public void saveExecutionContext(List<FlexoAction<?, ?, ?>> previouslyExecutedActions) {
		Vector<String> allProperties = new Vector<String>();
		allProperties.add("focusedObject");
		allProperties.add("globalSelection");
		for (String k : _action.getActionType().getPersistentProperties()) {
			allProperties.add(k);
		}

		for (String key : allProperties) {
			if (_action.isSingleProperty(key)) {
				_singleProperties.add(createNewSingleProperty(key, _action.getTypeForKey(key), previouslyExecutedActions));
			} else if (_action.isVectorProperty(key)) {
				_vectorProperties.add(createNewVectorProperty(key, _action.getTypeForKey(key), previouslyExecutedActions));
			}
		}

		for (SingleProperty<?> sp : _singleProperties) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Created SingleProperty " + sp._propertyKey + " value: " + sp.getValue());
			}
		}
		for (VectorProperty<?> sp : _vectorProperties) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Created VectorProperty " + sp._propertyKey + " value: " + sp.getValues());
			}
		}
	}

	public void notifyExternalObjectCreatedByAction(FlexoModelObject object, FlexoAction<?, ?, ?> action, String key,
			boolean considerAsOriginal) {
		for (SingleProperty sp : _singleProperties) {
			sp.notifyExternalObjectCreatedByAction(object, action, key, considerAsOriginal);
		}
		for (VectorProperty sp : _vectorProperties) {
			sp.notifyExternalObjectCreatedByAction(object, action, key, considerAsOriginal);
		}
	}

	public void notifyExternalObjectDeletedByAction(FlexoModelObject object, FlexoAction<?, ?, ?> action, String key,
			boolean considerAsOriginal) {
		for (SingleProperty sp : _singleProperties) {
			sp.notifyExternalObjectDeletedByAction(object, action, key, considerAsOriginal);
		}
		for (VectorProperty sp : _vectorProperties) {
			sp.notifyExternalObjectDeletedByAction(object, action, key, considerAsOriginal);
		}
	}

	private <T> SingleProperty<T> createNewSingleProperty(String key, Class<T> type, List<FlexoAction<?, ?, ?>> previouslyExecutedActions) {
		return new SingleProperty<T>(key, previouslyExecutedActions);
	}

	private <T> VectorProperty<T> createNewVectorProperty(String key, Class<T> type, List<FlexoAction<?, ?, ?>> previouslyExecutedActions) {
		return new VectorProperty<T>(key, previouslyExecutedActions);
	}

	public void delete() {
		_objectsCreatedWhileExecutingAction.clear();
		_objectsDeletedWhileExecutingAction.clear();
		_singleProperties.clear();
		_vectorProperties.clear();
	}

}
