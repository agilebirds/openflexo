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
package org.openflexo.ve;

import java.io.File;

import org.openflexo.toolbox.FileResource;

/**
 * Constants used by the ViewEditor module.
 * 
 * @author sylvain
 */
public class VECst {

	public static final boolean CUT_COPY_PASTE_ENABLED = false;

	public static final String DEFAULT_OE_BROWSER_WINDOW_TITLE = "ontology_browser";

	public static final int DEFAULT_OE_BROWSER_WINDOW_WIDTH = 300;

	public static final int DEFAULT_OE_BROWSER_WINDOW_HEIGHT = 250;

	public static final int DEFAULT_MAINFRAME_WIDTH = 850;

	public static final int DEFAULT_MAINFRAME_HEIGHT = 600;

	public static final int PALETTE_DOC_SPLIT_LOCATION = 300;

	// General
	public static File ONTOLOGY_VIEW_FIB = new FileResource("Fib/OntologyView.fib");
	public static File VIRTUAL_MODEL_INSTANCE_VIEW_FIB = new FileResource("Fib/VirtualModelInstanceView.fib");

	// View/VirtualModelInstance edition
	public static File CREATE_VIEW_DIALOG_FIB = new FileResource("Fib/Dialog/CreateViewDialog.fib");
	public static File CREATE_VIRTUAL_MODEL_INSTANCE_DIALOG_FIB = new FileResource("Fib/Dialog/CreateVirtualModelInstanceDialog.fib");
	public static File CONFIGURE_FREE_MODEL_SLOT_INSTANCE_DIALOG_FIB = new FileResource(
			"Fib/Dialog/ConfigureFreeModelSlotInstanceDialog.fib");
	public static File CONFIGURE_TYPESAFE_MODEL_SLOT_INSTANCE_DIALOG_FIB = new FileResource(
			"Fib/Dialog/ConfigureTypeSafeModelSlotInstanceDialog.fib");
	public static File CONFIGURE_VIRTUAL_MODEL_SLOT_INSTANCE_DIALOG_FIB = new FileResource(
			"Fib/Dialog/ConfigureVirtualModelSlotInstanceDialog.fib");

	// Diagram edition
	public static File CREATE_DIAGRAM_DIALOG_FIB = new FileResource("Fib/Dialog/CreateDiagramDialog.fib");
	public static File CHOOSE_AND_CONFIGURE_CREATION_SCHEME_DIALOG_FIB = new FileResource(
			"Fib/Dialog/ChooseAndConfigureCreationSchemeDialog.fib");
	public static File DELETE_DIAGRAM_ELEMENTS_DIALOG_FIB = new FileResource("Fib/Dialog/DeleteDiagramElementsDialog.fib");
	public static File REINDEX_DIAGRAM_ELEMENTS_DIALOG_FIB = new FileResource("Fib/Dialog/ReindexDiagramElementsDialog.fib");

}
