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
package org.openflexo.foundation.wkf.dm;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class ObjectVisibilityChanged extends WKFDataModification {

	private boolean _newVisibility;

	public ObjectVisibilityChanged(boolean visibility) {
		super("isVisible", new Boolean(!visibility), new Boolean(visibility));
		_newVisibility = visibility;
	}

	public boolean isVisible() {
		return _newVisibility;
	}

}
