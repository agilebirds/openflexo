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

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.AttributeDataModification;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectReference;
import org.openflexo.foundation.rm.ProjectData;
import org.openflexo.foundation.utils.FlexoIndexManager;
import org.openflexo.foundation.utils.Sortable;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.wkf.dm.ChildrenOrderChanged;
import org.openflexo.foundation.wkf.dm.ProcessNodeInserted;
import org.openflexo.foundation.wkf.dm.ProcessNodeRemoved;
import org.openflexo.foundation.xml.FlexoWorkflowBuilder;
import org.openflexo.toolbox.ToolBox;

/**
 * A FlexoProcessNode is an high-level representation of a FlexoProcess in the global workflow. Those FlexoProcessNode are embedded.
 * 
 * @author benoit,sylvain
 */
public class FlexoProcessNode extends FlexoFolderContainerNode implements Sortable {
	private int index = -1;

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FlexoProcessNode.class.getPackage().getName());
	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	/**
	 * Father of this FlexoProcessNode
	 */
	protected FlexoProcessNode _father;

	private FlexoProcess process;

	/**
	 * Childs of this FlexoProcessNode as Vector of FlexoProcessNode
	 */
	public Vector<FlexoProcessNode> _childs;

	protected String name = null;

	private String processResourceName;

	public FlexoProcessNode(FlexoWorkflowBuilder builder) {
		this(builder.workflow);
		initializeDeserialization(builder);
	}

	public FlexoProcessNode(FlexoWorkflow workflow) {
		super(workflow.getProject(), workflow);
		_childs = new Vector<FlexoProcessNode>();
	}

	public FlexoProcessNode(String aName, FlexoProcess aProcess, FlexoWorkflow workflow) {
		this(workflow);
		name = aName;
		if (aProcess != null) {
			setProcess(aProcess);
		}
	}

	@Override
	public String getFullyQualifiedName() {
		return getProcess().getFullyQualifiedName() + ".PROCESS_NODE";
	}

	public String getProcessResourceName() {
		if (getProcess() != null) {
			return getProcess().getFlexoResource().getName();
		}
		if (processResourceName == null) {
			return getName();// Kept for backward compatibility
		}
		return processResourceName;
	}

	public void setProcessResourceName(String processResourceName) {
		this.processResourceName = processResourceName;
	}

	@Override
	public boolean isImported() {
		if (getFatherProcessNode() != null) {
			return getFatherProcessNode().isImported();
		} else {
			return getWorkflow().getImportedRootNodeProcesses().contains(this);
		}
	}

	@Override
	public FlexoProcessNode getProcessNode() {
		return this;
	}

	public FlexoProcessNode getFatherProcessNode() {
		return _father;
	}

	public void setFatherProcessNode(FlexoProcessNode fatherProcessNode) {
		if (_father == fatherProcessNode) {
			return;
		}
		boolean isImported = isImported();
		// 1. remove from current father
		if (_father != null) {
			_father.removeFromSubProcesses(this);
		} else {
			if (isImported) {
				getWorkflow().removeFromImportedRootNodeProcesses(this);
			} else {
				getWorkflow().removeFromTopLevelNodeProcesses(this);
			}
		}
		// 2. We set the father
		_father = fatherProcessNode;

	}

	public Vector<FlexoProcessNode> getSubProcesses() {
		return _childs;
	}

	public void setSubProcesses(Vector<FlexoProcessNode> aVector) {
		_childs = aVector;
	}

	public void addToSubProcesses(FlexoProcessNode aProcessNode) {
		if (!_childs.contains(aProcessNode)) {
			_childs.add(aProcessNode);
			clearOrphanProcesses();
			aProcessNode.setFatherProcessNode(this);
			if (getProcess() != null) {
				getProcess().rebuildSubProcesses();
			}
			if (!isDeserializing()) {
				setChanged();
				notifyObservers(new ProcessNodeInserted(aProcessNode));
				aProcessNode.setIndexValue(aProcessNode.getCollection().length);
				FlexoIndexManager.reIndexObjectOfArray(aProcessNode.getCollection());
			}
		}
	}

	// Ugly little hack to prevent a node from being removed from is folder because it is currently moving;
	private boolean isMoving = false;

	public void startMoving() {
		isMoving = true;
	}

	public void stopMoving() {
		isMoving = false;
	}

	public void removeFromSubProcesses(FlexoProcessNode aProcessNode) {
		if (_childs.contains(aProcessNode)) {
			ProcessFolder folder = aProcessNode.getParentFolder();
			if (folder != null && !aProcessNode.isMoving) {
				folder.removeFromProcesses(aProcessNode);
			}
			_childs.remove(aProcessNode);
			clearOrphanProcesses();
			aProcessNode.setFatherProcessNode(null);
			if (getProcess() != null) {
				getProcess().rebuildSubProcesses();
			}
			FlexoProcessNode[] coll;
			if (folder != null) {
				coll = folder.getProcesses().toArray(new FlexoProcessNode[0]);
			} else {
				coll = getOrphanProcesses().toArray(new FlexoProcessNode[0]);
			}
			FlexoIndexManager.reIndexObjectOfArray(coll);
			setChanged();
			notifyObservers(new ProcessNodeRemoved(aProcessNode));
		}
	}

	private Vector<FlexoProcessNode> orphanProcesses;

	public Vector<FlexoProcessNode> getOrphanProcesses() {
		if (orphanProcesses == null) {
			orphanProcesses = new Vector<FlexoProcessNode>();
			for (Enumeration<FlexoProcessNode> en = getSortedSubprocesses(); en.hasMoreElements();) {
				FlexoProcessNode node = en.nextElement();
				if (node.getParentFolder() == null) {
					node.setIndex(orphanProcesses.size() + 1);
					orphanProcesses.add(node);
				}
			}
		}
		return orphanProcesses;
	}

	public void clearOrphanProcesses() {
		orphanProcesses = null;
		getPropertyChangeSupport().firePropertyChange("sortedOrphanSubprocesses", this.orphanProcesses, null);
	}

	@Override
	public void delete() {
		boolean isImported = isImported();
		if (getParentFolder() != null) {
			getParentFolder().removeFromProcesses(this);
		}
		if (getFatherProcessNode() != null) {
			getFatherProcessNode().removeFromSubProcesses(this);
			_father = null;
			setProcess(null);
		} else if (getWorkflow() != null) {
			if (isImported) {
				getWorkflow().removeFromImportedRootNodeProcesses(this);
			} else {
				getWorkflow().removeFromTopLevelNodeProcesses(this);
			}
		}
		super.delete();
	}

	// Not serialized
	private Vector<ProcessFolder> parentFolders = new Vector<ProcessFolder>();

	public Vector<ProcessFolder> getParentFolders() {
		return parentFolders;
	}

	public void addParentFolder(ProcessFolder parent) {
		if (!parentFolders.contains(parent)) {
			parentFolders.add(parent);
			if (getFatherProcessNode() != null) {
				getFatherProcessNode().clearOrphanProcesses();
			} else {
				getWorkflow().clearOrphanProcesses();
			}
		}
	}

	public void removeParentFolder(ProcessFolder parent) {
		if (parentFolders.contains(parent)) {
			parentFolders.remove(parent);
			if (getFatherProcessNode() != null) {
				getFatherProcessNode().clearOrphanProcesses();
				getFatherProcessNode().setChanged();
				getFatherProcessNode().notifyObservers(new ChildrenOrderChanged());
			} else {
				getWorkflow().clearOrphanProcesses();
				getWorkflow().setChanged();
				getWorkflow().notifyObservers(new ChildrenOrderChanged());
			}
		}
	}

	public ProcessFolder getParentFolder() {
		if (parentFolders.size() > 0) {
			return parentFolders.firstElement();
		} else {
			return null;
		}
	}

	@Override
	public String getName() {
		if (getProcess() != null) {
			name = getProcess().getName();
		}
		return name;
	}

	/**
	 * @deprecated - redundant information, only FlexoProcess.getName() and FlexoProcess.setName() are relevant now.
	 */
	@Override
	@Deprecated
	public void setName(String aName) {
		name = aName;
		setChanged();
	}

	public File getFile() {
		if (getProcess() != null && getProcess().getFlexoResource() != null) {
			return getProcess().getFlexoResource().getFile();
		}
		return null;
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "flexo_process_node";
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
			if (getFatherProcessNode() == null) {
				if (getWorkflow() != null) {
					getWorkflow().setChanged();
					getWorkflow().notifyObservers(new ChildrenOrderChanged());
				}
			} else {
				getFatherProcessNode().setChanged();
				getFatherProcessNode().notifyObservers(new ChildrenOrderChanged());
			}
		}
	}

	/**
	 * Overrides getCollection
	 * 
	 * @see org.openflexo.foundation.utils.Sortable#getCollection()
	 */
	@Override
	public FlexoProcessNode[] getCollection() {
		if (getFatherProcessNode() != null) {
			return getFatherProcessNode().getSubProcesses().toArray(new FlexoProcessNode[0]);
		}
		return getWorkflow()._getTopLevelNodeProcesses().toArray(new FlexoProcessNode[0]);
	}

	public FlexoProcess getProcess() {
		return getProcess(false);
	}

	public FlexoProcess getProcess(boolean force) {
		if (process != null || !force) {
			return process;
		} else {
			if (getWorkflow().isCache()) {
				ProjectData projectData = getProject().getProjectData();
				if (projectData != null) {
					FlexoProjectReference ref = projectData.getProjectReferenceWithURI(getWorkflow().getProjectURI());
					if (ref != null) {
						FlexoProject referredProject = ref.getReferredProject(true);
						if (referredProject != null) {
							return referredProject.getWorkflow().getLocalFlexoProcessWithName(getName());
						}
					}
				}
			}
		}
		return null;
	}

	public void setProcess(FlexoProcess process) {
		if (this.process == null) {
			this.process = process;
			if (process != null) {
				process.setProcessNode(this);
			}
		}
	}

	@Override
	public Vector<Validable> getAllEmbeddedValidableObjects() {
		Vector<Validable> v = new Vector<Validable>();
		v.add(this);
		return v;
	}

	public Enumeration<FlexoProcessNode> getSortedSubprocesses() {
		disableObserving();
		FlexoProcessNode[] o = FlexoIndexManager.sortArray(getSubProcesses().toArray(new FlexoProcessNode[0]));
		enableObserving();
		return ToolBox.getEnumeration(o);
	}

	public Enumeration<FlexoProcessNode> getSortedOrphanSubprocesses() {
		disableObserving();
		FlexoProcessNode[] o = FlexoIndexManager.sortArray(getOrphanProcesses().toArray(new FlexoProcessNode[0]));
		enableObserving();
		return ToolBox.getEnumeration(o);
	}

	public int getVectorIndex() {
		if (getParentFolder() != null) {
			return getParentFolder().getProcesses().indexOf(this) + 1;
		} else {
			if (getFatherProcessNode() != null) {
				return getFatherProcessNode().getOrphanProcesses().indexOf(this) + 1;
			} else if (isImported()) {
				return getWorkflow().getImportedRootNodeProcesses().indexOf(this) + 1;
			} else {
				return getWorkflow().getTopLevelFlexoProcesses().indexOf(this) + 1;
			}
		}
	}

	public void setVectorIndex(int index) {
		index--;
		if (getParentFolder() != null) {
			getParentFolder().setIndexForProcess(index, this);
			setChanged();
			notifyAttributeModification("vectorIndex", -1, index + 1);
		}/* else {
			if (getFatherProcessNode()!=null)
				return getFatherProcessNode().getOrphanProcesses().indexOf(this);
			else if (isImported())
				return getWorkflow().getImportedRootNodeProcesses().indexOf(this);
			else
				return getWorkflow().getTopLevelFlexoProcesses().indexOf(this);
			}*/
	}

}
