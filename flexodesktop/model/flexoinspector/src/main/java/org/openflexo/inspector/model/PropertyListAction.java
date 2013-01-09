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
package org.openflexo.inspector.model;

import java.util.List;
import java.util.Vector;

import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.Variable;

public class PropertyListAction extends ParametersContainerModelObject {
	public static final String ADD_TYPE = "ADD";
	public static final String DELETE_TYPE = "DELETE";
	public static final String ACTION_TYPE = "ACTION";
	public static final String STATIC_ACTION_TYPE = "STATIC_ACTION";

	public String name;

	public String help;

	public String type;

	private String method;
	private String isAvailable;

	private Expression methodExpression;
	private List<Expression> methodExpressionArgs;

	private Expression isAvailableExpression;
	private List<Expression> isAvailableExpressionArgs;

	private Vector<String> availableActionType;

	public Vector<String> getAvailableActionType() {
		if (availableActionType == null) {
			availableActionType = new Vector<String>();
			availableActionType.add(ADD_TYPE);
			availableActionType.add(DELETE_TYPE);
			availableActionType.add(ACTION_TYPE);
			availableActionType.add(STATIC_ACTION_TYPE);
		}
		return availableActionType;
	}

	private PropertyListModel _propertyListModel = null;

	public PropertyListAction() {
		super();
	}

	public PropertyListModel getPropertyListModel() {
		return _propertyListModel;
	}

	public void setPropertyListModel(PropertyListModel propertyListModel) {
		_propertyListModel = propertyListModel;
	}

	public String _getMethod() {
		return method;
	}

	@Deprecated
	// Not working anymore since related code in AnTAR has disappeared
	public void _setMethod(String method) {
		this.method = method;
		/*DefaultExpressionParser parser = new DefaultExpressionParser();
		try {
			methodExpression = parser.parse(method, null);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (methodExpression instanceof Function) {
			// System.out.println("Parsed "+method+" as a function");
			methodExpressionArgs = ((Function) methodExpression).getArgs();
		}*/
	}

	public Expression getMethodExpression() {
		return methodExpression;
	}

	@Deprecated
	// Not working anymore since related code in AnTAR has disappeared
	public String getMethodName() {
		if (methodExpression instanceof Variable) {
			return ((Variable) methodExpression).getName();
		}
		/*if (methodExpression instanceof Function) {
			return ((Function) methodExpression).getName();
		}*/
		return method;
	}

	public List<Expression> getMethodExpressionArgs() {
		return methodExpressionArgs;
	}

	public String _getIsAvailable() {
		return isAvailable;
	}

	@Deprecated
	// Not working anymore since related code in AnTAR has disappeared
	public void _setIsAvailable(String isAvailable) {
		this.isAvailable = isAvailable;
		/*	DefaultExpressionParser parser = new DefaultExpressionParser();
			try {
				isAvailableExpression = parser.parse(isAvailable, null);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			if (isAvailableExpression instanceof Function) {
				// System.out.println("Parsed "+isAvailable+" as a function");
				isAvailableExpressionArgs = ((Function) isAvailableExpression).getArgs();
			}*/
	}

	public Expression getIsAvailableExpression() {
		return isAvailableExpression;
	}

	@Deprecated
	// Not working anymore since related code in AnTAR has disappeared
	public String getIsAvailableMethodName() {
		if (isAvailableExpression instanceof Variable) {
			return ((Variable) isAvailableExpression).getName();
		}
		/*if (isAvailableExpression instanceof Function) {
			return ((Function) isAvailableExpression).getName();
		}*/
		return isAvailable;
	}

	public List<Expression> getIsAvailableExpressionArgs() {
		return isAvailableExpressionArgs;
	}

}
