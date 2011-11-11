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
package org.openflexo.ie.view.widget;

import java.awt.Cursor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.io.Serializable;
import java.util.logging.Logger;

import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.ie.view.IEPanel;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.ie.view.controller.dnd.WidgetMovable;

/**
 * @author bmangez
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public abstract class IEInnerDSWidgetView extends IEPanel {

	protected static final Logger logger = Logger.getLogger(IEInnerDSWidgetView.class.getPackage().getName());

	private TransparentMouseListener transparentMouseListener;

	public IEInnerDSWidgetView(IEController ieController, IEWidget model, boolean addDnDSupport) {
		super(ieController);
		if (addDnDSupport) {
			this.dgListener = new DGListener(model.getDraggedModel());
			this.dragSource = DragSource.getDefaultDragSource();
			this.dsListener = new DSListener(model.getDraggedModel());

			// component, action, listener
			this.dragSource.createDefaultDragGestureRecognizer(this, this.dragAction, this.dgListener);
		} else {
			this.addMouseListener(transparentMouseListener = new TransparentMouseListener(this, getParent()));
		}
	}

	protected void removeTransparentMouseListener() {
		if (transparentMouseListener != null) {
			removeMouseListener(transparentMouseListener);
			transparentMouseListener = null;
		}
	}

	public boolean isDragEnabled() {
		return true;
	}

	// ==========================================================================
	// ============================= Dnd Stuff
	// ==================================
	// ==========================================================================

	/**
	 * DGListener a listener that will start the drag. has access to top level's dsListener and dragSource
	 * 
	 * @see java.awt.dnd.DragGestureListener
	 * @see java.awt.dnd.DragSource
	 * @see java.awt.datatransfer.StringSelection
	 */
	class DGListener implements DragGestureListener, Serializable {
		private IEWidget _model;

		public DGListener(IEWidget model) {
			super();
			_model = model;
		}

		/**
		 * Start the drag if the operation is ok. uses java.awt.datatransfer.StringSelection to transfer the label's data
		 * 
		 * @param e
		 *            the event object
		 */
		@Override
		public void dragGestureRecognized(DragGestureEvent e) {
			// if the action is ok we go ahead
			// otherwise we punt
			if ((e.getDragAction() & IEInnerDSWidgetView.this.dragAction) == 0 || !isDragEnabled())
				return;
			// get the label's text and put it inside a Transferable
			// Transferable transferable = new StringSelection(
			// DragLabel.this.getText() );
			WidgetMovable movable = new WidgetMovable(_model);

			// now kick off the drag
			try {
				// initial cursor, transferrable, dsource listener
				e.startDrag(DragSource.DefaultCopyNoDrop, movable, IEInnerDSWidgetView.this.dsListener);
			} catch (Exception idoe) {

			}
		}
	}

	/**
	 * DSListener a listener that will track the state of the DnD operation
	 * 
	 * @see java.awt.dnd.DragSourceListener
	 * @see java.awt.dnd.DragSource
	 * @see java.awt.datatransfer.StringSelection
	 */
	public class DSListener implements DragSourceListener, Serializable {

		public DSListener(IEWidget model) {
			super();
			_model = model;

		}

		private IEWidget _model;

		/**
		 * @param e
		 *            the event
		 */
		@Override
		public void dragDropEnd(DragSourceDropEvent e) {
			boolean isDropSuccessfull = IEController.isDropSuccessFull;
			if (!isDropSuccessfull) {
				return;
			}
			IEController.isDropSuccessFull = false;
			/*
			 * the dropAction should be what the drop target specified in
			 * acceptDrop
			 */
			// this is the action selected by the drop target
			if (e.getDropAction() == DnDConstants.ACTION_MOVE)
				IEInnerDSWidgetView.this.setName("");
		}

		/**
		 * @param e
		 *            the event
		 */
		@Override
		public void dragEnter(DragSourceDragEvent e) {
			DragSourceContext context = e.getDragSourceContext();
			// intersection of the users selected action, and the source and
			// target actions
			int myaction = e.getDropAction();
			if ((myaction & IEInnerDSWidgetView.this.dragAction) != 0) {
				context.setCursor(DragSource.DefaultCopyDrop);
			} else {
				context.setCursor(DragSource.DefaultCopyNoDrop);
			}
		}

		/**
		 * @param e
		 *            the event
		 */
		@Override
		public void dragOver(DragSourceDragEvent e) {
			if (getIEController().currentDropTargetAsChanged()) {
				Cursor newCursor = getIEController().getCurrentDragCursor(_model);
				// System.out.println("set cursor to :"+newCursor);
				e.getDragSourceContext().setCursor(newCursor);
				// printEventDetails("IEDSWidgetView.dragOver",e);
			}
		}

		/**
		 * @param e
		 *            the event
		 */
		@Override
		public void dragExit(DragSourceEvent e) {
			// interface
		}

		/**
		 * for example, press shift during drag to change to a link action
		 * 
		 * @param e
		 *            the event
		 */
		@Override
		public void dropActionChanged(DragSourceDragEvent e) {
			DragSourceContext context = e.getDragSourceContext();
			context.setCursor(DragSource.DefaultCopyNoDrop);
		}
	}

	protected DragSource dragSource;

	protected transient DragGestureListener dgListener;

	protected transient DragSourceListener dsListener;

	int dragAction = DnDConstants.ACTION_COPY;
}
