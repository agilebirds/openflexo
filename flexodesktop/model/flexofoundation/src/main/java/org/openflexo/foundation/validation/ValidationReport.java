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
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;

import org.openflexo.localization.FlexoLocalization;

/**
 * Represents a validation report
 * 
 * @author sguerin
 * 
 */
public class ValidationReport extends AbstractTableModel {

	public enum ReportMode {
		ALL, ERRORS, WARNINGS;

		public String getLocalizedName() {
			return FlexoLocalization.localizedForKey(name().toLowerCase());
		}
	}

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

	private Validable _rootObject;

	private ValidationIssueVector _validationIssues;

	private int _infosNb = 0;

	private int _warningNb = 0;

	private int _errorNb = 0;

	private ValidationModel _model;

	private String _localizedTitle = null;

	protected ReportMode mode = ReportMode.ALL;

	public ValidationReport(ValidationModel model) {
		this(model, null);
	}

	public ValidationReport(ValidationModel model, Validable rootObject) {
		super();
		_model = model;
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
				return FlexoLocalization.localizedForKey("validation_report_for") + " " + _rootObject.getFullyQualifiedName();
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

	@Override
	public int getRowCount() {
		if (_validationIssues == null) {
			return 0;
		}
		switch (mode) {
		case ERRORS:
			return _errorNb;
		case WARNINGS:
			return _warningNb;
		default:
			return _validationIssues.size();
		}
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex == 0) { // Icon
			return " ";
		} else if (columnIndex == 1) {
			return FlexoLocalization.localizedForKey("message");
		} else if (columnIndex == 2) {
			return FlexoLocalization.localizedForKey("object");
		}
		return "???";
	}

	@Override
	public Class getColumnClass(int columnIndex) {
		if (columnIndex == 0) {
			return ValidationIssue.class;
		} else {
			return String.class;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (_validationIssues == null) {
			return null;
		}
		switch (mode) {
		case ERRORS:
			if (columnIndex == 0) {
				return (_validationIssues.errors.elementAt(rowIndex));
			} else if (columnIndex == 1) {
				return (_validationIssues.errors.elementAt(rowIndex)).getLocalizedMessage();
			} else if (columnIndex == 2) {
				return (_validationIssues.errors.elementAt(rowIndex)).getObject().getFullyQualifiedName();
			}
			break;
		case WARNINGS:
			if (columnIndex == 0) {
				return (_validationIssues.warnings.elementAt(rowIndex));
			} else if (columnIndex == 1) {
				return (_validationIssues.warnings.elementAt(rowIndex)).getLocalizedMessage();
			} else if (columnIndex == 2) {
				return (_validationIssues.warnings.elementAt(rowIndex)).getObject().getFullyQualifiedName();
			}
			break;
		default:
			if (columnIndex == 0) {
				return (_validationIssues.elementAt(rowIndex));
			} else if (columnIndex == 1) {
				return (_validationIssues.elementAt(rowIndex)).getLocalizedMessage();
			} else if (columnIndex == 2) {
				return (_validationIssues.elementAt(rowIndex)).getObject().getFullyQualifiedName();
			}
		}
		return null;
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
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
		if (issue instanceof CompoundIssue) {
			for (Enumeration en3 = ((CompoundIssue) issue).getContainedIssues().elements(); en3.hasMoreElements();) {
				ValidationIssue anIssue = (ValidationIssue) en3.nextElement();
				addToValidationIssues(anIssue);
			}
		} else {
			if (issue instanceof InformationIssue)
				_infosNb++;
			if (issue instanceof ValidationWarning)
				_warningNb++;
			if (issue instanceof ValidationError)
				_errorNb++;
			_validationIssues.add(issue);
			issue.setValidationReport(this);
			fireTableDataChanged();
		}
	}

	public void removeFromValidationIssues(ValidationIssue issue) {
		if (_validationIssues.contains(issue)) {
			if (issue instanceof InformationIssue)
				_infosNb--;
			if (issue instanceof ValidationWarning)
				_warningNb--;
			if (issue instanceof ValidationError)
				_errorNb--;
			_validationIssues.remove(issue);
			fireTableDataChanged();
		}
	}

	public void removeFromValidationIssues(Vector issues) {
		for (Enumeration e = issues.elements(); e.hasMoreElements();) {
			ValidationIssue issue = (ValidationIssue) e.nextElement();
			removeFromValidationIssues(issue);
		}
	}

	public ValidationIssue getIssueAt(int row) {
		switch (getMode()) {
		case ALL:
			if (row < _validationIssues.size()) {
				return _validationIssues.elementAt(row);
			}
			break;
		case ERRORS:
			if (row < _validationIssues.errors.size()) {
				return _validationIssues.errors.elementAt(row);
			}
		case WARNINGS:
			if (row < _validationIssues.warnings.size()) {
				return _validationIssues.warnings.elementAt(row);
			}
		}
		return null;
	}

	public ValidationModel getValidationModel() {
		return _model;
	}

	public Validable getRootObject() {
		return _rootObject;
	}

	public Vector<ValidationIssue> issuesRegarding(Validable object) {
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
			if (issue instanceof ValidationError)
				sb.append(issue.toString() + "\n");
		}
		return sb.toString();
	}

	public String warningAsString() {
		StringBuffer sb = new StringBuffer();
		for (ValidationIssue issue : _validationIssues) {
			if (issue instanceof ValidationWarning)
				sb.append(issue.toString() + "\n");
		}
		return sb.toString();
	}

	public ReportMode getMode() {
		return mode;
	}

	public void setMode(ReportMode mode) {
		if (mode == null)
			mode = ReportMode.ALL;
		this.mode = mode;
		fireTableDataChanged();
	}

	public void delete() {
		Enumeration<ValidationIssue> en = ((ValidationIssueVector) _validationIssues.clone()).elements();
		while (en.hasMoreElements())
			en.nextElement().delete();

	}

}
