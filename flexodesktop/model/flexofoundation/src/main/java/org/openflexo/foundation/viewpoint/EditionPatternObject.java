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

import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;

/**
 * Represents an object which is part of the model of an EditionPattern
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(EditionPatternObject.EditionPatternObjectImpl.class)
public interface EditionPatternObject extends NamedViewPointObject {

	public VirtualModelModelFactory getVirtualModelFactory();

	public EditionPattern getEditionPattern();

	@Override
	public ViewPoint getViewPoint();

	public VirtualModel getVirtualModel();

	@Override
	public String getStringRepresentation();

	@Override
	public String getFMLRepresentation(FMLRepresentationContext context);

	public abstract class EditionPatternObjectImpl extends NamedViewPointObjectImpl implements EditionPatternObject {

		@Override
		public VirtualModelModelFactory getVirtualModelFactory() {
			if (getVirtualModel() != null) {
				return getVirtualModel().getVirtualModelFactory();
			}
			return null;
		}

		@Override
		public abstract EditionPattern getEditionPattern();

		@Override
		public ViewPoint getViewPoint() {
			if (getVirtualModel() != null) {
				return getVirtualModel().getViewPoint();
			}
			if (getEditionPattern() != null && getEditionPattern() != this) {
				return getEditionPattern().getViewPoint();
			}
			return null;
		}

		@Override
		public VirtualModel getVirtualModel() {
			if (getEditionPattern() != null) {
				return getEditionPattern().getVirtualModel();
			}
			return null;
		}

		@Override
		public String getStringRepresentation() {
			return (getVirtualModel() != null ? getVirtualModel().getStringRepresentation() : "null") + "#"
					+ (getEditionPattern() != null ? getEditionPattern().getName() : "null") + "." + getClass().getSimpleName();
		}

		@Override
		public String getFMLRepresentation(FMLRepresentationContext context) {
			return "<not_implemented:" + getStringRepresentation() + ">";
		}

		@Override
		public void setChanged() {
			super.setChanged();
			if (getVirtualModel() != null) {
				getVirtualModel().setIsModified();
			}
		}

	}
}
