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
package org.openflexo.technologyadapter.diagram.fml.editionaction;

import org.openflexo.foundation.viewpoint.AssignableAction;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.technologyadapter.diagram.TypedDiagramModelSlot;

@ModelEntity(isAbstract = true)
@ImplementationClass(DiagramAction.DiagramActionImpl.class)
public interface DiagramAction<T> extends AssignableAction<TypedDiagramModelSlot, T> {

	public abstract class DiagramActionImpl<T> extends AssignableActionImpl<TypedDiagramModelSlot, T> {

		@Override
		public TypedDiagramModelSlot getModelSlot() {
			TypedDiagramModelSlot returned = super.getModelSlot();
			if (returned == null) {
				if (getVirtualModel() != null && getVirtualModel().getModelSlots(TypedDiagramModelSlot.class).size() > 0) {
					return getVirtualModel().getModelSlots(TypedDiagramModelSlot.class).get(0);
				}
			}
			return returned;
		}

	}

}
