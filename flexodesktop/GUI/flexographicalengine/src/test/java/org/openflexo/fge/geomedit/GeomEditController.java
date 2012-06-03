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
package org.openflexo.fge.geomedit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geomedit.edition.CreateBandFromLines;
import org.openflexo.fge.geomedit.edition.CreateCircleWithCenterAndPoint;
import org.openflexo.fge.geomedit.edition.CreateCubicCurveFromFourPoints;
import org.openflexo.fge.geomedit.edition.CreateCurveWithNPoints;
import org.openflexo.fge.geomedit.edition.CreateHalfBandWithLines;
import org.openflexo.fge.geomedit.edition.CreateHalfLineFromPoints;
import org.openflexo.fge.geomedit.edition.CreateHalfPlaneWithLineAndPoint;
import org.openflexo.fge.geomedit.edition.CreateHorizontalLineWithPoint;
import org.openflexo.fge.geomedit.edition.CreateIntersection;
import org.openflexo.fge.geomedit.edition.CreateLineFromPoints;
import org.openflexo.fge.geomedit.edition.CreateOrthogonalLineWithPoint;
import org.openflexo.fge.geomedit.edition.CreateParallelLineWithPoint;
import org.openflexo.fge.geomedit.edition.CreatePoint;
import org.openflexo.fge.geomedit.edition.CreatePointMiddleOfPoints;
import org.openflexo.fge.geomedit.edition.CreatePointSymetricOfPoint;
import org.openflexo.fge.geomedit.edition.CreatePolygonWithNPoints;
import org.openflexo.fge.geomedit.edition.CreatePolylinWithNPoints;
import org.openflexo.fge.geomedit.edition.CreateQuadCurveFromThreePoints;
import org.openflexo.fge.geomedit.edition.CreateRectPolylinWithStartAndEndArea;
import org.openflexo.fge.geomedit.edition.CreateRectangleFromPoints;
import org.openflexo.fge.geomedit.edition.CreateRotatedLineWithPoint;
import org.openflexo.fge.geomedit.edition.CreateRoundRectangleFromPoints;
import org.openflexo.fge.geomedit.edition.CreateSegmentFromPoints;
import org.openflexo.fge.geomedit.edition.CreateTangentLineWithCircleAndPoint;
import org.openflexo.fge.geomedit.edition.CreateVerticalLineWithPoint;
import org.openflexo.fge.geomedit.edition.Edition;
import org.openflexo.fge.geomedit.edition.EditionInput;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.inspector.selection.EmptySelection;
import org.openflexo.inspector.selection.MultipleSelection;
import org.openflexo.inspector.selection.UniqueSelection;
import org.openflexo.logging.FlexoLogger;

public class GeomEditController extends DrawingController<GeometricDrawing> implements TreeSelectionListener {
	private static final Logger logger = FlexoLogger.getLogger(GeomEditController.class.getPackage().getName());

	private JPopupMenu contextualMenu;
	private GraphicalRepresentation<?> contextualMenuInvoker;
	private Point contextualMenuClickedPoint;

	private GeometricObject copiedShape;

	private Edition currentEdition = null;

	private DefaultTreeModel treeModel;

	private JTree tree;
	private JPanel controlPanel;
	private JPanel availableMethodsPanel;
	private JLabel editionLabel;
	private JButton cancelButton;
	private JLabel positionLabel;

	private String NO_EDITION_STRING = "No edition";

	public GeomEditController(final GeometricDrawing aDrawing) {
		super(aDrawing);

		// !!!!! TAKE CARE !!!!!
		// When i tried to activate painting cache,
		// application was stuck in a call to fillRect() in SunGraphics2D while obtaining cache (print)
		// I investigated and found that default width and height values (10000) were too much
		// and freezed application in SUN's code (everything's ok with 1000 value for example)
		// I was disgusted and stopped further investigation, but i strongly recommand not to use cache
		// in the context of GeomEdit application

		treeModel = new DefaultTreeModel(aDrawing.getModel());
		tree = new JTree(treeModel);
		tree.setCellRenderer(new DefaultTreeCellRenderer() {
			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf,
					int row, boolean hasFocus) {
				JLabel returned = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
				if (value instanceof GeometricSet) {
					returned.setText(((GeometricSet) value).getTitle());
				} else if (value instanceof GeometricObject) {
					returned.setText(((GeometricObject) value).name);
				}
				return returned;
			}
		});
		tree.addTreeSelectionListener(this);

