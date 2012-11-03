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
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.BackgroundStyle.BackgroundStyleType;
import org.openflexo.fge.BackgroundStyle.ColorGradient.ColorGradientDirection;
import org.openflexo.fge.BackgroundStyle.Texture.TextureType;
import org.openflexo.fge.BackgroundStyleImpl;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.DrawingGraphicalRepresentationImpl;
import org.openflexo.fge.ForegroundStyleImpl;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShadowStyleImpl;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.ShapeBorder;
import org.openflexo.fge.ShapeGraphicalRepresentationImpl;
import org.openflexo.fge.controller.DrawingController;
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
public class FIBBackgroundStyleSelector extends CustomPopup<BackgroundStyle> implements
		FIBCustomComponent<BackgroundStyle, FIBBackgroundStyleSelector> {

	static final Logger logger = Logger.getLogger(FIBBackgroundStyleSelector.class.getPackage().getName());

	public static FileResource FIB_FILE = new FileResource("Fib/BackgroundStylePanel.fib");

	private static final Color DEFAULT_COLOR1 = Color.RED;
	private static final Color DEFAULT_COLOR2 = Color.WHITE;

	private BackgroundStyle _revertValue;

	protected BackgroundStyleDetailsPanel _selectorPanel;

	private BackgroundStylePreviewPanel backgroundStylePreviewPanel;

	public FIBBackgroundStyleSelector(BackgroundStyle editedObject) {
		super(editedObject);
		setRevertValue(editedObject != null ? editedObject.clone() : null);
		setFocusable(true);
	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
	}

	@Override
	public void delete() {
		super.delete();
		backgroundStylePreviewPanel.delete();
		if (_selectorPanel != null) {
			_selectorPanel.delete();
		}
	}

	/**
	 * Return a flag indicating if equals() method should be used to determine equality.<br>
	 * For the FIBForegroundStyleSelector implementation, we MUST return false, because we can otherwise switch between ForegroundStyle
	 * which are equals, and then start to share BackgroundStyle between many GraphicalRepresentation
	 * 
	 * @return false
	 */
	@Override
	public boolean useEqualsLookup() {
		return false;
	}

	@Override
	public void setRevertValue(BackgroundStyle oldValue) {
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
	public BackgroundStyle getRevertValue() {
		return _revertValue;
	}

	@Override
	protected ResizablePanel createCustomPanel(BackgroundStyle editedObject) {
		_selectorPanel = makeCustomPanel(editedObject);
		return _selectorPanel;
	}

	protected BackgroundStyleDetailsPanel makeCustomPanel(BackgroundStyle editedObject) {
		return new BackgroundStyleDetailsPanel(editedObject);
	}

	@Override
	public void updateCustomPanel(BackgroundStyle editedObject) {
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
	public static class BackgroundStyleFactory implements HasPropertyChangeSupport {
		private static final String DELETED = "deleted";
		private BackgroundStyle backgroundStyle;
		private Color color1 = Color.RED;
		private Color color2 = Color.WHITE;
		private ColorGradientDirection gradientDirection = ColorGradientDirection.NORTH_SOUTH;
		private TextureType textureType = TextureType.TEXTURE1;
		private File imageFile;
		private PropertyChangeSupport pcSupport;

		public BackgroundStyleFactory(BackgroundStyle backgroundStyle) {
			pcSupport = new PropertyChangeSupport(this);
			this.backgroundStyle = backgroundStyle;
		}

		@Override
		public PropertyChangeSupport getPropertyChangeSupport() {
			return pcSupport;
		}

		public void delete() {
			getPropertyChangeSupport().firePropertyChange(DELETED, false, true);
			pcSupport = null;
		}

		@Override
		public String getDeletedProperty() {
			return DELETED;
		}

		public BackgroundStyle getBackgroundStyle() {
			return backgroundStyle;
		}

		public void setBackgroundStyle(BackgroundStyle backgroundStyle) {
			BackgroundStyle oldBackgroundStyle = this.backgroundStyle;
			this.backgroundStyle = backgroundStyle;
			pcSupport.firePropertyChange("backgroundStyle", oldBackgroundStyle, backgroundStyle);
		}

		public BackgroundStyleType getBackgroundStyleType() {
			return backgroundStyle.getBackgroundStyleType();
		}

		public void setBackgroundStyleType(BackgroundStyleType backgroundStyleType) {
			// logger.info("setBackgroundStyleType with " + backgroundStyleType);
			BackgroundStyleType oldBackgroundStyleType = getBackgroundStyleType();
			switch (getBackgroundStyleType()) {
			case NONE:
				break;
			case COLOR:
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
				break;
			default:
				break;
			}

			switch (backgroundStyleType) {
			case NONE:
				backgroundStyle = BackgroundStyleImpl.makeEmptyBackground();
				break;
			case COLOR:
				backgroundStyle = BackgroundStyleImpl.makeColoredBackground(color1);
				break;
			case COLOR_GRADIENT:
				backgroundStyle = BackgroundStyleImpl.makeColorGradientBackground(color1, color2, gradientDirection);
				break;
			case TEXTURE:
				backgroundStyle = BackgroundStyleImpl.makeTexturedBackground(textureType, color1, color2);
				break;
			case IMAGE:
				backgroundStyle = BackgroundStyleImpl.makeImageBackground(imageFile);
				break;
			default:
				break;
			}

			pcSupport.firePropertyChange("backgroundStyleType", oldBackgroundStyleType, getBackgroundStyleType());
		}

	}

	public class BackgroundStyleDetailsPanel extends ResizablePanel {
		private FIBComponent fibComponent;
		private FIBView fibView;
		private CustomFIBController controller;
		private BackgroundStyleFactory bsFactory;

		protected BackgroundStyleDetailsPanel(BackgroundStyle backgroundStyle) {
			super();

			bsFactory = new BackgroundStyleFactory(backgroundStyle);
			fibComponent = FIBLibrary.instance().retrieveFIBComponent(FIB_FILE);
			controller = new CustomFIBController(fibComponent);
			fibView = controller.buildView(fibComponent);

			controller.setDataObject(bsFactory);

			setLayout(new BorderLayout());
			add(fibView.getResultingJComponent(), BorderLayout.CENTER);
		}

		public void update() {
			// logger.info("Update with " + getEditedObject());
			bsFactory.setBackgroundStyle(getEditedObject());
			controller.setDataObject(bsFactory, true);
		}

		@Override
		public Dimension getDefaultSize() {
			return new Dimension(fibComponent.getWidth(), fibComponent.getHeight());
		}

		public void delete() {
			controller.delete();
			fibView.delete();
			bsFactory.delete();
			fibComponent = null;
			controller = null;
			fibView = null;
			bsFactory = null;
		}

		public class CustomFIBController extends FIBController<BackgroundStyleFactory> {
			public CustomFIBController(FIBComponent component) {
				super(component);
			}

			public void apply() {
				setEditedObject(bsFactory.getBackgroundStyle());
				FIBBackgroundStyleSelector.this.apply();
			}

			public void cancel() {
				FIBBackgroundStyleSelector.this.cancel();
			}

			public void parameterChanged() {
				// System.out.println("parameterChanged() for "+bsFactory.getBackgroundStyle());
				setEditedObject(bsFactory.getBackgroundStyle());
				getFrontComponent().update();
				// notifyApplyPerformed();
			}

			public void backgroundStyleChanged() {
				// System.out.println("backgroundStyleChanged() for "+bsFactory.getBackgroundStyle());
				setEditedObject(bsFactory.getBackgroundStyle());
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

	public BackgroundStyleDetailsPanel getSelectorPanel() {
		return _selectorPanel;
	}

	@Override
	protected BackgroundStylePreviewPanel buildFrontComponent() {
		return backgroundStylePreviewPanel = new BackgroundStylePreviewPanel();
	}

	@Override
	public BackgroundStylePreviewPanel getFrontComponent() {
		return (BackgroundStylePreviewPanel) super.getFrontComponent();
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

	protected class BackgroundStylePreviewPanel extends JPanel {
		private Drawing drawing;
		private DrawingGraphicalRepresentation drawingGR;
		private DrawingController<?> controller;
		private Object rect;
		private ShapeGraphicalRepresentation rectGR;

		protected BackgroundStylePreviewPanel() {
			super(new BorderLayout());
			setBorder(BorderFactory.createEtchedBorder(Color.GRAY, Color.LIGHT_GRAY));
			// setBorder(BorderFactory.createEtchedBorder());
			setPreferredSize(new Dimension(40, 19));
			// setBackground(Color.WHITE);

			rect = new Object();

			final List<Object> singleton = Collections.singletonList(rect);

			drawing = new Drawing<BackgroundStylePreviewPanel>() {
				@Override
				public List<?> getContainedObjects(Object aDrawable) {
					if (aDrawable == BackgroundStylePreviewPanel.this) {
						return singleton;
					}
					return null;
				}

				@Override
				public Object getContainer(Object aDrawable) {
					if (aDrawable == rect) {
						return BackgroundStylePreviewPanel.this;
					}
					return null;
				}

				@Override
				public DrawingGraphicalRepresentation<BackgroundStylePreviewPanel> getDrawingGraphicalRepresentation() {
					return drawingGR;
				}

				@Override
				public <O> GraphicalRepresentation<O> getGraphicalRepresentation(O aDrawable) {
					if (aDrawable == BackgroundStylePreviewPanel.this) {
						return drawingGR;
					} else if (aDrawable == rect) {
						return rectGR;
					}
					return null;
				}

				@Override
				public BackgroundStylePreviewPanel getModel() {
					return BackgroundStylePreviewPanel.this;
				}

			};
			drawingGR = new DrawingGraphicalRepresentationImpl(drawing, false);
			drawingGR.setBackgroundColor(new Color(255, 255, 255));
			drawingGR.setWidth(35);
			drawingGR.setHeight(19);
			drawingGR.setDrawWorkingArea(false);
			rectGR = new ShapeGraphicalRepresentationImpl(ShapeType.RECTANGLE, rect, drawing);
			rectGR.setWidth(36);
			rectGR.setHeight(20);
			rectGR.setX(0);
			rectGR.setY(0);
			rectGR.setForeground(ForegroundStyleImpl.makeNone());
			rectGR.setBackground(getEditedObject() != null ? getEditedObject() : BackgroundStyleImpl.makeColoredBackground(DEFAULT_COLOR1));
			rectGR.setShadowStyle(ShadowStyleImpl.makeNone());
			rectGR.setIsSelectable(false);
			rectGR.setIsFocusable(false);
			rectGR.setIsReadOnly(true);
			rectGR.setBorder(new ShapeBorder(0, 0, 0, 0));
			rectGR.setValidated(true);

			controller = new DrawingController<Drawing<?>>(drawing);
			add(controller.getDrawingView());
		}

		public void delete() {
			drawingGR.delete();
			rectGR.delete();
			controller.delete();
			drawing = null;
			controller = null;
			drawingGR = null;
			rectGR = null;
			rect = null;
		}

		protected void update() {
			rectGR.setBackground(getEditedObject() != null ? getEditedObject() : BackgroundStyleImpl.makeColoredBackground(DEFAULT_COLOR1));
			// We do it later because producer of texture may not has finished its job
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (getEditedObject() == null) {
						return;
					}
					BackgroundStyle bg = getEditedObject();
					rectGR.setBackground(bg);
				}
			});
		}

	}

	@Override
	public FIBBackgroundStyleSelector getJComponent() {
		return this;
	}

	@Override
	public Class<BackgroundStyle> getRepresentedType() {
		return BackgroundStyle.class;
	}

}
