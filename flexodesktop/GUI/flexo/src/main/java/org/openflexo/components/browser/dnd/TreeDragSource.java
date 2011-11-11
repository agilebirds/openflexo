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

/**
 * @author bmangez
 * @version $Id: TreeDragSource.java,v 1.3 2011/09/12 11:46:52 gpolet Exp $ $Log: TreeDragSource.java,v $ Revision 1.3 2011/09/12 11:46:52
 *          gpolet Converted v2 to v3
 * 
 *          Revision 1.2 2011/06/21 14:46:32 gpolet MEDIUM: Added all missing @Override
 * 
 *          Revision 1.1 2011/05/24 01:00:46 gpolet LOW: First import of OpenFlexo
 * 
 *          Revision 1.1.2.2 2011/05/20 14:32:32 gpolet LOW: Added GPL v2 file header
 * 
 *          Revision 1.1.2.1 2011/05/20 08:39:18 gpolet low: renamed package
 * 
 *          Revision 1.1.2.1 2011/05/19 09:39:47 gpolet refactored package names
 * 
 *          Revision 1.7 2008/03/14 09:19:50 gpolet LOW: Properly Handles DnD in JTree for MAC OS X
 * 
 *          Revision 1.6 2007/09/17 15:14:24 gpolet IMPORTANT: First merge of branch b_1_1_0 from Root_b_1_1_0 until t_first_merge (after
 *          t_1_1_0RC10)
 * 
 *          Revision 1.5.2.2 2007/06/06 09:19:06 gpolet MEDIUM: Improved Dnd. Bug 1002479 - 1001944 - 1000565 Dnd should no more close the
 *          tree. Under windows, the Dnd feedback has been added. Dragged node is now painted over the potential targets. Selection of drop
 *          targets is also performed.Finally, auto-expand after 1.5s has been added too.
 * 
 *          Revision 1.5.2.1 2007/05/31 10:49:00 bmangez LOW/organize import
 * 
 *          Revision 1.5 2007/04/06 07:49:41 bmangez LOW/organize imports
 * 
 *          Revision 1.4 2007/01/15 12:39:07 gpolet IMPORTANT: Merge from branch from tag t_1_0_1RC7 to t_1_0_1RC7_8 New widgets, improved
 *          behaviors, etc...
 * 
 *          Revision 1.3.2.1 2007/01/11 14:26:14 gpolet IMPORTANT: Fixed editing troubles in TabularViews
 * 
 *          Revision 1.3 2006/07/28 10:45:32 gpolet LOW: Organize imports
 * 
 *          Revision 1.2 2006/02/02 15:07:09 bmangez merge from bdev
 * 
 *          Revision 1.1.2.6 2006/01/24 16:21:20 gpolet LOW: Organize Imports
 * 
 *          Revision 1.1.2.5 2006/01/24 15:10:59 sguerin New architecture of process hierarchy - Processes can now be context-free (no
 *          parent) - Support for a unique root process - SubProcessNodes could now embed a larger scope of processes - Modification of new
 *          FlexoProcess popup - Modification of new SubProcessNode popup - Implementation of process moving - Validation of consistency for
 *          process moving refactoring - Implementation of some new validation rules - Many minor modifications in Foundation/WKF/IE - Many
 *          ergonomy issues fixed - Many bug fixes
 * 
 *          Revision 1.1.2.4 2005/10/10 08:16:33 benoit *** empty log message ***
 * 
 *          Revision 1.1.2.3 2005/10/03 11:47:16 benoit organize import format code logger test Revision 1.1.2.2 2005/08/19 16:52:29 sguerin
 *          Commit on 19/08/2005, Sylvain GUERIN, version 7.1.10.alpha See committing documentation
 * 
 *          Revision 1.1.2.1 2005/06/28 12:53:59 benoit ReusableComponents
 * 
 * 
 *          <B>Class Description</B>
 */
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
		if ((path == null) || (path.getPathCount() <= 1)) {
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
		if (dsde.getDropSuccess() && (dsde.getDropAction() == DnDConstants.ACTION_MOVE)) {
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
