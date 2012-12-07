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
package org.openflexo.technologyadapter.owl.controller;

import java.io.File;

import org.openflexo.toolbox.FileResource;

/**
 * Encodes FIB components used in the context of OWL technology adapter
 * 
 * @author sylvain
 */
public class OWLFIBLibrary {

	public static File CREATE_ONTOLOGY_CLASS_DIALOG_FIB = new FileResource("Fib/Dialog/CreateOntologyClassDialog.fib");
	public static File CREATE_ONTOLOGY_INDIVIDUAL_FIB = new FileResource("Fib/Dialog/CreateOntologyIndividualDialog.fib");
	public static File DELETE_ONTOLOGY_OBJECTS_DIALOG_FIB = new FileResource("Fib/Dialog/DeleteOntologyObjectsDialog.fib");
	public static File CREATE_DATA_PROPERTY_DIALOG_FIB = new FileResource("Fib/Dialog/CreateDataPropertyDialog.fib");
	public static File CREATE_OBJECT_PROPERTY_DIALOG_FIB = new FileResource("Fib/Dialog/CreateObjectPropertyDialog.fib");

}