		controlPanel = new JPanel(new BorderLayout());
		availableMethodsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		controlPanel.add(availableMethodsPanel, BorderLayout.WEST);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetCurrentInput();
			}
		});
		controlPanel.add(cancelButton, BorderLayout.EAST);

		controlPanel.add(availableMethodsPanel, BorderLayout.WEST);

		editionLabel = new JLabel(NO_EDITION_STRING);

		positionLabel = new JLabel("                       ");

		resetCurrentInput();

		contextualMenu = new JPopupMenu();

		JMenu createPointItem = new JMenu("Create point");

		JMenuItem createExplicitPoint = new JMenuItem("As explicit position");
		createExplicitPoint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreatePoint(GeomEditController.this));
			}
		});
		createPointItem.add(createExplicitPoint);

		JMenuItem createPointAsMiddleFromPointsItem = new JMenuItem("As middle of two other points");
		createPointAsMiddleFromPointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreatePointMiddleOfPoints(GeomEditController.this));
			}
		});
		createPointItem.add(createPointAsMiddleFromPointsItem);

		JMenuItem createPointSymetricOfPointItem = new JMenuItem("Symetric to an other point");
		createPointSymetricOfPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreatePointSymetricOfPoint(GeomEditController.this));
			}
		});
		createPointItem.add(createPointSymetricOfPointItem);

		contextualMenu.add(createPointItem);

		JMenu createLineItem = new JMenu("Create line");

		JMenuItem createLineFromPointsItem = new JMenuItem("From points");
		createLineFromPointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateLineFromPoints(GeomEditController.this));
			}
		});
		createLineItem.add(createLineFromPointsItem);

		JMenuItem createHorizontalLineWithPointItem = new JMenuItem("Horizontal crossing point");
		createHorizontalLineWithPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateHorizontalLineWithPoint(GeomEditController.this));
			}
		});
		createLineItem.add(createHorizontalLineWithPointItem);

		JMenuItem createVerticalLineWithPointItem = new JMenuItem("Vertical crossing point");
		createVerticalLineWithPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateVerticalLineWithPoint(GeomEditController.this));
			}
		});
		createLineItem.add(createVerticalLineWithPointItem);

		JMenuItem createParallelLineWithPointItem = new JMenuItem("Parallel crossing point");
		createParallelLineWithPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateParallelLineWithPoint(GeomEditController.this));
			}
		});
		createLineItem.add(createParallelLineWithPointItem);

		JMenuItem createOrthogonalLineWithPointItem = new JMenuItem("Orthogonal crossing point");
		createOrthogonalLineWithPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateOrthogonalLineWithPoint(GeomEditController.this));
			}
		});
		createLineItem.add(createOrthogonalLineWithPointItem);

		JMenuItem createRotatedLineWithPointItem = new JMenuItem("Rotated line crossing point");
		createRotatedLineWithPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateRotatedLineWithPoint(GeomEditController.this));
			}
		});
		createLineItem.add(createRotatedLineWithPointItem);

		JMenuItem createTangentLineWithCircleAndPointItem = new JMenuItem("Tangent to a circle and crossing point");
		createTangentLineWithCircleAndPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateTangentLineWithCircleAndPoint(GeomEditController.this));
			}
		});
		createLineItem.add(createTangentLineWithCircleAndPointItem);

		contextualMenu.add(createLineItem);

		JMenu createHalfLineItem = new JMenu("Create half-line");

		JMenuItem createHalfLineFromPointsItem = new JMenuItem("From points");
		createHalfLineFromPointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateHalfLineFromPoints(GeomEditController.this));
			}
		});
		createHalfLineItem.add(createHalfLineFromPointsItem);

		contextualMenu.add(createHalfLineItem);

		JMenu createSegmentItem = new JMenu("Create segment");

		JMenuItem createSegmentFromPointsItem = new JMenuItem("From points");
		createSegmentFromPointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateSegmentFromPoints(GeomEditController.this));
			}
		});
		createSegmentItem.add(createSegmentFromPointsItem);

		contextualMenu.add(createSegmentItem);

		JMenu createRectangleItem = new JMenu("Create rectangle");
		JMenuItem createRectangleFromPointsItem = new JMenuItem("From points");
		createRectangleFromPointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateRectangleFromPoints(GeomEditController.this));
			}
		});
		createRectangleItem.add(createRectangleFromPointsItem);

		contextualMenu.add(createRectangleItem);

		JMenu createRoundRectangleItem = new JMenu("Create rounded rectangle");
		JMenuItem createRoundRectangleFromPointsItem = new JMenuItem("From points");
		createRoundRectangleFromPointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateRoundRectangleFromPoints(GeomEditController.this));
			}
		});
		createRoundRectangleItem.add(createRoundRectangleFromPointsItem);

		contextualMenu.add(createRoundRectangleItem);

		JMenu createCircleItem = new JMenu("Create circle");

		JMenuItem createCircleWithCenterAndPointItem = new JMenuItem("From center and point");
		createCircleWithCenterAndPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateCircleWithCenterAndPoint(GeomEditController.this));
			}
		});
		createCircleItem.add(createCircleWithCenterAndPointItem);

		contextualMenu.add(createCircleItem);

		JMenu createPolygonItem = new JMenu("Create polygon");

		JMenuItem createPolygonWithNPointsItem = new JMenuItem("From points");
		createPolygonWithNPointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreatePolygonWithNPoints(GeomEditController.this));
			}
		});
		createPolygonItem.add(createPolygonWithNPointsItem);

		contextualMenu.add(createPolygonItem);

		JMenu createPolylinItem = new JMenu("Create polylin");

		JMenuItem createRectPolylinWithStartAndEndAreaItem = new JMenuItem("From start and end area");
		createRectPolylinWithStartAndEndAreaItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateRectPolylinWithStartAndEndArea(GeomEditController.this));
			}
		});
		createPolylinItem.add(createRectPolylinWithStartAndEndAreaItem);

		JMenuItem createPolylinFromNPointsItem = new JMenuItem("From points");
		createPolylinFromNPointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreatePolylinWithNPoints(GeomEditController.this));
			}
		});
		createPolylinItem.add(createPolylinFromNPointsItem);

		contextualMenu.add(createPolylinItem);

		JMenu createCurveItem = new JMenu("Create curve");

		JMenuItem createQuadCurveWith3PointsItem = new JMenuItem("Quadradic curve with 3 points");
		createQuadCurveWith3PointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateQuadCurveFromThreePoints(GeomEditController.this));
			}
		});
		createCurveItem.add(createQuadCurveWith3PointsItem);

		JMenuItem createCubicCurveWith4PointsItem = new JMenuItem("Cubic curve with 4 points");
		createCubicCurveWith4PointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateCubicCurveFromFourPoints(GeomEditController.this));
			}
		});
		createCurveItem.add(createCubicCurveWith4PointsItem);

		JMenuItem createCurveWithNPointsItem = new JMenuItem("Complex curve crossing n points");
		createCurveWithNPointsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateCurveWithNPoints(GeomEditController.this));
			}
		});
		createCurveItem.add(createCurveWithNPointsItem);

		contextualMenu.add(createCurveItem);

		JMenu createHalfPlaneItem = new JMenu("Create half-plane");

		JMenuItem createHalfPlaneWithLineAndPointItem = new JMenuItem("From line and point");
		createHalfPlaneWithLineAndPointItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateHalfPlaneWithLineAndPoint(GeomEditController.this));
			}
		});
		createHalfPlaneItem.add(createHalfPlaneWithLineAndPointItem);

		contextualMenu.add(createHalfPlaneItem);

		JMenu createBandItem = new JMenu("Create band");

		JMenuItem createBandFromLinesItem = new JMenuItem("From lines");
		createBandFromLinesItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateBandFromLines(GeomEditController.this));
			}
		});
		createBandItem.add(createBandFromLinesItem);

		contextualMenu.add(createBandItem);

		JMenu createHalfBandItem = new JMenu("Create half band");

		JMenuItem createHalfBandFromLinesItem = new JMenuItem("From lines");
		createHalfBandFromLinesItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateHalfBandWithLines(GeomEditController.this));
			}
		});
		createHalfBandItem.add(createHalfBandFromLinesItem);

		contextualMenu.add(createHalfBandItem);

		contextualMenu.addSeparator();

		JMenuItem createIntersectionItem = new JMenuItem("Create intersection");
		createIntersectionItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCurrentEdition(new CreateIntersection(GeomEditController.this));
			}
		});
		contextualMenu.add(createIntersectionItem);

		contextualMenu.addSeparator();
		JMenuItem copyItem = new JMenuItem("Copy");
		copyItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copy();
			}
		});
		contextualMenu.add(copyItem);
		JMenuItem pasteItem = new JMenuItem("Paste");
		pasteItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				paste();
			}
		});
		contextualMenu.add(pasteItem);
		JMenuItem cutItem = new JMenuItem("Cut");
		cutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cut();
			}
		});
		contextualMenu.add(cutItem);
	}

	public void showContextualMenu(GraphicalRepresentation gr, Point p) {
		contextualMenuInvoker = gr;
		contextualMenuClickedPoint = p;
		contextualMenu.show(/*(Component)view*/getDrawingView(), p.x, p.y);
	}

	@Override
	public void addToSelectedObjects(GraphicalRepresentation anObject) {
		super.addToSelectedObjects(anObject);
		if (getSelectedObjects().size() == 1) {
			setChanged();
			notifyObservers(new UniqueSelection(getSelectedObjects().get(0), null));
		} else {
			setChanged();
			notifyObservers(new MultipleSelection());
		}
		tree.removeTreeSelectionListener(this);
		Object[] path = { getDrawing().getModel(), anObject.getDrawable() };
		tree.addSelectionPath(new TreePath(path));
		tree.addTreeSelectionListener(this);
	}

	@Override
	public void removeFromSelectedObjects(GraphicalRepresentation anObject) {
		super.removeFromSelectedObjects(anObject);
		if (getSelectedObjects().size() == 1) {
			setChanged();
			notifyObservers(new UniqueSelection(getSelectedObjects().get(0), null));
		} else {
			setChanged();
			notifyObservers(new MultipleSelection());
		}
		tree.removeTreeSelectionListener(this);
		Object[] path = { getDrawing().getModel(), anObject.getDrawable() };
		tree.removeSelectionPath(new TreePath(path));
		tree.addTreeSelectionListener(this);
	}

	@Override
	public void clearSelection() {
		super.clearSelection();
		notifyObservers(new EmptySelection());
		tree.removeTreeSelectionListener(this);
		tree.clearSelection();
		tree.addTreeSelectionListener(this);
	}

	@Override
	public void selectDrawing() {
		super.selectDrawing();
		setChanged();
		notifyObservers(new UniqueSelection(getDrawingGraphicalRepresentation(), null));
	}

	@Override
	public DrawingView<GeometricDrawing> makeDrawingView(GeometricDrawing drawing) {
		return new GeometricDrawingView(drawing, this);
	}

	@Override
	public GeometricDrawingView getDrawingView() {
		return (GeometricDrawingView) super.getDrawingView();
	}

	public void copy() {
		/*	if (contextualMenuInvoker instanceof GeometricObjectGraphicalRepresentation) {
				copiedShape = (GeometricObject)(((GeometricObjectGraphicalRepresentation)getFocusedObjects().firstElement()).getDrawable().clone());
				System.out.println("Copied: "+copiedShape);
			}*/
	}

	public void paste() {
		System.out.println("Paste in " + contextualMenuInvoker.getDrawable());
	}

	public void cut() {

	}

	public Edition getCurrentEdition() {
		return currentEdition;
	}

	public void setCurrentEdition(Edition anEdition) {
		currentEdition = anEdition;
		if (anEdition != null) {
			updateCurrentInput();
		} else {
			editionLabel.setText(NO_EDITION_STRING);
			editionLabel.revalidate();
			editionLabel.repaint();
			resetCurrentInput();
		}
	}

	private EditionInput currentInput;

	public void updateCurrentInput() {
		cancelButton.setEnabled(true);
		// controlPanel.setVisible(true);
		currentInput = currentEdition.inputs.get(currentEdition.currentStep);
		currentInput.updateControlPanel(controlPanel, availableMethodsPanel);
		editionLabel.setText(currentEdition.getLabel() + ", " + currentInput.getInputLabel() + ", " + currentInput.getActiveMethodLabel()
				+ (currentInput.endOnRightClick() ? " (right-click to finish)" : ""));
		editionLabel.revalidate();
		editionLabel.repaint();
		if (contextualMenu.isShowing()) {
			contextualMenu.setVisible(false);
		}
		getDrawingView().enableEditionInputMethod(currentInput.getDerivedActiveMethod());
	}

	private void resetCurrentInput() {
		if (currentInput != null) {
			currentInput.resetControlPanel(controlPanel);
		}
		availableMethodsPanel.removeAll();
		availableMethodsPanel.revalidate();
		availableMethodsPanel.repaint();
		// controlPanel.setVisible(false);
		cancelButton.setEnabled(false);
		editionLabel.setText(NO_EDITION_STRING);
		editionLabel.revalidate();
		editionLabel.repaint();
		getDrawingView().disableEditionInputMethod();
		currentEdition = null;
		currentInput = null;
		getDrawingView().revalidate();
		getDrawingView().repaint();
	}

	public void currentInputGiven() {
		if (currentEdition.next()) {
			// Done
			resetCurrentInput();
		} else {
			// Switch to next input
			updateCurrentInput();
		}
	}

	public JPanel getControlPanel() {
		return controlPanel;
	}

	public JLabel getEditionLabel() {
		return editionLabel;
	}

	public JLabel getPositionLabel() {
		return positionLabel;
	}

	public JTree getTree() {
		return tree;
	}

	public void notifiedObjectAdded() {
		treeModel.reload();
	}

	public void notifiedObjectRemoved() {
		treeModel.reload();
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		for (TreePath path : e.getPaths()) {
			GraphicalRepresentation gr = null;
			if (path.getLastPathComponent() instanceof GeometricSet) {
				gr = ((GeometricSet) path.getLastPathComponent()).getGraphicalRepresentation();
			}
			if (path.getLastPathComponent() instanceof GeometricObject) {
				gr = ((GeometricObject) path.getLastPathComponent()).getGraphicalRepresentation();
			}
			if (gr != null) {
				if (e.isAddedPath(path)) {
					addToSelectedObjects(gr);
				} else {
					removeFromSelectedObjects(gr);
				}
			}
		}
	}

	/**
	 * Implements strategy to preferencially choose a control point or an other during focus retrieving strategy
	 * 
	 * @param cp1
	 * @param cp2
	 * @return
	 */
	@Override
	public ControlArea<?> preferredFocusedControlArea(ControlArea<?> cp1, ControlArea<?> cp2) {
		if (cp1 instanceof DraggableControlPoint && !(cp2 instanceof DraggableControlPoint)) {
			return cp1;
		}
		if (cp2 instanceof DraggableControlPoint && !(cp1 instanceof DraggableControlPoint)) {
			return cp2;
		}
		if (cp1.isDraggable() && !cp2.isDraggable()) {
			return cp1;
		}
		if (cp2.isDraggable() && !cp1.isDraggable()) {
			return cp2;
		}
		return super.preferredFocusedControlArea(cp1, cp2);
	}

}