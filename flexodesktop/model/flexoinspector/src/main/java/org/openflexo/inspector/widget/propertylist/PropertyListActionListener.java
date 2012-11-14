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
package org.openflexo.inspector.widget.propertylist;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.expr.Constant.ObjectSymbolicConstant;
import org.openflexo.antar.expr.Constant.StringConstant;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.Function;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.PropertyListAction;
import org.openflexo.kvc.KeyValueCoding;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class PropertyListActionListener implements ActionListener {

	private static final Logger logger = Logger.getLogger(PropertyListActionListener.class.getPackage().getName());

	private PropertyListAction _plAction;

	private InspectableObject _selectedObject;

	private Vector<InspectableObject> selectedObjects;

	private InspectableObject _model;

	private PropertyListTableModel _tableModel;

	public PropertyListActionListener(PropertyListAction plAction, PropertyListTableModel tableModel) {
		super();
		_plAction = plAction;
		_selectedObject = null;
		_tableModel = tableModel;
	}

	public boolean isAddAction() {
		return _plAction.type.equals("ADD");
	}

	public boolean isActive(InspectableObject selectedObject) {
		boolean returned = false;
		if (_plAction.type.equals("ADD")) {
			returned = true;
		} else if (_plAction.type.equals("DELETE")) {
			returned = selectedObject != null;
		} else if (_plAction.type.equals("ACTION")) {
			returned = selectedObject != null;
		} else if (_plAction.type.equals("STATIC_ACTION")) {
			return isAvailable(null);
		}
		if (_plAction.getIsAvailableExpression() != null && selectedObject != null && returned == true) {
			return isAvailable(selectedObject);
		}
		return returned;
	}

	private KeyValueCoding getTargetObject(KeyValueCoding sourceObject, String listAccessor) {
		if (listAccessor == null) {
			return null;
		}
		StringTokenizer strTok = new StringTokenizer(listAccessor, ".");
		String accessor;
		Object currentObject = sourceObject;
		while (strTok.hasMoreTokens() && currentObject != null && currentObject instanceof KeyValueCoding) {
			accessor = strTok.nextToken();
			if (strTok.hasMoreTokens()) {
				if (currentObject != null) {
					currentObject = ((KeyValueCoding) currentObject).objectForKey(accessor);
				}
			}
		}
		if (currentObject instanceof KeyValueCoding) {
			return (KeyValueCoding) currentObject;
		} else {
			if (currentObject == null) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not find target object : currentObject is null !");
				}
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not find target object : must be a KeyValueCoding object but is a " + currentObject.getClass());
				}
			}
			return null;
		}
	}

	private String getLastAccessor(String listAccessor) {
		if (listAccessor == null) {
			return null;
		}
		int lastDotPosition = listAccessor.lastIndexOf(".");
		if (lastDotPosition < 0) {
			return listAccessor;
		}
		return listAccessor.substring(lastDotPosition + 1, listAccessor.length());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (_plAction.type.equals(PropertyListAction.ADD_TYPE)) {
			performAction();
		} else if (_plAction.type.equals(PropertyListAction.STATIC_ACTION_TYPE)) {
			performAction();
		} else if (_plAction.type.equals(PropertyListAction.DELETE_TYPE)) {
			performAction(getSelectedObject(), getSelectedObjects());
		} else if (_plAction.type.equals(PropertyListAction.ACTION_TYPE)) {
			performAction(getSelectedObject(), getSelectedObjects());
		}
	}

	public InspectableObject getSelectedObject() {
		return _selectedObject;
	}

	public void setSelectedObject(InspectableObject selectedObject) {
		_selectedObject = selectedObject;
	}

	public InspectableObject getModel() {
		return _model;
	}

	public void setModel(InspectableObject model) {
		_model = model;
	}

	// TODO: rewrite this property with AnTAR (Expression.evaluateExpression())...
	protected void performAction() {
		if (getActionMethod(getSelectedObject()) != null) {
			Object[] params;
			if (_plAction.getMethodExpression() instanceof Function) {
				params = new Object[_plAction.getMethodExpressionArgs().size()];
				for (int i = 0; i < _plAction.getMethodExpressionArgs().size(); i++) {
					Expression arg = _plAction.getMethodExpressionArgs().get(i);
					if (arg instanceof StringConstant) {
						params[i] = ((StringConstant) arg).getValue();
					} else if (arg == ObjectSymbolicConstant.NULL) {
						params[i] = null;
					} else if (arg == ObjectSymbolicConstant.THIS) {
						params[i] = getSelectedObject();
					}
				}
			} else {
				params = new Object[0];
			}
			try {
				Object targetObject = getTargetObject(getModel(), _plAction.getMethodName());
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("invoking " + getActionMethod(getSelectedObject()) + " on object" + targetObject);
				}
				getActionMethod(getSelectedObject()).invoke(targetObject, params);
				_tableModel.fireTableDataChanged();
				_tableModel.getPropertyListWidget().updateWidgetFromModel();
			} catch (IllegalArgumentException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			}
		}
	}

	// TODO: rewrite this property with AnTAR (Expression.evaluateExpression())...
	protected void performAction(InspectableObject paramObject, Vector<InspectableObject> selectedObjects) {
		if (selectedObjects == null) {
			selectedObjects = new Vector<InspectableObject>();
		}
		if (paramObject != null && !selectedObjects.contains(paramObject)) {
			selectedObjects.add(paramObject);
		}
		for (InspectableObject object : selectedObjects) {
			if (getActionMethod(paramObject) != null) {
				Object[] params;
				if (_plAction.getMethodExpression() instanceof Function) {
					params = new Object[_plAction.getMethodExpressionArgs().size()];
					for (int i = 0; i < _plAction.getMethodExpressionArgs().size(); i++) {
						Expression arg = _plAction.getMethodExpressionArgs().get(i);
						if (arg instanceof StringConstant) {
							params[i] = ((StringConstant) arg).getValue();
						} else if (arg == ObjectSymbolicConstant.NULL) {
							params[i] = null;
						} else if (arg == ObjectSymbolicConstant.THIS) {
							params[i] = paramObject;
						}
					}
				} else {
					params = new Object[1];
					params[0] = object;
				}
				try {
					Object targetObject = getTargetObject(getModel(), _plAction.getMethodName());
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("invoking " + getActionMethod(paramObject) + " on object" + targetObject);
					}
					getActionMethod(paramObject).invoke(targetObject, params);
					_tableModel.fireTableDataChanged();
					_tableModel.getPropertyListWidget().updateWidgetFromModel();
				} catch (IllegalArgumentException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
					}
					e.printStackTrace();
				}
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not find method: " + _plAction.getMethodName());
				}
			}
		}
	}

	private Method _actionMethod;

	// TODO: rewrite this property with AnTAR (Expression.evaluateExpression())...
	private Method getActionMethod(InspectableObject paramObject) {
		if (_actionMethod == null) {
			String methodName = getLastAccessor(_plAction.getMethodName());
			if (getModel() != null) {
				KeyValueCoding targetObject = getTargetObject(getModel(), _plAction.getMethodName());
				if (targetObject == null) {
					return null;
				}
				Class targetClass = targetObject.getClass();
				Class[] methodClassParams = null;
				if (_plAction.getMethodExpression() instanceof Function) {
					methodClassParams = new Class[_plAction.getMethodExpressionArgs().size()];
					for (int i = 0; i < _plAction.getMethodExpressionArgs().size(); i++) {
						Expression arg = _plAction.getMethodExpressionArgs().get(i);
						if (arg instanceof StringConstant) {
							methodClassParams[i] = String.class;
						} else if (arg == ObjectSymbolicConstant.NULL) {
							methodClassParams[i] = Object.class;
						} else if (arg == ObjectSymbolicConstant.THIS) {
							methodClassParams[i] = paramObject.getClass();
						}
					}
				} else {
					if (_plAction.type.equals(PropertyListAction.ADD_TYPE) || _plAction.type.equals(PropertyListAction.STATIC_ACTION_TYPE)) {
						methodClassParams = new Class[0];
					} else if (_plAction.type.equals(PropertyListAction.DELETE_TYPE)
							|| _plAction.type.equals(PropertyListAction.ACTION_TYPE)) {
						if (paramObject != null) {
							methodClassParams = new Class[1];
							methodClassParams[0] = paramObject.getClass();
						}
					}
				}
				if (methodClassParams != null) {
					try {
						_actionMethod = lookupMethod(targetClass, methodName, methodClassParams);
					} catch (SecurityException e) {
						// Warns about the exception
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("SecurityException raised: " + e.getClass().getName() + ". See console for details.");
						}
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// Warns about the exception
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("NoSuchMethodException raised: unable to find method " + methodName + " for class "
									+ targetClass);
						}
						e.printStackTrace();
					}
				}
			}
		}
		return _actionMethod;
	}

	private Method _isAvailableMethod;

	// TODO: rewrite this property with AnTAR (Expression.evaluateExpression())...
	private Method getAvailableMethod(InspectableObject paramObject) {
		if (_isAvailableMethod == null) {
			String methodName = getLastAccessor(_plAction.getIsAvailableMethodName());
			if (methodName == null || methodName.trim().equals("")) {
				return null;
			}
			if (getModel() != null) {
				// System.out.println("Model: "+getModel());
				KeyValueCoding targetObject = getTargetObject(getModel(), _plAction.getIsAvailableMethodName());
				if (targetObject == null) {
					return null;
				}
				Class targetClass = targetObject.getClass();
				Class[] methodClassParams = null;
				if (_plAction.getIsAvailableExpression() instanceof Function) {
					methodClassParams = new Class[_plAction.getIsAvailableExpressionArgs().size()];
					for (int i = 0; i < _plAction.getIsAvailableExpressionArgs().size(); i++) {
						Expression arg = _plAction.getIsAvailableExpressionArgs().get(i);
						if (arg instanceof StringConstant) {
							methodClassParams[i] = String.class;
						} else if (arg == ObjectSymbolicConstant.NULL) {
							methodClassParams[i] = Object.class;
						} else if (arg == ObjectSymbolicConstant.THIS) {
							methodClassParams[i] = paramObject.getClass();
						}
					}
				} else {
					if (_plAction.type.equals(PropertyListAction.STATIC_ACTION_TYPE)) {
						methodClassParams = new Class[0];
					} else if (paramObject != null) {
						methodClassParams = new Class[1];
						methodClassParams[0] = paramObject.getClass();
					}
				}

				try {
					_isAvailableMethod = lookupMethod(targetClass, methodName, methodClassParams);
					// System.out.println("Found method "+_isAvailableMethod);
				} catch (SecurityException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("SecurityException raised. See console for details.");
					}
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// Warns about the exception
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("NoSuchMethodException raised. See console for details.");
					}
					e.printStackTrace();
				}

			} else {
				logger.info("Pas de modele");
			}
		}
		return _isAvailableMethod;
	}

	// TODO: rewrite this property with AnTAR (Expression.evaluateExpression())...
	private boolean isAvailable(InspectableObject paramObject) {
		if (getAvailableMethod(paramObject) != null) {
			// System.out.println("Available "+getAvailableMethod(paramObject));
			Object[] params;

			if (_plAction.getIsAvailableExpression() instanceof Function) {
				params = new Object[_plAction.getIsAvailableExpressionArgs().size()];
				for (int i = 0; i < _plAction.getIsAvailableExpressionArgs().size(); i++) {
					Expression arg = _plAction.getIsAvailableExpressionArgs().get(i);
					if (arg instanceof StringConstant) {
						params[i] = ((StringConstant) arg).getValue();
					} else if (arg == ObjectSymbolicConstant.NULL) {
						params[i] = null;
					} else if (arg == ObjectSymbolicConstant.THIS) {
						params[i] = paramObject;
					}
				}
			} else {

				if (getAvailableMethod(paramObject).getParameterTypes().length == 0) {
					params = new Object[0];
				} else if (getAvailableMethod(paramObject).getParameterTypes().length == 1) {
					params = new Object[1];
					params[0] = paramObject;
				} else {
					return false;
				}
			}
			try {
				Object targetObject = getTargetObject(getModel(), _plAction.getIsAvailableMethodName());
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("invoking " + getAvailableMethod(paramObject) + " on object" + targetObject);
				}
				return ((Boolean) getAvailableMethod(paramObject).invoke(targetObject, params)).booleanValue();
			} catch (IllegalArgumentException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			}
		}
		return false;
	}

	public Vector<InspectableObject> getSelectedObjects() {
		return selectedObjects;
	}

	public void setSelectedObjects(Vector<InspectableObject> selectedObjects) {
		this.selectedObjects = selectedObjects;
	}

	public static Method lookupMethod(Class aClass, String methodName, Class[] parameterTypes) throws SecurityException,
			NoSuchMethodException {
		/*String s = "";
		for (Class c : parameterTypes) s += c.getName()+" ";
		System.out.println("Search method: "+methodName+" ( "+s+")");*/
		if (parameterTypes == null || parameterTypes.length == 0) {
			return aClass.getMethod(methodName, parameterTypes);
		}
		if (parameterTypes.length == 1) {
			while (parameterTypes[0] != null) {
				try {
					return aClass.getMethod(methodName, parameterTypes);
				} catch (NoSuchMethodException e) {
				}
				parameterTypes[0] = parameterTypes[0].getSuperclass();
			}
			logger.warning("Could not find method " + methodName + " on " + aClass);
			throw new NoSuchMethodException();
		} else {
			// TODO same code for multiple parameters, trying to generalize parameters
			return aClass.getMethod(methodName, parameterTypes);
		}
	}

}
