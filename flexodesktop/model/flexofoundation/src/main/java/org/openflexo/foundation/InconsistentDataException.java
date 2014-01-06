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

import org.openflexo.model.exceptions.InvalidDataException;

/**
 * Thrown when managed data is not consistent with model definition
 * 
 * @author sylvain
 * 
 */
@SuppressWarnings("serial")
public class InconsistentDataException extends FlexoException {
	private final InvalidDataException targetException;

	public InconsistentDataException(InvalidDataException targetException) {
		super("InconsistentDataException", "inconsistent_data_exception");
		this.targetException = targetException;
	}

	public InvalidDataException getTargetException() {
		return targetException;
	}

	@Override
	public InvalidDataException getCause() {
		return getTargetException();
	}

}
