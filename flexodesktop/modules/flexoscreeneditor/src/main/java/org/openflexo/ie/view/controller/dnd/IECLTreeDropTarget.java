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
import org.openflexo.components.browser.ie.ComponentElement;
import org.openflexo.components.browser.ie.ComponentFolderElement;
import org.openflexo.components.browser.ie.MonitoringScreenDefinitionElement;
import org.openflexo.components.browser.ie.ReusableComponentDefinitionElement;
import org.openflexo.components.browser.view.BrowserView.FlexoJTree;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.view.controller.FlexoController;

/**
 * 
 * @author gpolet
 * 
 */
public class IECLTreeDropTarget extends TreeDropTarget {

	public IECLTreeDropTarget(FlexoJTree tree, ProjectBrowser browser) {
		super(tree, browser);
	}

	@Override
	public boolean targetAcceptsSource(BrowserElement target, BrowserElement source) {
		if (!(target instanceof ComponentFolderElement) && (target.getParent() instanceof ComponentFolderElement)) {
			target = (ComponentFolderElement) target.getParent();
		}
		if (target instanceof ComponentFolderElement) {
			FlexoComponentFolder targetFolder = (FlexoComponentFolder) ((ComponentFolderElement) target).getObject();
			if (source instanceof ComponentFolderElement) {
				FlexoComponentFolder sourceFolder = (FlexoComponentFolder) ((ComponentFolderElement) source).getObject();
				if (sourceFolder.isRootFolder())
					return false;
				FlexoComponentFolder srcFolder = (FlexoComponentFolder) ((ComponentFolderElement) ((ComponentFolderElement) source)
						.getParent()).getObject();
				if (targetFolder == sourceFolder)
					return false;
				if (targetFolder.getFolderNamed(sourceFolder.getName()) != null) {
					return false;
				}
				if (targetFolder != srcFolder && !sourceFolder.isFatherOf(targetFolder)) {
					return true;
				} else {
					return false;
				}
			} else if ((source instanceof ReusableComponentDefinitionElement) || (source instanceof ComponentElement)
					|| (source instanceof MonitoringScreenDefinitionElement)) {
				ComponentDefinition sourceComp = null;
				FlexoComponentFolder srcFolder = null;
				if (source instanceof ReusableComponentDefinitionElement) {
					sourceComp = ((ReusableComponentDefinitionElement) source).getComponentDefinition();
					srcFolder = (FlexoComponentFolder) ((ComponentFolderElement) ((ReusableComponentDefinitionElement) source).getParent())
							.getObject();
				} else if (source instanceof ComponentElement) {
					sourceComp = ((ComponentElement) source).getComponentDefinition();
					srcFolder = sourceComp.getFolder();
				} else if (source instanceof MonitoringScreenDefinitionElement) {
					sourceComp = ((MonitoringScreenDefinitionElement) source).getComponentDefinition();
					srcFolder = sourceComp.getFolder();
				}
				if (targetFolder != srcFolder) {
					return true;
				} else {
					return false;
				}

			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean handleDrop(BrowserElement moved, BrowserElement destination) {
		if (!(destination instanceof ComponentFolderElement) && !(destination.getParent() instanceof ComponentFolderElement)) {
			return false;
		}
		if (!(destination instanceof ComponentFolderElement) && (destination.getParent() instanceof ComponentFolderElement)) {
			destination = (ComponentFolderElement) destination.getParent();
		}
		FlexoComponentFolder targetFolder = (FlexoComponentFolder) ((ComponentFolderElement) destination).getObject();

		if (moved instanceof ComponentFolderElement) {
			FlexoComponentFolder movedFolder = (FlexoComponentFolder) ((ComponentFolderElement) moved).getObject();
			if (movedFolder.isRootFolder())
				return false;
			FlexoComponentFolder srcFolder = (FlexoComponentFolder) ((ComponentFolderElement) ((ComponentFolderElement) moved).getParent())
					.getObject();
			if (targetFolder == movedFolder)
				return false;
			if (targetFolder.getFolderNamed(movedFolder.getName()) != null) {
				FlexoController.notify("there_is_already_a_folder_with that name");
				return false;
			}

			if (targetFolder != srcFolder && !movedFolder.isFatherOf(targetFolder)) {
				srcFolder.removeFromSubFolders(movedFolder);
				movedFolder.setFatherFolder(targetFolder);
				targetFolder.addToSubFolders(movedFolder);
				// srcFolder.getComponentLibrary().notifyTreeStructureChanged();
			} else {
				return false;
			}
		} else if ((moved instanceof ReusableComponentDefinitionElement) || (moved instanceof ComponentElement)
				|| (moved instanceof MonitoringScreenDefinitionElement)) {
			ComponentDefinition movedComp = null;
			FlexoComponentFolder srcFolder = null;
			if (moved instanceof ReusableComponentDefinitionElement) {
				movedComp = ((ReusableComponentDefinitionElement) moved).getComponentDefinition();
				srcFolder = (FlexoComponentFolder) ((ComponentFolderElement) ((ReusableComponentDefinitionElement) moved).getParent())
						.getObject();
			} else if (moved instanceof ComponentElement) {
				movedComp = ((ComponentElement) moved).getComponentDefinition();
				srcFolder = movedComp.getFolder();
			} else if (moved instanceof MonitoringScreenDefinitionElement) {
				movedComp = ((MonitoringScreenDefinitionElement) moved).getComponentDefinition();
				srcFolder = movedComp.getFolder();
			}
			if (targetFolder != srcFolder) {
				srcFolder.removeFromComponents(movedComp);
				targetFolder.addToComponents(movedComp);
				// srcFolder.getComponentLibrary().notifyTreeStructureChanged();
			} else {
				return false;
			}

		} else {
			return false;
		}
		return true;
	}
}
