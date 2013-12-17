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
package org.openflexo.foundation.validation;

import java.util.Hashtable;
import java.util.logging.Logger;

import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.TextFieldParameter;

/**
 * Automatic fix proposal with parameters
 * 
 * @author sguerin
 * 
 */
public abstract class ParameteredFixProposal<R extends ValidationRule<R, V>, V extends Validable> extends FixProposal<R, V> {

	private static final Logger logger = Logger.getLogger(ParameteredFixProposal.class.getPackage().getName());

	// private Hashtable _params;
	// private Hashtable _labels;

	private Hashtable _parameters;
	private ParameterDefinition[] _paramsArray;

	public ParameteredFixProposal(String aMessage, ParameterDefinition[] parameters) {
		super(aMessage);
		_parameters = new Hashtable();
		for (int i = 0; i < parameters.length; i++) {
			_parameters.put(parameters[i].getName(), parameters[i]);
		}
		_paramsArray = parameters;
	}

	public ParameteredFixProposal(String aMessage, String paramName, String paramLabel, String paramDefaultValue) {
		this(aMessage, singleParameterWith(paramName, paramLabel, paramDefaultValue));
	}

	private static ParameterDefinition[] singleParameterWith(String paramName, String paramLabel, String paramDefaultValue) {
		ParameterDefinition[] returned = { new TextFieldParameter(paramName, paramLabel, paramDefaultValue) };
		return returned;
	}

	public Object getValueForParameter(String name) {
		return ((ParameterDefinition) _parameters.get(name)).getValue();
	}

	public ParameterDefinition[] getParameters() {
		return _paramsArray;
	}

	public void updateBeforeApply() {
		// Override
	}
}
