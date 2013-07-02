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
package org.openflexo.foundation.viewpoint.dm;

import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.VirtualModel;

/**
 * Notify that a new model slot has been added
 * 
 * @author sguerin
 * 
 */
public class ModelSlotAdded extends ViewPointDataModification {

	private ViewPoint viewPoint;

	public ModelSlotAdded(ModelSlot modelSlot, ViewPoint viewPoint) {
		super("modelSlots", null, modelSlot);
		this.viewPoint = viewPoint;
	}

	public ModelSlotAdded(ModelSlot modelSlot, VirtualModel virtualModel) {
		super("modelSlots", null, modelSlot);
		this.viewPoint = viewPoint;
	}

	public ViewPoint getViewPoint() {
		return viewPoint;
	}

}
