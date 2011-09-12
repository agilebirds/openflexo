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
package org.openflexo.wkf.controller;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.dnd.TreeDropTarget;
import org.openflexo.components.browser.view.BrowserView.FlexoJTree;
import org.openflexo.components.browser.wkf.FlexoProcessNodeElement;
import org.openflexo.components.browser.wkf.ProcessElement;
import org.openflexo.components.browser.wkf.ProcessFolderElement;
import org.openflexo.components.browser.wkf.WorkflowElement;
import org.openflexo.foundation.wkf.FlexoFolderContainerNode;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoProcessNode;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.ProcessFolder;
import org.openflexo.foundation.wkf.action.AddToProcessFolder;
import org.openflexo.foundation.wkf.action.MoveFlexoProcess;
import org.openflexo.foundation.wkf.action.MoveProcessFolder;
import org.openflexo.foundation.wkf.action.RemoveFromProcessFolder;


/**
 *
 * @author gpolet
 *
 */
public class WKFTreeDropTarget extends TreeDropTarget {

	public WKFTreeDropTarget(FlexoJTree tree, ProjectBrowser browser) {
		super(tree, browser);
	}

	@Override
	public boolean targetAcceptsSource(BrowserElement target,
			BrowserElement source) {
		FlexoProcess srcProcess = null;
		FlexoProcessNode srcNode = null;
		ProcessFolder srcFolder = null;
		FlexoProcess targetProcess = null;
		FlexoProcessNode targetNode = null;
		FlexoWorkflow targetWKF = null;
		ProcessFolder targetFolder = null;
		if (source instanceof ProcessElement) {
			srcProcess = ((ProcessElement)source).getFlexoProcess();
			srcNode = srcProcess.getProcessNode();
		} else if (source instanceof FlexoProcessNodeElement) {
			srcNode = ((FlexoProcessNodeElement)source).getProcessNode();
			srcProcess = srcNode.getProcess();
		} else if (source instanceof ProcessFolderElement) {
			srcFolder = ((ProcessFolderElement)source).getFolder();
		}
		if (target instanceof ProcessElement) {
			targetProcess = ((ProcessElement)target).getFlexoProcess();
			targetNode = targetProcess.getProcessNode();
		} else if (target instanceof FlexoProcessNodeElement) {
			targetNode = ((FlexoProcessNodeElement)target).getProcessNode();
			targetProcess = targetNode.getProcess();
		} else if (target instanceof WorkflowElement) {
			targetWKF = ((WorkflowElement)target).getFlexoWorkflow();
		} else if (target instanceof ProcessFolderElement) {
			targetFolder = ((ProcessFolderElement)target).getFolder();
		}

		if (srcProcess!=null) {
			if (targetProcess!=null) {
				if (srcNode.getParentFolder()!=null && srcNode.getParentFolder().getProcessNode()==targetNode) // Remove process from folder
					return true;
				if (srcProcess.isAcceptableAsParentProcess(targetProcess)) // Move process to another process
					return true;
			} else if (targetFolder!=null) {
				if (targetFolder.getProcessNode()==srcNode.getFatherProcessNode()) // Add process to folder;
					return true;
			} else if (targetWKF!=null) {
				if (srcProcess.isAcceptableAsParentProcess(null))
					return true;
			}
		} else if (srcFolder!=null) {
			FlexoFolderContainerNode targetWKFNode = null;
			if (targetNode!=null) {
				targetWKFNode = targetNode;
			} else if (targetFolder!=null) {
				if (srcFolder.isAcceptableParentFolder(targetFolder))
					targetWKFNode = targetFolder;
			}
			if (targetWKFNode!=null) { // Move folder
				FlexoProcess targetParentProcess = targetWKFNode.getProcessNode().getProcess();
				if (canMoveFolder(srcFolder, targetParentProcess)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean handleDrop(BrowserElement source, BrowserElement target) {
		FlexoProcess srcProcess = null;
		FlexoProcessNode srcNode = null;
		ProcessFolder srcFolder = null;
		FlexoProcess targetProcess = null;
		FlexoProcessNode targetNode = null;
		FlexoWorkflow targetWKF = null;
		ProcessFolder targetFolder = null;
		if (source instanceof ProcessElement) {
			srcProcess = ((ProcessElement)source).getFlexoProcess();
			srcNode = srcProcess.getProcessNode();
		} else if (source instanceof FlexoProcessNodeElement) {
			srcNode = ((FlexoProcessNodeElement)source).getProcessNode();
			srcProcess = srcNode.getProcess();
		} else if (source instanceof ProcessFolderElement) {
			srcFolder = ((ProcessFolderElement)source).getFolder();
		}
		if (target instanceof ProcessElement) {
			targetProcess = ((ProcessElement)target).getFlexoProcess();
			targetNode = targetProcess.getProcessNode();
		} else if (target instanceof FlexoProcessNodeElement) {
			targetNode = ((FlexoProcessNodeElement)target).getProcessNode();
			targetProcess = targetNode.getProcess();
		} else if (target instanceof WorkflowElement) {
			targetWKF = ((WorkflowElement)target).getFlexoWorkflow();
		} else if (target instanceof ProcessFolderElement) {
			targetFolder = ((ProcessFolderElement)target).getFolder();
		}
		if (srcProcess!=null) {
			if (targetProcess!=null) {
				if (srcNode.getParentFolder()!=null && srcNode.getParentFolder().getProcessNode()==targetNode) { // Remove process from folder
					RemoveFromProcessFolder remove = RemoveFromProcessFolder.actionType.makeNewAction(srcNode, null, _browser.getEditor());
					return remove.doAction().hasActionExecutionSucceeded();
				}
				if (srcProcess.isAcceptableAsParentProcess(targetProcess)) {
					MoveFlexoProcess moveProcessAction = MoveFlexoProcess.actionType.makeNewAction(srcProcess, null, _browser.getEditor());
		            moveProcessAction.setNewParentProcess(targetProcess);
		            moveProcessAction.setDoImmediately(true);
		            return moveProcessAction.doAction().hasActionExecutionSucceeded();
				}
			} else if (targetFolder!=null) {
				if (targetFolder.getProcessNode()==srcNode.getFatherProcessNode()) { // Add process to folder;
					AddToProcessFolder add = AddToProcessFolder.actionType.makeNewAction(srcNode, null, _browser.getEditor());
					add.setDestination(targetFolder);
					return add.doAction().hasActionExecutionSucceeded();
				}
			} else if (targetWKF!=null) {
				if (srcProcess.isAcceptableAsParentProcess(null)) {
					MoveFlexoProcess moveProcessAction = MoveFlexoProcess.actionType.makeNewAction(srcProcess, null, _browser.getEditor());
		            moveProcessAction.setNewParentProcess(null);
		            moveProcessAction.setDoImmediately(true);
		            return moveProcessAction.doAction().hasActionExecutionSucceeded();
				}
			}
		} else if (srcFolder!=null) {
			FlexoFolderContainerNode targetWKFNode = null;
			if (targetNode!=null) {
				targetWKFNode = targetNode;
			} else if (targetFolder!=null) {
				if (srcFolder.isAcceptableParentFolder(targetFolder))
					targetWKFNode = targetFolder;
			}
			if (targetWKFNode!=null) { // Move folder
				FlexoProcess targetParentProcess = targetWKFNode.getProcessNode().getProcess();
				if (canMoveFolder(srcFolder, targetParentProcess)) {
					MoveProcessFolder move = MoveProcessFolder.actionType.makeNewAction(srcFolder, null, _browser.getEditor());
					move.setDestination(targetWKFNode);
					return move.doAction().hasActionExecutionSucceeded();
				}
			}
		}
		return false;
	}

	private boolean canMoveFolder(ProcessFolder srcFolder, FlexoProcess targetProcess) {
		if (targetProcess==null || targetProcess.isImported())
			return false;
		boolean ok = true;
		for (FlexoProcessNode node:srcFolder.getAllDirectSubProcessNodes()) {
			ok&=node.getProcess().isAcceptableAsParentProcess(targetProcess);
		}
		return ok;
	}
}
