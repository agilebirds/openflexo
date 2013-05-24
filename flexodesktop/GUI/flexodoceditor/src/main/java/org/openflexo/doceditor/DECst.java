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
package org.openflexo.doceditor;

import java.awt.Color;
import java.awt.Font;
import java.io.File;

import org.openflexo.toolbox.FileResource;

/**
 * Constants used by the Doc editor module.
 * 
 * @author gpolet
 */
public class DECst {

	public static final int DEFAULT_DE_WIDTH = 850;

	public static final int DEFAULT_DE_HEIGHT = 700;

	public static final Font HEADER_FONT = new Font("Verdana", Font.BOLD, 14);
	public static final Font SUB_TITLE_FONT = new Font("Verdana", Font.ITALIC, 10);

	public static final int MINIMUM_BROWSER_VIEW_WIDTH = 240;
	public static final int MINIMUM_BROWSER_VIEW_HEIGHT = 0;
	public static final int PREFERRED_BROWSER_VIEW_WIDTH = 240;
	public static final int PREFERRED_BROWSER_VIEW_HEIGHT = 240;

	public static final Color DEFAULT_CONSOLE_COLOR = new Color(0, 128, 64);

	public static File CREATE_TOC_ENTRY_DIALOG_FIB = new FileResource("Fib/Dialog/CreateTOCEntryDialog.fib");
	public static File IMPORT_DOCUMENTATION_TEMPLATES_FIB = new FileResource("Fib/Dialog/ImportDocumentationTemplates.fib");

}
