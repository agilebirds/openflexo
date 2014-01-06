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
package org.openflexo.foundation.param;

import org.openflexo.inspector.widget.DenaliWidget;
import org.openflexo.inspector.widget.LabelWidget;

public class LabelParameter extends ParameterDefinition<String> {

	public LabelParameter(String name, String label, String defaultValue) {
		super(name, label, defaultValue);
	}

	public LabelParameter(String name, String label, String defaultValue, boolean displayLabel) {
		this(name, label, defaultValue);
		addParameter(DenaliWidget.DISPLAY_LABEL, "" + displayLabel);
	}

	@Override
	public String getWidgetName() {
		return DenaliWidget.LABEL;
	}

	public void setWidth(int width) {
		addParameter(LabelWidget.WIDTH, "" + width);
	}

	public void setHeight(int height) {
		addParameter(LabelWidget.HEIGHT, "" + height);
	}

	public void setAlign(String align) {
		addParameter(LabelWidget.ALIGN, align);
	}

}
