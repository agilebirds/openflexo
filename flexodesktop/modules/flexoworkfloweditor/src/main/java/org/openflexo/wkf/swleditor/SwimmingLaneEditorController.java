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
package org.openflexo.wkf.swleditor;

import java.awt.geom.Point2D;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.MoveInfo;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.wkf.ActionPetriGraph;
import org.openflexo.foundation.wkf.ActivityPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.OperationPetriGraph;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.action.WKFMove;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.module.UserType;
import org.openflexo.selection.SelectionManager;
import org.openflexo.selection.SelectionManagingDrawingController;
import org.openflexo.wkf.controller.WKFController;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation.SwimmingLaneRepresentationObjectVisibilityDelegate;
import org.openflexo.wkf.swleditor.gr.WKFObjectGR;

public class SwimmingLaneEditorController extends SelectionManagingDrawingController<SwimmingLaneRepresentation> {

	private static final Logger logger = FlexoLogger.getLogger(SwimmingLaneEditorController.class.getPackage().getName());

	private WKFController _controller;
	private BasicPalette basicPalette;
	private ExtendedPalette extendedPalette;
	private EventPalette eventPalette;
	private OperationPalette operationPalette;
	private ActionPalette actionPalette;

	public SwimmingLaneEditorController(WKFController controller, FlexoProcess process) {
		this(process, controller, controller.getSelectionManager(), null);
		_controller = controller;
	}

	public SwimmingLaneEditorController(FlexoProcess process, WKFController controller, SelectionManager sm,
			SwimmingLaneRepresentationObjectVisibilityDelegate delegate) {
		super(new SwimmingLaneRepresentation(process, controller, delegate), sm);
		basicPalette = new BasicPalette();
		extendedPalette = new ExtendedPalette();
		eventPalette = new EventPalette();
		operationPalette = new OperationPalette();
		actionPalette = new ActionPalette();
		registerPalette(basicPalette);
		registerPalette(eventPalette);
		registerPalette(extendedPalette);
		registerPalette(operationPalette);
		registerPalette(actionPalette);
		activatePalette(getBasicPalette());
		setScale(process.getScale(SWLEditorConstants.SWIMMING_LANE_EDITOR, 1.0));
	}

	@Override
	public void setScale(double aScale) {
		super.setScale(aScale);
		getDrawing().getProcess().setScale(SWLEditorConstants.SWIMMING_LANE_EDITOR, aScale);
	}

	@Override
	public void delete() {
		if (_controller != null) {
			if (getDrawingView() != null) {
				_controller.removeModuleView(getDrawingView());
			}
			_controller.SWIMMING_LANE_PERSPECTIVE.removeProcessController(this);
		}
		super.delete();
		getDrawing().delete();
	}

	@Override
	public DrawingView<SwimmingLaneRepresentation> makeDrawingView(SwimmingLaneRepresentation drawing) {
		return new SwimmingLaneView(drawing, this);
	}

	public WKFController getWKFController() {
		return _controller;
	}

	@Override
	public SwimmingLaneView getDrawingView() {
		return (SwimmingLaneView) super.getDrawingView();
	}

	public FlexoEditor getEditor() {
		return getDrawing().getEditor();
	}

	public BasicPalette getBasicPalette() {
		return basicPalette;
	}

	public OperationPalette getOperationPalette() {
		return operationPalette;
	}

	public ActionPalette getActionPalette() {
		return actionPalette;
	}

	public EventPalette getEventPalette() {
		return eventPalette;
	}

	public ExtendedPalette getExtendedPalette() {
		return extendedPalette;
	}

	private JTabbedPane paletteView;

