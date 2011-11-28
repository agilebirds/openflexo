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

import org.openflexo.fge.shapes.Shape;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent;
import org.openflexo.fib.view.FIBView;
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

	private Shape _revertValue;

	protected ShapeDetailsPanel _selectorPanel;

	public FIBShapeSelector(Shape editedObject) {
		super(editedObject);
		setRevertValue(editedObject != null ? editedObject.clone() : null);
		setFocusable(true);
	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
	}

	@Override
	public void setRevertValue(Shape oldValue) {
		// WARNING: we need here to clone to keep track back of previous data !!!
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
		getFrontComponent().update();
	}

	/**
	 * Convenient class use to manipulate BackgroundStyle
	 * 
	 * @author sylvain
	 * 
	 */
	public static class ShapeFactory implements HasPropertyChangeSupport {
		private Shape shape;
		/*private Color color1 = Color.RED;
		private Color color2 = Color.WHITE;
		private ColorGradientDirection gradientDirection = ColorGradientDirection.NORTH_SOUTH;
		private TextureType textureType = TextureType.TEXTURE1;
		private File imageFile;*/
		private PropertyChangeSupport pcSupport;

		public ShapeFactory(Shape shape) {
			pcSupport = new PropertyChangeSupport(this);
			this.shape = shape;
		}

		@Override
		public PropertyChangeSupport getPropertyChangeSupport() {
			return pcSupport;
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
			return shape.getShapeType();
		}

		public void setShapeType(ShapeType shapeType) {
			// logger.info("setBackgroundStyleType with " + backgroundStyleType);
			ShapeType oldShapeType = getShapeType();
			switch (getShapeType()) {
			case RECTANGLE:
				break;
			/*case COLOR:
				color1 = ((BackgroundStyle.Color) backgroundStyle).getColor();
				break;
			case COLOR_GRADIENT:
				color1 = ((BackgroundStyle.ColorGradient) backgroundStyle).getColor1();
				color2 = ((BackgroundStyle.ColorGradient) backgroundStyle).getColor2();
				gradientDirection = ((BackgroundStyle.ColorGradient) backgroundStyle).getDirection();
				break;
			case TEXTURE:
				color1 = ((BackgroundStyle.Texture) backgroundStyle).getColor1();
				color2 = ((BackgroundStyle.Texture) backgroundStyle).getColor2();
				textureType = ((BackgroundStyle.Texture) backgroundStyle).getTextureType();
				break;
			case IMAGE:
				imageFile = ((BackgroundStyle.BackgroundImage) backgroundStyle).getImageFile();
				break;*/
			default:
				break;
			}

			shape = Shape.makeShape(shapeType, null);

			pcSupport.firePropertyChange("shapeType", oldShapeType, getShapeType());
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
		}

		public class CustomFIBController extends FIBController<ShapeFactory> {
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

			public void parameterChanged() {
				// System.out.println("parameterChanged() for "+bsFactory.getBackgroundStyle());
				setEditedObject(shapeFactory.getShape());
				getFrontComponent().update();
				// notifyApplyPerformed();
			}

			public void shapeChanged() {
				// System.out.println("backgroundStyleChanged() for "+bsFactory.getBackgroundStyle());
				setEditedObject(shapeFactory.getShape());
				getFrontComponent().update();
				notifyApplyPerformed();
			}

		}

	}

	/* @Override
	 public void setEditedObject(BackgroundStyle object)
	 {
	 	logger.info("setEditedObject with "+object);
	 	super.setEditedObject(object);
	 }*/

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

	/*protected void pointerLeavesPopup()
	{
	   cancel();
	}*/

	public ShapeDetailsPanel getSelectorPanel() {
		return _selectorPanel;
	}

	@Override
	protected ShapePreviewPanel buildFrontComponent() {
		return new ShapePreviewPanel(getEditedObject());
	}

	@Override
	public ShapePreviewPanel getFrontComponent() {
		return (ShapePreviewPanel) super.getFrontComponent();
	}

	/*@Override
	protected Border getDownButtonBorder()
	{
		return BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(1,1,1,1),
				BorderFactory.createRaisedBevelBorder());
		//return BorderFactory.createRaisedBevelBorder();
		//return BorderFactory.createLoweredBevelBorder()
		//return BorderFactory.createEtchedBorder();
		//return BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		//return BorderFactory.createBevelBorder(BevelBorder.LOWERED);
	}*/

	@Override
	public FIBShapeSelector getJComponent() {
		return this;
	}

	@Override
	public Class<Shape> getRepresentedType() {
		return Shape.class;
	}

}
