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
import java.util.ArrayList;
import java.util.Collections;
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

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.RootNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.cp.ConnectorAdjustingControlPoint;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.notifications.GraphicalObjectsHierarchyRebuildEnded;
import org.openflexo.fge.notifications.GraphicalObjectsHierarchyRebuildStarted;
import org.openflexo.fge.shapes.Shape;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.fge.view.ConnectorView;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.fge.view.FGEPaintManager;
import org.openflexo.fge.view.LabelView;
import org.openflexo.fge.view.ShapeView;

public class DrawingController<M> extends Observable implements Observer {

	private static final Logger logger = Logger.getLogger(DrawingController.class.getPackage().getName());

	private Drawing<M> drawing;
	private DrawingView<M> drawingView;

	public enum EditorTool {
		SelectionTool, DrawShapeTool, DrawConnectorTool, DrawTextTool
	}

	private EditorTool currentTool;

	private DrawShapeToolController<?> drawShapeToolController;
	private DrawShapeAction drawShapeAction;

	private ForegroundStyle currentForegroundStyle;
	private BackgroundStyle currentBackgroundStyle;
	private TextStyle currentTextStyle;
	private ShadowStyle currentShadowStyle;
	private Shape currentShape;

	private ScalePanel _scalePanel;
	private EditorToolbox toolbox;

	private GraphicalRepresentation focusedFloatingLabel;
	// private GraphicalRepresentation focusedObject;

	private Vector<GraphicalRepresentation> focusedObjects;
	private Vector<GraphicalRepresentation> selectedObjects;

	private LabelView _currentlyEditedLabel;

	private ControlArea<?> focusedControlArea;

	private double scale = 1.0;

	private Vector<DrawingPalette> palettes;

	private FGEPoint lastClickedPoint;
	private GraphicalRepresentation lastSelectedGR;

	/**
	 * This factory is the one which is used to creates and maintains object graph
	 */
	private FGEModelFactory factory;

