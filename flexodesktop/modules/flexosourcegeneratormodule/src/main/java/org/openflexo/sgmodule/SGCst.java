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
package org.openflexo.sgmodule;

import java.awt.Font;
import java.io.File;

import org.openflexo.toolbox.FileResource;

/**
 * Constants used by the Source Generator module.
 * 
 * @author sylvain
 */
public class SGCst {

	public static String SG_MODULE_VERSION = "0.0.1";

	public static final int MINIMUM_BROWSER_VIEW_WIDTH = 240;
	public static final int MINIMUM_BROWSER_VIEW_HEIGHT = 0;

	public static int DEFAULT_MAINFRAME_WIDTH = 850;
	public static int DEFAULT_MAINFRAME_HEIGHT = 600;

	public static final Font HEADER_FONT = new Font("Verdana", Font.BOLD, 14);
	public static final Font SUB_TITLE_FONT = new Font("Verdana", Font.ITALIC, 10);
	public static final Font NORMAL_FONT = new Font("Verdana", Font.PLAIN, 11);

	// Main views
	public static File IMPLEMENTATION_MODEL_VIEW_FIB = new FileResource("Fib/ImplementationModelView.fib");

	// Dialogs
	public static File CREATE_IMPLEMENTATION_MODEL_DIALOG_FIB = new FileResource("Fib/Dialog/CreateImplementationModelDialog.fib");
	public static File CREATE_TECHNOLOGY_MODULE_IMPLEMENTATION_DIALOG_FIB = new FileResource(
			"Fib/Dialog/CreateTechnologyModuleImplementationDialog.fib");
	public static File CREATE_SOURCE_REPOSITORY_DIALOG_FIB = new FileResource("Fib/Dialog/CreateSourceRepositoryDialog.fib");
}
