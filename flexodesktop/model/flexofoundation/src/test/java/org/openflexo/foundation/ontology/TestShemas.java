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
package org.openflexo.foundation.ontology;

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.dkv.TestPopulateDKV;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.view.ViewFolder;
import org.openflexo.foundation.view.action.AddView;
import org.openflexo.foundation.view.action.AddViewFolder;
import org.openflexo.toolbox.FileUtils;

public class TestShemas extends FlexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestPopulateDKV.class.getPackage().getName());

	private static FlexoEditor _editor;
	private static FlexoProject _project;

	public TestShemas(String name) {
		super(name);
	}

	/**
	 * Creates a new empty project in a temp directory
	 */
	public void test0CreateProject() {
		log("test0CreateProject()");

		_editor = createProject("TestShema");
		_project = _editor.getProject();
	}

	/**
	 * Creates a new empty project in a temp directory, create shema library, and reload
	 */
	public void test1CreateOntology() {
		log("test1CreateOntology()");

		logger.info("Hop" + _project.getShemaLibrary());

		saveProject(_project);

		logger.info("Reload project");

		_editor = reloadProject(_project.getProjectDirectory());
		if (_project != null) {
			_project.close();
		}
		_project = _editor.getProject();

	}

	/**
	 * Creates some shema folders
	 */
	public void test2CreateFolders() {
		log("test2CreateFolders()");

		logger.info("Hop " + _project.getShemaLibrary());
		AddViewFolder addFolder1 = AddViewFolder.actionType.makeNewAction(_project.getShemaLibrary(), null, _editor);
		addFolder1.setNewFolderName("Folder1");
		addFolder1.doAction();

		AddViewFolder addFolder2 = AddViewFolder.actionType.makeNewAction(_project.getShemaLibrary(), null, _editor);
		addFolder2.setNewFolderName("Folder2");
		addFolder2.doAction();

		AddViewFolder addFolder3 = AddViewFolder.actionType.makeNewAction(addFolder2.getNewFolder(), null, _editor);
		addFolder3.setNewFolderName("Folder3");
		addFolder3.doAction();

		saveProject(_project);

		logger.info("Reload project");

		_editor = reloadProject(_project.getProjectDirectory());
		if (_project != null) {
			_project.close();
		}
		_project = _editor.getProject();

	}

	/**
	 * Creates a shema
	 */
	public void test3CreateShema() {
		log("test3CreateShema()");

		ViewFolder folder3 = _project.getShemaLibrary().getFolderWithName("Folder3");

		logger.info("Hop " + folder3);

		AddView addShema = AddView.actionType.makeNewAction(folder3, null, _editor);
		addShema.newViewName = "TestShema";
		addShema.doAction();

		try {
			addShema.getNewDiagram().getShemaResource().saveResourceData();
		} catch (SaveResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		saveProject(_project);

		logger.info("Reload project");

		_editor = reloadProject(_project.getProjectDirectory());
		if (_project != null) {
			_project.close();
		}
		_project = _editor.getProject();
		FileUtils.deleteDir(_project.getProjectDirectory());
		_project = null;
		_editor = null;
	}

}
