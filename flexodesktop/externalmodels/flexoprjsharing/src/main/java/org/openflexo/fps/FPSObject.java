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
package org.openflexo.fps;

import org.openflexo.foundation.TemporaryFlexoModelObject;
import org.openflexo.fps.automerge.AutomergeInitializer;
import org.openflexo.inspector.InspectableObject;

public abstract class FPSObject extends TemporaryFlexoModelObject implements InspectableObject {

	static {
		AutomergeInitializer.initialize();
	}

	public boolean isEnabled() {
		return true;
	}

	@Override
	public abstract String getClassNameKey();

	// ==========================================================================
	// ========================== Embedding implementation =====================
	// ==========================================================================

	public abstract boolean isContainedIn(FPSObject obj);

	public boolean contains(FPSObject obj) {
		return obj.isContainedIn(this);
	}

}
