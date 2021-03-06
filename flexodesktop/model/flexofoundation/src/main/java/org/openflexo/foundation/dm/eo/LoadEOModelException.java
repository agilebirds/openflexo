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
package org.openflexo.foundation.dm.eo;

import org.openflexo.foundation.rm.FlexoEOModelResource;
import org.openflexo.foundation.rm.LoadResourceException;
import org.openflexo.foundation.utils.FlexoProjectFile;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class LoadEOModelException extends LoadResourceException {
	private FlexoProjectFile _eoModelFile;

	private EOAccessException _exception;

	public LoadEOModelException(FlexoEOModelResource fileResource, EOAccessException e) {
		super(fileResource, null);
		_eoModelFile = fileResource.getResourceFile();
		_exception = e;
	}

	@Override
	public String getMessage() {
		return _exception.getMessage();
	}

}
