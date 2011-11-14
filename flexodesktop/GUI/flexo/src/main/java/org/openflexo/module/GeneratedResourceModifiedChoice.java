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
package org.openflexo.module;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.kvc.ChoiceList;
import org.openflexo.kvc.KVCObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

public abstract class GeneratedResourceModifiedChoice extends KVCObject implements StringConvertable, ChoiceList {

	private static final Logger logger = Logger.getLogger(GeneratedResourceModifiedChoice.class.getPackage().getName());

	public static final StringEncoder.Converter converter = new Converter<GeneratedResourceModifiedChoice>(
			GeneratedResourceModifiedChoice.class) {

		@Override
		public GeneratedResourceModifiedChoice convertFromString(String value) {
			return GeneratedResourceModifiedChoice.get(value);
		}

		@Override
		public String convertToString(GeneratedResourceModifiedChoice value) {
			return value.getIdentifier();
		}

	};

	private static Vector<GeneratedResourceModifiedChoice> availableChoices = null;

	public static final GeneratedResourceModifiedChoice ASK = new GeneratedResourceModifiedChoice() {
		@Override
		public String getIdentifier() {
			return "ASK";
		}
	};

	public static final GeneratedResourceModifiedChoice IGNORE = new GeneratedResourceModifiedChoice() {
		@Override
		public String getIdentifier() {
			return "IGNORE";
		}
	};

	public static final GeneratedResourceModifiedChoice REINJECT_IN_MODEL = new GeneratedResourceModifiedChoice() {
		@Override
		public String getIdentifier() {
			return "REINJECT_IN_MODEL";
		}
	};

	public static final GeneratedResourceModifiedChoice AUTOMATICALLY_REINJECT_IN_MODEL = new GeneratedResourceModifiedChoice() {
		@Override
		public String getIdentifier() {
			return "AUTOMATICALLY_REINJECT_IN_MODEL";
		}
	};

	public static final GeneratedResourceModifiedChoice ACCEPT = new GeneratedResourceModifiedChoice() {
		@Override
		public String getIdentifier() {
			return "ACCEPT";
		}
	};

	public static final GeneratedResourceModifiedChoice ACCEPT_AND_REINJECT = new GeneratedResourceModifiedChoice() {
		@Override
		public String getIdentifier() {
			return "ACCEPT_AND_REINJECT";
		}
	};

	public static final GeneratedResourceModifiedChoice ACCEPT_AND_AUTOMATICALLY_REINJECT = new GeneratedResourceModifiedChoice() {
		@Override
		public String getIdentifier() {
			return "ACCEPT_AND_AUTOMATICALLY_REINJECT";
		}
	};

	public static Vector<GeneratedResourceModifiedChoice> getAvailableChoices() {
		if (availableChoices == null) {
			availableChoices = new Vector<GeneratedResourceModifiedChoice>();
			availableChoices.add(ASK);
			availableChoices.add(IGNORE);
			availableChoices.add(REINJECT_IN_MODEL);
			availableChoices.add(AUTOMATICALLY_REINJECT_IN_MODEL);
			availableChoices.add(ACCEPT);
			availableChoices.add(ACCEPT_AND_REINJECT);
			availableChoices.add(ACCEPT_AND_AUTOMATICALLY_REINJECT);
		}
		return availableChoices;
	}

	/**
	 * Return a Vector of possible values (which must be of the same type as the one declared as class implemented this interface)
	 * 
	 * @return a Vector of ChoiceList
	 */
	@Override
	public Vector<GeneratedResourceModifiedChoice> getAvailableValues() {
		return getAvailableChoices();
	}

	public static GeneratedResourceModifiedChoice get(String anIdentifier) {
		if (anIdentifier == null) {
			return ASK;
		}
		for (Enumeration e = getAvailableChoices().elements(); e.hasMoreElements();) {
			GeneratedResourceModifiedChoice next = (GeneratedResourceModifiedChoice) e.nextElement();
			if (next.getIdentifier().equalsIgnoreCase(anIdentifier)) {
				return next;
			}
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Cannot find choice " + anIdentifier);
		}
		if (getAvailableChoices().size() > 0) {
			return getAvailableChoices().firstElement();
		}
		return null;
	}

	public abstract String getIdentifier();

	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(getIdentifier());
	}

	@Override
	public StringEncoder.Converter getConverter() {
		return converter;
	}

}
