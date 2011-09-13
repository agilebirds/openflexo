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

import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.javaparser.FJPJavaElement;


public abstract class JavaBrowserElement extends BrowserElement {

    protected static final Logger logger = Logger.getLogger(JavaBrowserElement.class.getPackage().getName());

	public JavaBrowserElement(FJPJavaElement object, BrowserElementType elementType, JavaParserBrowser browser, BrowserElement parent)
	{
		super(object, elementType, browser, parent);
	}
	
	@Override
	public FJPJavaElement getObject()
	{
		return (FJPJavaElement)super.getObject();
	}

	public ImageIcon getBaseIcon()
	{
		return getElementType().getIcon();
	}
	    
}
