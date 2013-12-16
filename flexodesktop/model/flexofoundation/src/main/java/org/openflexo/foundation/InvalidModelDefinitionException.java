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

import org.openflexo.model.exceptions.ModelDefinitionException;

/**
 * Thrown when model definition is inconsistent.<br>
 * This supposes a programmatic error (and not corrupted data)
 * 
 * @author sylvain
 * 
 */
@SuppressWarnings("serial")
public class InvalidModelDefinitionException extends FlexoException {
	private final ModelDefinitionException targetException;

	public InvalidModelDefinitionException(ModelDefinitionException targetException) {
		super("InconsistentDataException", "inconsistent_data_exception");
		this.targetException = targetException;
	}

	public ModelDefinitionException getTargetException() {
		return targetException;
	}

	@Override
	public ModelDefinitionException getCause() {
		return getTargetException();
	}

}
