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
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(EditionPatternInstanceParameter.EditionPatternInstanceParameterImpl.class)
@XMLElement
public interface EditionPatternInstanceParameter extends InnerModelSlotParameter<VirtualModelModelSlot> {

	@PropertyIdentifier(type = String.class)
	public static final String EDITION_PATTERN_TYPE_URI_KEY = "editionPatternTypeURI";

	@Getter(value = EDITION_PATTERN_TYPE_URI_KEY)
	@XMLAttribute
	public String _getEditionPatternTypeURI();

	@Setter(EDITION_PATTERN_TYPE_URI_KEY)
	public void _setEditionPatternTypeURI(String editionPatternTypeURI);

	public static abstract class EditionPatternInstanceParameterImpl extends InnerModelSlotParameterImpl<VirtualModelModelSlot> implements
			EditionPatternInstanceParameter {

		private EditionPattern editionPatternType;
		private String editionPatternTypeURI;

		public EditionPatternInstanceParameterImpl() {
			super();
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

		@Override
		public String _getEditionPatternTypeURI() {
			if (editionPatternType != null) {
				return editionPatternType.getURI();
			}
			return editionPatternTypeURI;
		}

		@Override
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
		public void setModelSlot(VirtualModelModelSlot modelSlot) {
			super.setModelSlot(modelSlot);
			setChanged();
			notifyObservers(new DataModification("modelSlotVirtualModel", null, modelSlot));
		}

		public VirtualModel getModelSlotVirtualModel() {
			if (getModelSlot() != null && getModelSlot().getVirtualModelResource() != null) {
				return getModelSlot().getVirtualModelResource().getVirtualModel();
			}
			return null;
		}

		@Override
		public VirtualModelModelSlot getModelSlot() {
			if (super.getModelSlot() instanceof VirtualModelModelSlot) {
				VirtualModelModelSlot returned = super.getModelSlot();
				if (returned == null) {
					if (getVirtualModel() != null && getVirtualModel().getModelSlots(VirtualModelModelSlot.class).size() > 0) {
						return getVirtualModel().getModelSlots(VirtualModelModelSlot.class).get(0);
					}
				}
				return returned;
			}
			return null;
		}

	}
}
