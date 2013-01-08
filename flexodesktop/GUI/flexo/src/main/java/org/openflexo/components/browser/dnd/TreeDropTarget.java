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
package org.openflexo.components.browser.dnd;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.toolbox.ToolBox;

/**
 * @author bmangez <B>Class Description</B>
 */
public class TreeDropTarget implements DropTargetListener {
	private static Cursor VALID_CURSOR;

	private static Cursor INVALID_CURSOR;

	static {
		try {
			VALID_CURSOR = Cursor.getSystemCustomCursor("MoveDrop32x32");
			INVALID_CURSOR = Cursor.getSystemCustomCursor("Invalid32x32");
		} catch (Exception e) {
			e.printStackTrace();
			VALID_CURSOR = Cursor.getDefaultCursor();
			INVALID_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
		}
	}

	protected DropTarget target;

	protected BrowserView.FlexoJTree targetTree;

	protected ProjectBrowser _browser;

	public TreeDropTarget(BrowserView.FlexoJTree tree, ProjectBrowser browser) {
		targetTree = tree;
		target = new DropTarget(targetTree, this);
		_browser = browser;
	}

	protected FlexoEditor getEditor() {
		return targetTree.getBrowserView().getEditor();
	}

	/*
	 * Drop Event Handlers
	 */
	private BrowserElement getNodeForEvent(DropTargetDragEvent dtde) {
		Point p = dtde.getLocation();
		DropTargetContext dtc = dtde.getDropTargetContext();
		JTree tree = (JTree) dtc.getComponent();
		TreePath path = tree.getClosestPathForLocation(p.x, p.y);
		if (path == null) {
			return null;
		} else {
			return (BrowserElement) path.getLastPathComponent();
		}
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		dtde.acceptDrag(DnDConstants.ACTION_MOVE);
		BrowserElement node = getNodeForEvent(dtde);
		TreePath path = targetTree.getClosestPathForLocation(dtde.getLocation().x, dtde.getLocation().y);
		if (!targetTree.getModel().isLeaf(path.getLastPathComponent()) && !targetTree.isExpanded(path)) {
			targetTree.handleAutoExpand(path);
		}
		targetTree.paintDraggedNode(dtde.getLocation());
		/*
		if (node==null || !sourceIsDroppableInTarget(getSourceNode(dtde.getTransferable()), node)) {
		    dtde.rejectDrag();
		} else {
		    targetTree.setSelectionPath(path);
		    // start by supporting move operations
		    dtde.acceptDrag(DnDConstants.ACTION_MOVE);
		}*/
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		BrowserElement node = getNodeForEvent(dtde);
		TreePath path = targetTree.getClosestPathForLocation(dtde.getLocation().x, dtde.getLocation().y);
		TreePath realPath = targetTree.getPathForLocation(dtde.getLocation().x, dtde.getLocation().y);
		if (realPath != null && !targetTree.getModel().isLeaf(realPath.getLastPathComponent()) && !targetTree.isExpanded(path)) {
			targetTree.handleAutoExpand(realPath);
		} else {
			targetTree.stopExpandCountDown();
		}
		targetTree.paintDraggedNode(dtde.getLocation());
		targetTree.setSelectionPath(realPath);
		BrowserElement source = getSourceNode(dtde.getTransferable());
		if (source == node) {
			return;
		}
		if (node == null || !targetAcceptsSource(node, source)) {
			dtde.rejectDrag();
			if (ToolBox.getPLATFORM() == ToolBox.MACOS && _browser != null && _browser.getSelectionManager() != null
					&& _browser.getSelectionManager().getController() != null
					&& _browser.getSelectionManager().getController().getFlexoFrame() != null) {
				_browser.getSelectionManager().getController().getFlexoFrame().setCursor(INVALID_CURSOR);
			}
		} else {
			targetTree.setSelectionPath(path);
			// start by supporting move operations
			dtde.acceptDrag(DnDConstants.ACTION_MOVE);
			if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
				_browser.getSelectionManager().getController().getFlexoFrame().setCursor(VALID_CURSOR);
			}
		}
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
		targetTree.stopExpandCountDown();
		targetTree.clearDraggedNode();
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {

	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
		targetTree.stopExpandCountDown();
		targetTree.clearDraggedNode();
		Point pt = dtde.getLocation();
		DropTargetContext dtc = dtde.getDropTargetContext();
		JTree tree = (JTree) dtc.getComponent();
		TreePath parentpath = tree.getClosestPathForLocation(pt.x, pt.y);
		if (parentpath == null) {
			return;
		}
		BrowserElement destination = (BrowserElement) parentpath.getLastPathComponent();
		try {
			Transferable transferable = dtde.getTransferable();
			DataFlavor[] dataFlavors = transferable.getTransferDataFlavors();
			for (int i = 0; i < dataFlavors.length; i++) {
				if (transferable.isDataFlavorSupported(dataFlavors[i])) {
					BrowserElement moved = null;
					try {
						moved = (BrowserElement) transferable.getTransferData(dataFlavors[i]);
					} catch (ClassCastException e) {
						dtde.rejectDrop();
						return;
					}
					if (targetAcceptsSource(destination, moved) && handleDrop(moved, destination)) {
						dtde.acceptDrop(dtde.getDropAction());
						dtde.dropComplete(true);
						return;
					}
				}
			}
			dtde.rejectDrop();
		} catch (Exception e) {
			e.printStackTrace();
			dtde.rejectDrop();
		}
	}

	private BrowserElement getSourceNode(Transferable transferable) {
		if (transferable instanceof ElementMovable) {
			return ((ElementMovable) transferable).path;
		}
		DataFlavor[] dataFlavors = transferable.getTransferDataFlavors();
		for (int i = 0; i < dataFlavors.length; i++) {
			if (transferable.isDataFlavorSupported(dataFlavors[i])) {
				BrowserElement moved = null;
				try {
					moved = (BrowserElement) transferable.getTransferData(dataFlavors[i]);
					return moved;
				} catch (ClassCastException e) {
				} catch (UnsupportedFlavorException e) {
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	public boolean handleDrop(BrowserElement source, BrowserElement target) {
		return false;
	}

	public boolean targetAcceptsSource(BrowserElement target, BrowserElement source) {
		return false;
	}

}
