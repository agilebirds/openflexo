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
import org.openflexo.foundation.action.AddRepositoryFolder;
import org.openflexo.foundation.dkv.TestPopulateDKV;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoViewResource;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.view.action.CreateView;
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
	 * Creates a new empty project in a temp directory, create view library, and reload
	 */
	public void test1CreateOntology() {
		log("test1CreateOntology()");

		logger.info("Hop" + _project.getViewLibrary());

		saveProject(_project);

		logger.info("Reload project");

		_editor = reloadProject(_project.getProjectDirectory());
		if (_project != null) {
			_project.close();
		}
		_project = _editor.getProject();

	}

	/**
	 * Creates some view folders
	 */
	public void test2CreateFolders() {
		log("test2CreateFolders()");

		logger.info("Hop " + _project.getViewLibrary());
		AddRepositoryFolder addFolder1 = AddRepositoryFolder.actionType.makeNewAction(_project.getViewLibrary().getRootFolder(), null,
				_editor);
		addFolder1.setNewFolderName("Folder1");
		addFolder1.doAction();

		AddRepositoryFolder addFolder2 = AddRepositoryFolder.actionType.makeNewAction(_project.getViewLibrary().getRootFolder(), null,
				_editor);
		addFolder2.setNewFolderName("Folder2");
		addFolder2.doAction();

		AddRepositoryFolder addFolder3 = AddRepositoryFolder.actionType.makeNewAction(addFolder2.getNewFolder(), null, _editor);
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
	 * Creates a view
	 */
	public void test3CreateShema() {
		log("test3CreateShema()");

		RepositoryFolder<FlexoViewResource> folder3 = _project.getViewLibrary().getFolderWithName("Folder3");

		logger.info("Hop " + folder3);

		CreateView addShema = CreateView.actionType.makeNewAction(folder3, null, _editor);
		addShema.newViewName = "TestShema";
		addShema.doAction();

		try {
			addShema.getNewView().getFlexoResource().saveResourceData();
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
