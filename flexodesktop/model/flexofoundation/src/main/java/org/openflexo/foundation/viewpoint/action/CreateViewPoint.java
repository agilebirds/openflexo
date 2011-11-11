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
package org.openflexo.foundation.viewpoint.action;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ontology.FlexoOntology;
import org.openflexo.foundation.ontology.ImportedOntology;
import org.openflexo.foundation.ontology.FlexoOntology.OntologyNotFoundException;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.viewpoint.ViewPointFolder;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.ViewPointLibraryObject;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.JavaUtils;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;

public class CreateViewPoint extends FlexoAction<CreateViewPoint, ViewPointLibraryObject, ViewPointObject> {

	private static final Logger logger = Logger.getLogger(CreateViewPoint.class.getPackage().getName());

	public static FlexoActionType<CreateViewPoint, ViewPointLibraryObject, ViewPointObject> actionType = new FlexoActionType<CreateViewPoint, ViewPointLibraryObject, ViewPointObject>(
			"create_view_point", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateViewPoint makeNewAction(ViewPointLibraryObject focusedObject, Vector<ViewPointObject> globalSelection,
				FlexoEditor editor) {
			return new CreateViewPoint(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(ViewPointLibraryObject object, Vector<ViewPointObject> globalSelection) {
			return object != null;
		}

		@Override
		protected boolean isEnabledForSelection(ViewPointLibraryObject object, Vector<ViewPointObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(CreateViewPoint.actionType, ViewPointLibrary.class);
		FlexoModelObject.addActionForClass(CreateViewPoint.actionType, ViewPointFolder.class);
	}

	private String _newCalcName;
	private String _newCalcURI;
	private String _newCalcDescription;
	private File _ontologyFile;
	private ViewPointFolder _calcFolder;
	private ViewPoint _newCalc;

	public Vector<ImportedOntology> importedOntologies = new Vector<ImportedOntology>();

	private boolean createsOntology = false;

	CreateViewPoint(ViewPointLibraryObject focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws IOFlexoException {
		logger.info("Create new calc");

		ViewPointLibrary viewPointLibrary = getFocusedObject().getViewPointLibrary();

		File newCalcDir = getCalcDir();

		newCalcDir.mkdirs();

		if (createsOntology) {
			buildOntology();
		} else {
			try {
				FileUtils.copyFileToDir(getOntologyFile(), newCalcDir);
			} catch (IOException e) {
				throw new IOFlexoException(e);
			}

			_ontologyFile = new File(newCalcDir, _ontologyFile.getName());
		}

		// Instanciate new Calc
		_newCalc = ViewPoint.newViewPoint(getBaseName(), getNewCalcURI(), getOntologyFile(), newCalcDir, viewPointLibrary, getCalcFolder());
		_newCalc.setDescription(getNewCalcDescription());

		// And register it to the library
		viewPointLibrary.registerViewPoint(_newCalc);
	}

	private ImportedOntology buildOntology() {
		ImportedOntology newOntology = ImportedOntology.createNewImportedOntology(getNewCalcURI(), _ontologyFile, getCalcFolder()
				.getOntologyLibrary());
		for (ImportedOntology importedOntology : importedOntologies) {
			try {
				newOntology.importOntology(importedOntology);
			} catch (OntologyNotFoundException e) {
				e.printStackTrace();
			}
		}
		try {
			newOntology.save();
		} catch (SaveResourceException e) {
			e.printStackTrace();
		}
		return newOntology;
	}

	public String getNewCalcName() {
		return _newCalcName;
	}

	public void setNewCalcName(String newCalcName) {
		this._newCalcName = newCalcName;
	}

	public String getNewCalcURI() {
		if (StringUtils.isEmpty(_newCalcURI) && getOntologyFile() != null) {
			return FlexoOntology.findOntologyURI(getOntologyFile());
		}
		return _newCalcURI;
	}

	public void setNewCalcURI(String newCalcURI) {
		this._newCalcURI = newCalcURI;
	}

	public String getNewCalcDescription() {
		return _newCalcDescription;
	}

	public void setNewCalcDescription(String newCalcDescription) {
		this._newCalcDescription = newCalcDescription;
	}

	public File getOntologyFile() {
		return _ontologyFile;
	}

	public void setOntologyFile(File ontologyFile) {
		createsOntology = false;
		this._ontologyFile = ontologyFile;
		if (ontologyFile != null) {
			String ontologyURI = FlexoOntology.findOntologyURI(getOntologyFile());
			String ontologyName = ToolBox.getJavaClassName(FlexoOntology.findOntologyName(getOntologyFile()));
			if (ontologyName == null && ontologyFile != null && ontologyFile.getName().length() > 4) {
				ontologyName = ontologyFile.getName().substring(0, ontologyFile.getName().length() - 4);
			}
			if (StringUtils.isNotEmpty(ontologyURI))
				_newCalcURI = ontologyURI;
			setNewCalcName(ontologyName);
		}
	}

	public ViewPointFolder getCalcFolder() {
		if (_calcFolder == null) {
			if (getFocusedObject() instanceof ViewPointFolder) {
				return _calcFolder = (ViewPointFolder) getFocusedObject();
			} else if (getFocusedObject() instanceof ViewPointLibrary) {
				return _calcFolder = ((ViewPointLibrary) getFocusedObject()).getRootFolder();
			}
			return null;
		}
		return _calcFolder;
	}

	public void setCalcFolder(ViewPointFolder viewPointFolder) {
		_calcFolder = viewPointFolder;
	}

	public boolean isNewCalcNameValid() {
		if (StringUtils.isEmpty(getNewCalcName())) {
			errorMessage = "please_supply_valid_view_point_name";
			return false;
		}
		return true;
	}

	public boolean isNewCalcURIValid() {
		if (StringUtils.isEmpty(getNewCalcURI())) {
			errorMessage = "please_supply_valid_uri";
			return false;
		}
		try {
			new URL(getNewCalcURI());
		} catch (MalformedURLException e) {
			errorMessage = "malformed_uri";
			return false;
		}
		if (!getNewCalcURI().endsWith(".owl")) {
			errorMessage = "malformed_uri";
			return false;
		}
		return true;
	}

	public String errorMessage;

	public boolean isValid() {
		return isNewCalcNameValid() && isNewCalcURIValid() && getOntologyFile() != null;
	}

	public ViewPoint getNewCalc() {
		return _newCalc;
	}

	private String getBaseName() {
		return JavaUtils.getClassName(getNewCalcName());
	}

	private File getCalcDir() {
		String baseName = getBaseName();
		return new File(getCalcFolder().getExpectedPath(), baseName + ".viewpoint");
	}

	public void createOntology() {
		createsOntology = true;
		_ontologyFile = new File(getCalcDir(), getBaseName() + ".owl");
	}

	public void addToImportedOntologies(ImportedOntology ontology) {
		System.out.println("import ontology " + ontology);
		importedOntologies.add(ontology);
	}

	public void removeFromImportedOntologies(ImportedOntology ontology) {
		System.out.println("remove ontology " + ontology);
		importedOntologies.remove(ontology);
	}
}