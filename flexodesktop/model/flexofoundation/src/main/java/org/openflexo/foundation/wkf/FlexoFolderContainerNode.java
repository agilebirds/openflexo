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
package org.openflexo.foundation.wkf;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.FlexoIndexManager;
import org.openflexo.foundation.wkf.dm.ProcessFolderAdded;
import org.openflexo.foundation.wkf.dm.ProcessFolderRemoved;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.ToolBox;

public abstract class FlexoFolderContainerNode extends WorkflowModelObject {

	private static final Logger logger = FlexoLogger.getLogger(FlexoFolderContainerNode.class.getPackage().getName());

	private Vector<ProcessFolder> folders;

	public FlexoFolderContainerNode(FlexoProject project, FlexoWorkflow workflow) {
		super(project, workflow);
		folders = new Vector<ProcessFolder>();
	}

	@Override
	public void delete() {
		for (ProcessFolder folder : new Vector<ProcessFolder>(getFolders())) {
			folder.delete();
		}
		super.delete();
	}

	public Vector<ProcessFolder> getFolders() {
		return folders;
	}

	public void setFolders(Vector<ProcessFolder> folders) {
		this.folders = folders;
	}

	public void addToFolders(ProcessFolder folder) {
		if (folder == this) {
			return;
		}
		if (!folders.contains(folder)) {
			if (!isDeserializing()) {
				for (FlexoProcessNode node : folder.getProcesses()) {
					if (node.getFatherProcessNode() != getProcessNode()) {
						if (logger.isLoggable(Level.WARNING)) {
							logger.warning("Folder " + folder.getName() + " cannot be added to " + this.getName() + " because process "
									+ node.getName() + " is not one of my sub-processes");
						}
						return;
					}
				}
			}
			folders.add(folder);
			folder.setParent(this);
			if (!isDeserializing()) {
				folder.setIndexValue(folders.size());
				setChanged();
				notifyObservers(new ProcessFolderAdded(folder));
				getPropertyChangeSupport().firePropertyChange("sortedFolders", null, folder);
			}
		}
	}

	public void removeFromFolders(ProcessFolder folder) {
		if (folders.contains(folder)) {
			folders.remove(folder);
			folder.setParent(null);
			FlexoIndexManager.reIndexObjectOfArray(folders.toArray(new ProcessFolder[0]));
			setChanged();
			notifyObservers(new ProcessFolderRemoved(folder));
			getPropertyChangeSupport().firePropertyChange("sortedFolders", folder, null);
		}
	}

	public ProcessFolder getFolderWithName(String name) {
		if (name == null) {
			return null;
		}
		for (ProcessFolder folder : getFolders()) {
			if (name.equals(folder.getName())) {
				return folder;
			}
		}
		return null;
	}

	public Enumeration<ProcessFolder> getSortedFolders() {
		disableObserving();
		ProcessFolder[] o = FlexoIndexManager.sortArray(getFolders().toArray(new ProcessFolder[0]));
		enableObserving();
		return ToolBox.getEnumeration(o);
	}

	public String getNewFolderName() {
		String base = FlexoLocalization.localizedForKey("process_folder");
		String attempt = base;
		int i = 0;
		while (getFolderWithName(attempt) != null || attempt.equals(getName())) {
			attempt = base + "-" + i++;
		}
		return attempt;
	}

	public abstract FlexoProcessNode getProcessNode();

}
