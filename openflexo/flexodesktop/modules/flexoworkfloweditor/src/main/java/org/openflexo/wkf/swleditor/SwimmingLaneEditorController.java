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

import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.selection.SelectionManager;
import org.openflexo.selection.SelectionManagingDrawingController;
import org.openflexo.wkf.controller.WKFController;
import org.openflexo.wkf.swleditor.SwimmingLaneRepresentation.SwimmingLaneRepresentationObjectVisibilityDelegate;
import org.openflexo.wkf.swleditor.gr.WKFObjectGR;


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


public class SwimmingLaneEditorController extends SelectionManagingDrawingController<SwimmingLaneRepresentation> {

	private static final Logger logger = FlexoLogger.getLogger(SwimmingLaneEditorController.class.getPackage().getName());

	private WKFController _controller;
	private ActivityPalette _activityPalette;
	private OperationPalette _operationPalette;
	private ActionPalette _actionPalette;
	private EventPalette _eventPalette;
	private ArtefactPalette artefactPalette;

	public SwimmingLaneEditorController(WKFController controller,FlexoProcess process)
	{
		this(process,controller.getEditor(),controller.getSelectionManager(),null);
		_controller = controller;
	}

	public SwimmingLaneEditorController(FlexoProcess process, FlexoEditor editor, SelectionManager sm,SwimmingLaneRepresentationObjectVisibilityDelegate delegate)
	{
		super(new SwimmingLaneRepresentation(process,editor,delegate),sm);
		_activityPalette = new ActivityPalette();
		_operationPalette = new OperationPalette();
		_actionPalette = new ActionPalette();
		_eventPalette = new EventPalette();
		artefactPalette = new ArtefactPalette();
		registerPalette(_operationPalette);
		registerPalette(_actionPalette);
		registerPalette(_eventPalette);
		registerPalette(_activityPalette);
		registerPalette(artefactPalette);
		activatePalette(getActivityPalette());
		setScale(process.getScale(SWLEditorConstants.SWIMMING_LANE_EDITOR, 1.0));
	}

	@Override
	public void setScale(double aScale) {
		super.setScale(aScale);
		getDrawing().getProcess().setScale(SWLEditorConstants.SWIMMING_LANE_EDITOR, aScale);
	}

	@Override
	public void delete() {
		if (_controller!=null) {
			if (getDrawingView()!=null)
				_controller.removeModuleView(getDrawingView());
			_controller.SWIMMING_LANE_PERSPECTIVE.removeProcessController(this);
		}
		super.delete();
	}

	@Override
	public DrawingView<SwimmingLaneRepresentation> makeDrawingView(SwimmingLaneRepresentation drawing)
	{
		return new SwimmingLaneView(drawing,this);
	}

	public WKFController getWKFController() {
		return _controller;
	}

	@Override
	public SwimmingLaneView getDrawingView()
	{
		return (SwimmingLaneView)super.getDrawingView();
	}

	public FlexoEditor getEditor()
	{
		return getDrawing().getEditor();
	}

	public ActivityPalette getActivityPalette()
	{
		return _activityPalette;
	}

	public OperationPalette getOperationPalette()
	{
		return _operationPalette;
	}

	public ActionPalette getActionPalette()
	{
		return _actionPalette;
	}

	public EventPalette getEventPalette()
	{
		return _eventPalette;
	}

	public ArtefactPalette getArtefactPalette() {
		return artefactPalette;
	}

	private JTabbedPane paletteView;

