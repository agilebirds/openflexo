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
 * Constants used by the OE module.
 * 
 * @author sylvain
 */
public class OECst
{

    public static final String DEFAULT_OE_BROWSER_WINDOW_TITLE = "ontology_browser";

    public static final int DEFAULT_OE_BROWSER_WINDOW_WIDTH = 300;

    public static final int DEFAULT_OE_BROWSER_WINDOW_HEIGHT = 250;

    public static final int DEFAULT_MAINFRAME_WIDTH = 850;

    public static final int DEFAULT_MAINFRAME_HEIGHT = 600;

    public static final int PALETTE_DOC_SPLIT_LOCATION = 300;
    
    // General
    public static File ONTOLOGY_VIEW_FIB = new FileResource("Fib/OntologyView.fib");
    
    // Shema edition
    public static File ADD_VIEW_DIALOG_FIB = new FileResource("Fib/Dialog/AddViewDialog.fib");
    public static File DELETE_VIEW_ELEMENTS_DIALOG_FIB = new FileResource("Fib/Dialog/DeleteViewElementsDialog.fib");
 
    // Ontology edition
    public static File CREATE_ONTOLOGY_CLASS_DIALOG_FIB = new FileResource("Fib/Dialog/CreateOntologyClassDialog.fib");
    public static File CREATE_ONTOLOGY_INDIVIDUAL_FIB = new FileResource("Fib/Dialog/CreateOntologyIndividualDialog.fib");
    public static File DELETE_ONTOLOGY_OBJECTS_DIALOG_FIB = new FileResource("Fib/Dialog/DeleteOntologyObjectsDialog.fib");
    public static File CREATE_DATA_PROPERTY_DIALOG_FIB = new FileResource("Fib/Dialog/CreateDataPropertyDialog.fib");
    public static File CREATE_OBJECT_PROPERTY_DIALOG_FIB = new FileResource("Fib/Dialog/CreateObjectPropertyDialog.fib");

}
