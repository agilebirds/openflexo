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

import java.util.Enumeration;
import java.util.Vector;

import org.openflexo.foundation.utils.FlexoListModel;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class ValidationRuleSet extends FlexoListModel {

	protected Class _type;

	protected Vector<ValidationRule> _rules;

	public ValidationRuleSet(Class type) {
		super();
		_type = type;
		_rules = new Vector<ValidationRule>();
	}

	@Override
	public String getDeletedProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param type1
	 * @param vector
	 */
	public ValidationRuleSet(Class type, Vector<ValidationRule> vectorOfRules) {
		this(type);
		_rules = new Vector<ValidationRule>(vectorOfRules);
	}

	public void addRule(ValidationRule rule) {
		if (!_rules.contains(rule)) {
			_rules.add(rule);
		}
	}

	public void removeRule(ValidationRule rule) {
		if (_rules.contains(rule)) {
			_rules.remove(rule);
		}
	}

	public Enumeration<ValidationRule> elements() {
		return _rules.elements();
	}

	/**
	 * Implements
	 * 
	 * @see javax.swing.ListModel#getSize()
	 * @see javax.swing.ListModel#getSize()
	 */
	@Override
	public int getSize() {
		return _rules.size();
	}

	/**
	 * Implements
	 * 
	 * @see javax.swing.ListModel#getElementAt(int)
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	@Override
	public ValidationRule getElementAt(int index) {
		return _rules.get(index);
	}

	public Class getType() {
		return _type;
	}

	public Vector<ValidationRule> getRules() {
		return _rules;
	}

	private String _typeName;

	public String getTypeName() {
		if (_typeName == null) {
			_typeName = getType().getSimpleName();
		}
		return _typeName;
	}

}
