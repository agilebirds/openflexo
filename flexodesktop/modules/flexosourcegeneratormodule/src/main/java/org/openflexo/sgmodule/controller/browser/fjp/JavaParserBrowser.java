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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.components.browser.BrowserConfiguration;
import org.openflexo.components.browser.ConfigurableProjectBrowser;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.javaparser.FJPJavaElement;
import org.openflexo.sg.file.SGJavaFile;
import org.openflexo.sgmodule.controller.SGController;


/**
 * Browser for Code Generator module
 * 
 * @author sguerin
 * 
 */
public class JavaParserBrowser extends ConfigurableProjectBrowser implements FlexoObserver
{

	private static final Logger logger = Logger.getLogger(JavaParserBrowser.class.getPackage().getName());

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public JavaParserBrowser(SGJavaFile javaFile, SGController controller)
	{
		super(makeDefaultBrowserConfiguration(javaFile),controller.getSelectionManager());
		update();
	}

	@Override
	public void update(FlexoObservable o, DataModification arg)
	{
		if (logger.isLoggable(Level.FINE))
			logger.fine("JavaParserBrowser update");
	}

	public static BrowserConfiguration makeDefaultBrowserConfiguration(SGJavaFile javaFile)
	{
		BrowserConfiguration returned = new JavaParserBrowserConfiguration(javaFile);
		return returned;
	}

	@Override
	public FJPJavaElement getRootObject() 
	{
		return (FJPJavaElement)super.getRootObject();
	}
}
