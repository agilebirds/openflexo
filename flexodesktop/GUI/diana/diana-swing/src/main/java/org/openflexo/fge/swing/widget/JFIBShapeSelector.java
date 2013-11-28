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
package org.openflexo.fge.swing.widget;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.fge.control.tools.ShapeSpecificationFactory;
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.fge.view.widget.FIBShapeSelector;
import org.openflexo.fge.view.widget.ShapePreviewPanel;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.view.FIBView;
import org.openflexo.fib.view.widget.FIBCustomWidget;
import org.openflexo.swing.CustomPopup;

/**
 * Widget allowing to view and edit a ShapeSpecification
 * 
 * @author sguerin
 * 
 */
@SuppressWarnings("serial")
public class JFIBShapeSelector extends CustomPopup<ShapeSpecification> implements FIBShapeSelector<JFIBShapeSelector> {

	static final Logger logger = Logger.getLogger(JFIBShapeSelector.class.getPackage().getName());

	private ShapeSpecification _revertValue;

	protected ShapeDetailsPanel _selectorPanel;
	private JShapePreviewPanel frontComponent;

	private ShapeSpecificationFactory factory;

	public JFIBShapeSelector(ShapeSpecificationFactory factory) {
		super(factory != null ? factory.getShapeSpecification() : null);
		this.factory = factory;
		// setRevertValue(factory.getShape() != null ? (ShapeSpecification) factory.getShape().clone() : null);
		setFocusable(true);
	}

	public ShapeSpecificationFactory getFactory() {
		if (factory == null) {
			factory = new ShapeSpecificationFactory(null);
		}
		return factory;
	}

	public void setFactory(ShapeSpecificationFactory factory) {
		this.factory = factory;
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
	public void setRevertValue(ShapeSpecification oldValue) {
		// WARNING: we need here to clone to keep track back of previous data
		// !!!
		if (oldValue != null) {
			_revertValue = (ShapeSpecification) oldValue.clone();
		} else {
			_revertValue = null;
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Sets revert value to " + _revertValue);
		}
	}

	@Override
	public ShapeSpecification getRevertValue() {
		return _revertValue;
	}

	@Override
	protected ResizablePanel createCustomPanel(ShapeSpecification editedObject) {
		_selectorPanel = makeCustomPanel(editedObject);
		return _selectorPanel;
	}

	protected ShapeDetailsPanel makeCustomPanel(ShapeSpecification editedObject) {
		return new ShapeDetailsPanel(editedObject);
	}

	@Override
	public void updateCustomPanel(ShapeSpecification editedObject) {
		if (_selectorPanel != null) {
			_selectorPanel.update();
		}
		getFrontComponent().setShape(editedObject);
		// getFrontComponent().update();
	}

	public class ShapeDetailsPanel extends ResizablePanel {
		private FIBComponent fibComponent;
		private FIBView<?, ?, ?> fibView;
		private CustomFIBController controller;

		protected ShapeDetailsPanel(ShapeSpecification backgroundStyle) {
			super();

			fibComponent = FIBLibrary.instance().retrieveFIBComponent(FIB_FILE);
			controller = new CustomFIBController(fibComponent);
			fibView = controller.buildView(fibComponent);

			controller.setDataObject(getFactory());

			setLayout(new BorderLayout());
			add(fibView.getResultingJComponent(), BorderLayout.CENTER);

		}

		public void update() {
			// logger.info("Update with " + getEditedObject());
			logger.warning("Un truc a voir ici comment s'en sortir: ligne suivante commentee");
			// getFactory().setShape(getEditedObject());
			controller.setDataObject(getFactory(), true);
		}

		@Override
		public Dimension getDefaultSize() {
			return new Dimension(fibComponent.getWidth(), fibComponent.getHeight());
		}

		public void delete() {
			controller.delete();
			fibView.delete();
			factory.delete();
			fibComponent = null;
			controller = null;
			fibView = null;
			factory = null;
		}

		public class CustomFIBController extends FIBController {
			public CustomFIBController(FIBComponent component) {
				super(component);
			}

			public void apply() {
				setEditedObject(getFactory().getShapeSpecification());
				JFIBShapeSelector.this.apply();
			}

			public void cancel() {
				JFIBShapeSelector.this.cancel();
			}

			public void shapeChanged() {

				getFrontComponent().setShape(getFactory().getShapeSpecification());
				// getFrontComponent().update();

				FIBView<?, ?, ?> previewComponent = viewForComponent(fibComponent.getComponentNamed("PreviewPanel"));
				if (previewComponent instanceof FIBCustomWidget) {
					JComponent customComponent = ((FIBCustomWidget<?, ?>) previewComponent).getJComponent();
					if (customComponent instanceof ShapePreviewPanel) {
						((JShapePreviewPanel) customComponent).setShape(getFactory().getShapeSpecification());
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
		setRevertValue(getEditedObject() != null ? (ShapeSpecification) getEditedObject().clone() : null);
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
	protected JShapePreviewPanel buildFrontComponent() {
		frontComponent = new JShapePreviewPanel(getEditedObject());
		frontComponent.setBorderSize(1);
		frontComponent.setPanelWidth(40);
		frontComponent.setPanelHeight(19);
		return frontComponent;
	}

	@Override
	public JShapePreviewPanel getFrontComponent() {
		return (JShapePreviewPanel) super.getFrontComponent();
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
	public JFIBShapeSelector getJComponent() {
		return this;
	}

	@Override
	public Class<ShapeSpecification> getRepresentedType() {
		return ShapeSpecification.class;
	}

}
