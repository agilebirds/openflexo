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
package org.openflexo.foundation.dm.javaparser;

import java.util.logging.Logger;

import org.openflexo.diff.ComputeDiff;
import org.openflexo.diff.ComputeDiff.DiffReport;
import org.openflexo.foundation.TemporaryFlexoModelObject;
import org.openflexo.foundation.dm.DuplicateMethodSignatureException;
import org.openflexo.foundation.dm.dm.DMAttributeDataModification;
import org.openflexo.toolbox.StringUtils;

public abstract class AbstractSourceCode extends TemporaryFlexoModelObject {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(AbstractSourceCode.class.getPackage().getName());

	private SourceCodeOwner _owner;

	private String _computedCode = null;
	protected String _editedCode = null;

	private boolean _hasParseError = false;
	private String _parseErrorWarning = null;

	private String _propertyName;
	private String _hasParseErrorPropertyName;
	private String _parseErrorWarningPropertyName;

	protected AbstractSourceCode(SourceCodeOwner owner, String propertyName, String hasParseErrorPropertyName,
			String parseErrorWarningPropertyName) {
		this(owner);
		_propertyName = propertyName;
		_hasParseErrorPropertyName = hasParseErrorPropertyName;
		_parseErrorWarningPropertyName = parseErrorWarningPropertyName;
	}

	protected AbstractSourceCode(SourceCodeOwner owner) {
		super();
		_owner = owner;
	}

