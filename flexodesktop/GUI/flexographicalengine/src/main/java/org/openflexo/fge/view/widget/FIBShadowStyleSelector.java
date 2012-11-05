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
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.openflexo.fge.BackgroundStyleImpl;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.DrawingGraphicalRepresentationImpl;
import org.openflexo.fge.ForegroundStyleImpl;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentationImpl;
import org.openflexo.fge.ShapeGraphicalRepresentationImpl.ShapeBorderImpl;
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

/**
 * Widget allowing to view and edit a ShadowStyle
 * 
 * @author sguerin
 * 
 */
public class FIBShadowStyleSelector extends CustomPopup<ShadowStyle> implements FIBCustomComponent<ShadowStyle, FIBShadowStyleSelector> {

	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(FIBShadowStyleSelector.class.getPackage().getName());

	public static FileResource FIB_FILE = new FileResource("Fib/ShadowStylePanel.fib");

	private ShadowStyle _revertValue;

	protected ShadowStyleDetailsPanel _selectorPanel;

	private ShadowStylePreviewPanel shadowStylePreviewPanel;

	public FIBShadowStyleSelector(ShadowStyle editedObject) {
		super(editedObject);
		setRevertValue(editedObject != null ? editedObject.clone() : null);
		setFocusable(true);
	}

	@Override
	public void delete() {
		super.delete();
		shadowStylePreviewPanel.delete();
		if (_selectorPanel != null) {
			_selectorPanel.delete();
		}
	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
	}

	/**
	 * Return a flag indicating if equals() method should be used to determine equality.<br>
	 * For the FIBForegroundStyleSelector implementation, we MUST return false, because we can otherwise switch between ForegroundStyle
	 * which are equals, and then start to share ShadowStyle between many GraphicalRepresentation
	 * 
	 * @return false
	 */
	@Override
	public boolean useEqualsLookup() {
		return false;
	}

	@Override
	public void setRevertValue(ShadowStyle oldValue) {
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
	public ShadowStyle getRevertValue() {
		return _revertValue;
	}

	@Override
	protected ResizablePanel createCustomPanel(ShadowStyle editedObject) {
		_selectorPanel = makeCustomPanel(editedObject);
		return _selectorPanel;
	}

	protected ShadowStyleDetailsPanel makeCustomPanel(ShadowStyle editedObject) {
		return new ShadowStyleDetailsPanel(editedObject);
	}

	@Override
	public void updateCustomPanel(ShadowStyle editedObject) {
		if (_selectorPanel != null) {
			_selectorPanel.update();
		}
		getFrontComponent().update();
	}

	public class ShadowStyleDetailsPanel extends ResizablePanel {
		private FIBComponent fibComponent;
		private FIBView fibView;
		private CustomFIBController controller;

		protected ShadowStyleDetailsPanel(ShadowStyle shadowStyle) {
			super();

			fibComponent = FIBLibrary.instance().retrieveFIBComponent(FIB_FILE);
			controller = new CustomFIBController(fibComponent);
			fibView = controller.buildView(fibComponent);

			controller.setDataObject(shadowStyle);

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

		public class CustomFIBController extends FIBController<ShadowStyle> {
			public CustomFIBController(FIBComponent component) {
				super(component);
			}

			public void apply() {
				FIBShadowStyleSelector.this.apply();
			}

			public void cancel() {
				FIBShadowStyleSelector.this.cancel();
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

	public ShadowStyleDetailsPanel getSelectorPanel() {
		return _selectorPanel;
	}

	@Override
	protected ShadowStylePreviewPanel buildFrontComponent() {
		return shadowStylePreviewPanel = new ShadowStylePreviewPanel();
	}

	@Override
	public ShadowStylePreviewPanel getFrontComponent() {
		return (ShadowStylePreviewPanel) super.getFrontComponent();
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

	protected class ShadowStylePreviewPanel extends JPanel {
		private Drawing drawing;
		private DrawingGraphicalRepresentation drawingGR;
		private DrawingController<?> controller;
		private Object p1, p2, text;
		private ShapeGraphicalRepresentation shapeGR;

		protected ShadowStylePreviewPanel() {
			super(new BorderLayout());
			setBorder(BorderFactory.createEtchedBorder(Color.GRAY, Color.LIGHT_GRAY));
			// setBorder(BorderFactory.createEtchedBorder());
			setPreferredSize(new Dimension(40, 19));
			// setBackground(Color.WHITE);

			text = new Object();

			final Vector<Object> singleton = new Vector<Object>();
			singleton.add(text);

			drawing = new Drawing<ShadowStylePreviewPanel>() {
				@Override
				public List<?> getContainedObjects(Object aDrawable) {
					if (aDrawable == ShadowStylePreviewPanel.this) {
						return singleton;
					}
					return null;
				}

				@Override
				public Object getContainer(Object aDrawable) {
					if (aDrawable == text) {
						return ShadowStylePreviewPanel.this;
					}
					return null;
				}

				@Override
				public DrawingGraphicalRepresentation<ShadowStylePreviewPanel> getDrawingGraphicalRepresentation() {
					return drawingGR;
				}

				@Override
				public <O> GraphicalRepresentation<O> getGraphicalRepresentation(O aDrawable) {
					if (aDrawable == ShadowStylePreviewPanel.this) {
						return drawingGR;
					} else if (aDrawable == text) {
						return shapeGR;
					}
					return null;
				}

				@Override
				public ShadowStylePreviewPanel getModel() {
					return ShadowStylePreviewPanel.this;
				}

			};
			drawingGR = new DrawingGraphicalRepresentationImpl(drawing, false);
			drawingGR.setBackgroundColor(new Color(255, 255, 255));
			drawingGR.setWidth(35);
			drawingGR.setHeight(19);
			drawingGR.setDrawWorkingArea(false);
			shapeGR = new ShapeGraphicalRepresentationImpl(ShapeType.RECTANGLE, text, drawing);
			shapeGR.setWidth(130);
			shapeGR.setHeight(130);
			shapeGR.setAllowToLeaveBounds(true);
			shapeGR.setX(-130);
			shapeGR.setY(-143);
			shapeGR.setForeground(ForegroundStyleImpl.makeStyle(Color.BLACK));
			shapeGR.setBackground(BackgroundStyleImpl.makeColoredBackground(new Color(252, 242, 175)));

			shapeGR.setIsSelectable(false);
			shapeGR.setIsFocusable(false);
			shapeGR.setIsReadOnly(true);
			shapeGR.setBorder(new ShapeBorderImpl(20, 20, 20, 20));
			shapeGR.setValidated(true);

			update();

			controller = new DrawingController<Drawing<?>>(drawing);
			add(controller.getDrawingView());

		}

		public void delete() {
			controller.delete();
			drawingGR.delete();
			shapeGR.delete();
			drawing = null;
		}

		protected void update() {
			if (getEditedObject() == null) {
				return;
			}
			shapeGR.setShadowStyle(getEditedObject());
		}

	}

	@Override
	public FIBShadowStyleSelector getJComponent() {
		return this;
	}

	@Override
	public Class<ShadowStyle> getRepresentedType() {
		return ShadowStyle.class;
	}

}
