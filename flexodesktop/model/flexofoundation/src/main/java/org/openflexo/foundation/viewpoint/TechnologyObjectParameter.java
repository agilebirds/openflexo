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

import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

/**
 * Allows the manipulation of an instance of a TechnologyObject
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(TechnologyObjectParameter.TechnologyObjectParameterImpl.class)
@XMLElement
public interface TechnologyObjectParameter<MS extends ModelSlot<?>> extends InnerModelSlotParameter<MS> {

	public static abstract class TechnologyObjectParameterImpl<MS extends ModelSlot<?>> extends InnerModelSlotParameterImpl<MS> implements
			TechnologyObjectParameter<MS> {

		public TechnologyObjectParameterImpl() {
			super();
		}

		@Override
		public Type getType() {
			return Object.class;
		};

		@Override
		public WidgetType getWidget() {
			return WidgetType.TECHNOLOGY_OBJECT;
		}

	}
}
