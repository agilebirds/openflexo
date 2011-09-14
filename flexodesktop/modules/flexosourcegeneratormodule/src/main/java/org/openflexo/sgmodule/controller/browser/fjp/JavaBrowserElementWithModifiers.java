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

import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconMarker;
import org.openflexo.icon.JavaIconLibrary;
import org.openflexo.javaparser.FJPJavaEntity;
import org.openflexo.javaparser.FJPJavaMethod;


public abstract class JavaBrowserElementWithModifiers extends JavaBrowserElement {

    protected static final Logger logger = Logger.getLogger(JavaBrowserElementWithModifiers.class.getPackage().getName());

	public JavaBrowserElementWithModifiers(FJPJavaEntity object, BrowserElementType elementType, JavaParserBrowser browser, BrowserElement parent)
	{
		super(object, elementType, browser,parent);
	}
	
	@Override
	public FJPJavaEntity getObject()
	{
		return (FJPJavaEntity)super.getObject();
	}

	@Override
	public Icon getIcon()
	{
		if (getObject() == null) return null;
		
		ImageIcon returned = getBaseIcon();
		Vector<IconMarker> markers = new Vector<IconMarker>();
		
		if (getObject().isStatic()) {
			markers.add(JavaIconLibrary.STATIC_MARKER);
		}
		else if (getObject().isFinal()) {
			markers.add(JavaIconLibrary.FINAL_MARKER);
		}
		else if (getObject().isAbstract()) {
			markers.add(JavaIconLibrary.ABSTRACT_MARKER);
		}
		else if (getObject().isSynchronized()) {
			markers.add(JavaIconLibrary.SYNCHRONIZED_MARKER);
		}
		else if ((getObject() instanceof FJPJavaMethod) && ((FJPJavaMethod)getObject()).isConstructor()) {
			markers.add(JavaIconLibrary.CONSTRUCTOR_MARKER);
		}
		// Get icon with all markers
		IconMarker[] markersArray = markers.toArray(new IconMarker[markers.size()]);
		returned = IconFactory.getImageIcon(returned, markersArray);
		
		return returned;
	}

}
