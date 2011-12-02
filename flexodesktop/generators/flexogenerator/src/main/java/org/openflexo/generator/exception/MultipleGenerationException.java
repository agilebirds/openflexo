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
package org.openflexo.generator.exception;

import java.util.Vector;

import org.openflexo.foundation.FlexoException;

public class MultipleGenerationException extends GenerationException {

	private Vector<FlexoException> exceptions;

	public MultipleGenerationException() {
		super(null);
		exceptions = new Vector<FlexoException>();
	}

	public void addToExceptions(FlexoException exception) {
		exceptions.add(exception);
	}

	@Override
	public String getMessage() {
		StringBuffer sb = new StringBuffer();
		for (FlexoException ex : exceptions) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(ex.getMessage());
		}
		return sb.toString();
	}
}
