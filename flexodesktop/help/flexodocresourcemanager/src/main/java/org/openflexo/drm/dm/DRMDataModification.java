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
package org.openflexo.drm.dm;

import org.openflexo.foundation.DataModification;

/**
 * Represents a DataModification in DM
 * 
 * @author sguerin
 */
public abstract class DRMDataModification extends DataModification {

	public DRMDataModification(Object oldValue, Object newValue) {
		super(oldValue, newValue);
	}

	public DRMDataModification(String propertyName, Object oldValue, Object newValue) {
		super(propertyName, oldValue, newValue);
	}

}
