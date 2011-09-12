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
package org.openflexo.ced;

import java.io.File;

import org.openflexo.toolbox.FileResource;


/**
 * Constants used by the CalcEditor module.
 * 
 * @author yourname
 */
public class CEDCst
{

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
    public static File ONTOLOGY_VIEW_FIB = new FileResource("Fib/OntologyView.fib");
    public static File CALC_LIBRARY_VIEW_FIB = new FileResource("Fib/CalcLibraryView.fib");
    public static File CALC_VIEW_FIB = new FileResource("Fib/CalcView.fib");
    public static File EDITION_PATTERN_VIEW_FIB = new FileResource("Fib/EditionPatternView.fib");

    // Calc edition
    public static File CREATE_CALC_DRAWING_SHEMA_DIALOG_FIB = new FileResource("Fib/Dialog/CreateCalcDrawingShemaDialog.fib");
    public static File CREATE_CALC_PALETTE_DIALOG_FIB = new FileResource("Fib/Dialog/CreateCalcPaletteDialog.fib");
    public static File CREATE_ONTOLOGY_CALC_DIALOG_FIB = new FileResource("Fib/Dialog/CreateOntologyCalcDialog.fib");
    public static File DECLARE_IN_EDITION_PATTERN_DIALOG_FIB = new FileResource("Fib/Dialog/DeclareInEditionPatternDialog.fib");
    public static File PUSH_TO_PALETTE_DIALOG_FIB = new FileResource("Fib/Dialog/PushToPaletteDialog.fib");

    // Ontology edition
    public static File CREATE_ONTOLOGY_CLASS_DIALOG_FIB = new FileResource("Fib/Dialog/CreateOntologyClassDialog.fib");
    public static File CREATE_ONTOLOGY_INDIVIDUAL_FIB = new FileResource("Fib/Dialog/CreateOntologyIndividualDialog.fib");
    public static File DELETE_ONTOLOGY_OBJECTS_DIALOG_FIB = new FileResource("Fib/Dialog/DeleteOntologyObjectsDialog.fib");
    public static File CREATE_DATA_PROPERTY_DIALOG_FIB = new FileResource("Fib/Dialog/CreateDataPropertyDialog.fib");
    public static File CREATE_OBJECT_PROPERTY_DIALOG_FIB = new FileResource("Fib/Dialog/CreateObjectPropertyDialog.fib");
   
    // Saving operations
    public static File REVIEW_UNSAVED_CALC_EDITOR_DIALOG_FIB = new FileResource("Fib/Dialog/ReviewUnsavedCalcEditorDialog.fib");
    public static File SAVE_CALC_EDITOR_DIALOG_FIB = new FileResource("Fib/Dialog/SaveCalcEditorDialog.fib");

}
