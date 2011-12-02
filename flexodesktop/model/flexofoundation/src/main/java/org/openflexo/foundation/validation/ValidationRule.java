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

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.TargetType;
import org.openflexo.localization.FlexoLocalization;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class ValidationRule<R extends ValidationRule<R, V>, V extends Validable> extends FlexoObject {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ValidationRule.class.getPackage().getName());

	protected String _ruleName;

	private boolean isEnabled = true;

	private String _ruleDescription;

	private Class<? super V> _objectType;

	private String _typeName;

	public ValidationRule(Class<? super V> objectType, String ruleName) {
		super();
		_ruleName = ruleName;
		_ruleDescription = ruleName + "_description";
		getLocalizedName();
		getLocalizedDescription();
		_objectType = objectType;
	}

	public abstract ValidationIssue<R, V> applyValidation(final V object);

	public Class<? super V> getObjectType() {
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

	public boolean isValidForTarget(TargetType targetType) {
		return true;
	}

	public boolean getIsEnabled() {
		return isEnabled;
	}

	public void setIsEnabled(boolean v) {
		isEnabled = v;
	}
}
