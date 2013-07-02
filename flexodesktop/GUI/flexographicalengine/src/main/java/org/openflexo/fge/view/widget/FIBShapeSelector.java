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
package org.openflexo.fge.view.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeSupport;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.shapes.Arc;
import org.openflexo.fge.shapes.Circle;
import org.openflexo.fge.shapes.Losange;
import org.openflexo.fge.shapes.Oval;
import org.openflexo.fge.shapes.Polygon;
import org.openflexo.fge.shapes.Rectangle;
import org.openflexo.fge.shapes.RegularPolygon;
import org.openflexo.fge.shapes.Shape;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.fge.shapes.Square;
import org.openflexo.fge.shapes.Star;
import org.openflexo.fge.shapes.Triangle;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent;
import org.openflexo.fib.view.FIBView;
import org.openflexo.fib.view.widget.FIBCustomWidget;
import org.openflexo.swing.CustomPopup;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * Widget allowing to view and edit a BackgroundStyle
 * 
 * @author sguerin
 * 
 */
public class FIBShapeSelector extends CustomPopup<Shape> implements FIBCustomComponent<Shape, FIBShapeSelector> {

	static final Logger logger = Logger.getLogger(FIBShapeSelector.class.getPackage().getName());

	public static FileResource FIB_FILE = new FileResource("Fib/ShapeSelectorPanel.fib");

	private static final Color DEFAULT_COLOR1 = Color.RED;
	private static final Color DEFAULT_COLOR2 = Color.WHITE;

	private static final FGEModelFactory SHAPE_FACTORY = FGEUtils.TOOLS_FACTORY;

	private Shape _revertValue;

	protected ShapeDetailsPanel _selectorPanel;
	private ShapePreviewPanel frontComponent;

	public FIBShapeSelector(Shape editedObject) {
		super(editedObject);
		setRevertValue(editedObject != null ? editedObject.clone() : null);
		setFocusable(true);
	}

	@Override
	public void delete() {
		super.delete();
		if (_selectorPanel != null) {
			_selectorPanel.delete();
		}
	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
	}

	@Override
	public void setRevertValue(Shape oldValue) {
		// WARNING: we need here to clone to keep track back of previous data
		// !!!
		if (oldValue != null) {
			_revertValue = oldValue.clone();
		} else {
			_revertValue = null;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Sets revert value to " + _revertValue);
		}
	}

	@Override
	public Shape getRevertValue() {
		return _revertValue;
	}

	@Override
	protected ResizablePanel createCustomPanel(Shape editedObject) {
		_selectorPanel = makeCustomPanel(editedObject);
		return _selectorPanel;
	}

	protected ShapeDetailsPanel makeCustomPanel(Shape editedObject) {
		return new ShapeDetailsPanel(editedObject);
	}

	@Override
	public void updateCustomPanel(Shape editedObject) {
		if (_selectorPanel != null) {
			_selectorPanel.update();
		}
		getFrontComponent().setShape(editedObject);
		// getFrontComponent().update();
	}

	/**
	 * Convenient class use to manipulate BackgroundStyle
	 * 
	 * @author sylvain
	 * 
	 */
	public static class ShapeFactory implements HasPropertyChangeSupport {
		private static final String DELETED = "deleted";

		private Shape shape;

		private Rectangle rectangle;
		private Square square;
		private Polygon polygon;
		private RegularPolygon regularPolygon;
		private Losange losange;
		private Triangle triangle;
		private Oval oval;
		private Circle circle;
		private Arc arc;
		private Star star;

		private PropertyChangeSupport pcSupport;

		public ShapeFactory(Shape shape) {
			pcSupport = new PropertyChangeSupport(this);
			this.shape = shape;
		}

		@Override
		public PropertyChangeSupport getPropertyChangeSupport() {
			return pcSupport;
		}

		public void delete() {
			pcSupport.firePropertyChange(DELETED, false, true);
			pcSupport = null;
		}

		@Override
		public String getDeletedProperty() {
			return DELETED;
		}

		public Shape getShape() {
			return shape;
		}

		public void setShape(Shape shape) {
			Shape oldShape = this.shape;
			this.shape = shape;
			pcSupport.firePropertyChange("shape", shape, shape);
		}

		public ShapeType getShapeType() {
			if (shape != null) {
				return shape.getShapeType();
			}
			return null;
		}

