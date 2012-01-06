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
package org.openflexo.foundation.utils;

import org.openflexo.foundation.FlexoException;

/**
 * Must be thrown whenever the user or flexo choose to cancel <i>open project procedure</i>. It can append whenever user choose it, or flexo
 * itself detect (by version inspection) that it cannot open the prj.
 * <ul>
 * <li>user click on "cancel" in a fileChooser to select a prj file</li>
 * <li>user choose to not convert a project requiring conversion
 * <li>
 * <li>prj version is less than 1.3</li>
 * <li>prj version is higher than current flexo version (i.e. the prj has been modified by a newer version of Flexo)</li>
 * </ul>
 */
public class ProjectLoadingCancelledException extends FlexoException {

	public ProjectLoadingCancelledException(String message) {
		super();
	}

	public ProjectLoadingCancelledException() {
		super();
	}
}
