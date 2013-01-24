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
package org.openflexo.foundation.view.diagram.viewpoint.editionaction;

import org.openflexo.foundation.view.diagram.DiagramMetaModel;
import org.openflexo.foundation.view.diagram.DiagramModelSlot;
import org.openflexo.foundation.view.diagram.model.View;
import org.openflexo.foundation.view.diagram.model.ViewObject;
import org.openflexo.foundation.viewpoint.AssignableAction;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;

public abstract class DiagramAction<T extends ViewObject> extends AssignableAction<View, DiagramMetaModel, T> {

	public DiagramAction(ViewPointBuilder builder) {
		super(builder);
	}

	/*@Override
	protected void updatePatternRoleType()
	{
	}*/

	@Override
	public DiagramModelSlot getModelSlot() {
		DiagramModelSlot returned = (DiagramModelSlot) super.getModelSlot();
		if (returned == null) {
			if (getVirtualModel() != null && getVirtualModel().getModelSlots(DiagramModelSlot.class).size() > 0) {
				return getVirtualModel().getModelSlots(DiagramModelSlot.class).get(0);
			}
		}
		return returned;
	}

}