		public void setShapeType(ShapeType shapeType) {
			// logger.info("setBackgroundStyleType with " +
			// backgroundStyleType);
			ShapeType oldShapeType = getShapeType();

			if (oldShapeType != shapeType) {

				// System.out.println("set shape type to " + shapeType);

				switch (shapeType) {
				case RECTANGLE:
					if (rectangle == null) {
						rectangle = (Rectangle) SHAPE_FACTORY.makeShape(shapeType, null);
					}
					shape = rectangle;
					break;
				case SQUARE:
					if (square == null) {
						square = (Square) SHAPE_FACTORY.makeShape(shapeType, null);
					}
					shape = square;
					break;
				case CUSTOM_POLYGON:
					if (polygon == null) {
						polygon = SHAPE_FACTORY.makePolygon(null, new FGEPoint(0.1, 0.1), new FGEPoint(0.3, 0.9), new FGEPoint(0.9, 0.3));
					}
					shape = polygon;
					break;
				case POLYGON:
					if (regularPolygon == null) {
						regularPolygon = (RegularPolygon) SHAPE_FACTORY.makeShape(shapeType, null);
					}
					shape = regularPolygon;
					break;
				case TRIANGLE:
					if (triangle == null) {
						triangle = (Triangle) SHAPE_FACTORY.makeShape(shapeType, null);
					}
					shape = triangle;
					break;
				case LOSANGE:
					if (losange == null) {
						losange = (Losange) SHAPE_FACTORY.makeShape(shapeType, null);
					}
					shape = losange;
					break;
				case OVAL:
					if (oval == null) {
						oval = (Oval) SHAPE_FACTORY.makeShape(shapeType, null);
					}
					shape = oval;
					break;
				case CIRCLE:
					if (circle == null) {
						circle = (Circle) SHAPE_FACTORY.makeShape(shapeType, null);
					}
					shape = circle;
					break;
				case ARC:
					if (arc == null) {
						arc = (Arc) SHAPE_FACTORY.makeShape(shapeType, null);
					}
					shape = arc;
					break;
				case STAR:
					if (star == null) {
						star = (Star) SHAPE_FACTORY.makeShape(shapeType, null);
					}
					shape = star;
					break;
				default:
					shape = SHAPE_FACTORY.makeShape(shapeType, null);
				}

				pcSupport.firePropertyChange("shapeType", oldShapeType, getShapeType());
			}
		}
	}

	public class ShapeDetailsPanel extends ResizablePanel {
		private FIBComponent fibComponent;
		private FIBView fibView;
		private CustomFIBController controller;
		private ShapeFactory shapeFactory;

		protected ShapeDetailsPanel(Shape backgroundStyle) {
			super();

			shapeFactory = new ShapeFactory(backgroundStyle);
			fibComponent = FIBLibrary.instance().retrieveFIBComponent(FIB_FILE);
			controller = new CustomFIBController(fibComponent);
			fibView = controller.buildView(fibComponent);

			controller.setDataObject(shapeFactory);

			setLayout(new BorderLayout());
			add(fibView.getResultingJComponent(), BorderLayout.CENTER);

		}

		public void update() {
			// logger.info("Update with " + getEditedObject());
			shapeFactory.setShape(getEditedObject());
			controller.setDataObject(shapeFactory, true);
		}

		@Override
		public Dimension getDefaultSize() {
			return new Dimension(fibComponent.getWidth(), fibComponent.getHeight());
		}

		public void delete() {
			controller.delete();
			fibView.delete();
			shapeFactory.delete();
			fibComponent = null;
			controller = null;
			fibView = null;
			shapeFactory = null;
		}

		public class CustomFIBController extends FIBController {
			public CustomFIBController(FIBComponent component) {
				super(component);
			}

			public void apply() {
				setEditedObject(shapeFactory.getShape());
				FIBShapeSelector.this.apply();
			}

			public void cancel() {
				FIBShapeSelector.this.cancel();
			}

			public void shapeChanged() {

				getFrontComponent().setShape(shapeFactory.getShape());
				// getFrontComponent().update();

				FIBView previewComponent = viewForComponent(fibComponent.getComponentNamed("PreviewPanel"));
				if (previewComponent instanceof FIBCustomWidget) {
					JComponent customComponent = ((FIBCustomWidget) previewComponent).getJComponent();
					if (customComponent instanceof ShapePreviewPanel) {
						((ShapePreviewPanel) customComponent).setShape(shapeFactory.getShape());
						// ((ShapePreviewPanel) customComponent).update();
					}
				}
				notifyApplyPerformed();
			}

		}

	}

	/*
	 * @Override public void setEditedObject(BackgroundStyle object) {
	 * logger.info("setEditedObject with "+object);
	 * super.setEditedObject(object); }
	 */

	@Override
	public void apply() {
		setRevertValue(getEditedObject() != null ? getEditedObject().clone() : null);
		closePopup();
		super.apply();
	}

	@Override
	public void cancel() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("CANCEL: revert to " + getRevertValue());
		}
		setEditedObject(getRevertValue());
		closePopup();
		super.cancel();
	}

	@Override
	protected void deletePopup() {
		if (_selectorPanel != null) {
			_selectorPanel.delete();
		}
		_selectorPanel = null;
		super.deletePopup();
	}

	/*
	 * protected void pointerLeavesPopup() { cancel(); }
	 */

	public ShapeDetailsPanel getSelectorPanel() {
		return _selectorPanel;
	}

	@Override
	protected ShapePreviewPanel buildFrontComponent() {
		frontComponent = new ShapePreviewPanel(getEditedObject());
		frontComponent.setBorderSize(1);
		frontComponent.setPanelWidth(40);
		frontComponent.setPanelHeight(19);
		return frontComponent;
	}

	@Override
	public ShapePreviewPanel getFrontComponent() {
		return (ShapePreviewPanel) super.getFrontComponent();
	}

	/*
	 * @Override protected Border getDownButtonBorder() { return
	 * BorderFactory.createCompoundBorder(
	 * BorderFactory.createEmptyBorder(1,1,1,1),
	 * BorderFactory.createRaisedBevelBorder()); //return
	 * BorderFactory.createRaisedBevelBorder(); //return
	 * BorderFactory.createLoweredBevelBorder() //return
	 * BorderFactory.createEtchedBorder(); //return
	 * BorderFactory.createBevelBorder(BevelBorder.LOWERED); //return
	 * BorderFactory.createBevelBorder(BevelBorder.LOWERED); }
	 */

	@Override
	public FIBShapeSelector getJComponent() {
		return this;
	}

	@Override
	public Class<Shape> getRepresentedType() {
		return Shape.class;
	}

}
