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
package org.openflexo.javaparser;

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.localization.FlexoLocalization;

import com.thoughtworks.qdox.parser.ParseException;

public class FJPJavaParseException extends FJPJavaElement {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FJPJavaParseException.class.getPackage().getName());

	ParseException _parseException;
	String _sourceName;

	public FJPJavaParseException(String sourceName, ParseException parseException) {
		super(null);
		_parseException = parseException;
		_sourceName = sourceName;
	}

	@Override
	public String getInspectorName() {
		return null;
	}

	public int getColumn() {
		return _parseException.getColumn();
	}

	public int getLine() {
		return _parseException.getLine();
	}

	public String getLocalizedMessage() {
		return _parseException.getLocalizedMessage();
	}

	public String getMessage() {
		return _parseException.getMessage();
	}

	private FJPParseException _flexoException;

	public FJPParseException getParseException() {
		if (_flexoException == null) {
			_flexoException = new FJPParseException();
		}
		return _flexoException;
	}

	public class FJPParseException extends FlexoException {
		public FJPParseException() {
			super("Parse error while parsing " + _sourceName);
		}

		public int getColumn() {
			return _parseException.getColumn();
		}

		public int getLine() {
			return _parseException.getLine();
		}

		@Override
		public String getLocalizedMessage() {
			return FlexoLocalization.localizedForKey("parse_error_while_parsing") + " " + _sourceName + " "
					+ FlexoLocalization.localizedForKey("line") + " " + getLine() + " " + FlexoLocalization.localizedForKey("at") + " "
					+ getColumn();
		}

		@Override
		public String getMessage() {
			return _parseException.getMessage();
		}

		public ParseException getParseException() {
			return _parseException;
		}

	}

}
