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

import org.openflexo.inspector.LocalizedString;
import org.openflexo.inspector.widget.DenaliWidget;
import org.openflexo.inspector.widget.TextFieldWidget;

public class LocalizedTextFieldParameter extends ParameterDefinition<LocalizedString> {

	public LocalizedTextFieldParameter(String name, String label, LocalizedString defaultValue) {
		super(name, label, defaultValue);
	}

	public LocalizedTextFieldParameter(String name, String label, LocalizedString defaultValue, int cols) {
		this(name, label, defaultValue);
		setColumns(cols);
	}

	public void setValidateOnReturn(boolean validateOnReturn) {
		addParameter(TextFieldWidget.VALIDATE_ON_RETURN, "" + validateOnReturn);
	}

	public void setIsPassword(boolean isPassword) {
		addParameter(TextFieldWidget.PASSWORD_PARAM, "" + isPassword);
	}

	public void setColumns(int cols) {
		addParameter(TextFieldWidget.COLUMNS_PARAM, "" + cols);
	}

	@Override
	public String getWidgetName() {
		return DenaliWidget.LOCALIZED_TEXT_FIELD;
	}

}
