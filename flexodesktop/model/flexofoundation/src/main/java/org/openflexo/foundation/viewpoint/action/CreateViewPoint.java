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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.ViewPointObject;
import org.openflexo.foundation.viewpoint.ViewPointRepository;
import org.openflexo.toolbox.JavaUtils;
import org.openflexo.toolbox.StringUtils;

public class CreateViewPoint extends FlexoAction<CreateViewPoint, RepositoryFolder, ViewPointObject> {

	private static final Logger logger = Logger.getLogger(CreateViewPoint.class.getPackage().getName());

	public static FlexoActionType<CreateViewPoint, RepositoryFolder, ViewPointObject> actionType = new FlexoActionType<CreateViewPoint, RepositoryFolder, ViewPointObject>(
			"create_view_definition", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateViewPoint makeNewAction(RepositoryFolder focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
			return new CreateViewPoint(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(RepositoryFolder object, Vector<ViewPointObject> globalSelection) {
			return object.getResourceRepository() instanceof ViewPointRepository;
		}

		@Override
		public boolean isEnabledForSelection(RepositoryFolder object, Vector<ViewPointObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(CreateViewPoint.actionType, RepositoryFolder.class);
	}

	private String _newViewPointName;
	private String _newViewPointURI;
	private String _newViewPointDescription;
	private ViewPoint newViewPoint;

	CreateViewPoint(RepositoryFolder focusedObject, Vector<ViewPointObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public ViewPointLibrary getViewPointLibrary() {
		if (!(getFocusedObject().getResourceRepository() instanceof ViewPointRepository)) {
			return null;
		}
		return ((ViewPointRepository) getFocusedObject().getResourceRepository()).getViewPointLibrary();
	}

	@Override
	protected void doAction(Object context) throws IOFlexoException {

		if (!(getFocusedObject().getResourceRepository() instanceof ViewPointRepository)) {
			return;
		}

		logger.info("Create new viewpoint");

		ViewPointLibrary viewPointLibrary = getViewPointLibrary();
		ViewPointRepository vpRepository = (ViewPointRepository) getFocusedObject().getResourceRepository();

		File newViewPointDir = getViewPointDir();

		logger.info("Creating viewpoint " + newViewPointDir.getAbsolutePath());

		// newViewPointDir.mkdirs();

		/*if (ontologicalScopeChoice == OntologicalScopeChoices.CREATES_NEW_ONTOLOGY) {
			buildOntology();
		} else if (ontologicalScopeChoice == OntologicalScopeChoices.IMPORT_EXISTING_ONTOLOGY) {
			try {
				FileUtils.copyFileToDir(getOntologyFile(), newViewPointDir);
			} catch (IOException e) {
				throw new IOFlexoException(e);
			}

			_ontologyFile = new File(newViewPointDir, _ontologyFile.getName());
		}*/

		// Instanciate new ViewPoint
		newViewPoint = ViewPoint.newViewPoint(getBaseName(), getNewViewPointURI(), newViewPointDir, viewPointLibrary);
		newViewPoint.setDescription(getNewViewPointDescription());

		vpRepository.registerResource(newViewPoint.getResource(), getFocusedObject());

	}

	/*private OWLMetaModel buildOntology() {
		_ontologyFile = new File(getViewPointDir(), getBaseName() + ".owl");
		OWLMetaModel newOntology = OWLMetaModel.createNewImportedOntology(getNewViewPointURI(), _ontologyFile, getViewPointFolder()
				.getOntologyLibrary());
		for (IFlexoOntology importedOntology : importedOntologies) {
			try {
				if (importedOntology instanceof OWLOntology) {
					newOntology.importOntology(importedOntology);
				} else {
					logger.warning("Could not import anything else than an OWL ontology");
				}
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
	}*/

	public String getNewViewPointName() {
		return _newViewPointName;
	}

	public void setNewViewPointName(String newViewPointName) {
		this._newViewPointName = newViewPointName;
	}

	public String getNewViewPointURI() {
		return _newViewPointURI;
	}

	public void setNewViewPointURI(String newViewPointURI) {
		this._newViewPointURI = newViewPointURI;
	}

	public String getNewViewPointDescription() {
		return _newViewPointDescription;
	}

	public void setNewViewPointDescription(String newViewPointDescription) {
		this._newViewPointDescription = newViewPointDescription;
	}

	/*	public File getOntologyFile() {
		return _ontologyFile;
	}

	public void setOntologyFile(File ontologyFile) {
		this._ontologyFile = ontologyFile;
		if (ontologyFile != null) {
			String ontologyURI = OWLOntology.findOntologyURI(getOntologyFile());
			String ontologyName = ToolBox.getJavaClassName(OWLOntology.findOntologyName(getOntologyFile()));
			if (ontologyName == null && ontologyFile != null && ontologyFile.getName().length() > 4) {
				ontologyName = ontologyFile.getName().substring(0, ontologyFile.getName().length() - 4);
			}
			if (StringUtils.isNotEmpty(ontologyURI)) {
				_newViewPointURI = ontologyURI;
			}
			setNewViewPointName(ontologyName);
		}
		}*/

	public RepositoryFolder getViewPointFolder() {
		return getFocusedObject();
	}

	public boolean isNewViewPointNameValid() {
		if (StringUtils.isEmpty(getNewViewPointName())) {
			errorMessage = "please_supply_valid_view_point_name";
			return false;
		}
		return true;
	}

	public boolean isNewViewPointURIValid() {
		if (StringUtils.isEmpty(getNewViewPointURI())) {
			errorMessage = "please_supply_valid_uri";
			return false;
		}
		try {
			new URL(getNewViewPointURI());
		} catch (MalformedURLException e) {
			errorMessage = "malformed_uri";
			return false;
		}
		if (getViewPointLibrary().getViewPointResource(getNewViewPointURI()) != null) {
			errorMessage = "already_existing_viewpoint_uri";
		}

		return true;
	}

	public String errorMessage;

	@Override
	public boolean isValid() {
		if (!isNewViewPointNameValid()) {
			return false;
		}
		if (!isNewViewPointURIValid()) {
			return false;
		}
		/*if (ontologicalScopeChoice == OntologicalScopeChoices.IMPORT_EXISTING_ONTOLOGY) {
			return getOntologyFile() != null;
		}*/
		return true;
	}

	public ViewPoint getNewViewPoint() {
		return newViewPoint;
	}

	private String getBaseName() {
		return JavaUtils.getClassName(getNewViewPointName());
	}

	private File getViewPointDir() {
		String baseName = getBaseName();
		if (getFocusedObject().getResourceRepository() instanceof ViewPointRepository) {
			return new File(getFocusedObject().getFile(), baseName + ".viewpoint");
		}
		return null;
	}

}
