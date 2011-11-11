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
import org.openflexo.javaparser.FJPJavaClass;
import org.openflexo.javaparser.FJPJavaEntity;

public class JavaClassElement extends JavaBrowserElementWithModifiers {
	public JavaClassElement(FJPJavaClass aClass, JavaParserBrowser browser, BrowserElement parent) {
		super(aClass, BrowserElementType.CLASS, browser, parent);
	}

	@Override
	protected void buildChildrenVector() {
		for (FJPJavaEntity e : getJavaClass().getOrderedChildren()) {
			addToChilds(e);
		}
	}

	public FJPJavaClass getJavaClass() {
		return (FJPJavaClass) getObject();
	}

	@Override
	public ImageIcon getBaseIcon() {
		if (getJavaClass().isEnum()) {
			if (getJavaClass().isPublic()) {
				return JavaIconLibrary.FJP_ENUM_PUBLIC_ICON;
			} else if (getJavaClass().isProtected()) {
				return JavaIconLibrary.FJP_ENUM_PROTECTED_ICON;
			} else if (getJavaClass().isPrivate()) {
				return JavaIconLibrary.FJP_ENUM_PRIVATE_ICON;
			} else {
				return JavaIconLibrary.FJP_ENUM_DEFAULT_ICON;
			}
		} else if (getJavaClass().isInterface()) {
			if (getJavaClass().isPublic()) {
				return JavaIconLibrary.FJP_INTERFACE_PUBLIC_ICON;
			} else if (getJavaClass().isProtected()) {
				return JavaIconLibrary.FJP_INTERFACE_PROTECTED_ICON;
			} else if (getJavaClass().isPrivate()) {
				return JavaIconLibrary.FJP_INTERFACE_PRIVATE_ICON;
			} else {
				return JavaIconLibrary.FJP_INTERFACE_DEFAULT_ICON;
			}
		} else {
			if (getJavaClass().isPublic()) {
				return JavaIconLibrary.FJP_CLASS_PUBLIC_ICON;
			} else if (getJavaClass().isProtected()) {
				return JavaIconLibrary.FJP_CLASS_PROTECTED_ICON;
			} else if (getJavaClass().isPrivate()) {
				return JavaIconLibrary.FJP_CLASS_PRIVATE_ICON;
			} else {
				return JavaIconLibrary.FJP_CLASS_DEFAULT_ICON;
			}
		}
	}

	@Override
	public String getName() {
		return getJavaClass().getName();
	}
}