	public JTabbedPane getPaletteView()
	{
		if (paletteView == null) {
			paletteView = new JTabbedPane() {
				@Override
				public String getToolTipTextAt(int index) {
					switch (index) {
					case 0:
						return FlexoLocalization.localizedForKey("Activity");
					case 1:
						return FlexoLocalization.localizedForKey("Operation");
					case 2:
						return FlexoLocalization.localizedForKey("Action");
					case 3:
						return FlexoLocalization.localizedForKey("Event");
					case 4:
						return FlexoLocalization.localizedForKey("Artefact");
					}
					return "";
				};
			};
			paletteView.addTab(null, WKFIconLibrary.ACTIVITY_NODE_ICON, getActivityPalette().getPaletteViewInScrollPane(),FlexoLocalization.localizedForKey("Activity"));
			paletteView.addTab(null, WKFIconLibrary.OPERATION_NODE_ICON, getOperationPalette().getPaletteViewInScrollPane(),FlexoLocalization.localizedForKey("Operation"));
			paletteView.addTab(null, WKFIconLibrary.ACTION_NODE_ICON, getActionPalette().getPaletteViewInScrollPane(),FlexoLocalization.localizedForKey("Action"));
			paletteView.addTab(null, WKFIconLibrary.EVENT_ICON, getEventPalette().getPaletteViewInScrollPane(),FlexoLocalization.localizedForKey("Event"));
			paletteView.addTab(null, WKFIconLibrary.ARTEFACT_ICON, getArtefactPalette().getPaletteViewInScrollPane(),FlexoLocalization.localizedForKey("Artefact"));
			getEventPalette().getPaletteView().getDrawingGraphicalRepresentation().setDrawWorkingArea(true);
			getEventPalette().getPaletteView().getDrawingGraphicalRepresentation().setDecorationPainter(getEventPalette().getEventPaletteDecorationPainter());

			/*
			paletteView.add(FlexoLocalization.localizedForKey("Activity",getActivityPalette().getPaletteView()),getActivityPalette().getPaletteView());
			paletteView.add(FlexoLocalization.localizedForKey("Operation",getOperationPalette().getPaletteView()),getOperationPalette().getPaletteView());
			paletteView.add(FlexoLocalization.localizedForKey("Action",getActionPalette().getPaletteView()),getActionPalette().getPaletteView());
			paletteView.add(FlexoLocalization.localizedForKey("Event",getEventPalette().getPaletteView()),getEventPalette().getPaletteView());
			paletteView.add(FlexoLocalization.localizedForKey("Artefact",getArtefactPalette().getPaletteView()),getArtefactPalette().getPaletteView());*/
			paletteView.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if (paletteView.getSelectedIndex() == 0) {
						activatePalette(getActivityPalette());
					}
					else if (paletteView.getSelectedIndex() == 1) {
						activatePalette(getOperationPalette());
					}
					else if (paletteView.getSelectedIndex() == 2) {
						activatePalette(getActionPalette());
					}
					else if (paletteView.getSelectedIndex() == 3) {
						activatePalette(getEventPalette());
					}
					else if (paletteView.getSelectedIndex() == 4) {
						activatePalette(getArtefactPalette());
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
		if (this.objectForPaletteSwitch!=null && this.objectForPaletteSwitch!=objectForPaletteSwitch) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("Received palette switch for different objects. Cannot fulfill everyone's needs, latest wins.");
		}
		this.objectForPaletteSwitch = objectForPaletteSwitch;
	}

	/**
	 * @param
	 */
	private synchronized void switchPaletteForNewSelection() {
		if (paletteSwitchRequested)
			return;
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
		if (getSelectionManager()==null)
			return;
		if (objectForPaletteSwitch==null)
			objectForPaletteSwitch = getSelectionManager().getLastSelectedObject();
		if (objectForPaletteSwitch!=null) {
			if (objectForPaletteSwitch instanceof AbstractActivityNode || objectForPaletteSwitch instanceof ActivityPetriGraph || objectForPaletteSwitch instanceof FlexoProcess)
				selectActivityPalette();
			else if (objectForPaletteSwitch instanceof OperationNode || objectForPaletteSwitch instanceof OperationPetriGraph)
				selectOperationPalette();
			else if (objectForPaletteSwitch instanceof ActionNode || objectForPaletteSwitch instanceof ActionPetriGraph)
				selectActionPalette();
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
		getPaletteView().setSelectedComponent(getActivityPalette().getPaletteViewInScrollPane());
	}

	public void performAutoLayout()
	{
		getDrawing().performAutoLayout();
	}

	private WKFMove currentMoveAction = null;
	
	@Override
	public void notifyWillMove(MoveInfo currentMove)
	{
		currentMoveAction = null;
		
		WKFObject movedObject = null;
		Vector<WKFObject> movedObjects = new Vector<WKFObject>();
		
		if (currentMove.getMovedObject() instanceof WKFObjectGR<?>) 
			movedObject = ((WKFObjectGR<?>)currentMove.getMovedObject()).getModel();
		
		for (ShapeGraphicalRepresentation<?> gr : currentMove.getMovedObjects()) {
			if (gr instanceof WKFObjectGR<?>) 
				movedObjects.add(((WKFObjectGR<?>)gr).getModel());
		}
		
		if (movedObject != null) {
			currentMoveAction = WKFMove.actionType.makeNewAction(movedObject,movedObjects,getWKFController().getEditor());
			currentMoveAction.setGraphicalContext(SWLEditorConstants.SWIMMING_LANE_EDITOR);
			for (ShapeGraphicalRepresentation<?> gr : currentMove.getMovedObjects()) {
				if (gr instanceof WKFObjectGR<?>) {
					WKFObject o = ((WKFObjectGR<?>)gr).getModel();
					FGEPoint initialLocation = currentMove.getInitialLocations().get(gr);
					currentMoveAction.getInitialLocations().put(o,new Point2D.Double(initialLocation.x,initialLocation.y));
				}
			}
			
		}
		
	}
	
	@Override
	public void notifyHasMoved(MoveInfo currentMove)
	{
		if (currentMoveAction != null) {
			for (ShapeGraphicalRepresentation<?> gr : currentMove.getMovedObjects()) {
				if (gr instanceof WKFObjectGR<?>) {
					WKFObject o = ((WKFObjectGR<?>)gr).getModel();
					currentMoveAction.getNewLocations().put(o,new Point2D.Double(gr.getX(),gr.getY()));
				}
			}
			// This is already done, but keep it for record and undo/redo
			currentMoveAction.setWasInteractivelyPerformed(true);
			currentMoveAction.doAction();

		}
	}
	

}
