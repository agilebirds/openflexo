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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.util.logging.Logger;

import javax.swing.tree.TreePath;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.view.BrowserView;

public class TreeDragSource implements DragSourceListener, DragGestureListener {

	private static final Logger logger = Logger.getLogger(TreeDragSource.class.getPackage().getName());

	DragSource source;

	DragGestureRecognizer recognizer;

	ElementMovable transferable;

	BrowserElement oldNode;

	BrowserView.FlexoJTree sourceTree;

	public TreeDragSource(BrowserView.FlexoJTree tree, int actions) {
		sourceTree = tree;
		source = new DragSource();
		recognizer = source.createDefaultDragGestureRecognizer(sourceTree, actions, this);
	}

	/*
	 * Drag Gesture Handler
	 */
	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {
		TreePath path = sourceTree.getSelectionPath();
		if (path == null || path.getPathCount() <= 1) {
			// We can't move the root node or an empty selection
			return;
		}
		BrowserElement elem = (BrowserElement) path.getLastPathComponent();
		oldNode = (BrowserElement) path.getLastPathComponent();
		transferable = new ElementMovable(elem);
		try {
			source.startDrag(dge, DragSource.DefaultMoveDrop, transferable, this);
			Rectangle bounds = sourceTree.getPathBounds(path);
			sourceTree.captureDraggedNode(path, new Point(dge.getDragOrigin().x - bounds.x, dge.getDragOrigin().y - bounds.y));
		} catch (InvalidDnDOperationException e) {
			// If Dnd already started
		}

		// If you support dropping the node anywhere, you should probably
		// start with a valid move cursor:
		// source.startDrag(dge, DragSource.DefaultMoveDrop, transferable,
		// this);
	}

	/*
	 * Drag Event Handlers
	 */
	@Override
	public void dragEnter(DragSourceDragEvent dsde) {
		int action = dsde.getDropAction();
		if (action == DnDConstants.ACTION_COPY) {
			dsde.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
		} else {
			if (action == DnDConstants.ACTION_MOVE) {
				dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
			} else {
				dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
			}
		}
	}

	@Override
	public void dragExit(DragSourceEvent dse) {
		dse.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
	}

	@Override
	public void dragOver(DragSourceDragEvent dsde) {
		int action = dsde.getDropAction();
		if (action == DnDConstants.ACTION_COPY) {
			dsde.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
		} else {
			if (action == DnDConstants.ACTION_MOVE) {
				dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
			} else {
				dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
			}
		}
	}

	@Override
	public void dropActionChanged(DragSourceDragEvent dsde) {
		int action = dsde.getDropAction();
		if (action == DnDConstants.ACTION_COPY) {
			dsde.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
		} else {
			if (action == DnDConstants.ACTION_MOVE) {
				dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
			} else {
				dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
			}
		}
	}

	@Override
	public void dragDropEnd(DragSourceDropEvent dsde) {
		sourceTree.stopExpandCountDown();
		/*
		 * to support move or copy, we have to check which occurred:
		 */
		if (dsde.getDropSuccess() && dsde.getDropAction() == DnDConstants.ACTION_MOVE) {
			// ((ProjectBrowser) sourceTree.getModel()).reload();
		} else {
			logger.fine("drop failed");
		}

		/*
		 * to support move only... if (dsde.getDropSuccess()) {
		 * ((DefaultTreeModel)sourceTree.getModel()).removeNodeFromParent(oldNode); }
		 */
	}
}
