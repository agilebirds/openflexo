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
package org.openflexo.fge.controller;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.DefaultDrawing;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.cp.ConnectorAdjustingControlPoint;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.ShadowStyle;
import org.openflexo.fge.graphics.TextStyle;
import org.openflexo.fge.notifications.GraphicalObjectsHierarchyRebuildEnded;
import org.openflexo.fge.notifications.GraphicalObjectsHierarchyRebuildStarted;
import org.openflexo.fge.view.ConnectorView;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.fge.view.FGEPaintManager;
import org.openflexo.fge.view.LabelView;
import org.openflexo.fge.view.ShapeView;

public class DrawingController<D extends Drawing<?>> extends Observable implements Observer {

	private static final Logger logger = Logger.getLogger(DrawingController.class.getPackage().getName());

	private D drawing;
	private DrawingView<D> drawingView;

	public enum EditorTool {
		SelectionTool, DrawShapeTool, DrawConnectorTool, DrawTextTool
	}

	private EditorTool currentTool;

	private DrawShapeToolController drawShapeToolController;
	private DrawShapeAction drawShapeAction;

	private ForegroundStyle currentForegroundStyle;
	private BackgroundStyle currentBackgroundStyle;
	private TextStyle currentTextStyle;
	private ShadowStyle currentShadowStyle;

	private ScalePanel _scalePanel;
	private EditorToolbox toolbox;

	private GraphicalRepresentation focusedFloatingLabel;
	// private GraphicalRepresentation focusedObject;

	private Vector<GraphicalRepresentation> focusedObjects;
	private Vector<GraphicalRepresentation> selectedObjects;

	private LabelView<?> _currentlyEditedLabel;

	private ControlArea<?> focusedControlArea;

	private double scale = 1.0;

	private Vector<DrawingPalette> palettes;

	private FGEPoint lastClickedPoint;
	private GraphicalRepresentation<?> lastSelectedGR;

