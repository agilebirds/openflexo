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

/**
 * Represents an object which is part of the model of an EditionPattern
 * 
 * @author sylvain
 * 
 */
public abstract class EditionPatternObject extends NamedViewPointObject {

	public EditionPatternObject(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

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

	public VirtualModel getVirtualModel() {
		if (getEditionPattern() != null) {
			return getEditionPattern().getVirtualModel();
		}
		return null;
	}

	@Override
	public String getFullyQualifiedName() {
		return (getVirtualModel() != null ? getVirtualModel().getFullyQualifiedName() : "null") + "#"
				+ (getEditionPattern() != null ? getEditionPattern().getName() : "null") + "." + getClass().getSimpleName();
	}

	@Override
	public String getLanguageRepresentation(LanguageRepresentationContext context) {
		return "<not_implemented:" + getFullyQualifiedName() + ">";
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (getVirtualModel() != null) {
			getVirtualModel().setIsModified();
		}
	}

}