	public JTabbedPane getPaletteView() {
		if (paletteView == null) {
			paletteView = new JTabbedPane() {
				@Override
				public String getToolTipTextAt(int index) {
					return getTitleAt(index);
				};
			};
			paletteView.addTab(FlexoLocalization.localizedForKey("basic", getBasicPalette().getPaletteViewInScrollPane()),
					getBasicPalette().getPaletteViewInScrollPane());
			paletteView.addTab(FlexoLocalization.localizedForKey("extended", getExtendedPalette().getPaletteViewInScrollPane()),
					getExtendedPalette().getPaletteViewInScrollPane());
			paletteView.addTab(FlexoLocalization.localizedForKey("event", getEventPalette().getPaletteViewInScrollPane()),
					getEventPalette().getPaletteViewInScrollPane());
			if (!UserType.isLite()) {
				paletteView.addTab(FlexoLocalization.localizedForKey("operation", getOperationPalette().getPaletteViewInScrollPane()),
						getOperationPalette().getPaletteViewInScrollPane());
				paletteView.addTab(FlexoLocalization.localizedForKey("action", getActionPalette().getPaletteViewInScrollPane()),
						getActionPalette().getPaletteViewInScrollPane());
			}

			getEventPalette().getPaletteView().getDrawingGraphicalRepresentation().setDrawWorkingArea(true);
			getEventPalette().getPaletteView().getDrawingGraphicalRepresentation()
					.setDecorationPainter(getEventPalette().getEventPaletteDecorationPainter());
			paletteView.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if (paletteView.getSelectedComponent() == getBasicPalette().getPaletteViewInScrollPane()) {
						activatePalette(getBasicPalette());
					} else if (paletteView.getSelectedComponent() == getOperationPalette().getPaletteViewInScrollPane()) {
						activatePalette(getOperationPalette());
					} else if (paletteView.getSelectedComponent() == getActionPalette().getPaletteViewInScrollPane()) {
						activatePalette(getActionPalette());
					} else if (paletteView.getSelectedComponent() == getEventPalette().getPaletteViewInScrollPane()) {
						activatePalette(getEventPalette());
					} else if (paletteView.getSelectedComponent() == getExtendedPalette().getPaletteViewInScrollPane()) {
						activatePalette(getExtendedPalette());
					}
				}
			});
		}
		return paletteView;
	}

	@Override
	public void setSelectedObject(GraphicalRepresentation graphicalRepresentation) {
		switchPaletteForNewSelection();
		super.setSelectedObject(graphicalRepresentation);
	}

	@Override
	public void addToSelectedObjects(GraphicalRepresentation anObject) {
		switchPaletteForNewSelection();
		super.addToSelectedObjects(anObject);
	}

	protected boolean paletteSwitchRequested = false;
	protected FlexoModelObject objectForPaletteSwitch;

	public void setObjectForPaletteSwitch(FlexoModelObject objectForPaletteSwitch) {
		if (this.objectForPaletteSwitch != null && this.objectForPaletteSwitch != objectForPaletteSwitch) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Received palette switch for different objects. Cannot fulfill everyone's needs, latest wins.");
			}
		}
		this.objectForPaletteSwitch = objectForPaletteSwitch;
	}

	/**
	 * @param
	 */
	private synchronized void switchPaletteForNewSelection() {
		if (paletteSwitchRequested) {
			return;
		}
		paletteSwitchRequested = true;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				performPaletteSwitch();
			}
		});
	}

	/**
	 *
	 */
	protected synchronized void performPaletteSwitch() {
		if (getSelectionManager() == null) {
			return;
		}
		if (objectForPaletteSwitch == null) {
			objectForPaletteSwitch = getSelectionManager().getLastSelectedObject();
		}
		if (objectForPaletteSwitch != null) {
			if (getPaletteView().getSelectedComponent() != getExtendedPalette().getPaletteViewInScrollPane()
					&& getPaletteView().getSelectedComponent() != getEventPalette().getPaletteViewInScrollPane()) {
				if (objectForPaletteSwitch instanceof AbstractActivityNode || objectForPaletteSwitch instanceof ActivityPetriGraph
						|| objectForPaletteSwitch instanceof FlexoProcess) {
					selectActivityPalette();
				} else if (objectForPaletteSwitch instanceof OperationNode || objectForPaletteSwitch instanceof OperationPetriGraph) {
					selectOperationPalette();
				} else if (objectForPaletteSwitch instanceof ActionNode || objectForPaletteSwitch instanceof ActionPetriGraph) {
					selectActionPalette();
				}
			}
		}
		objectForPaletteSwitch = null;
		paletteSwitchRequested = false;
	}

	/**
	 *
	 */
	public void selectActionPalette() {
		getPaletteView().setSelectedComponent(getActionPalette().getPaletteViewInScrollPane());
	}

	/**
	 *
	 */
	public void selectOperationPalette() {
		getPaletteView().setSelectedComponent(getOperationPalette().getPaletteViewInScrollPane());
	}

	/**
	 *
	 */
	public void selectActivityPalette() {
		getPaletteView().setSelectedComponent(getBasicPalette().getPaletteViewInScrollPane());
	}

	public void performAutoLayout() {
		getDrawing().performAutoLayout();
	}

	private WKFMove currentMoveAction = null;

	@Override
	public void notifyWillMove(MoveInfo currentMove) {
		currentMoveAction = null;

		WKFObject movedObject = null;
		Vector<WKFObject> movedObjects = new Vector<WKFObject>();

		if (currentMove.getMovedObject() instanceof WKFObjectGR<?>) {
			movedObject = ((WKFObjectGR<?>) currentMove.getMovedObject()).getModel();
		}

		for (GraphicalRepresentation<?> gr : currentMove.getMovedObjects()) {
			if (gr instanceof WKFObjectGR<?>) {
				movedObjects.add(((WKFObjectGR<?>) gr).getModel());
			}
		}

		if (movedObject != null) {
			currentMoveAction = WKFMove.actionType.makeNewAction(movedObject, movedObjects, getWKFController().getEditor());
			currentMoveAction.setGraphicalContext(SWLEditorConstants.SWIMMING_LANE_EDITOR);
			for (ShapeGraphicalRepresentation<?> gr : currentMove.getMovedObjects()) {
				if (gr instanceof WKFObjectGR<?>) {
					WKFObject o = ((WKFObjectGR<?>) gr).getModel();
					FGEPoint initialLocation = currentMove.getInitialLocations().get(gr);
					currentMoveAction.getInitialLocations().put(o, new Point2D.Double(initialLocation.x, initialLocation.y));
				}
			}

		}

	}

	@Override
	public void notifyHasMoved(MoveInfo currentMove) {
		if (currentMoveAction != null) {
			for (ShapeGraphicalRepresentation<?> gr : currentMove.getMovedObjects()) {
				if (gr instanceof WKFObjectGR<?>) {
					WKFObject o = ((WKFObjectGR<?>) gr).getModel();
					currentMoveAction.getNewLocations().put(o, new Point2D.Double(gr.getX(), gr.getY()));
				}
			}
			// This is already done, but keep it for record and undo/redo
			currentMoveAction.setWasInteractivelyPerformed(true);
			currentMoveAction.doAction();

		}
	}

}
