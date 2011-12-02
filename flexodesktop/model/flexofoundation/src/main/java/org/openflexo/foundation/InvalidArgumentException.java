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

import java.util.logging.Logger;

public class InvalidArgumentException extends FlexoException {

	private static final Logger logger = Logger.getLogger(InvalidArgumentException.class.getPackage().getName());

	public InvalidArgumentException(String message, String localizationKey) {
		super(message, localizationKey);
	}

	public InvalidArgumentException(String message) {
		super(message, "invalid_argument_exception_see_logs_for_details");
		logger.warning("InvalidArgumentException: " + message);
	}
}
