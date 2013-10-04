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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.PersistenceMode;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GRStructureWalker;
import org.openflexo.fge.ShapeGraphicalRepresentation;
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

/**
 * Widget allowing to view and edit a ForegroundStyle
 * 
 * @author sguerin
 * 
 */
@SuppressWarnings("serial")
public class FIBForegroundStyleSelector extends CustomPopup<ForegroundStyle> implements
		FIBCustomComponent<ForegroundStyle, FIBForegroundStyleSelector> {

	static final Logger logger = Logger.getLogger(FIBForegroundStyleSelector.class.getPackage().getName());

	public static FileResource FIB_FILE = new FileResource("Fib/ForegroundStylePanel.fib");

	private ForegroundStyle _revertValue;

	protected ForegroundStyleDetailsPanel _selectorPanel;

	private ForegroundStylePreviewPanel foregroundStylePreviewPanel;

	public FIBForegroundStyleSelector(ForegroundStyle editedObject) {
		super(editedObject);
		setRevertValue(editedObject != null ? editedObject.clone() : null);
		setFocusable(true);
	}

	@Override
	public void delete() {
		super.delete();
		foregroundStylePreviewPanel.delete();
		if (_selectorPanel != null) {
			_selectorPanel.delete();
		}
	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
	}

	@Override
	public Class<ForegroundStyle> getRepresentedType() {
		return ForegroundStyle.class;
	}

	/**
	 * Return a flag indicating if equals() method should be used to determine equality.<br>
	 * For the FIBForegroundStyleSelector implementation, we MUST return false, because we can otherwise switch between ForegroundStyle
	 * which are equals, and then start to share ForegroundStyle between many GraphicalRepresentation
	 * 
	 * @return false
	 */
	@Override
	public boolean useEqualsLookup() {
		return false;
	}

	@Override
	public void setRevertValue(ForegroundStyle oldValue) {
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
	public ForegroundStyle getRevertValue() {
		return _revertValue;
	}

	@Override
	protected ResizablePanel createCustomPanel(ForegroundStyle editedObject) {
		_selectorPanel = makeCustomPanel(editedObject);
		return _selectorPanel;
	}

	protected ForegroundStyleDetailsPanel makeCustomPanel(ForegroundStyle editedObject) {
		return new ForegroundStyleDetailsPanel(editedObject);
	}

	@Override
	public void updateCustomPanel(ForegroundStyle editedObject) {
		if (_selectorPanel != null) {
			_selectorPanel.update();
		}
		getFrontComponent().update();
	}

	@Override
	public FIBForegroundStyleSelector getJComponent() {
		return this;
	}

	public class ForegroundStyleDetailsPanel extends ResizablePanel {
		private FIBComponent fibComponent;
		private FIBView<?, ?> fibView;
		private CustomFIBController controller;

		protected ForegroundStyleDetailsPanel(ForegroundStyle fs) {
			super();

			fibComponent = FIBLibrary.instance().retrieveFIBComponent(FIB_FILE);
			controller = new CustomFIBController(fibComponent);
			fibView = controller.buildView(fibComponent);

			controller.setDataObject(fs);

			setLayout(new BorderLayout());
			add(fibView.getResultingJComponent(), BorderLayout.CENTER);

		}

		public void update() {
			controller.setDataObject(getEditedObject(), true);
		}

		@Override
		public Dimension getDefaultSize() {
			return new Dimension(fibComponent.getWidth(), fibComponent.getHeight());
		}

		public void delete() {
			controller.delete();
			fibView.delete();
			fibComponent = null;
			controller = null;
			fibView = null;
		}

		public class CustomFIBController extends FIBController {
			public CustomFIBController(FIBComponent component) {
				super(component);
			}

			public void apply() {
				FIBForegroundStyleSelector.this.apply();
			}

			public void cancel() {
				FIBForegroundStyleSelector.this.cancel();
			}

			public void parameterChanged() {
				getFrontComponent().update();
			}
		}

	}

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

	/* protected void pointerLeavesPopup()
	 {
	     cancel();
	 }*/

	public ForegroundStyleDetailsPanel getSelectorPanel() {
		return _selectorPanel;
	}

	@Override
	protected ForegroundStylePreviewPanel buildFrontComponent() {
		return foregroundStylePreviewPanel = new ForegroundStylePreviewPanel();
	}

	@Override
	public ForegroundStylePreviewPanel getFrontComponent() {
		return (ForegroundStylePreviewPanel) super.getFrontComponent();
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

	protected class ForegroundStylePreviewPanel extends JPanel {
		private Drawing<ForegroundStylePreviewPanel> drawing;
		private DrawingGraphicalRepresentation drawingGR;
		private DrawingControllerImpl<ForegroundStylePreviewPanel> controller;
		private ShapeGraphicalRepresentation lineGR;

		private FGEModelFactory factory;

		protected ForegroundStylePreviewPanel() {
			super(new BorderLayout());
			setBorder(BorderFactory.createEtchedBorder(Color.GRAY, Color.LIGHT_GRAY));
			// setBorder(BorderFactory.createEtchedBorder());
			setPreferredSize(new Dimension(40, 19));
			// setBackground(Color.WHITE);

			factory = FGEUtils.TOOLS_FACTORY;

			drawing = new DrawingImpl<ForegroundStylePreviewPanel>(this, factory, PersistenceMode.UniqueGraphicalRepresentations) {
				@Override
				public void init() {
					final DrawingGRBinding<ForegroundStylePreviewPanel> previewPanelBinding = bindDrawing(
							ForegroundStylePreviewPanel.class, "previewPanel", new DrawingGRProvider<ForegroundStylePreviewPanel>() {
								@Override
								public DrawingGraphicalRepresentation provideGR(ForegroundStylePreviewPanel drawable,
										FGEModelFactory factory) {
									return drawingGR;
								}
							});
					final ShapeGRBinding<ForegroundStylePreviewPanel> shapeBinding = bindShape(ForegroundStylePreviewPanel.class, "line",
							new ShapeGRProvider<ForegroundStylePreviewPanel>() {
								@Override
								public ShapeGraphicalRepresentation provideGR(ForegroundStylePreviewPanel drawable, FGEModelFactory factory) {
									return lineGR;
								}
							});

					previewPanelBinding.addToWalkers(new GRStructureWalker<ForegroundStylePreviewPanel>() {

						@Override
						public void walk(ForegroundStylePreviewPanel previewPanel) {
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
			lineGR = factory.makeShapeGraphicalRepresentation(ShapeType.RECTANGLE, drawing);
			lineGR.setWidth(25);
			lineGR.setHeight(0);
			lineGR.setX(-5);
			lineGR.setY(-2);
			lineGR.setForeground(getEditedObject() != null ? getEditedObject() : factory.makeDefaultForegroundStyle());
			lineGR.setBackground(factory.makeEmptyBackground());
			lineGR.setShadowStyle(factory.makeNoneShadowStyle());
			lineGR.setIsSelectable(false);
			lineGR.setIsFocusable(false);
			lineGR.setIsReadOnly(true);
			lineGR.setBorder(factory.makeShapeBorder(10, 10, 10, 10));

			controller = new DrawingControllerImpl<ForegroundStylePreviewPanel>(drawing, factory);
			add(controller.getDrawingView());
		}

		public void delete() {
			controller.delete();
			drawingGR.delete();
			lineGR.delete();
			drawing = null;
			lineGR = null;
			drawingGR = null;
			controller = null;
		}

		protected void update() {
			if (getEditedObject() == null) {
				return;
			}
			lineGR.setForeground(getEditedObject());
		}

	}

}
