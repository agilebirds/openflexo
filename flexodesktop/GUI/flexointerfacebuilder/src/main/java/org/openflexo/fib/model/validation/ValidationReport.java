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

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.localization.FlexoLocalization;

/**
 * Represents a validation report
 * 
 * @author sguerin
 * 
 */
public class ValidationReport {

	private class ValidationIssueVector extends Vector<ValidationIssue> {

		Vector<InformationIssue> infoIssue;
		Vector<ValidationError> errors;
		Vector<ValidationWarning> warnings;

		/**
         * 
         */
		public ValidationIssueVector() {
			infoIssue = new Vector<InformationIssue>();
			errors = new Vector<ValidationError>();
			warnings = new Vector<ValidationWarning>();
		}

		/**
		 * Overrides add
		 * 
		 * @see java.util.Vector#add(java.lang.Object)
		 */
		@Override
		public synchronized boolean add(ValidationIssue o) {
			if (o instanceof InformationIssue) {
				infoIssue.add((InformationIssue) o);
			} else if (o instanceof ValidationError) {
				errors.add((ValidationError) o);
			} else if (o instanceof ValidationWarning) {
				warnings.add((ValidationWarning) o);
			}
			return super.add(o);
		}

		/**
		 * Overrides remove
		 * 
		 * @see java.util.Vector#remove(java.lang.Object)
		 */
		@Override
		public boolean remove(Object o) {
			if (o instanceof InformationIssue) {
				infoIssue.remove(o);
			} else if (o instanceof ValidationError) {
				errors.remove(o);
			} else if (o instanceof ValidationWarning) {
				warnings.remove(o);
			}
			return super.remove(o);
		}
	}

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ValidationReport.class.getPackage().getName());

	private FIBModelObject _rootObject;

	private ValidationIssueVector _validationIssues;

	private int _infosNb = 0;

	private int _warningNb = 0;

	private int _errorNb = 0;

	private String _localizedTitle = null;

	public ValidationReport(FIBModelObject rootObject) {
		super();
		_rootObject = rootObject;
		_validationIssues = new ValidationIssueVector();
	}

	public boolean hasCustomLocalizedTitle() {
		return (_localizedTitle != null);
	}

	public String getLocalizedTitle() {
		if (_localizedTitle == null) {
			if (_rootObject == null) {
				return FlexoLocalization.localizedForKey("no_validation_report");
			} else {
				return FlexoLocalization.localizedForKey("validation_report_for") + " " + _rootObject;
			}
		} else {
			return _localizedTitle;
		}
	}

	public void setLocalizedTitle(String aTitle) {
		_localizedTitle = aTitle;
	}

	public String getLocalizedSubTitle() {
		return ("" + getErrorNb() + " " + FlexoLocalization.localizedForKey("errors") + ", " + getWarningNb() + " " + FlexoLocalization
				.localizedForKey("warnings"));
	}

	public int getInfosNb() {
		return _infosNb;
	}

	public int getWarningNb() {
		return _warningNb;
	}

	public int getErrorNb() {
		return _errorNb;
	}

	public Vector<ValidationIssue> getValidationIssues() {
		return _validationIssues;
	}

	public Vector<ValidationError> getErrors() {
		return _validationIssues.errors;
	}

	public Vector<ValidationWarning> getWarnings() {
		return _validationIssues.warnings;
	}

	public Vector<InformationIssue> getInformationIssues() {
		return _validationIssues.infoIssue;
	}

	public void addToValidationIssues(ValidationIssue issue) {
		if (issue instanceof InformationIssue) {
			_infosNb++;
		}
		if (issue instanceof ValidationWarning) {
			_warningNb++;
		}
		if (issue instanceof ValidationError) {
			_errorNb++;
		}
		_validationIssues.add(issue);
		issue.setValidationReport(this);
	}

	public void removeFromValidationIssues(ValidationIssue issue) {
		if (_validationIssues.contains(issue)) {
			if (issue instanceof InformationIssue) {
				_infosNb--;
			}
			if (issue instanceof ValidationWarning) {
				_warningNb--;
			}
			if (issue instanceof ValidationError) {
				_errorNb--;
			}
			_validationIssues.remove(issue);
		}
	}

	public void removeFromValidationIssues(Vector issues) {
		for (Enumeration e = issues.elements(); e.hasMoreElements();) {
			ValidationIssue issue = (ValidationIssue) e.nextElement();
			removeFromValidationIssues(issue);
		}
	}

	public FIBModelObject getRootObject() {
		return _rootObject;
	}

	public Vector<ValidationIssue> issuesRegarding(FIBModelObject object) {
		Vector<ValidationIssue> returned = new Vector<ValidationIssue>();
		for (Enumeration e = _validationIssues.elements(); e.hasMoreElements();) {
			ValidationIssue issue = (ValidationIssue) e.nextElement();
			if (issue.getObject() == object) {
				returned.add(issue);
			}
		}
		return returned;
	}

	public String reportAsString() {
		StringBuffer sb = new StringBuffer();
		for (ValidationIssue issue : _validationIssues) {
			sb.append(issue.toString() + "\n");
		}
		return sb.toString();
	}

	public String errorAsString() {
		StringBuffer sb = new StringBuffer();
		for (ValidationIssue issue : _validationIssues) {
			if (issue instanceof ValidationError) {
				sb.append(issue.toString() + "\n");
			}
		}
		return sb.toString();
	}

	public String warningAsString() {
		StringBuffer sb = new StringBuffer();
		for (ValidationIssue issue : _validationIssues) {
			if (issue instanceof ValidationWarning) {
				sb.append(issue.toString() + "\n");
			}
		}
		return sb.toString();
	}

	public void delete() {
		Enumeration<ValidationIssue> en = ((ValidationIssueVector) _validationIssues.clone()).elements();
		while (en.hasMoreElements()) {
			en.nextElement().delete();
		}

	}

}
