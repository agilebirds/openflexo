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
package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.EditionPatternInstance;

public class EditionPatternInstanceParameter extends InnerModelSlotParameter {

	private EditionPattern editionPatternType;
	private String editionPatternTypeURI;

	public EditionPatternInstanceParameter(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public Type getType() {
		if (getEditionPatternType() != null) {
			return EditionPatternInstanceType.getEditionPatternInstanceType(getEditionPatternType());
		}
		return EditionPatternInstance.class;
	};

	@Override
	public WidgetType getWidget() {
		return WidgetType.EDITION_PATTERN;
	}

	public String _getEditionPatternTypeURI() {
		if (editionPatternType != null) {
			return editionPatternType.getURI();
		}
		return editionPatternTypeURI;
	}

	public void _setEditionPatternTypeURI(String editionPatternURI) {
		this.editionPatternTypeURI = editionPatternURI;
	}

	public EditionPattern getEditionPatternType() {
		if (editionPatternType == null && editionPatternTypeURI != null && getModelSlotVirtualModel() != null) {
			editionPatternType = getModelSlotVirtualModel().getEditionPattern(editionPatternTypeURI);
			for (EditionScheme s : getEditionPattern().getEditionSchemes()) {
				s.updateBindingModels();
			}
		}
		return editionPatternType;
	}

	public void setEditionPatternType(EditionPattern editionPatternType) {
		if (editionPatternType != this.editionPatternType) {
			this.editionPatternType = editionPatternType;
			for (EditionScheme s : getEditionPattern().getEditionSchemes()) {
				s.updateBindingModels();
			}
		}
	}

	@Override
	public void setModelSlot(ModelSlot modelSlot) {
		super.setModelSlot(modelSlot);
		setChanged();
		notifyObservers(new DataModification("modelSlotVirtualModel", null, modelSlot));
	}

	public VirtualModel<?> getModelSlotVirtualModel() {
		if (getModelSlot() instanceof VirtualModelModelSlot) {
			return ((VirtualModelModelSlot) getModelSlot()).getVirtualModelResource().getVirtualModel();
		}
		return null;
	}

}
