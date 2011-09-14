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
package org.openflexo.foundation.ontology.calc.action;

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
import org.openflexo.foundation.ontology.calc.CalcFolder;
import org.openflexo.foundation.ontology.calc.CalcLibrary;
import org.openflexo.foundation.ontology.calc.CalcLibraryObject;
import org.openflexo.foundation.ontology.calc.CalcObject;
import org.openflexo.foundation.ontology.calc.OntologyCalc;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.JavaUtils;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.toolbox.ToolBox;


public class CreateOntologyCalc extends FlexoAction<CreateOntologyCalc,CalcLibraryObject,CalcObject> 
{

	private static final Logger logger = Logger.getLogger(CreateOntologyCalc.class.getPackage().getName());

	public static FlexoActionType<CreateOntologyCalc,CalcLibraryObject,CalcObject> actionType 
	= new FlexoActionType<CreateOntologyCalc,CalcLibraryObject,CalcObject> (
			"create_ontology_calc",
			FlexoActionType.newMenu,
			FlexoActionType.defaultGroup,
			FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateOntologyCalc makeNewAction(CalcLibraryObject focusedObject, Vector<CalcObject> globalSelection, FlexoEditor editor) 
		{
			return new CreateOntologyCalc(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(CalcLibraryObject object, Vector<CalcObject> globalSelection) 
		{
			return object != null;
		}

		@Override
		protected boolean isEnabledForSelection(CalcLibraryObject object, Vector<CalcObject> globalSelection) 
		{
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass (CreateOntologyCalc.actionType, CalcLibrary.class);
		FlexoModelObject.addActionForClass (CreateOntologyCalc.actionType, CalcFolder.class);
	}


	private String _newCalcName;
	private String _newCalcURI;
	private String _newCalcDescription;
	private File _ontologyFile;
	private CalcFolder _calcFolder;
	private OntologyCalc _newCalc;

	public Vector<ImportedOntology> importedOntologies = new Vector<ImportedOntology>();
	
	private boolean createsOntology = false;
	
	CreateOntologyCalc (CalcLibraryObject focusedObject, Vector<CalcObject> globalSelection, FlexoEditor editor)
	{
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws IOFlexoException
	{
		logger.info ("Create new calc");  	

		CalcLibrary calcLibrary = getFocusedObject().getCalcLibrary();
		
		File newCalcDir = getCalcDir();
		
		newCalcDir.mkdirs();
		
		if (createsOntology) {
			buildOntology();
		}
		else {
			try {
				FileUtils.copyFileToDir(getOntologyFile(), newCalcDir);
			} catch (IOException e) {
				throw new IOFlexoException(e);
			}

			_ontologyFile = new File(newCalcDir,_ontologyFile.getName());
		}
				
		// Instanciate new Calc
		_newCalc = OntologyCalc.newOntologyCalc(getBaseName(),getNewCalcURI(),getOntologyFile(),newCalcDir,calcLibrary,getCalcFolder());
		_newCalc.setDescription(getNewCalcDescription());
		
		// And register it to the library
		calcLibrary.registerCalc(_newCalc);
	}
	
	private ImportedOntology buildOntology()
	{
		ImportedOntology newOntology =  ImportedOntology.createNewImportedOntology(getNewCalcURI(), _ontologyFile, getCalcFolder().getOntologyLibrary());
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
	

	public String getNewCalcName()
	{
		return _newCalcName;
	}

	public void setNewCalcName(String newCalcName)
	{
		this._newCalcName = newCalcName;
	}

	public String getNewCalcURI()
	{
		if (StringUtils.isEmpty(_newCalcURI) && getOntologyFile() != null) {
			return FlexoOntology.findOntologyURI(getOntologyFile());
		}
		return _newCalcURI;
	}

	public void setNewCalcURI(String newCalcURI)
	{
		this._newCalcURI = newCalcURI;
	}

	public String getNewCalcDescription()
	{
		return _newCalcDescription;
	}

	public void setNewCalcDescription(String newCalcDescription)
	{
		this._newCalcDescription = newCalcDescription;
	}

	public File getOntologyFile()
	{
		return _ontologyFile;
	}

	public void setOntologyFile(File ontologyFile)
	{
		createsOntology = false;
		this._ontologyFile = ontologyFile;
		if (ontologyFile != null) {
			String ontologyURI = FlexoOntology.findOntologyURI(getOntologyFile());
			String ontologyName = ToolBox.getJavaClassName(FlexoOntology.findOntologyName(getOntologyFile()));
			if (ontologyName == null && ontologyFile != null && ontologyFile.getName().length() > 4) {
				ontologyName = ontologyFile.getName().substring(0, ontologyFile.getName().length()-4);
			}
			if (StringUtils.isNotEmpty(ontologyURI)) _newCalcURI = ontologyURI;
			setNewCalcName(ontologyName);
		}
	}
	
	public CalcFolder getCalcFolder() 
	{
		if (_calcFolder == null) {
			if (getFocusedObject() instanceof CalcFolder) {
				return _calcFolder = (CalcFolder)getFocusedObject();
			}
			else if (getFocusedObject() instanceof CalcLibrary) {
				return _calcFolder = ((CalcLibrary)getFocusedObject()).getRootFolder();
			}
			return null;
		}
		return _calcFolder;
	}

	public void setCalcFolder(CalcFolder calcFolder) 
	{
		_calcFolder = calcFolder;
	}

	public boolean isNewCalcNameValid()
	{
		if (StringUtils.isEmpty(getNewCalcName())) {
			errorMessage = "please_supply_valid_calc_name";
			return false;
		}
		return true;
	}

	public boolean isNewCalcURIValid()
	{
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
	
	public boolean isValid()
	{
		return isNewCalcNameValid() && isNewCalcURIValid() && getOntologyFile() != null;
	}

	public OntologyCalc getNewCalc()
	{
		return _newCalc;
	}
	
	private String getBaseName()
	{
		return  JavaUtils.getClassName(getNewCalcName());
	}
	
	private File getCalcDir()
	{
		String baseName = getBaseName();
		return new File(getCalcFolder().getExpectedPath(),baseName+".calc");
	}
	
	public void createOntology()
	{
		System.out.println("createOntology() !!!");
		createsOntology = true;
		_ontologyFile = new File(getCalcDir(),getBaseName()+".owl");
	}
	
	public void addToImportedOntologies(ImportedOntology ontology)
	{
		System.out.println("import ontology "+ontology);
		importedOntologies.add(ontology);
	}

	public void removeFromImportedOntologies(ImportedOntology ontology)
	{
		System.out.println("remove ontology "+ontology);
		importedOntologies.remove(ontology);
	}
}