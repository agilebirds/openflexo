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
package org.openflexo.foundation;

import org.openflexo.localization.FlexoLocalization;

/**
 * Thrown when invoking modification on FLEXO model
 * 
 * @author sguerin
 * 
 */
public class FlexoException extends Exception {

	// key used for localized messages, in FlexoLocalization
	protected String _localizationKey;

	public FlexoException() {
		super();
	}

	public FlexoException(Exception exception) {
		super(exception);
	}

	public FlexoException(String message) {
		super(message);
	}

	public FlexoException(String message, Exception e) {
		super(message, e);
	}

	public FlexoException(String message, String localizationKey) {
		super(message);
		_localizationKey = localizationKey;
	}

	public String getLocalizationKey() {
		return _localizationKey;
	}

	@Override
	public String getLocalizedMessage() {
		if (_localizationKey == null) {
			return getMessage();
		}
		return FlexoLocalization.localizedForKey(_localizationKey);
	}

}
