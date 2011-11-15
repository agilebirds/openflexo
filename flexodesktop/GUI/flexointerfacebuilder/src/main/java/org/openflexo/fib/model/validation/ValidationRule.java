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
package org.openflexo.fib.model.validation;

import java.util.logging.Logger;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.localization.FlexoLocalization;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class ValidationRule<R extends ValidationRule<R, C>, C extends FIBModelObject> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ValidationRule.class.getPackage().getName());

	protected String _ruleName;

	private boolean isEnabled = true;

	private String _ruleDescription;

	private Class<? super C> _objectType;

	private String _typeName;

	public ValidationRule(Class<? super C> objectType, String ruleName) {
		super();
		_ruleName = ruleName;
		_ruleDescription = ruleName + "_description";
		getLocalizedName();
		getLocalizedDescription();
		_objectType = objectType;
	}

	public abstract ValidationIssue<R, C> applyValidation(final C object);

	public Class<? super C> getObjectType() {
		return _objectType;
	}

	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(_ruleName);
	}

	public String getLocalizedDescription() {
		return FlexoLocalization.localizedForKey(_ruleDescription);
	}

	public String getNameKey() {
		return _ruleName;
	}

	public String getDescriptionKey() {
		return _ruleDescription;
	}

	public String getTypeName() {
		if (_typeName == null) {
			_typeName = _objectType.getSimpleName();
		}
		return _typeName;
	}

	public boolean getIsEnabled() {
		return isEnabled;
	}

	public void setIsEnabled(boolean v) {
		isEnabled = v;
	}
}
