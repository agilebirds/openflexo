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

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.AttributeDataModification;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.utils.FlexoIndexManager;
import org.openflexo.foundation.utils.Sortable;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.wkf.dm.ChildrenOrderChanged;
import org.openflexo.foundation.wkf.dm.ProcessAddedToFolder;
import org.openflexo.foundation.wkf.dm.ProcessRemovedFromFolder;
import org.openflexo.foundation.xml.FlexoWorkflowBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.toolbox.EmptyVector;

/**
 * A Process folder is a set of process nodes that simply aggregates some process into a logical category
 * 
 * @author guillaume
 */
public class ProcessFolder extends FlexoFolderContainerNode implements InspectableObject, Sortable {
	private static final Logger logger = Logger.getLogger(ProcessFolder.class.getPackage().getName());

	private FlexoFolderContainerNode parent;

	private Vector<FlexoProcessNode> processes;
	private int index = -1;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public ProcessFolder(FlexoWorkflowBuilder builder) {
		this(builder.workflow);
		initializeDeserialization(builder);
	}

	public ProcessFolder(FlexoWorkflow workflow) {
		super(workflow.getProject(), workflow);
		processes = new Vector<FlexoProcessNode>();
	}

	public ProcessFolder(FlexoWorkflow workflow, FlexoFolderContainerNode parent) {
		this(workflow);
		parent.addToFolders(this);
	}

	@Override
	public void delete() {
		super.delete();
		for (FlexoProcessNode node : new Vector<FlexoProcessNode>(getProcesses())) {
			if (getParent() instanceof ProcessFolder) {
				((ProcessFolder) getParent()).addToProcesses(node);
			} else {
				node.removeParentFolder(this);
			}
		}
		setParent(null);
		deleteObservers();
	}

	public FlexoFolderContainerNode getParent() {
		return parent;
	}

	public void setParent(FlexoFolderContainerNode parent) {
		if (this.parent == parent) {
			return;
		}
		if (this.parent != null) {
			this.parent.removeFromFolders(this);
		}
		this.parent = parent;
		if (this.parent != null) {
			this.parent.addToFolders(this);
		}
	}

	@Override
	public String getInspectorName() {
		return Inspectors.WKF.PROCESS_FOLDER_INSPECTOR;
	}

	@Override
	public String getFullyQualifiedName() {
		return "PROCESS_FOLDER." + getName();
	}

	public Vector<FlexoProcessNode> getProcesses() {
		return processes;
	}

	public void setProcesses(Vector<FlexoProcessNode> processes) {
		this.processes = processes;
	}

	public void addToProcesses(FlexoProcessNode process) {
		if (!processes.contains(process) && (process.getFatherProcessNode() == getProcessNode() || isDeserializing())) {
			if (process.getParentFolder() != null) {
				process.getParentFolder().removeFromProcesses(process);
			}
			processes.add(process);
			process.addParentFolder(this);
			if (!isDeserializing()) {
				setChanged();
				notifyObservers(new ProcessAddedToFolder(process));
				if (getProcessNode() != null) {
					getProcessNode().setChanged();
					getProcessNode().notifyObservers(new ProcessAddedToFolder(process));
				} else {
					getWorkflow().setChanged();
					getWorkflow().notifyObservers(new ProcessAddedToFolder(process));
				}
			}
		}
	}

	public void removeFromProcesses(FlexoProcessNode process) {
		if (processes.contains(process)) {
			processes.remove(process);
			process.removeParentFolder(this);
			if (!isDeserializing()) {
				setChanged();
				notifyObservers(new ProcessRemovedFromFolder(process));
				if (getProcessNode() != null) {
					getProcessNode().setChanged();
					getProcessNode().notifyObservers(new ProcessRemovedFromFolder(process));
				} else {
					getWorkflow().setChanged();
					getWorkflow().notifyObservers(new ProcessRemovedFromFolder(process));
				}
			}
		}
	}

	@Override
	public Vector<Validable> getAllEmbeddedValidableObjects() {
		return EmptyVector.EMPTY_VECTOR(Validable.class);
	}

	@Override
	public String getClassNameKey() {
		return "process_folder";
	}

	@Override
	public int getIndex() {
		if (isBeingCloned()) {
			return -1;
		}
		if (index == -1 && getCollection() != null) {
			index = getCollection().length;
			FlexoIndexManager.reIndexObjectOfArray(getCollection());
		}
		return index;
	}

	@Override
	public void setIndex(int index) {
		if (isDeserializing() || isCreatedByCloning()) {
			setIndexValue(index);
			return;
		}
		FlexoIndexManager.switchIndexForKey(this.index, index, this);
		if (getIndex() != index) {
			setChanged();
			AttributeDataModification dm = new AttributeDataModification("index", null, getIndex());
			dm.setReentrant(true);
			notifyObservers(dm);
		}
	}

	@Override
	public int getIndexValue() {
		return index;
	}

	@Override
	public void setIndexValue(int index) {
		if (this.index == index) {
			return;
		}
		int old = this.index;
		this.index = index;
		setChanged();
		notifyAttributeModification("index", old, index);
		if (!isDeserializing() && !isCreatedByCloning()) {
			getParent().setChanged();
			getParent().notifyObservers(new ChildrenOrderChanged());
		}
	}

	/**
	 * Overrides getCollection
	 * 
	 * @see org.openflexo.foundation.utils.Sortable#getCollection()
	 */
	@Override
	public ProcessFolder[] getCollection() {
		return getParent().getFolders().toArray(new ProcessFolder[0]);
	}

	public void setIndexForProcess(int index, FlexoProcessNode node) {
		if (processes.indexOf(node) > -1) {
			index = Math.min(processes.size() - 1, index);
			if (index < 0) {
				index = 0;
			}
			processes.remove(node);
			processes.insertElementAt(node, index);
			setChanged();
			notifyObservers(new ChildrenOrderChanged());
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Trying to set index for a process which is not in this folder");
			}
		}
	}

	@Override
	public FlexoProcessNode getProcessNode() {
		if (getParent() instanceof ProcessFolder) {
			return getParent().getProcessNode();
		}
		if (getParent() instanceof FlexoProcessNode) {
			return (FlexoProcessNode) getParent();
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("No parent for process folder " + getName());
			}
			return null;
		}
	}

	public Vector<FlexoProcessNode> getAllDirectSubProcessNodes() {
		Vector<FlexoProcessNode> nodes = new Vector<FlexoProcessNode>(processes);
		for (ProcessFolder folder : getFolders()) {
			nodes.addAll(folder.getAllDirectSubProcessNodes());
		}
		return nodes;
	}

	public boolean isAcceptableParentFolder(ProcessFolder parent) {
		if (parent == null || parent.getProcessNode() == null) {
			return false;
		}
		ProcessFolder current = parent;
		while (current != null) {
			if (current == this) {
				return false;
			}
			if (current.getParent() instanceof ProcessFolder) {
				current = (ProcessFolder) current.getParent();
			} else {
				break;
			}
		}
		if (getProcessNode() != parent.getProcessNode()) {
			for (FlexoProcessNode node : getAllDirectSubProcessNodes()) {
				if (node.getProcess().isAcceptableAsParentProcess(parent.getProcessNode().getProcess())) {
					return false;
				}
			}
		}
		return true;
	}

}
