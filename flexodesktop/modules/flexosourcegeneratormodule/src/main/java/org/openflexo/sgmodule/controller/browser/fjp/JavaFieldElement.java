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
package org.openflexo.sgmodule.controller.browser.fjp;

import javax.swing.ImageIcon;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.icon.JavaIconLibrary;
import org.openflexo.javaparser.FJPJavaField;

public class JavaFieldElement extends JavaBrowserElementWithModifiers {
	public JavaFieldElement(FJPJavaField aField, JavaParserBrowser browser, BrowserElement parent) {
		super(aField, BrowserElementType.FIELD, browser, parent);
	}

	@Override
	protected void buildChildrenVector() {
	}

	public FJPJavaField getJavaField() {
		return (FJPJavaField) getObject();
	}

	@Override
	public ImageIcon getBaseIcon() {
		if (getJavaField().isPublic()) {
			return JavaIconLibrary.FJP_FIELD_PUBLIC_ICON;
		} else if (getJavaField().isProtected()) {
			return JavaIconLibrary.FJP_FIELD_PROTECTED_ICON;
		} else if (getJavaField().isPrivate()) {
			return JavaIconLibrary.FJP_FIELD_PRIVATE_ICON;
		} else {
			return JavaIconLibrary.FJP_FIELD_DEFAULT_ICON;
		}
	}

	@Override
	public String getName() {
		return getJavaField().getName();
	}
}
