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

import java.io.File;

import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.generator.AbstractProjectGenerator;
import org.openflexo.localization.FlexoLocalization;

public class PermissionDeniedException extends GenerationException {
	public String path;

	public PermissionDeniedException(File file, AbstractProjectGenerator<? extends GenerationRepository> projectGenerator) {
		super("Permission denied for " + file != null ? file.getAbsolutePath() : "null", "permission_denied", file.getAbsolutePath(), null);
		path = file.getAbsolutePath();
	}

	/**
	 * Overrides getLocalizedMessage
	 * 
	 * @see org.openflexo.foundation.FlexoException#getLocalizedMessage()
	 */
	@Override
	public String getLocalizedMessage() {
		return FlexoLocalization.localizedForKey("permission_denied") + "\n" + path;
	}
}
