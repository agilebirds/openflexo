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
package org.openflexo.vpm;

import java.io.File;

import org.openflexo.toolbox.FileResource;

/**
 * Constants used by the CalcEditor module.
 * 
 * @author yourname
 */
public class VPMCst {

	public static String CED_MODULE_VERSION = "0.0.1";

	public static String CED_MODULE_SHORT_NAME = "CED";

	public static String CED_MODULE_NAME = "ced_module_name";

	public static String CED_MODULE_DESCRIPTION = "ced_module_name_description";

	public static String DEFAULT_CED_BROWSER_WINDOW_TITLE = "calc_browser";

	public static int DEFAULT_CED_BROWSER_WINDOW_WIDTH = 300;

	public static int DEFAULT_CED_BROWSER_WINDOW_HEIGHT = 250;

	public static int DEFAULT_MAINFRAME_WIDTH = 850;

	public static int DEFAULT_MAINFRAME_HEIGHT = 600;

	public static final int PALETTE_DOC_SPLIT_LOCATION = 300;

	// General components
	public static File VIEWPOINT_VIEW_FIB = new FileResource("Fib/ViewPointView.fib");
	public static File STANDARD_EDITION_PATTERN_VIEW_FIB = new FileResource("Fib/StandardEditionPatternView.fib");
	public static File DIAGRAM_EDITION_PATTERN_VIEW_FIB = new FileResource("Fib/DiagramEditionPatternView.fib");
	public static File VIRTUAL_MODEL_VIEW_FIB = new FileResource("Fib/VirtualModelView.fib");
	public static File DIAGRAM_SPECIFICATION_VIEW_FIB = new FileResource("Fib/DiagramSpecificationView.fib");

	// ViewPoint edition
	public static File CREATE_MODEL_SLOT_DIALOG_FIB = new FileResource("Fib/Dialog/CreateModelSlotDialog.fib");
	public static File CREATE_PATTERN_ROLE_DIALOG_FIB = new FileResource("Fib/Dialog/CreatePatternRoleDialog.fib");
	public static File CREATE_EDITION_ACTION_DIALOG_FIB = new FileResource("Fib/Dialog/CreateEditionActionDialog.fib");
	public static File CREATE_EXAMPLE_DIAGRAM_DIALOG_FIB = new FileResource("Fib/Dialog/CreateExampleDrawingDialog.fib");
	public static File CREATE_PALETTE_DIALOG_FIB = new FileResource("Fib/Dialog/CreatePaletteDialog.fib");
	public static File CREATE_VIEW_POINT_DIALOG_FIB = new FileResource("Fib/Dialog/CreateViewPointDialog.fib");
	public static File CREATE_VIRTUAL_MODEL_DIALOG_FIB = new FileResource("Fib/Dialog/CreateVirtualModelDialog.fib");
	public static File CREATE_DIAGRAM_SPECIFICATION_DIALOG_FIB = new FileResource("Fib/Dialog/CreateDiagramSpecificationDialog.fib");
	public static File DECLARE_SHAPE_IN_EDITION_PATTERN_DIALOG_FIB = new FileResource("Fib/Dialog/DeclareShapeInEditionPatternDialog.fib");
	public static File DECLARE_CONNECTOR_IN_EDITION_PATTERN_DIALOG_FIB = new FileResource(
			"Fib/Dialog/DeclareConnectorInEditionPatternDialog.fib");
	public static File PUSH_TO_PALETTE_DIALOG_FIB = new FileResource("Fib/Dialog/PushToPaletteDialog.fib");
	public static File SHOW_FML_REPRESENTATION_DIALOG_FIB = new FileResource("Fib/Dialog/ShowFMLRepresentationDialog.fib");

}
