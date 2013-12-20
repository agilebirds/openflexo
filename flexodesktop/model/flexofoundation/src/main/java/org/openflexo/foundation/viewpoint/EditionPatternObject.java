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

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.viewpoint.NamedViewPointObject.NamedViewPointObjectImpl;

/**
 * Represents an object which is part of the model of an EditionPattern
 * 
 * @author sylvain
 * 
 */
public abstract class EditionPatternObject extends NamedViewPointObjectImpl {

	public EditionPatternObject() {
		super();
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
	public String getFMLRepresentation(FMLRepresentationContext context) {
		return "<not_implemented:" + getFullyQualifiedName() + ">";
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (getVirtualModel() != null) {
			getVirtualModel().setIsModified();
		}
	}

	@Override
	public Object performSuperGetter(String propertyIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void performSuperSetter(String propertyIdentifier, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void performSuperAdder(String propertyIdentifier, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void performSuperRemover(String propertyIdentifier, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object performSuperGetter(String propertyIdentifier, Class<?> modelEntityInterface) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void performSuperSetter(String propertyIdentifier, Object value, Class<?> modelEntityInterface) {
		// TODO Auto-generated method stub

	}

	@Override
	public void performSuperAdder(String propertyIdentifier, Object value, Class<?> modelEntityInterface) {
		// TODO Auto-generated method stub

	}

	@Override
	public void performSuperRemover(String propertyIdentifier, Object value, Class<?> modelEntityInterface) {
		// TODO Auto-generated method stub

	}

	@Override
	public void performSuperSetModified(boolean modified) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object performSuperFinder(String finderIdentifier, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object performSuperFinder(String finderIdentifier, Object value, Class<?> modelEntityInterface) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSerializing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDeserializing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setModified(boolean modified) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean equalsObject(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasKey(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean performSuperDelete(Object... context) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean performSuperUndelete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void performSuperDelete(Class<?> modelEntityInterface, Object... context) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean delete(Object... context) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean undelete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object cloneObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object cloneObject(Object... context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCreatedByCloning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isBeingCloned() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BindingModel getBindingModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getURI() {
		// TODO Auto-generated method stub
		return null;
	}

}
