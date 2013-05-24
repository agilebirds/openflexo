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

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.ie.ReusableComponentDefinitionElement;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEReusableComponent;
import org.openflexo.foundation.ie.IETabComponent;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.action.DropIEElement;
import org.openflexo.foundation.ie.action.DropPartialComponent;
import org.openflexo.foundation.ie.action.MoveIEElement;
import org.openflexo.foundation.ie.cl.ReusableComponentDefinition;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IEButtonWidget;
import org.openflexo.foundation.ie.widget.IECustomButtonWidget;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.foundation.ie.widget.IEReusableWidget;
import org.openflexo.foundation.ie.widget.IESequenceButton;
import org.openflexo.foundation.ie.widget.IESequenceTD;
import org.openflexo.foundation.ie.widget.IESequenceTR;
import org.openflexo.foundation.ie.widget.IESequenceTab;
import org.openflexo.foundation.ie.widget.IESequenceWidget;
import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.foundation.ie.widget.IETRWidget;
import org.openflexo.foundation.ie.widget.IETabWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.ie.widget.ITableData;
import org.openflexo.foundation.ie.widget.ITableRow;
import org.openflexo.foundation.ie.widget.InnerBlocReusableWidget;
import org.openflexo.foundation.ie.widget.TopComponentReusableWidget;
import org.openflexo.ie.view.DropZoneTopComponent;
import org.openflexo.ie.view.IEContainer;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.ie.view.palette.IEDSWidget;
import org.openflexo.ie.view.widget.ButtonPanel;
import org.openflexo.ie.view.widget.DropTabZone;
import org.openflexo.ie.view.widget.DropTableZone;
import org.openflexo.ie.view.widget.IEBlocWidgetView;
import org.openflexo.ie.view.widget.IESequenceWidgetWidgetView;

/**
 * IEDTListener a listener that tracks the state of the operation
 * 
 * @see java.awt.dnd.DropTargetListener
 * @see java.awt.dnd.DropTarget
 */
public class IEDTListener implements DropTargetListener {

	private static final boolean USE_FLEXO_ACTION = true;

	private static final Logger logger = Logger.getLogger(IEDTListener.class.getPackage().getName());

	public static boolean isValidDropTargetContainer(IEWidget container, IEWidget widget) {
		if (widget == null) {
			return false;
		}
		// All the following are not movable
		if (widget instanceof IETRWidget || widget instanceof IESequenceTR || widget instanceof IETDWidget
				|| widget instanceof IESequenceTD || widget instanceof IESequenceButton) {
			return false;
		}
		boolean ok = isValidTargetClassForDropTargetContainer(container, widget.getClass(), widget.isTopComponent());
		if (ok) {
			ok = !widget.isParentOf(container);
		}
		if (ok) {
			if (/*container.getWOComponent()!=widget.getWOComponent() && */container.getWOComponent() instanceof IEReusableComponent) {
				ok &= !widget.checkWidgetDoesNotEmbedWOComponent((IEReusableComponent) container.getWOComponent());
			}
		}
		return ok;
	}

	public static boolean isValidTargetClassForDropTargetContainer(IEWidget container, Class cls, boolean isTopComponent) {
		if (cls == IETRWidget.class || cls == IESequenceTR.class || cls == IETDWidget.class || cls == IESequenceTD.class
				|| cls == IESequenceButton.class) {
			return false;
		}
		if (cls == IESequenceTab.class && container.getWOComponent() instanceof IETabComponent) {
			return false;
		}
		if (container instanceof IESequenceWidget && ((IESequenceWidget) container).isTopComponent()) {
			return cls == IEBlocWidget.class || cls == IESequenceTab.class || cls == IEHTMLTableWidget.class
					|| cls == TopComponentReusableWidget.class && !container.getWOComponent().hasTabContainer()
					|| cls == IESequenceTR.class || cls == IESequenceWidget.class && isTopComponent;
		}

		else if (container instanceof IESequenceWidget || container instanceof IETDWidget) {
			return true;
		}
		if (container instanceof IESequenceButton || container instanceof IEBlocWidget || container instanceof IESequenceTab) {
			if (cls.equals(IEButtonWidget.class) || cls.equals(IEHyperlinkWidget.class) || cls.equals(IECustomButtonWidget.class)) {
				return true;
			}
		}
		if (container instanceof IESequenceTab && ((IESequenceTab) container).isRoot()) {
			return cls.equals(IETabWidget.class);
		}

		if (container instanceof IEBlocWidget) {
			return (cls.equals(IEHTMLTableWidget.class) || cls.equals(InnerBlocReusableWidget.class))
					&& ((IEBlocWidget) container).getContent() == null;
		}
		return false;

	}