	private static String getCleanCode(String code) {
		if (code == null) {
			return null;
		}
		String[] s = code.split("\n");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length; i++) {
			String string = s[i];
			if (string.length() > 0 && string.charAt(string.length() - 1) == '\r') {
				sb.append(string.substring(0, string.length() - 1));
			} else {
				sb.append(string);
			}
			sb.append(StringUtils.LINE_SEPARATOR);
		}
		return sb.toString();
	}

	protected abstract boolean isJavaParserInstalled();

	public boolean isEdited() {
		return (_editedCode != null);
	}

	public boolean hasParseErrors() {
		return _hasParseError;
	}

	public void setHasParseErrors(boolean value) {
		if (_hasParseError != value) {
			_hasParseError = value;
			if (_hasParseErrorPropertyName != null) {
				getOwner().setChanged();
				getOwner().notifyObservers(new DMAttributeDataModification(_hasParseErrorPropertyName, !_hasParseError, _hasParseError));
			}
		}
	}

	public String getParseErrorWarning() {
		return _parseErrorWarning;
	}

	public void setParseErrorWarning(String warning) {
		if ((warning == null && _parseErrorWarning != null) || (warning != null && !warning.equals(_parseErrorWarning))) {
			String oldValue = _parseErrorWarning;
			_parseErrorWarning = warning;
			if (_parseErrorWarningPropertyName != null) {
				getOwner().setChanged();
				getOwner().notifyObservers(new DMAttributeDataModification(_parseErrorWarningPropertyName, oldValue, warning));
			}
		}
	}

	public String updateComputedCode() {
		_computedCode = getOwner().codeIsComputable() ? makeComputedCode() : null;
		return _computedCode;
	}

	public String updateComputedCode(String someCode) {
		someCode = getCleanCode(someCode);
		_computedCode = someCode;
		return _computedCode;
	}

	public abstract String makeComputedCode();

	public abstract void interpretEditedCode(ParsedJavaElement javaElement) throws DuplicateMethodSignatureException;

	private DiffReport diffReport;
	private boolean diffReportNeedsToBeRecomputed = true;

	public DiffReport getDiffReport() {
		if (diffReportNeedsToBeRecomputed) {
			diffReport = null;
			updateComputedCode();
			if (getCode() != null && _computedCode != null) {
				diffReport = ComputeDiff.diff(getCode(), _computedCode);
			}
		}
		return diffReport;
	}

	/**
	 * Return code this method represents
	 * 
	 * @return
	 */
	public String getCode() {
		// No need to serialize if not edited
		if (getOwner().isSerializing() && !isEdited()) {
			return null;
		}

		if (isEdited()) {
			return _editedCode;
		}

		if (_computedCode == null && !getOwner().isDeserializing()) {
			updateComputedCode();
		}

		return _computedCode;
	}

	/*public String getIndentedCode() 
	{
		String codeToIndent = getCode();
		BufferedReader rdr = new BufferedReader(new StringReader(codeToIndent));
		StringBuffer sb = new StringBuffer();
		boolean firstLine = true;
		for (;;) {
			String line = null;
			try {
				line = rdr.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (line == null) break;
			sb.append(firstLine?line+LINE_SEPARATOR:"\t"+line+LINE_SEPARATOR);
			firstLine = false;
		}
		return sb.toString();
	}*/

	/**
	 * Sets code this method represents, and when not deserializing, update owner object with informations contained in supplied qualified
	 * code.
	 * 
	 * @param qualifiedCode
	 * @throws ParserNotInstalledException
	 * @throws DuplicateMethodSignatureException
	 * @throws UnresolvedTypesException
	 */
	public void setCode(final String qualifiedCode) throws ParserNotInstalledException, DuplicateMethodSignatureException {
		if (getOwner() != null && getOwner().isDeserializing()) {
			if (isJavaParserInstalled()) {
				parseCode(qualifiedCode);
			}
			setCode(qualifiedCode, false);
		} else {
			setCode(qualifiedCode, true);
		}
	}

	/**
	 * Sets code this method represents, and if parseCode flag is set to true, update owner object with informations contained in supplied
	 * qualified code.
	 * 
	 * @param someCode
	 * @param parseCode
	 * @throws ParserNotInstalledException
	 * @throws DuplicateMethodSignatureException
	 * @throws UnresolvedTypesException
	 */
	public void setCode(String someCode, boolean parseCode) throws ParserNotInstalledException, DuplicateMethodSignatureException {
		diffReportNeedsToBeRecomputed = true;

		someCode = getCleanCode(someCode);
		if (_computedCode == null && getOwner().codeIsComputable()) {
			_computedCode = makeComputedCode();
		}

		if (someCode == null || someCode.trim().equals("") || someCode.trim().equals(_computedCode)) {
			// Use computed code
			_editedCode = null;
			updateComputedCode();
			if (_propertyName != null) {
				getOwner().setChanged();
				getOwner().notifyObserversAsReentrantModification(new DMAttributeDataModification(_propertyName, null, someCode));
			}
			_parseErrorWarning = null;
			setHasParseErrors(false);
			return;
		}

		if (someCode.equals(getCode())) {
			// Unchanged, just return;
			return;
		}

		_editedCode = someCode;

		if (!parseCode) {
			return;
		}

		ParsedJavaElement parsedJavaElement = parseCode(someCode);

		if (parsedJavaElement != null) {

			// It's parsable, try to interpret this
			interpretEditedCode(parsedJavaElement);
			if (_propertyName != null) {
				getOwner().setChanged();
				getOwner().notifyObserversAsReentrantModification(new DMAttributeDataModification(_propertyName, null, someCode));
			}
		}
	}

	protected abstract ParsedJavaElement parseCode(final String qualifiedCode) throws ParserNotInstalledException;

	protected abstract ParsedJavadoc parseJavadoc(final String qualifiedCode) throws ParserNotInstalledException;

	public SourceCodeOwner getOwner() {
		return _owner;
	}

	public void setOwner(SourceCodeOwner owner) {
		_owner = owner;
	}

	public void replaceJavadocInEditedCode(ParsedJavadoc newJavadoc) {
		replaceJavadocInEditedCode(newJavadoc.getStringRepresentation());
	}

	public void replaceJavadocInEditedCode(String newJavadoc) {
		// logger.info("Called replaceJavadocInEditedCode()");

		// First look javadoc
		int javadocBeginIndex = _editedCode.indexOf("/**");
		if (javadocBeginIndex > -1) {
			int endJavadocIndex = _editedCode.indexOf("*/") + 2 + StringUtils.LINE_SEPARATOR.length();
			_editedCode = _editedCode.substring(0, javadocBeginIndex) + newJavadoc + _editedCode.substring(endJavadocIndex);
		} else {
			_editedCode = newJavadoc + _editedCode;
		}
	}

}