	public DrawingController(Drawing<M> aDrawing, FGEModelFactory factory) {
		super();

		this.factory = factory;

		setCurrentTool(EditorTool.SelectionTool);
		currentForegroundStyle = factory.makeDefaultForegroundStyle();
		currentBackgroundStyle = factory.makeColoredBackground(FGEConstants.DEFAULT_BACKGROUND_COLOR);
		currentTextStyle = factory.makeDefaultTextStyle();
		currentShadowStyle = factory.makeDefaultShadowStyle();
		currentShape = factory.makeShape(ShapeType.RECTANGLE);

		toolbox = new EditorToolbox(this);

		drawing = aDrawing;
		if (drawing instanceof DrawingImpl<?>) {
			((DrawingImpl<?>) drawing).addObserver(this);
		}

		focusedObjects = new Vector<GraphicalRepresentation>();
		selectedObjects = new Vector<GraphicalRepresentation>();
		palettes = new Vector<DrawingPalette>();
		buildDrawingView();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Building DrawingController: " + this);
		}
	}

	public FGEModelFactory getFactory() {
		return factory;
	}

	public DrawingView<?> rebuildDrawingView() {
		if (drawingView != null) {
			drawingView.delete();
		}
		buildDrawingView();
		return drawingView;
	}

	private DrawingView<?> buildDrawingView() {
		drawingView = makeDrawingView(drawing.getRoot());
		for (DrawingTreeNode<?, ?> dtn : drawing.getRoot().getChildNodes()) {
			if (dtn instanceof ShapeNode) {
				ShapeView<?> v = recursivelyBuildShapeView((ShapeNode<?>) dtn);
				drawingView.add(v);
			} else if (dtn instanceof ConnectorNode) {
				ConnectorView<?> v = makeConnectorView((ConnectorNode<?>) dtn);
				drawingView.add(v);
			}
		}
		return drawingView;
	}

	private <O> ShapeView<?> recursivelyBuildShapeView(ShapeNode<O> shapeNode) {
		ShapeView<O> returned = makeShapeView(shapeNode);
		for (DrawingTreeNode<?, ?> dtn : shapeNode.getChildNodes()) {
			if (dtn instanceof ShapeNode) {
				ShapeView<?> v = recursivelyBuildShapeView((ShapeNode<?>) dtn);
				returned.add(v);
			} else if (dtn instanceof ConnectorNode) {
				ConnectorView<?> v = makeConnectorView((ConnectorNode<?>) dtn);
				returned.add(v);
			}
		}
		return returned;
	}

	// Override for a custom view managing
	public DrawingView<M> makeDrawingView(RootNode<M> rootNode) {
		return new DrawingView<M>(rootNode, this);
	}

	// Override for a custom view managing
	public <O> ShapeView<O> makeShapeView(ShapeNode<O> shapeNode) {
		return new ShapeView<O>(shapeNode, this);
	}

	// Override for a custom view managing
	public <O> ConnectorView<O> makeConnectorView(ConnectorNode<O> connectorNode) {
		return new ConnectorView<O>(connectorNode, this);
	}

	public DrawShapeToolController<?> getDrawShapeToolController() {
		return drawShapeToolController;
	}

	public EditorTool getCurrentTool() {
		return currentTool;
	}

	public void setCurrentTool(EditorTool aTool) {
		if (aTool != currentTool) {
			logger.fine("Switch to tool " + aTool);
			switch (aTool) {
			case SelectionTool:
				/*if (currentTool == EditorTool.DrawShapeTool && drawShapeToolController != null) {
					drawShapeToolController.makeNewShape();
				}*/
				break;
			case DrawShapeTool:
				// if (drawShapeAction != null) {
				drawShapeToolController = new DrawPolygonToolController(this, drawShapeAction);
				// }
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

	public Shape getCurrentShape() {
		return currentShape;
	}

	public void setCurrentShape(Shape currentShape) {
		this.currentShape = currentShape;
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
			super(/* new FlowLayout(FlowLayout.LEFT, 10, 0) */);
			setOpaque(false);
			scaleTF = new JTextField(5);
			int currentScale = (int) (getScale() * 100);
			scaleTF.setText(currentScale + "%");
			slider = new JSlider(SwingConstants.HORIZONTAL, 0, MAX_ZOOM_VALUE, currentScale);
			slider.setOpaque(false);
			slider.setMajorTickSpacing(100);
			slider.setMinorTickSpacing(20);
			slider.setPaintTicks(false/* true */);
			slider.setPaintLabels(false);
			slider.setBorder(BorderFactory.createEmptyBorder());
			sliderChangeListener = new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if (slider.getValue() > 0) {
						setScale((double) slider.getValue() / 100);
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
						setScale((double) newScale / 100);
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

	public Drawing<M> getDrawing() {
		return drawing;
	}

	/*public DrawingGraphicalRepresentation getDrawingGraphicalRepresentation() {
		return drawing.getDrawingGraphicalRepresentation();
	}*/

	public DrawingView getDrawingView() {
		return drawingView;
	}

	/*public <O> GraphicalRepresentation getGraphicalRepresentation(O drawable) {
		if (drawable == null) {
			return null;
		}
		return drawing.getGraphicalRepresentation(drawable);
	}*/

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
			 * if (aFocusedlabel == null) { focusedFloatingLabel = null;
			 * drawingView.getPaintManager().repaint(drawingView); } else if
			 * (focusedFloatingLabel != aFocusedlabel) { focusedFloatingLabel =
			 * aFocusedlabel;
			 * drawingView.getPaintManager().repaint(drawingView); }
			 */
		}
	}

	private ShapeGraphicalRepresentation getFirstSelectedShape() {
		for (GraphicalRepresentation gr : getSelectedObjects()) {
			if (gr instanceof ShapeGraphicalRepresentation) {
				return (ShapeGraphicalRepresentation) gr;
			}
		}
		return null;
	}

	public List<GraphicalRepresentation> getSelectedObjects() {
		return selectedObjects;
	}

	public void setSelectedObjects(List<? extends GraphicalRepresentation> someSelectedObjects) {
		stopEditionOfEditedLabelIfAny();
		if (someSelectedObjects == null) {
			setSelectedObjects(new ArrayList<GraphicalRepresentation>());
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
		setSelectedObjects(Collections.singletonList(aGraphicalRepresentation));
		if (getToolbox() != null) {
			getToolbox().update();
		}
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

	public void toggleSelection(GraphicalRepresentation aGraphicalRepresentation) {
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
			setFocusedObjects(Collections.<GraphicalRepresentation> emptyList());
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

		setFocusedObjects(Collections.singletonList(aGraphicalRepresentation));
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

	public void toggleFocusSelection(GraphicalRepresentation aGraphicalRepresentation) {
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

	public void setEditedLabel(LabelView aLabel) {
		stopEditionOfEditedLabelIfAny();
		_currentlyEditedLabel = aLabel;
	}

	public void resetEditedLabel(LabelView editedLabel) {
		if (_currentlyEditedLabel == editedLabel) {
			_currentlyEditedLabel = null;
		}
	}

	public boolean hasEditedLabel() {
		return _currentlyEditedLabel != null;
	}

	public LabelView getEditedLabel() {
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
		if (ca1.getNode().getGraphicalRepresentation().getLayer() == ca2.getNode().getGraphicalRepresentation().getLayer()) {
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
		return ca1.getNode().getGraphicalRepresentation().getLayer() > ca2.getNode().getGraphicalRepresentation().getLayer() ? ca1 : ca2;
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
		// if (getDrawingView() != null)
		// getDrawingView().registerPalette(aPalette);
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
		if (getDrawingView() != null) {
			return getDrawingView().getPaintManager();
		}
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
			GraphicalRepresentation gr = getFocusedObjects().firstElement();
			if (gr.getToolTipText() != null) {
				// logger.info("getToolTipText() ? return "+gr.getToolTipText());
				return gr.getToolTipText();
			}
		}
		// logger.info("getToolTipText() ? return null");
		return null;
	}

	public void delete() {
		if (drawing instanceof DrawingImpl<?>) {
			((DrawingImpl<?>) drawing).deleteObserver(this);
		}
		if (palettes != null) {
			for (DrawingPalette palette : palettes) {
				palette.delete();
			}
		}
		if (drawingView != null) {
			drawingView.delete();
		}
		if (toolbox != null) {
			toolbox.delete();
		}
		focusedObjects.clear();
		selectedObjects.clear();
		focusedControlArea = null;
		toolbox = null;
		palettes = null;
		storedSelection = null;
		drawingView = null;
	}

	public FGEPoint getLastClickedPoint() {
		return lastClickedPoint;
	}

	public void setLastClickedPoint(FGEPoint lastClickedPoint) {
		this.lastClickedPoint = lastClickedPoint;
	}

	public GraphicalRepresentation getLastSelectedGR() {
		return lastSelectedGR;
	}

	public void setLastSelectedGR(GraphicalRepresentation lastSelectedGR) {
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
				GraphicalRepresentation gr = getGraphicalRepresentation(o);
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
		for (GraphicalRepresentation gr : getSelectedObjects()) {
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

	/**
	 * Process 'UP' key pressed
	 * 
	 * @return boolean indicating if event was successfully processed
	 */
	public boolean upKeyPressed() {
		// System.out.println("Up");
		return getDrawing().isEditable() && keyDrivenMove(0, -1);
	}

	/**
	 * Process 'DOWN' key pressed
	 * 
	 * @return boolean indicating if event was successfully processed
	 */
	public boolean downKeyPressed() {
		// System.out.println("Down");
		return getDrawing().isEditable() && keyDrivenMove(0, 1);
	}

	/**
	 * Process 'LEFT' key pressed
	 * 
	 * @return boolean indicating if event was successfully processed
	 */
	public boolean leftKeyPressed() {
		// System.out.println("Left");
		return getDrawing().isEditable() && keyDrivenMove(-1, 0);
	}

	/**
	 * Process 'RIGHT' key pressed
	 * 
	 * @return boolean indicating if event was successfully processed
	 */
	public boolean rightKeyPressed() {
		// System.out.println("Right");
		return getDrawing().isEditable() && keyDrivenMove(1, 0);
	}

	private MoveInfo keyDrivenMovingSession;
	private KeyDrivenMovingSessionTimer keyDrivenMovingSessionTimer = null;

	private synchronized boolean keyDrivenMove(int deltaX, int deltaY) {
		if (keyDrivenMovingSessionTimer == null && getFirstSelectedShape() != null) {
			// System.out.println("BEGIN to move with keyboard");
			if (startKeyDrivenMovingSession()) {
				doMoveInSession(deltaX, deltaY);
				return true;
			}
			return false;
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

	private synchronized boolean startKeyDrivenMovingSession() {

		if (getFirstSelectedShape().getLocationConstraints() != LocationConstraints.UNMOVABLE) {

			keyDrivenMovingSessionTimer = new KeyDrivenMovingSessionTimer();
			keyDrivenMovingSessionTimer.start();
			ShapeGraphicalRepresentation movedObject = getFirstSelectedShape();
			keyDrivenMovingSession = new MoveInfo(movedObject, this);
			notifyWillMove(keyDrivenMovingSession);
			return true;
		} else {
			return false;
		}
	}

	private synchronized void stopKeyDrivenMovingSession() {
		keyDrivenMovingSessionTimer = null;
		notifyHasMoved(keyDrivenMovingSession);
		keyDrivenMovingSession = null;
	}

	private class KeyDrivenMovingSessionTimer extends Thread {
		volatile boolean typed = false;

		public KeyDrivenMovingSessionTimer() {
			typed = true;
		}

		@Override
		public void run() {
			while (typed) {
				typed = false;
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
