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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.openflexo.fge.BackgroundImageBackgroundStyle;
import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.BackgroundStyle.BackgroundStyleType;
import org.openflexo.fge.ColorBackgroundStyle;
import org.openflexo.fge.ColorGradientBackgroundStyle;
import org.openflexo.fge.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.PersistenceMode;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GRStructureWalker;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.TextureBackgroundStyle;
import org.openflexo.fge.TextureBackgroundStyle.TextureType;
import org.openflexo.fge.controller.DrawingControllerImpl;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
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
@SuppressWarnings("serial")
public class FIBBackgroundStyleSelector extends CustomPopup<BackgroundStyle> implements
		FIBCustomComponent<BackgroundStyle, FIBBackgroundStyleSelector> {

	static final Logger logger = Logger.getLogger(FIBBackgroundStyleSelector.class.getPackage().getName());

	public static FileResource FIB_FILE = new FileResource("Fib/BackgroundStylePanel.fib");

	private static final Color DEFAULT_COLOR1 = Color.RED;
	// private static final Color DEFAULT_COLOR2 = Color.WHITE;

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
		private FGEModelFactory fgeFactory;

		public BackgroundStyleFactory(BackgroundStyle backgroundStyle) {
			pcSupport = new PropertyChangeSupport(this);
			this.backgroundStyle = backgroundStyle;
			if (backgroundStyle != null) {
				fgeFactory = backgroundStyle.getFactory();
			}
			if (fgeFactory == null) {
				logger.warning("Could not find any FGE factory, creating a default one");
				fgeFactory = FGEUtils.TOOLS_FACTORY;
			}
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

			if (oldBackgroundStyleType == backgroundStyleType) {
				return;
			}

			switch (getBackgroundStyleType()) {
			case NONE:
				break;
			case COLOR:
				color1 = ((ColorBackgroundStyle) backgroundStyle).getColor();
				break;
			case COLOR_GRADIENT:
				color1 = ((ColorGradientBackgroundStyle) backgroundStyle).getColor1();
				color2 = ((ColorGradientBackgroundStyle) backgroundStyle).getColor2();
				gradientDirection = ((ColorGradientBackgroundStyle) backgroundStyle).getDirection();
				break;
			case TEXTURE:
				color1 = ((TextureBackgroundStyle) backgroundStyle).getColor1();
				color2 = ((TextureBackgroundStyle) backgroundStyle).getColor2();
				textureType = ((TextureBackgroundStyle) backgroundStyle).getTextureType();
				break;
			case IMAGE:
				imageFile = ((BackgroundImageBackgroundStyle) backgroundStyle).getImageFile();
				break;
			default:
				break;
			}

			switch (backgroundStyleType) {
			case NONE:
				setBackgroundStyle(fgeFactory.makeEmptyBackground());
				break;
			case COLOR:
				setBackgroundStyle(fgeFactory.makeColoredBackground(color1));
				break;
			case COLOR_GRADIENT:
				setBackgroundStyle(fgeFactory.makeColorGradientBackground(color1, color2, gradientDirection));
				break;
			case TEXTURE:
				setBackgroundStyle(fgeFactory.makeTexturedBackground(textureType, color1, color2));
				break;
			case IMAGE:
				setBackgroundStyle(fgeFactory.makeImageBackground(imageFile));
				break;
			default:
				break;
			}

			pcSupport.firePropertyChange("backgroundStyleType", oldBackgroundStyleType, getBackgroundStyleType());
		}

	}

	public class BackgroundStyleDetailsPanel extends ResizablePanel {
		private FIBComponent fibComponent;
		private FIBView<?, ?> fibView;
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

		public class CustomFIBController extends FIBController {
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
		private Drawing<BackgroundStylePreviewPanel> drawing;
		private DrawingGraphicalRepresentation drawingGR;
		private DrawingControllerImpl<BackgroundStylePreviewPanel> controller;
		private ShapeGraphicalRepresentation rectGR;

		private FGEModelFactory factory;

		protected BackgroundStylePreviewPanel() {
			super(new BorderLayout());
			setBorder(BorderFactory.createEtchedBorder(Color.GRAY, Color.LIGHT_GRAY));
			// setBorder(BorderFactory.createEtchedBorder());
			setPreferredSize(new Dimension(40, 19));
			// setBackground(Color.WHITE);

			factory = FGEUtils.TOOLS_FACTORY;

			drawing = new DrawingImpl<BackgroundStylePreviewPanel>(this, factory, PersistenceMode.UniqueGraphicalRepresentations) {
				@Override
				public void init() {
					final DrawingGRBinding<BackgroundStylePreviewPanel> previewPanelBinding = bindDrawing(
							BackgroundStylePreviewPanel.class, "previewPanel", new DrawingGRProvider<BackgroundStylePreviewPanel>() {
								@Override
								public DrawingGraphicalRepresentation provideGR(BackgroundStylePreviewPanel drawable,
										FGEModelFactory factory) {
									return drawingGR;
								}
							});
					final ShapeGRBinding<BackgroundStylePreviewPanel> shapeBinding = bindShape(BackgroundStylePreviewPanel.class, "line",
							new ShapeGRProvider<BackgroundStylePreviewPanel>() {
								@Override
								public ShapeGraphicalRepresentation provideGR(BackgroundStylePreviewPanel drawable, FGEModelFactory factory) {
									return rectGR;
								}
							});

					previewPanelBinding.addToWalkers(new GRStructureWalker<BackgroundStylePreviewPanel>() {

						@Override
						public void walk(BackgroundStylePreviewPanel previewPanel) {
							drawShape(shapeBinding, previewPanel, previewPanel);
						}
					});
				}
			};
			drawing.setEditable(false);

			drawingGR = factory.makeDrawingGraphicalRepresentation(drawing, false);
			drawingGR.setBackgroundColor(new Color(255, 255, 255));
			drawingGR.setWidth(35);
			drawingGR.setHeight(19);
			drawingGR.setDrawWorkingArea(false);
			rectGR = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE, drawing);
			rectGR.setWidth(36);
			rectGR.setHeight(20);
			rectGR.setX(0);
			rectGR.setY(0);
			rectGR.setForeground(factory.makeNoneForegroundStyle());
			rectGR.setBackground(getEditedObject() != null ? getEditedObject() : factory.makeColoredBackground(DEFAULT_COLOR1));
			rectGR.setShadowStyle(factory.makeNoneShadowStyle());
			rectGR.setIsSelectable(false);
			rectGR.setIsFocusable(false);
			rectGR.setIsReadOnly(true);
			rectGR.setBorder(factory.makeShapeBorder(0, 0, 0, 0));

			controller = new DrawingControllerImpl<BackgroundStylePreviewPanel>(drawing, factory);
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
		}

		protected void update() {
			rectGR.setBackground(getEditedObject() != null ? getEditedObject() : factory.makeColoredBackground(DEFAULT_COLOR1));
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
