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
package org.openflexo.foundation.rm;

import java.util.ArrayList;
import java.util.List;

/**
 * Thrown when one or more SaveResourceException were raised during resource saving
 * 
 * @author sguerin
 * 
 */
public class SaveResourceExceptionList extends Exception {

	protected List<SaveResourceException> _saveExceptions;

	public SaveResourceExceptionList(List<SaveResourceException> exceptions) {
		super();
		_saveExceptions = exceptions;
	}

	public SaveResourceExceptionList(SaveResourceException exception) {
		super();
		_saveExceptions = new ArrayList<SaveResourceException>();
		registerNewException(exception);
	}

	public void registerNewException(SaveResourceException exception) {
		_saveExceptions.add(exception);
	}

	public List<SaveResourceException> getSaveExceptions() {
		return _saveExceptions;
	}

	public String errorFilesList() {
		StringBuilder sb = new StringBuilder();
		for (SaveResourceException excep : _saveExceptions) {
			sb.append(excep.getFileResource().getFile().getName()).append('\n');
		}
		return sb.toString();
	}
}