	/*	public static boolean isValidDropTargetContainer(IEContainer container,
				IEWidget widget) {
			if (widget == null)
				return false;
			boolean ok = isValidTargetClassForDropTargetContainer(container, widget.getClass(), widget.isTopComponent());
			if (ok) {
				if (container.getContainerModel() instanceof IEWidget) {
					ok = !widget.isParentOf((IEWidget) container
							.getContainerModel());
				}
			}
			if (ok) {
				if (/*container.getWOComponent()!=widget.getWOComponent() && *//*container.getWOComponent() instanceof IEReusableComponent) {
																		ok&=!widget.checkWidgetDoesNotEmbedWOComponent((IEReusableComponent) container.getWOComponent());
																		}
																		}
																		return ok;
																		}
																		
																		public static boolean isValidTargetClassForDropTargetContainer(
																		IEContainer container, Class cls, boolean isTopComponent) {
																		if(cls.equals(IESequenceTab.class) && (container.getWOComponent() instanceof IETabComponent))
																		return false;
																		if (container instanceof DropZoneTopComponent) {
																		return cls.equals(IEBlocWidget.class)
																		|| (cls.equals(IESequenceTab.class))
																		|| cls.equals(IEHTMLTableWidget.class)
																		|| (cls.equals(TopComponentReusableWidget.class) && !((DropZoneTopComponent) container)
																		.getModel().getComponentDefinition()
																		.getWOComponent().hasTabContainer())
																		|| cls.equals(IESequenceTR.class)
																		|| (cls.equals(IESequenceWidget.class) && isTopComponent);
																		}

																		else if (container instanceof IESequenceWidgetWidgetView) {
																		return true;
																		}

																		else if (container instanceof DropTabZone) {
																		return cls.equals(IETabWidget.class);
																		}

																		else if (container instanceof DropTableZone) {
																		return (cls.equals(IEHTMLTableWidget.class) || cls
																		.equals(InnerBlocReusableWidget.class))
																		&& (((DropTableZone) container).getBlocModel().getContent() == null);
																		}
																		else if (container instanceof ButtonPanel || container instanceof IEBlocWidgetView) {
																		return (cls.equals(IEButtonWidget.class) || cls
																		.equals(IEHyperlinkWidget.class) || cls.equals(IECustomButtonWidget.class));
																		}

																		return false;

																		}
																		*/
	private int acceptableActions = DnDConstants.ACTION_COPY_OR_MOVE;

	private IEContainer _dropContainer;

	private IEObject _dropContainerModel;

	private IEController _ieController;

	public IEDTListener(IEController ieController, IEContainer flexoDropPanel, IEObject model) {
		super();
		_ieController = ieController;
		_dropContainer = flexoDropPanel;
		_dropContainerModel = model;
	}

	/**
	 * Called by isDragOk Checks to see if the flavor drag flavor is acceptable
	 * 
	 * @param e
	 *            the DropTargetDragEvent object
	 * @return whether the flavor is acceptable
	 */
	private boolean isDragFlavorSupported(DropTargetDragEvent e) {
		boolean ok = false;
		if (e.isDataFlavorSupported(WidgetTransferable.widgetFlavor())) {
			ok = true;
		}
		if (e.isDataFlavorSupported(WidgetMovable.widgetFlavor())) {
			ok = true;
		}
		return ok;
	}

