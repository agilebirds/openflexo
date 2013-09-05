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

import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.FlexoListModel;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.localization.FlexoLocalization;

/**
 * Represents a validation issue embedded in a validation report
 * 
 * @author sguerin
 * 
 */
public abstract class ValidationIssue<R extends ValidationRule<R, V>, V extends Validable> extends FlexoListModel implements FlexoObserver {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ValidationIssue.class.getPackage().getName());

	private V _object;

	private String _message;

	private String _localizedMessage;

	private ValidationReport _validationReport;

	private R _cause;

	private boolean _isLocalized;

	public ValidationIssue(V anObject, String aLocalizedMessage) {
		this(anObject, aLocalizedMessage, true);
	}

	public ValidationIssue(V anObject, String aMessage, boolean isLocalized) {
		super();
		_object = anObject;
		_message = aMessage;
		_isLocalized = isLocalized;
		if (!isLocalized) {
			_localizedMessage = aMessage;
		}
		if (_object instanceof FlexoObservable) {
			((FlexoObservable) _object).addObserver(this);
		}
	}

	public FlexoProject getProject() {
		if (_object != null && _object instanceof FlexoModelObject) {
			return ((FlexoModelObject) _object).getProject();
		}
		return null;
	}

	public String getMessage() {
		return _message;
	}

	public String getLocalizedMessage() {
		if (_localizedMessage == null && _message != null && _isLocalized) {
			_localizedMessage = FlexoLocalization.localizedForKeyWithParams(_message, this);
		}
		return _localizedMessage;
	}

	public void setMessage(String message) {
		this._message = message;
	}

	public V getObject() {
		return _object;
	}

	// TODO : Check if this is ok => generalized to fix a bug in selection of ViewPointObjects in Viewpoint validation tool
	public FlexoObject getSelectableObject() {
		if (_object instanceof FlexoModelObject || _object instanceof ViewPointObject) {
			return (FlexoObject) _object;
		}
		return null;
	}

	@Override
	public int getSize() {
		return 0;
	}

	@Override
	public Object getElementAt(int index) {
		return null;
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
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable instanceof FlexoModelObject) {
			if (((FlexoModelObject) observable).isDeleted()) {
				delete();
			}
		}

	}

	public void delete() {
		if (_object instanceof FlexoObservable) {
			((FlexoObservable) _object).deleteObserver(this);
		}
		_validationReport.removeFromValidationIssues(this);
		setChanged();
		notifyObservers(new DataModification(DELETED_PROPERTY, this, null));
	}

	@Override
	public String getDeletedProperty() {
		return DELETED_PROPERTY;
	}

	public void setLocalized(boolean localized) {
		_isLocalized = localized;
		if (!localized) {
			_localizedMessage = null;
		} else {
			_localizedMessage = _message;
		}
	}
}
