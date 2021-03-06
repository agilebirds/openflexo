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
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.ShapeBorder;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.ShadowStyle;
import org.openflexo.fge.graphics.TextStyle;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBCustom.FIBCustomComponent;
import org.openflexo.fib.view.FIBView;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.CustomPopup;
import org.openflexo.swing.JFontChooser;
import org.openflexo.toolbox.FileResource;

/**
 * Widget allowing to view and edit a TextStyle
 * 
 * @author sguerin
 * 
 */
public class FIBTextStyleSelector extends CustomPopup<TextStyle> implements FIBCustomComponent<TextStyle, FIBTextStyleSelector> {

	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(FIBTextStyleSelector.class.getPackage().getName());

	public static FileResource FIB_FILE = new FileResource("Fib/TextStylePanel.fib");

	private TextStyle _revertValue;

	protected TextStyleDetailsPanel _selectorPanel;

	private TextStylePreviewPanel textStylePreviewPanel;

	public FIBTextStyleSelector(TextStyle editedObject) {
		super(editedObject);
		setRevertValue(editedObject != null ? editedObject.clone() : null);
		setFocusable(true);
	}

	@Override
	public void delete() {
		super.delete();
		textStylePreviewPanel.delete();
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
	 * which are equals, and then start to share TextStyle between many GraphicalRepresentation
	 * 
	 * @return false
	 */
	@Override
	public boolean useEqualsLookup() {
		return false;
	}

	@Override
	public void setRevertValue(TextStyle oldValue) {
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
	public TextStyle getRevertValue() {
		return _revertValue;
	}

	@Override
	protected ResizablePanel createCustomPanel(TextStyle editedObject) {
		_selectorPanel = makeCustomPanel(editedObject);
		return _selectorPanel;
	}

	protected TextStyleDetailsPanel makeCustomPanel(TextStyle editedObject) {
		return new TextStyleDetailsPanel(editedObject);
	}

	@Override
	public void updateCustomPanel(TextStyle editedObject) {
		if (_selectorPanel != null) {
			_selectorPanel.update();
		}
		getFrontComponent().update();
	}

	public class TextStyleDetailsPanel extends ResizablePanel {
		private FIBComponent fibComponent;
		private FIBView fibView;
		private CustomFIBController controller;

		protected TextStyleDetailsPanel(TextStyle textStyle) {
			super();

			fibComponent = FIBLibrary.instance().retrieveFIBComponent(FIB_FILE);
			controller = new CustomFIBController(fibComponent);
			fibView = controller.buildView(fibComponent);

			controller.setDataObject(textStyle);

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
				FIBTextStyleSelector.this.apply();
			}

			public void cancel() {
				FIBTextStyleSelector.this.cancel();
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

	public TextStyleDetailsPanel getSelectorPanel() {
		return _selectorPanel;
	}

	@Override
	protected TextStylePreviewPanel buildFrontComponent() {
		return textStylePreviewPanel = new TextStylePreviewPanel();
	}

	@Override
	public TextStylePreviewPanel getFrontComponent() {
		return (TextStylePreviewPanel) super.getFrontComponent();
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

	protected class TextStylePreviewPanel extends JPanel {
		private Drawing drawing;
		private DrawingGraphicalRepresentation drawingGR;
		private DrawingController<?> controller;
		private Object p1, p2, text;
		private ShapeGraphicalRepresentation textGR;

		protected TextStylePreviewPanel() {
			super(new BorderLayout());
			setBorder(BorderFactory.createEtchedBorder(Color.GRAY, Color.LIGHT_GRAY));
			// setBorder(BorderFactory.createEtchedBorder());
			// setPreferredSize(new Dimension(40,19));
			// setBackground(Color.WHITE);
			setMinimumSize(new Dimension(40, 19));

			text = new Object();

			final List<Object> singleton = Collections.singletonList(text);

			drawing = new Drawing<TextStylePreviewPanel>() {
				@Override
				public List<?> getContainedObjects(Object aDrawable) {
					if (aDrawable == TextStylePreviewPanel.this) {
						return singleton;
					}
					return null;
				}

				@Override
				public Object getContainer(Object aDrawable) {
					if (aDrawable == text) {
						return TextStylePreviewPanel.this;
					}
					return null;
				}

				@Override
				public DrawingGraphicalRepresentation<TextStylePreviewPanel> getDrawingGraphicalRepresentation() {
					return drawingGR;
				}

				@Override
				public <O> GraphicalRepresentation<O> getGraphicalRepresentation(O aDrawable) {
					if (aDrawable == TextStylePreviewPanel.this) {
						return drawingGR;
					} else if (aDrawable == text) {
						return textGR;
					}
					return null;
				}

				@Override
				public TextStylePreviewPanel getModel() {
					return TextStylePreviewPanel.this;
				}

				@Override
				public boolean isEditable() {
					return false;
				}

			};
			drawingGR = new DrawingGraphicalRepresentation(drawing, false);
			drawingGR.setBackgroundColor(new Color(255, 255, 255));
			drawingGR.setWidth(199);
			drawingGR.setHeight(19);
			drawingGR.setDrawWorkingArea(false);
			textGR = new ShapeGraphicalRepresentation(ShapeType.RECTANGLE, text, drawing);
			textGR.setWidth(200);
			textGR.setHeight(20);
			textGR.setX(0);
			textGR.setY(0);
			textGR.setText(FlexoLocalization.localizedForKey(GraphicalRepresentation.LOCALIZATION, "no_font_selected"));
			textGR.setIsFloatingLabel(false);
			textGR.setRelativeTextX(0.5);
			textGR.setRelativeTextY(0.35);
			textGR.setForeground(ForegroundStyle.makeNone());
			textGR.setBackground(BackgroundStyle.makeEmptyBackground());
			textGR.setTextStyle(getEditedObject());
			textGR.setShadowStyle(ShadowStyle.makeNone());
			textGR.setIsSelectable(false);
			textGR.setIsFocusable(false);
			textGR.setIsReadOnly(true);
			textGR.setBorder(new ShapeBorder(0, 0, 0, 0));
			textGR.setValidated(true);

			controller = new DrawingController<Drawing<?>>(drawing);
			add(controller.getDrawingView());

			update();
		}

		public void delete() {
			controller.delete();
			drawingGR.delete();
			textGR.delete();
			controller = null;
			drawingGR = null;
			textGR = null;
			drawing = null;
		}

		protected void update() {
			if (getEditedObject() == null) {
				return;
			}
			textGR.setTextStyle(getEditedObject());
			textGR.setText(JFontChooser.fontDescription(getEditedObject().getFont()));
		}

	}

	@Override
	public FIBTextStyleSelector getJComponent() {
		return this;
	}

	@Override
	public Class<TextStyle> getRepresentedType() {
		return TextStyle.class;
	}

}
