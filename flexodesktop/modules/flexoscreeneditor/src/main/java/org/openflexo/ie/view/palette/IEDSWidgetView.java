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
package org.openflexo.ie.view.palette;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.openflexo.ie.view.IEPanel;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.ie.view.controller.dnd.WidgetTransferable;
import org.openflexo.localization.FlexoLocalization;

/**
 * @author bmangez
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public class IEDSWidgetView extends IEPanel {

	public IEDSWidgetView(IEController ieController, IEDSWidget model, boolean addDeleteSupport) {
		super(ieController);
		setLayout(new BorderLayout());
		setToolTipText(model.getName());
		_model = model;
		_model.refresh(ieController.getProject().getCssSheet());
		add(_model.getLabel(), BorderLayout.CENTER);
		if (model.getLabel().getIcon() == null) {
			setBorder(BorderFactory.createLineBorder(Color.BLUE));
			setPreferredSize(new Dimension(90, 35));
		}

		this.dgListener = new DGListener();
		this.dragSource = DragSource.getDefaultDragSource();
		this.dsListener = new DSListener();
		// component, action, listener
		this.dragSource.createDefaultDragGestureRecognizer(this, this.dragAction, this.dgListener);
		if (addDeleteSupport) {
			addMouseListener(new MouseListener() {

				private Component invoker;

				private boolean isPopupTrigger = false;

				@Override
				public void mouseClicked(MouseEvent e) {
				}

				@Override
				public void mouseEntered(MouseEvent e) {
				}

				@Override
				public void mouseExited(MouseEvent e) {
				}

				@Override
				public void mousePressed(MouseEvent e) {
					invoker = e.getComponent();
					isPopupTrigger = e.isPopupTrigger();
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					isPopupTrigger |= e.isPopupTrigger();
					if (isPopupTrigger && invoker == e.getComponent() && invoker instanceof IEDSWidgetView) {
						JPopupMenu menu = makePopupMenu((IEDSWidgetView) invoker);
						menu.show(invoker, e.getPoint().x, e.getPoint().y);
					} else {
						invoker = null;
						isPopupTrigger = false;
					}
				}

				public JPopupMenu makePopupMenu(IEDSWidgetView view) {
					JPopupMenu menu = new JPopupMenu();
					JMenuItem menuItem = menu.add(new DeleteAction(view));
					menuItem.setText(FlexoLocalization.localizedForKey("delete_this_widget"));
					return menu;
				}

			});
		}

	}

	private class DeleteAction extends AbstractAction {

		IEDSWidgetView view;

		/**
         * 
         */
		public DeleteAction(IEDSWidgetView view) {
			this.view = view;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			view.removeWidget();
		}
	}

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	IEDSWidget _model;

	// ==========================================================================
	// ============================= Dnd Stuff
	// ==================================
	// ==========================================================================

	public void removeWidget() {
		_model.delete(getIEController().getProject());
		if (getParent() != null) {
			Container parent = getParent();
			parent.remove(this);
			parent.validate();
			parent.repaint();
		}
	}

	/**
	 * DGListener a listener that will start the drag. has access to top level's dsListener and dragSource
	 * 
	 * @see java.awt.dnd.DragGestureListener
	 * @see java.awt.dnd.DragSource
	 * @see java.awt.datatransfer.StringSelection
	 */
	class DGListener implements DragGestureListener {
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
			if ((e.getDragAction() & IEDSWidgetView.this.dragAction) == 0) {
				return;
			}
			// get the label's text and put it inside a Transferable
			// Transferable transferable = new StringSelection(
			// DragLabel.this.getText() );
			WidgetTransferable transferable = new WidgetTransferable(_model);

			// now kick off the drag
			try {
				e.startDrag(DragSource.DefaultCopyNoDrop, transferable, IEDSWidgetView.this.dsListener);
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
	public class DSListener implements DragSourceListener {

		/**
		 * @param e
		 *            the event
		 */
		@Override
		public void dragDropEnd(DragSourceDropEvent e) {
			if (e.getDropSuccess() == false) {
				return;
			}

			/*
			 * the dropAction should be what the drop target specified in
			 * acceptDrop
			 */

			// this is the action selected by the drop target
			if (e.getDropAction() == DnDConstants.ACTION_MOVE) {
				IEDSWidgetView.this.setName("");
			}
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
			// printEventDetails("IEDSWidgetView.dragEnter",e);
			if ((myaction & IEDSWidgetView.this.dragAction) != 0) {
				context.setCursor(DragSource.DefaultCopyDrop);
			} else {
				context.setCursor(DragSource.DefaultCopyNoDrop);
			}
		}

		private void printEventDetails(String prefix, DragSourceDragEvent e) {
			System.out.println(prefix + " - e.getDropAction() = " + printAction(e.getDropAction()));
			System.out.println(prefix + " - e.getUserAction() = " + printAction(e.getUserAction()));
			System.out.println(prefix + " - e.getTargetActions() = " + printAction(e.getTargetActions()));
		}

		private String printAction(int i) {
			if (i == DnDConstants.ACTION_COPY) {
				return "ACTION_COPY (" + i + ")";
			}
			if (i == DnDConstants.ACTION_COPY_OR_MOVE) {
				return "ACTION_COPY_OR_MOVE (" + i + ")";
			}
			if (i == DnDConstants.ACTION_MOVE) {
				return "ACTION_MOVE (" + i + ")";
			}
			if (i == DnDConstants.ACTION_LINK) {
				return "ACTION_LINK (" + i + ")";
			}
			if (i == DnDConstants.ACTION_REFERENCE) {
				return "ACTION_REFERENCE (" + i + ")";
			}
			if (i == DnDConstants.ACTION_NONE) {
				return "ACTION_NONE (" + i + ")";
			}
			return "ACTION_UNKNOWN (" + i + ")";
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
			// e.getDragSourceContext().setCursor(Cursor.)
			// interface
			// System.out.println(e.getSource().getClass());
		}

		/**
		 * @param e
		 *            the event
		 */
		@Override
		public void dragExit(DragSourceEvent e) {
			// DragSourceContext context = e.getDragSourceContext();
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

	private DragSource dragSource;

	private DragGestureListener dgListener;

	DragSourceListener dsListener;

	int dragAction = DnDConstants.ACTION_COPY;
}