	public DrawingController(D aDrawing) {
		super();

		setCurrentTool(EditorTool.SelectionTool);
		currentForegroundStyle = ForegroundStyle.makeDefault();
		currentBackgroundStyle = BackgroundStyle.makeColoredBackground(FGEConstants.DEFAULT_BACKGROUND_COLOR);
		currentTextStyle = TextStyle.makeDefault();
		currentShadowStyle = ShadowStyle.makeDefault();

		toolbox = new EditorToolbox(this);

		drawing = aDrawing;
		if (drawing instanceof DefaultDrawing<?>) {
			((DefaultDrawing<?>) drawing).addObserver(this);
		}

		focusedObjects = new Vector<GraphicalRepresentation>();
		selectedObjects = new Vector<GraphicalRepresentation>();
		palettes = new Vector<DrawingPalette>();
		_buildDrawingView();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Building DrawingController: " + this);
		}
	}

	public DrawingView<D> rebuildDrawingView() {
		if (drawingView != null) {
			drawingView.delete();
		}
		_buildDrawingView();
		return drawingView;
	}

	private DrawingView<D> _buildDrawingView() {
		drawingView = makeDrawingView(drawing);
		if (drawing.getContainedObjects(drawing.getModel()) != null) {
			for (Object o : drawing.getContainedObjects(drawing.getModel())) {
				GraphicalRepresentation<?> gr = drawing.getGraphicalRepresentation(o);
				if (gr instanceof ShapeGraphicalRepresentation) {
					ShapeView<?> v = _buildShapeView((ShapeGraphicalRepresentation<?>) gr);
					drawingView.add(v);
				} else if (gr instanceof ConnectorGraphicalRepresentation) {
					ConnectorGraphicalRepresentation<?> connectorGR = (ConnectorGraphicalRepresentation<?>) gr;
					ConnectorView<?> v = connectorGR.makeConnectorView(this);
					drawingView.add(v);
				}
			}
		}
		return drawingView;
	}

	private <O> ShapeView<O> _buildShapeView(ShapeGraphicalRepresentation<O> shapedGR) {
		ShapeView<O> returned = shapedGR.makeShapeView(this);
		if (shapedGR.getContainedObjects() != null) {
			for (Object o : shapedGR.getContainedObjects()) {
				GraphicalRepresentation<?> gr = shapedGR.getDrawing().getGraphicalRepresentation(o);
				if (gr instanceof ShapeGraphicalRepresentation) {
					ShapeView<?> v = _buildShapeView((ShapeGraphicalRepresentation<?>) gr);
					returned.add(v);
				} else if (gr instanceof ConnectorGraphicalRepresentation) {
					ConnectorGraphicalRepresentation<?> connectorGR = (ConnectorGraphicalRepresentation<?>) gr;
					ConnectorView<?> v = connectorGR.makeConnectorView(this);
					returned.add(v);
				}
			}
		}
		return returned;
	}

	// Override for a custom view
	public DrawingView<D> makeDrawingView(D drawing) {
		return new DrawingView<D>(drawing, this);
	}

	public DrawShapeToolController<?> getDrawShapeToolController() {
		return drawShapeToolController;
	}

	public EditorTool getCurrentTool() {
		return currentTool;
	}

	public void setCurrentTool(EditorTool aTool) {
		if (aTool != currentTool) {
			logger.info("Switch to tool " + aTool);
			switch (aTool) {
			case SelectionTool:
				if (currentTool == EditorTool.DrawShapeTool && drawShapeToolController != null) {
					drawShapeToolController.makeNewShape();
				}
				break;
			case DrawShapeTool:
				if (drawShapeAction != null) {
					drawShapeToolController = new DrawPolygonToolController(this, drawShapeAction);
				}
				break;
			case DrawConnectorTool:
				break;
			case DrawTextTool:
				break;
			default:
				break;
			}
			currentTool = aTool;
			if (getToolbox() != null) {
				getToolbox().getToolPanel().updateButtons();
			}
			if (getPaintManager() != null) {
				getPaintManager().repaint(getDrawingView());
			}
		}
	}

	public ForegroundStyle getCurrentForegroundStyle() {
		return currentForegroundStyle;
	}

	public void setCurrentForegroundStyle(ForegroundStyle currentForegroundStyle) {
		this.currentForegroundStyle = currentForegroundStyle;
	}

	public BackgroundStyle getCurrentBackgroundStyle() {
		return currentBackgroundStyle;
	}

	public void setCurrentBackgroundStyle(BackgroundStyle currentBackgroundStyle) {
		this.currentBackgroundStyle = currentBackgroundStyle;
	}

	public TextStyle getCurrentTextStyle() {
		return currentTextStyle;
	}

	public void setCurrentTextStyle(TextStyle currentTextStyle) {
		this.currentTextStyle = currentTextStyle;
	}

	public ShadowStyle getCurrentShadowStyle() {
		return currentShadowStyle;
	}

	public void setCurrentShadowStyle(ShadowStyle currentShadowStyle) {
		this.currentShadowStyle = currentShadowStyle;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double aScale) {
		if (aScale < 0) {
			return;
		}
		scale = aScale;
		if (_scalePanel != null) {
			_scalePanel.slider.setValue((int) (aScale * 100));
		}
		drawingView.rescale();
	}

	public DrawShapeAction getDrawShapeAction() {
		return drawShapeAction;
	}

	public void setDrawShapeAction(DrawShapeAction drawShapeAction) {
		this.drawShapeAction = drawShapeAction;
	}

	public EditorToolbox getToolbox() {
		return toolbox;
	}

	public ScalePanel getScalePanel() {
		if (_scalePanel == null) {
			_scalePanel = new ScalePanel();
		}
		return _scalePanel;
	}

	public class ScalePanel extends JToolBar {

		private static final int MAX_ZOOM_VALUE = 300;
		protected JTextField scaleTF;
		protected JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 500, 100);

		protected ChangeListener sliderChangeListener;
		protected ActionListener actionListener;

		protected ScalePanel() {
			super(/*new FlowLayout(FlowLayout.LEFT, 10, 0)*/);
			scaleTF = new JTextField(5);
			int currentScale = (int) (getScale() * 100);
			scaleTF.setText("" + currentScale + "%");
			slider = new JSlider(SwingConstants.HORIZONTAL, 0, MAX_ZOOM_VALUE, currentScale);
			slider.setMajorTickSpacing(100);
			slider.setMinorTickSpacing(20);
			slider.setPaintTicks(false/*true*/);
			slider.setPaintLabels(false);
			slider.setBorder(BorderFactory.createEmptyBorder());
			sliderChangeListener = new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if (slider.getValue() > 0) {
						setScale(((double) slider.getValue()) / 100);
						scaleTF.removeActionListener(actionListener);
						scaleTF.setText("" + (int) (getScale() * 100) + "%");
						scaleTF.addActionListener(actionListener);
					}
				}
			};
			actionListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						// logger.info("On fait avec "+scaleTF.getText()+" ce qui donne: "+(((double)Integer.decode(scaleTF.getText()))/100));
						Integer newScale = null;
						if (scaleTF.getText().indexOf("%") > -1) {
							newScale = Integer.decode(scaleTF.getText().substring(0, scaleTF.getText().indexOf("%")));
						} else {
							newScale = Integer.decode(scaleTF.getText());
						}
						if (newScale > MAX_ZOOM_VALUE) {
							newScale = MAX_ZOOM_VALUE;
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									scaleTF.setText(MAX_ZOOM_VALUE + "%");
								}
							});
						}
						setScale(((double) newScale) / 100);
					} catch (NumberFormatException exception) {
						// Forget
					}
					scaleTF.removeActionListener(actionListener);
					slider.removeChangeListener(sliderChangeListener);
					scaleTF.setText("" + (int) (getScale() * 100) + "%");
					slider.setValue((int) (getScale() * 100));
					slider.addChangeListener(sliderChangeListener);
					scaleTF.addActionListener(actionListener);
				}
			};
			scaleTF.addActionListener(actionListener);
			slider.addChangeListener(sliderChangeListener);
			add(slider);
			add(scaleTF);
			// setBorder(BorderFactory.createEmptyBorder());
		}
	}

	public D getDrawing() {
		return drawing;
	}

	public DrawingGraphicalRepresentation<?> getDrawingGraphicalRepresentation() {
		return drawing.getDrawingGraphicalRepresentation();
	}

	public DrawingView<D> getDrawingView() {
		return drawingView;
	}

	public <O> GraphicalRepresentation<O> getGraphicalRepresentation(O drawable) {
		if (drawable == null) {
			return null;
		}
		return drawing.getGraphicalRepresentation(drawable);
	}

	public GraphicalRepresentation getFocusedFloatingLabel() {
		return focusedFloatingLabel;
	}

	public void setFocusedFloatingLabel(GraphicalRepresentation aFocusedlabel) {
		// logger.info("setFocusedFloatingLabel() with "+aFocusedlabel);
		if (focusedFloatingLabel == null) {
			if (aFocusedlabel == null) {
				return;
			} else {
				focusedFloatingLabel = aFocusedlabel;
				if (getPaintManager().isPaintingCacheEnabled()) {
					// Just repaint connector
					drawingView.getPaintManager().repaint(focusedFloatingLabel);
				} else {
					// @brutal mode
					drawingView.getPaintManager().repaint(drawingView);
				}
			}
		} else {
			GraphicalRepresentation oldFocusedFloatingLabel = focusedFloatingLabel;
			focusedFloatingLabel = aFocusedlabel;
			if (aFocusedlabel == null || focusedFloatingLabel != aFocusedlabel) {
				if (getPaintManager().isPaintingCacheEnabled()) {
					// Just repaint old and eventual new connector
					drawingView.getPaintManager().repaint(oldFocusedFloatingLabel);
					if (aFocusedlabel != null) {
						drawingView.getPaintManager().repaint(focusedFloatingLabel);
					}
				} else {
					// @brutal mode
					drawingView.getPaintManager().repaint(drawingView);
				}
			}
			/*
			if (aFocusedlabel == null) {
				focusedFloatingLabel = null;
				drawingView.getPaintManager().repaint(drawingView);
			}
			else if (focusedFloatingLabel != aFocusedlabel) {
				focusedFloatingLabel = aFocusedlabel;
				drawingView.getPaintManager().repaint(drawingView);
			}*/
		}
	}

	public ShapeGraphicalRepresentation getFirstSelectedShape() {
		for (GraphicalRepresentation gr : getSelectedObjects()) {
			if (gr instanceof ShapeGraphicalRepresentation) {
				return (ShapeGraphicalRepresentation) gr;
			}
		}
		return null;
	}

	public Vector<GraphicalRepresentation> getSelectedObjects() {
		return selectedObjects;
	}

	public void setSelectedObjects(List<? extends GraphicalRepresentation> someSelectedObjects) {
		stopEditionOfEditedLabelIfAny();
		if (someSelectedObjects == null) {
			setSelectedObjects(new Vector<GraphicalRepresentation>());
			return;
		}

		if (!selectedObjects.equals(someSelectedObjects)) {
			clearSelection();
			for (GraphicalRepresentation d : someSelectedObjects) {
				addToSelectedObjects(d);
			}
		}
	}

	public void setSelectedObject(GraphicalRepresentation aGraphicalRepresentation) {
		stopEditionOfEditedLabelIfAny();
		Vector<GraphicalRepresentation> singleton = new Vector<GraphicalRepresentation>();
		singleton.add(aGraphicalRepresentation);
		setSelectedObjects(singleton);
		getToolbox().update();
	}

	public void addToSelectedObjects(GraphicalRepresentation aGraphicalRepresentation) {
		stopEditionOfEditedLabelIfAny();
		if (aGraphicalRepresentation == null) {
			logger.warning("Cannot add null object");
			return;
		}
		if (!selectedObjects.contains(aGraphicalRepresentation)) {
			selectedObjects.add(aGraphicalRepresentation);
			aGraphicalRepresentation.setIsSelected(true);
		}
		getToolbox().update();
	}

	public void removeFromSelectedObjects(GraphicalRepresentation aGraphicalRepresentation) {
		stopEditionOfEditedLabelIfAny();
		if (aGraphicalRepresentation == null) {
			logger.warning("Cannot remove null object");
			return;
		}
		if (selectedObjects.contains(aGraphicalRepresentation)) {
			selectedObjects.remove(aGraphicalRepresentation);
		}
		aGraphicalRepresentation.setIsSelected(false);
		getToolbox().update();
	}

	public void toogleSelection(GraphicalRepresentation aGraphicalRepresentation) {
		// logger.info("BEGIN toggle selection with "+aGraphicalRepresentation+" with selection="+selectedObjects);
		stopEditionOfEditedLabelIfAny();
		if (aGraphicalRepresentation.getIsSelected()) {
			removeFromSelectedObjects(aGraphicalRepresentation);
		} else {
			addToSelectedObjects(aGraphicalRepresentation);
		}
		// logger.info("END toggle selection with "+aGraphicalRepresentation+" with selection="+selectedObjects);
	}

	public void clearSelection() {
		// logger.info("Clear selection");
		stopEditionOfEditedLabelIfAny();
		for (GraphicalRepresentation gr : selectedObjects) {
			gr.setIsSelected(false);
		}
		selectedObjects.clear();
	}

	public Vector<GraphicalRepresentation> getFocusedObjects() {
		return focusedObjects;
	}

	public void setFocusedObjects(List<? extends GraphicalRepresentation> someFocusedObjects) {
		if (someFocusedObjects == null) {
			setFocusedObjects(new Vector<GraphicalRepresentation>());
			return;
		}

		if (!focusedObjects.equals(someFocusedObjects)) {
			clearFocusSelection();
			for (GraphicalRepresentation d : someFocusedObjects) {
				addToFocusedObjects(d);
			}
		}
	}

	public void setFocusedObject(GraphicalRepresentation aGraphicalRepresentation) {
		if (aGraphicalRepresentation == null) {
			clearFocusSelection();
			return;
		}
		Vector<GraphicalRepresentation> singleton = new Vector<GraphicalRepresentation>();
		singleton.add(aGraphicalRepresentation);
		setFocusedObjects(singleton);
	}

	public void addToFocusedObjects(GraphicalRepresentation aGraphicalRepresentation) {
		if (aGraphicalRepresentation == null) {
			logger.warning("Cannot add null object");
			return;
		}
		if (!focusedObjects.contains(aGraphicalRepresentation)) {
			focusedObjects.add(aGraphicalRepresentation);
			aGraphicalRepresentation.setIsFocused(true);
		}
	}

	public void removeFromFocusedObjects(GraphicalRepresentation aGraphicalRepresentation) {
		if (aGraphicalRepresentation == null) {
			logger.warning("Cannot remove null object");
			return;
		}
		if (focusedObjects.contains(aGraphicalRepresentation)) {
			focusedObjects.remove(aGraphicalRepresentation);
		}
		aGraphicalRepresentation.setIsFocused(false);
	}

	public void toogleFocusSelection(GraphicalRepresentation aGraphicalRepresentation) {
		if (aGraphicalRepresentation.getIsFocused()) {
			removeFromFocusedObjects(aGraphicalRepresentation);
		} else {
			addToFocusedObjects(aGraphicalRepresentation);
		}
	}

	public void clearFocusSelection() {
		// stopEditionOfEditedLabelIfAny();
		for (GraphicalRepresentation gr : focusedObjects) {
			gr.setIsFocused(false);
		}
		focusedObjects.clear();
	}

	public void selectDrawing() {
		stopEditionOfEditedLabelIfAny();
		// Override when required
	}

	public void setEditedLabel(LabelView<?> aLabel) {
		stopEditionOfEditedLabelIfAny();
		_currentlyEditedLabel = aLabel;
	}

	public void resetEditedLabel() {
		_currentlyEditedLabel = null;
	}

	public boolean hasEditedLabel() {
		return _currentlyEditedLabel != null;
	}

	public LabelView<?> getEditedLabel() {
		return _currentlyEditedLabel;
	}

	public void stopEditionOfEditedLabelIfAny() {
		if (_currentlyEditedLabel != null) {
			_currentlyEditedLabel.stopEdition();
		}

	}

	private MouseDragControl currentMouseDrag = null;

	public MouseDragControl getCurrentMouseDrag() {
		return currentMouseDrag;
	}

	public void setCurrentMouseDrag(MouseDragControl aMouseDrag) {
		currentMouseDrag = aMouseDrag;
	}

	/**
	 * Implements strategy to preferencially choose a control point or an other during focus retrieving strategy
	 * 
	 * @param cp1
	 * @param cp2
	 * @return
	 */
	public ControlArea<?> preferredFocusedControlArea(ControlArea<?> ca1, ControlArea<?> ca2) {
		if (ca1.getGraphicalRepresentation().getLayer() == ca2.getGraphicalRepresentation().getLayer()) {
			// ControlPoint have priority on other ControlArea
			if (ca1 instanceof ConnectorAdjustingControlPoint) {
				return ca1;
			} else if (ca2 instanceof ConnectorAdjustingControlPoint) {
				return ca2;
			}
			if (ca1 instanceof ControlPoint) {
				return ca1;
			} else if (ca2 instanceof ControlPoint) {
				return ca2;
			}
		}
		return (ca1.getGraphicalRepresentation().getLayer() > ca2.getGraphicalRepresentation().getLayer() ? ca1 : ca2);
	}

	public ControlArea<?> getFocusedControlArea() {
		return focusedControlArea;
	}

	public void _setFocusedControlArea(ControlArea<?> aControlArea) {
		if (focusedControlArea != aControlArea) {
			this.focusedControlArea = aControlArea;
			// getDrawingView().getPaintManager().repaint(getDrawingView());
		}
	}

	public Vector<DrawingPalette> getPalettes() {
		return palettes;
	}

	public void registerPalette(DrawingPalette aPalette) {
		logger.fine("Register palette for " + this);
		palettes.add(aPalette);
		aPalette.registerController(this);
		// if (getDrawingView() != null) getDrawingView().registerPalette(aPalette);
	}

	public void unregisterPalette(DrawingPalette aPalette) {
		logger.fine("Un-Register palette for " + this);
		palettes.remove(aPalette);
	}

	public void activatePalette(DrawingPalette aPalette) {
		if (getDrawingView() != null) {
			getDrawingView().registerPalette(aPalette);
		}
	}

	public FGEPaintManager getPaintManager() {
		if (getDrawingView() != null)
			return getDrawingView().getPaintManager();
		return null;
	}

	public void enablePaintingCache() {
		getPaintManager().enablePaintingCache();
	}

	public void disablePaintingCache() {
		getPaintManager().disablePaintingCache();
	}

	public String getToolTipText() {
		if (getFocusedObjects().size() > 0) {
			GraphicalRepresentation<?> gr = getFocusedObjects().firstElement();
			if (gr.getToolTipText() != null) {
				// logger.info("getToolTipText() ? return "+gr.getToolTipText());
				return gr.getToolTipText();
			}
		}
		// logger.info("getToolTipText() ? return null");
		return null;
	}

	public void delete() {
		if (drawing instanceof DefaultDrawing<?>) {
			((DefaultDrawing<?>) drawing).deleteObserver(this);
		}
		if (drawingView != null) {
			drawingView.delete();
		}
		storedSelection = null;
		drawingView = null;
	}

	public FGEPoint getLastClickedPoint() {
		return lastClickedPoint;
	}

	public void setLastClickedPoint(FGEPoint lastClickedPoint) {
		this.lastClickedPoint = lastClickedPoint;
	}

	public GraphicalRepresentation<?> getLastSelectedGR() {
		return lastSelectedGR;
	}

	public void setLastSelectedGR(GraphicalRepresentation<?> lastSelectedGR) {
		this.lastSelectedGR = lastSelectedGR;
	}

	private Vector<Object> storedSelection;

	private void restoreStoredSelection() {
		if (storedSelection == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Cannot restore null selection");
			}
			return;
		}
		try {
			for (Object o : storedSelection) {
				GraphicalRepresentation<?> gr = getGraphicalRepresentation(o);
				if (gr != null) {
					addToSelectedObjects(gr);
				}
			}
		} finally {
			storedSelection = null;
		}
	}

	private void storeCurrentSelection() {
		if (storedSelection != null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Cannot store selection when there is already a stored selection");
			}
			return;
		}
		storedSelection = new Vector<Object>();
		for (GraphicalRepresentation<?> gr : getSelectedObjects()) {
			storedSelection.add(gr.getDrawable());
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o == getDrawing()) {
			if (arg instanceof GraphicalObjectsHierarchyRebuildStarted) {
				storeCurrentSelection();
			} else if (arg instanceof GraphicalObjectsHierarchyRebuildEnded) {
				restoreStoredSelection();
			}
		}
	}

	// Override when required
	public void notifyWillMove(MoveInfo currentMove) {
	}

	// Override when required
	public void notifyHasMoved(MoveInfo currentMove) {
	}

	public void upKeyPressed() {
		// System.out.println("Up");
		keyDrivenMove(0, -1);
	}

	public void downKeyPressed() {
		// System.out.println("Down");
		keyDrivenMove(0, 1);
	}

	public void leftKeyPressed() {
		// System.out.println("Left");
		keyDrivenMove(-1, 0);
	}

	public void rightKeyPressed() {
		// System.out.println("Right");
		keyDrivenMove(1, 0);
	}

	private MoveInfo keyDrivenMovingSession;
	private KeyDrivenMovingSessionTimer keyDrivenMovingSessionTimer = null;

	private synchronized boolean keyDrivenMove(int deltaX, int deltaY) {
		if (keyDrivenMovingSessionTimer == null && getFirstSelectedShape() != null) {
			// System.out.println("BEGIN to move with keyboard");
			startKeyDrivenMovingSession();
			doMoveInSession(deltaX, deltaY);
			return true;
		} else if (keyDrivenMovingSessionTimer != null) {
			doMoveInSession(deltaX, deltaY);
			return true;
		}
		return false;
	}

	public void doMoveInSession(int deltaX, int deltaY) {
		keyDrivenMovingSessionTimer.typed();
		Point newLocation = keyDrivenMovingSession.getCurrentLocationInDrawingView();
		newLocation.x += deltaX;
		newLocation.y += deltaY;
		keyDrivenMovingSession.moveTo(newLocation);
	}

	private synchronized void startKeyDrivenMovingSession() {
		keyDrivenMovingSessionTimer = new KeyDrivenMovingSessionTimer();
		keyDrivenMovingSessionTimer.start();

		ShapeGraphicalRepresentation movedObject = getFirstSelectedShape();

		keyDrivenMovingSession = new MoveInfo(movedObject, this);

		notifyWillMove(keyDrivenMovingSession);
	}

	private synchronized void stopKeyDrivenMovingSession() {
		keyDrivenMovingSessionTimer = null;
		notifyHasMoved(keyDrivenMovingSession);
		keyDrivenMovingSession = null;
	}

	private class KeyDrivenMovingSessionTimer extends Thread {
		boolean typed = false;

		public KeyDrivenMovingSessionTimer() {
			typed = true;
		}

		@Override
		public void run() {
			while (typed) {
				synchronized (this) {
					typed = false;
				}
				try {
					sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					stopKeyDrivenMovingSession();
				}
			});
		}

		public synchronized void typed() {
			typed = true;
			// System.out.println("Tiens on retape sur le clavier");
		}
	}
}
