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
package org.openflexo.foundation.ie.menu.action;

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.ie.menu.FlexoItemMenu;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.toolbox.FileUtils;

public class TestMenu extends FlexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestMenu.class.getPackage().getName());

	private FlexoEditor _editor;
	private FlexoProject _project;

	/**
	 * Creates a new empty project in a temp directory
	 */
	public void test0CreateProject() {
		_editor = createProject("MenuTest");
		_project = _editor.getProject();
		FlexoItemMenu rootMenu = _project.getFlexoNavigationMenu().getRootMenu();
		assertNotNull(rootMenu);
		FlexoItemMenu menu1 = createMenu("menu1", rootMenu, _editor);
		FlexoItemMenu menu2 = createMenu("menu2", rootMenu, _editor);
		moveMenuDown(menu1, _editor);
		moveMenuUp(menu1, _editor);
		FlexoItemMenu menu21 = createMenu("menu21", menu2, _editor);
		moveMenuUpperLevel(menu21, _editor);
		_project.close();
		FileUtils.deleteDir(_project.getProjectDirectory());
		_project = null;
		_editor = null;
	}

	protected void moveMenuUp(FlexoItemMenu itemMenu, FlexoEditor editor) {
		MoveMenuUp addmenu = (MoveMenuUp) MoveMenuUp.actionType.makeNewAction(itemMenu, null, editor);
		addmenu.setItemMenu(itemMenu);
		int oldIndex = -1;
		if (itemMenu.getFather() != null) {
			oldIndex = itemMenu.getFather().getSubItems().indexOf(itemMenu);
		}
		boolean isFirstElement = itemMenu.getFather() != null && itemMenu.getFather().getSubItems().indexOf(itemMenu) == 0;
		addmenu.doAction();
		if (!isFirstElement) {
			assertTrue(addmenu.hasActionExecutionSucceeded());
		} else {
			assertFalse(addmenu.hasActionExecutionSucceeded());
			return;
		}
		assertEquals(oldIndex - 1, itemMenu.getFather().getSubItems().indexOf(itemMenu));
	}

	protected void moveMenuDown(FlexoItemMenu itemMenu, FlexoEditor editor) {
		MoveMenuDown addmenu = (MoveMenuDown) MoveMenuDown.actionType.makeNewAction(itemMenu, null, editor);
		addmenu.setItemMenu(itemMenu);
		int oldIndex = -1;
		boolean isLastElement = itemMenu.getFather() != null && itemMenu.getFather().getSubItems().lastElement().equals(itemMenu);
		if (itemMenu.getFather() != null) {
			oldIndex = itemMenu.getFather().getSubItems().indexOf(itemMenu);
		}
		addmenu.doAction();
		if (itemMenu.getFather() != null && !isLastElement) {
			assertTrue(addmenu.hasActionExecutionSucceeded());
		} else {
			assertFalse(addmenu.hasActionExecutionSucceeded());
			return;
		}
		assertEquals(oldIndex + 1, itemMenu.getFather().getSubItems().indexOf(itemMenu));
	}

	protected void moveMenuUpperLevel(FlexoItemMenu itemMenu, FlexoEditor editor) {
		MoveMenuUpperLevel addmenu = (MoveMenuUpperLevel) MoveMenuUpperLevel.actionType.makeNewAction(itemMenu, null, editor);
		addmenu.setItemMenu(itemMenu);
		FlexoItemMenu newFather = itemMenu.getFather() == null ? null : itemMenu.getFather().getFather();
		addmenu.doAction();
		if (newFather != null) {
			assertTrue(addmenu.hasActionExecutionSucceeded());
		} else {
			assertFalse(addmenu.hasActionExecutionSucceeded());
			return;
		}
		assertEquals(newFather, itemMenu.getFather());
	}

	protected FlexoItemMenu createMenu(String menuLabel, FlexoItemMenu parentMenu, FlexoEditor editor) {
		AddMenu addmenu = (AddMenu) AddMenu.actionType.makeNewAction(parentMenu, null, editor);
		addmenu.setFather(parentMenu);
		addmenu.setMenuLabel(menuLabel);
		addmenu.doAction();
		if (parentMenu != null && menuLabel != null) {
			assertTrue(addmenu.hasActionExecutionSucceeded());
		} else {
			assertFalse(addmenu.hasActionExecutionSucceeded());
			return null;
		}
		assertNotNull(addmenu.getNewMenu());
		assertEquals(addmenu.getNewMenu().getMenuLabel(), menuLabel);
		return addmenu.getNewMenu();
	}

	public TestMenu(String arg0) {
		super(arg0);
	}

}
