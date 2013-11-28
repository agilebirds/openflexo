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

import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.LocalizedDelegate;
import org.openflexo.localization.LocalizedDelegateImpl;
import org.openflexo.toolbox.FileResource;

/**
 * Represents a validation issue embedded in a validation report
 * 
 * @author sguerin
 * 
 */
public abstract class ValidationIssue<R extends ValidationRule<R, C>, C extends FIBModelObject> implements Observer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ValidationIssue.class.getPackage().getName());

	public static LocalizedDelegate VALIDATION_LOCALIZATION = new LocalizedDelegateImpl(new FileResource("FIBValidationLocalized"), null,
			true);

	private C _object;

	private String _message;

	private String _localizedMessage;

	private ValidationReport _validationReport;

	private R _cause;

	private boolean _isLocalized;

	public ValidationIssue(C anObject, String aLocalizedMessage) {
		this(anObject, aLocalizedMessage, true);
	}

	public ValidationIssue(C anObject, String aMessage, boolean isLocalized) {
		super();
		_object = anObject;
		_message = aMessage;
		_isLocalized = isLocalized;
		if (!isLocalized) {
			_localizedMessage = aMessage;
		}
		_object.addObserver(this);

	}

	public String getMessage() {
		return _message;
	}

	public String getLocalizedMessage() {
		if (_localizedMessage == null && _message != null && _isLocalized) {
			_localizedMessage = FlexoLocalization.localizedForKeyWithParams(VALIDATION_LOCALIZATION, _message, this);
		}
		return _localizedMessage;
	}

	public void setMessage(String message) {
		this._message = message;
	}

	public C getObject() {
		return _object;
	}

	public void setValidationReport(ValidationReport report) {
		_validationReport = report;
	}

	public ValidationReport getValidationReport() {
		return _validationReport;
	}

	private String _typeName;

	public String getTypeName() {
		if (_typeName == null) {
			StringTokenizer st = new StringTokenizer(getObject().getClass().getName(), ".");
			while (st.hasMoreTokens()) {
				_typeName = st.nextToken();
			}
		}
		return _typeName;
	}

	@Override
	public abstract String toString();

	public void setCause(R rule) {
		_cause = rule;
	}

	public R getCause() {
		return _cause;
	}

	@Override
	public void update(Observable observable, Object dataModification) {
		// System.out.println("Received " + dataModification);
	}

	public void delete() {
		_object.deleteObserver(this);
		_validationReport.removeFromValidationIssues(this);
	}

	public void setLocalized(boolean localized) {
		_isLocalized = localized;
		if (!localized) {
			_localizedMessage = null;
		} else {
			_localizedMessage = _message;
		}
	}

	public boolean isProblemIssue() {
		return false;
	}
}