	/**
	 * Called by dragEnter and dragOver Checks the flavors and operations
	 * 
	 * @param e
	 *            the event object
	 * @return whether the flavor and operation is ok
	 */
	private boolean isDragOk(DropTargetDragEvent e) {
		int da = e.getDropAction();
		// we're saying that these actions are necessary
		if ((da & acceptableActions) == 0) {
			return false;
		}
		return true;
	}

	/**
	 * start "drag under" feedback on component invoke acceptDrag or rejectDrag based on isDragOk
	 * 
	 * @param e
	 */
	@Override
	public void dragEnter(DropTargetDragEvent e) {
		_ieController.setCurrentlyDroppingTarget(_dropContainer);

	}

	/**
	 * continue "drag under" feedback on component invoke acceptDrag or rejectDrag based on isDragOk
	 * 
	 * @param e
	 */
	@Override
	public void dragOver(DropTargetDragEvent e) {
		if (isDragOk(e) == false) {
			e.rejectDrag();
			return;
		}
		e.acceptDrag(e.getDropAction());
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent e) {
		if (isDragOk(e) == false) {
			e.rejectDrag();
			return;
		}
		e.acceptDrag(e.getDropAction());
	}

	@Override
	public void dragExit(DropTargetEvent e) {
		// interface
	}

	/**
	 * perform action from getSourceActions on the transferrable invoke acceptDrop or rejectDrop invoke dropComplete if its a local (same
	 * JVM) transfer, use StringTransferable.localStringFlavor find a match for the flavor check the operation get the transferable
	 * according to the chosen flavor do the transfer
	 * 
	 * @param e
	 */
	@Override
	public void drop(DropTargetDropEvent e) {
		Object data = null;
		try {
			Transferable tr = e.getTransferable();
			DataFlavor[] flavors = tr.getTransferDataFlavors();
			for (int i = 0; i < flavors.length; i++) {
				if (tr.isDataFlavorSupported(flavors[i])) {
					data = e.getTransferable().getTransferData(flavors[i]);
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			e.rejectDrop();
		}

		try {
			if (data == null) {
				throw new NullPointerException();
			}
		} catch (Throwable t) {
			t.printStackTrace();
			e.dropComplete(false);
			return;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Drop with data :" + data.getClass());
		}
		if (data instanceof TransferedWidget) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("DATA instance of TransferedWidget");
			}

			try {
				Point position = e.getLocation();
				if (position.x < 0 || position.y < 0) {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Invalid position");
					}
					e.rejectDrop();
					return;
				}
				IEDSWidget droppedWidget = ((TransferedWidget) data).getWidget();
				IEWidget newWidget = null;
				newWidget = droppedWidget.getWidget(_dropContainer.getWOComponent().getComponentDefinition());
				Class targetClass = droppedWidget.getTargetClassModel();
				if (targetClass.equals(IEReusableWidget.class)) {

				} else if (!/*isValidTargetClassForDropTargetContainer(
							_dropContainer, droppedWidget.getTargetClassModel())*/isValidDropTargetContainer(
						_dropContainer.getContainerModel(), newWidget)) {
					e.rejectDrop();
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Invalid target class:" + droppedWidget.getTargetClassModel() + " for container:"
								+ _dropContainer.getClass());
					}
					return;
				}

				DropIEElement dropAction = DropIEElement.actionType.makeNewAction(_dropContainerModel, null, _ieController.getEditor());
				dropAction.setUpdateDates(_ieController.currentPaletteIsBasicPalette());
				dropAction.setDroppedWidget(newWidget);

				if (_dropContainer instanceof DropZoneTopComponent) {
					dropAction.setIndex(((DropZoneTopComponent) _dropContainer).findInsertionIndex(position.x, position.y));
				}

