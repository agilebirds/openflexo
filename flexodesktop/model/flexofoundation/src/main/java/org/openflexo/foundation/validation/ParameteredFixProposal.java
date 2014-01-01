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

import java.util.Collection;
import java.util.Hashtable;
import java.util.logging.Logger;

/**
 * Automatic fix proposal with parameters
 * 
 * @author sguerin
 * 
 */
public abstract class ParameteredFixProposal<R extends ValidationRule<R, V>, V extends Validable> extends FixProposal<R, V> {

	private static final Logger logger = Logger.getLogger(ParameteredFixProposal.class.getPackage().getName());

	private Hashtable<String, ParameterDefinition<?>> parameters;

	public ParameteredFixProposal(String aMessage, ParameterDefinition<?>[] parameters) {
		super(aMessage);
		this.parameters = new Hashtable<String, ParameterDefinition<?>>();
		for (int i = 0; i < parameters.length; i++) {
			this.parameters.put(parameters[i].getName(), parameters[i]);
		}
	}

	public ParameteredFixProposal(String aMessage, String paramName, String paramLabel, String paramDefaultValue) {
		this(aMessage, singleParameterWith(paramName, paramLabel, paramDefaultValue));
	}

	private static ParameterDefinition<?>[] singleParameterWith(String paramName, String paramLabel, String paramDefaultValue) {
		ParameterDefinition<?>[] returned = { new StringParameter(paramName, paramLabel, paramDefaultValue) };
		return returned;
	}

	public Object getValueForParameter(String name) {
		return ((ParameterDefinition<?>) parameters.get(name)).getValue();
	}

	public Collection<ParameterDefinition<?>> getParameters() {
		return parameters.values();
	}

	public void updateBeforeApply() {
		// Override
	}

	public static class ParameterDefinition<T> {

		private String name;
		private String label;
		private T value;

		public ParameterDefinition(String name, String label, T value) {
			super();
			this.name = name;
			this.label = label;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public T getValue() {
			return value;
		}

		public void setValue(T value) {
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}
	}

	public static class StringParameter extends ParameterDefinition<String> {

		public StringParameter(String name, String label, String value) {
			super(name, label, value);
		}
	}
}
