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
package org.openflexo.ie.view.controller.dnd;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.dnd.TreeDropTarget;
import org.openflexo.components.browser.ie.FlexoItemMenuElement;
import org.openflexo.components.browser.view.BrowserView.FlexoJTree;
import org.openflexo.foundation.ie.menu.FlexoItemMenu;

/**
 * 
 * @author gpolet
 * 
 */
public class MenuTreeDropTarget extends TreeDropTarget {

	public MenuTreeDropTarget(FlexoJTree tree, ProjectBrowser browser) {
		super(tree, browser);
	}

	@Override
	public boolean targetAcceptsSource(BrowserElement target, BrowserElement source) {
		if (source instanceof FlexoItemMenuElement && target instanceof FlexoItemMenuElement) {
			return !((FlexoItemMenuElement) target).getItemMenu().isChildOf(((FlexoItemMenuElement) source).getItemMenu());
		}
		return false;
	}

	@Override
	public boolean handleDrop(BrowserElement moved, BrowserElement destination) {
		if (moved instanceof FlexoItemMenuElement && destination instanceof FlexoItemMenuElement) {
			FlexoItemMenu movedMenu = ((FlexoItemMenuElement) moved).getItemMenu();
			FlexoItemMenu newParentMenu = null;
			FlexoItemMenu oldParentMenu = movedMenu.getFather();
			if (destination instanceof FlexoItemMenuElement) {
				newParentMenu = ((FlexoItemMenuElement) destination).getItemMenu();
			}
			if (newParentMenu != null && movedMenu != null && oldParentMenu != null) {
				if (!newParentMenu.isChildOf(movedMenu)) {
					oldParentMenu.removeFromSubItems(movedMenu);
					newParentMenu.addToSubItems(movedMenu);
					movedMenu.setFather(newParentMenu);
					_browser.update();
					return true;
				}
			}
			return false;
		}
		return false;
	}
}