				if (_dropContainer instanceof ButtonPanel) {
					dropAction.setIndex(((ButtonPanel) _dropContainer).findInsertionIndex(position.x));
				}

				if (_dropContainer instanceof IESequenceWidgetWidgetView) {
					dropAction.setIndex(((IESequenceWidgetWidgetView) _dropContainer).findInsertionIndex(position.x, position.y));
				}

				dropAction.doAction();

				if (dropAction.hasActionExecutionSucceeded()) {
					e.acceptDrop(acceptableActions);
					((JComponent) _dropContainer).repaint();
					_ieController.getIESelectionManager().resetSelection();
					_ieController.getIESelectionManager().addToSelected(newWidget);
				}

				else {
					e.rejectDrop();
					return;
				}
			} catch (Exception ex) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("This should never happen! Invalid XML in node in Palete");
				}
				ex.printStackTrace();
			}
		} else if (data instanceof MovedWidget) {
			try {
				Point position = e.getLocation();
				if (position.x < 0 || position.y < 0) {
					e.rejectDrop();
					return;
				}
				IEWidget movedWidget = ((MovedWidget) data).getWidget();

				/*
				 * if (_dropContainer instanceof EmptyCellView && movedWidget
				 * instanceof IETableWidget) { IEController.isDropSuccessFull =
				 * false; e.rejectDrop(); return; }
				 */
				if (!isValidDropTargetContainer(_dropContainer.getContainerModel(), movedWidget)) {
					// e.dropComplete(false);
					e.rejectDrop();

					return;
				}
				e.acceptDrop(acceptableActions);
				if (movedWidget != null) {
					if (USE_FLEXO_ACTION) {
						MoveIEElement moveAction = MoveIEElement.actionType.makeNewAction(_dropContainerModel, null,
								_ieController.getEditor());
						moveAction.setMovedWidget(movedWidget);

						if (_dropContainer instanceof DropZoneTopComponent) {
							moveAction.setIndex(((DropZoneTopComponent) _dropContainer).findInsertionIndex(position.x, position.y));
						}

						if (_dropContainer instanceof ButtonPanel) {
							moveAction.setIndex(((ButtonPanel) _dropContainer).findInsertionIndex(position.x));
						}

						if (_dropContainer instanceof IESequenceWidgetWidgetView) {
							moveAction.setIndex(((IESequenceWidgetWidgetView) _dropContainer).findInsertionIndex(position.x, position.y));
						}

						moveAction.doAction();
					} else {
						movedWidget.removeFromContainer();
						insertView(movedWidget, _dropContainer, position);
					}
					e.dropComplete(true);
					/*
					 * if (_dropContainer instanceof EmptyCellView) {
					 * tableContainer.findViewForModel(movedWidget); } else if
					 * (_dropContainer instanceof IEPanel) { ((IEPanel)
					 * _dropContainer).findViewForModel(movedWidget); }
					 */
					_ieController.getIESelectionManager().resetSelection();
					_ieController.getIESelectionManager().addToSelected(movedWidget);
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (data instanceof BrowserElement) {
			if (data instanceof ReusableComponentDefinitionElement) {
				ReusableComponentDefinition partialComponent = ((ReusableComponentDefinitionElement) data).getComponentDefinition();
				DropPartialComponent dropAction = DropPartialComponent.actionType.makeNewAction(_dropContainerModel, null,
						_ieController.getEditor());
				dropAction.setPartialComponent(partialComponent);

				if (_dropContainer instanceof DropZoneTopComponent) {
					Point position = e.getLocation();
					dropAction.setIndex(((DropZoneTopComponent) _dropContainer).findInsertionIndex(position.x, position.y));
				}

				dropAction.doAction();

				if (dropAction.hasActionExecutionSucceeded()) {
					e.acceptDrop(acceptableActions);
					((JComponent) _dropContainer).repaint();
					_ieController.getIESelectionManager().resetSelection();
					_ieController.getIESelectionManager().addToSelected(dropAction.getDroppedWidget());
				} else {
					e.rejectDrop();
					return;
				}
			} else {
				logger.warning("dropped a " + data);
			}

		} else {
			e.dropComplete(false);
			return;
		}

	}

	/**
	 * 
	 * @deprecated DO IT WITH FLEXO ACTION
	 * @param movedWidget
	 * @param parent
	 * @param copyFlexoID
	 * @return
	 */
	@Deprecated
	public static IEWidget createModelFromMovedWidget(IEWidget movedWidget, IEObject parent, boolean copyFlexoID) {
		IEWOComponent component = movedWidget.getWOComponent();
		if (component == null) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Could not clone widget because it has no WOComponent");
			}
			return null;
		}
		try {
			component.initializeSerialization();
			IEWidget returned = (IEWidget) movedWidget.cloneUsingXMLMapping(null, !copyFlexoID, component.getXMLMapping());
			returned.setParent(parent);
			if (parent instanceof IEWOComponent) {
				returned.setWOComponent((IEWOComponent) parent);
			} else if (parent instanceof IEWidget) {
				returned.setWOComponent(((IEWidget) parent).getWOComponent());
			} else {
				returned.setWOComponent(movedWidget.getWOComponent());
			}
			return returned;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			component.finalizeSerialization();
		}
		return null;
	}

	/**
	 * 
	 * @deprecated DO IT WITH FLEXO ACTION
	 * @param model
	 * @param container
	 * @param position
	 */
	@Deprecated
	public static void insertView(IEWidget model, IEContainer container, Point position) {
		if (model instanceof ITableRow || model instanceof ITableData || model == null) {
			return;
		}
		if (container instanceof DropTableZone && model instanceof IEHTMLTableWidget) {
			((DropTableZone) container).add((IEHTMLTableWidget) model);
			if (logger.isLoggable(Level.INFO)) {
				logger.info("insert HTMLTable in DropTableZone");
			}
			container.validate();
		}

		if (container instanceof ButtonPanel && model instanceof IEHyperlinkWidget) {
			int i = ((ButtonPanel) container).findInsertionIndex(position.x);
			((ButtonPanel) container).getButtonedWidgetModel().insertButtonAtIndex((IEHyperlinkWidget) model, i);
			container.validate();

		}
		if (container instanceof IEBlocWidgetView && model instanceof IEHyperlinkWidget) {
			((IEBlocWidgetView) container).getModel().insertButtonAtIndex((IEHyperlinkWidget) model, Integer.MAX_VALUE);
			// We use Max value to insert at the end of the sequence
			container.validate();

		}
		if (container instanceof DropTabZone && model instanceof IETabWidget) {
			((DropTabZone) container).getTabModel().addToInnerWidgets((IETabWidget) model);
			container.validate();

		}
		if (container instanceof IESequenceWidgetWidgetView) {
			int i = ((IESequenceWidgetWidgetView) container).findInsertionIndex(position.x, position.y);
			if (model instanceof IETDWidget) {
				IESequenceWidget seq = ((IETDWidget) model).getSequenceWidget();
				for (int j = seq.size() - 1; j > -1; j--) {
					model = seq.get(j);
					model.setIndex(i);
					((IESequenceWidgetWidgetView) container).getSequenceModel().insertElementAt(model, i);
				}
			} else if (model instanceof IESequenceWidget && ((IESequenceWidget) model).getOperator() == null) {
				IESequenceWidget seq = (IESequenceWidget) model;
				for (int j = seq.size() - 1; j > -1; j--) {
					model = seq.get(j);
					model.setIndex(i);
					((IESequenceWidgetWidgetView) container).getSequenceModel().insertElementAt(model, i);
				}
			} else {
				model.setIndex(i);
				((IESequenceWidgetWidgetView) container).getSequenceModel().insertElementAt(model, i);
			}
			container.validate();
		}
	}

}
