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

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.cg.generator.IGenerationException;

/**
 * @author gpolet
 * 
 */
public class GenerationException extends FlexoException implements IGenerationException {
	private String _details;
	private Throwable _targetException;

	public GenerationException(String message) {
		super(message);
	}

	public GenerationException(String message, String localizationKey) {
		super(message, localizationKey);
	}

	public GenerationException(String message, String localizationKey, String details, Throwable targetException) {
		super(message, localizationKey);
		setDetails(details);
		setTargetException(targetException);
	}

	public void setDetails(String someDetails) {
		_details = someDetails;
	}

	@Override
	public Throwable getTargetException() {
		return _targetException;
	}

	public void setTargetException(Throwable targetException) {
		_targetException = targetException;
	}

	@Override
	public String getDetails() {
		if (_details == null || _details.trim().length() == 0) {
			return getMessage();
		}
		return _details;
	}

	@Override
	public Throwable getCause() {
		return getTargetException();
	}

}
