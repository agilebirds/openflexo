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
package org.openflexo.foundation.view;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.rm.FlexoProject;

public class TestCreateView extends FlexoTestCase {

	public static FlexoProject project;
	private static FlexoEditor editor;

	/**
	 * Instantiate test resource center
	 */
	public void test0InstantiateResourceCenter() {

		log("test0InstantiateResourceCenter()");

		// TODO: create a project where all those tests don't need a manual import of projects
		// TODO: copy all test VP in tmp dir and work with those VP instead of polling GIT workspace
		instanciateTestServiceManager();
	}

	public void test1CreateProject() {
		editor = createProject("TestCreateView");
		project = editor.getProject();
	}

}
