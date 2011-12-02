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
package org.openflexo.components.tabular.model;

import org.openflexo.foundation.FlexoModelObject;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class CheckColumn<D extends FlexoModelObject> extends AbstractColumn<D, Boolean> implements EditableColumn<D, Boolean> {

	public CheckColumn(String title, int defaultWidth) {
		super(title, defaultWidth, false);
	}

	@Override
	public String getLocalizedTitle() {
		return " ";
	}

	@Override
	public Class getValueClass() {
		return Boolean.class;
	}

	@Override
	public Boolean getValueFor(D object) {
		return getBooleanValue(object);
	}

	public abstract Boolean getBooleanValue(D object);

	public abstract void setBooleanValue(D object, Boolean aBoolean);

	@Override
	public String toString() {
		return "CheckColumn " + "[" + getTitle() + "]" + Integer.toHexString(hashCode());
	}

	/**
	 * No custom cell renderer: use JTable default one
	 * 
	 * @return
	 */
	@Override
	public boolean requireCellRenderer() {
		return false;
	}

	@Override
	public boolean isCellEditableFor(FlexoModelObject object) {
		return true;
	}

	@Override
	public void setValueFor(D object, Boolean value) {
		setBooleanValue(object, value);
	}

}
