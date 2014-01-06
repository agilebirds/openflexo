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
package org.openflexo.foundation.ie.widget;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.xml.FlexoComponentBuilder;

@Deprecated
public class IECustomButtonWidget extends IEHyperlinkWidget {

	/**
     * 
     */
	public static final String CUSTOM_BUTTON_WIDGET = "custom_button_widget";

	@Deprecated
	public IECustomButtonWidget(FlexoComponentBuilder builder) {
		this(builder.woComponent, null, builder.getProject());
		initializeDeserialization(builder);
	}

	@Deprecated
	public IECustomButtonWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj) {
		super(woComponent, parent, prj);
	}

	public String getCustomButtonValue() {
		return getValue();
	}

	public void setCustomButtonValue(String buttonValue) {
		setValue(buttonValue);
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.AgileBirdsObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return CUSTOM_BUTTON_WIDGET;
	}

	@Override
	public void setInspectorName(String inspectorName) {
		// The inspector of custom buttons has been removed.
	}

	@Override
	public boolean isHyperlink() {
		return false;
	}

	@Override
	public boolean isCustomButton() {
		return true;
	}

	@Override
	public boolean isImageButton() {
		return false;
	}
}
