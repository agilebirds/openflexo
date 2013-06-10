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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEIconLibrary;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.ShapeBorder;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.LayoutToolBar.LayoutButton;
import org.openflexo.fge.controller.LayoutToolBar.TranslationTransition;
import org.openflexo.fge.layout.Layout;
import org.openflexo.fge.layout.ILayout.LayoutStatus;
import org.openflexo.fge.layout.Layout.LayoutType;
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
 * Widget allowing to view and edit a Layout 
 * 
 */
public class FIBLayoutSelector extends CustomPopup<Layout> implements
		FIBCustomComponent<Layout, FIBLayoutSelector> {

	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(FIBLayoutSelector.class.getPackage().getName());

	// The serialized fib for the layout 
	public static FileResource FIB_FILE = new FileResource("Fib/LayoutSelectorPanel.fib");

	// The previous value for the layout 
	private Layout _revertValue;

	// ?
	protected LayoutDetailsPanel _selectorPanel;

	// The preview panel always visible in the tool box
	private LayoutPreviewPanel layoutPreviewPanel;

	public FIBLayoutSelector(Layout editedObject) {
		super(editedObject);
		setRevertValue(editedObject != null ? editedObject.clone() : null);
		setFocusable(true);
	}

	@Override
	public void delete() {
		super.delete();
		layoutPreviewPanel.delete();
		if (_selectorPanel != null) {
			_selectorPanel.delete();
		}
	}

	@Override
	public void init(FIBCustom component, FIBController controller) {
	}

	@Override
	public Class<Layout> getRepresentedType() {
		return Layout.class;
	}

	/**
	 * Return a flag indicating if equals() method should be used to determine equality.<br>
	 * For the FIBlayoutSelector implementation, we MUST return false, because we can otherwise switch between layout
	 * which are equals, and then start to share layout between many GraphicalRepresentation
	 * 
	 * @return false
	 */
	@Override
	public boolean useEqualsLookup() {
		return false;
	}

	@Override
	public void setRevertValue(Layout oldValue) {
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
	public Layout getRevertValue() {
		return _revertValue;
	}

	@Override
	protected ResizablePanel createCustomPanel(Layout editedObject) {
		_selectorPanel = makeCustomPanel(editedObject);
		return _selectorPanel;
	}

	// The detail panel to custumize more properties
	protected LayoutDetailsPanel makeCustomPanel(Layout editedObject) {
		return new LayoutDetailsPanel(editedObject);
	}

	@Override
	public void updateCustomPanel(Layout editedObject) {
		if (_selectorPanel != null) {
			_selectorPanel.update();
		}
		getFrontComponent().update();
	}

	@Override
	public FIBLayoutSelector getJComponent() {
		return this;
	}

	public class LayoutDetailsPanel extends ResizablePanel {
		private FIBComponent fibComponent;
		private FIBView fibView;
		private CustomFIBController controller;
		private LayoutFactory layoutFactory;

		protected LayoutDetailsPanel(Layout layout) {
			super();
			layoutFactory = new LayoutFactory(layout);
			fibComponent = FIBLibrary.instance().retrieveFIBComponent(FIB_FILE);
			controller = new CustomFIBController(fibComponent);
			fibView = controller.buildView(fibComponent);

			controller.setDataObject(layoutFactory);

			setLayout(new BorderLayout());
			add(fibView.getResultingJComponent(), BorderLayout.CENTER);

		}

		public void update() {
			// logger.info("Update with " + getEditedObject());
			layoutFactory.setLayout(getEditedObject());
			controller.setDataObject(layoutFactory, true);
		}

		@Override
		public Dimension getDefaultSize() {
			return new Dimension(fibComponent.getWidth(), fibComponent.getHeight());
		}

		public void delete() {
			controller.delete();
			fibView.delete();
			layoutFactory.delete();
			fibComponent = null;
			controller = null;
			fibView = null;
		}

		// Controller for the detail layout panel
		public class CustomFIBController extends FIBController {
			
			public CustomFIBController(FIBComponent component) {
				super(component);
			}

			public void apply() {
				//setEditedObject(layout);
				FIBLayoutSelector.this.apply();
			}

			public void cancel() {
				FIBLayoutSelector.this.cancel();
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

	public LayoutDetailsPanel getSelectorPanel() {
		return _selectorPanel;
	}

	@Override
	protected LayoutPreviewPanel buildFrontComponent() {
		return layoutPreviewPanel = new LayoutPreviewPanel();
	}

	@Override
	public LayoutPreviewPanel getFrontComponent() {
		return (LayoutPreviewPanel) super.getFrontComponent();
	}

	protected class LayoutPreviewPanel extends JPanel {

		protected LayoutPreviewPanel() {

			//add(new JLabel(FGEIconLibrary.FORCE_DIRECTED_ICON));
			add(new LayoutButton(FGEIconLibrary.FORCE_DIRECTED_ICON, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					apply();
				}
			}));

		}

		public class LayoutButton extends JButton {
			public LayoutButton(ImageIcon icon, ActionListener al) {
				super(icon);
				setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.GRAY),
						BorderFactory.createEmptyBorder(2, 0, 2, 0)));
				addActionListener(al);
			}
		}
		
		public void delete() {

		}

		protected void update() {
			if (getEditedObject() == null) {
				return;
			}
		}

	}
	
	/**
	 * Convenient class use to manipulate Layout
	 * 
	 * @author vincent
	 * 
	 */
	public static class LayoutFactory implements HasPropertyChangeSupport {
		private static final String DELETED = "deleted";
		private Layout layout;
		private PropertyChangeSupport pcSupport;	
		
		public LayoutFactory(Layout layout) {
			pcSupport = new PropertyChangeSupport(this);
			this.layout = layout;
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

		public Layout getLayout() {
			return layout;
		}

		public void setLayout(Layout layout) {
			Layout oldLayout = this.layout;
			this.layout = layout;
			pcSupport.firePropertyChange("layout", oldLayout, layout);
		}

		public LayoutType getLayoutType() {
			if (layout != null) {
				return layout.getLayoutType();
			}
			return null;
		}

		public void setLayoutType(LayoutType layoutType) {
			System.out.println("setLayoutType with " + layoutType);
			LayoutType oldLayoutType = getLayoutType();
			if (oldLayoutType != layoutType) {
				switch (layoutType) {
				case NONE:
					layout = ((Layout) layout).makeLayout(layoutType,null);
					break;
				case FORCE_DIRECTED_PLACEMENT:
					layout = ((Layout) layout).makeLayout(layoutType,null);
					break;
				case HIERARCHICAL_PLACEMENT:
					layout = ((Layout) layout).makeLayout(layoutType, null);
					break;
				case RANDOM_PLACEMENT:
					layout = ((Layout) layout).makeLayout(layoutType, null);
					break;
				case CIRCULAR_PLACEMENT:
					layout = ((Layout) layout).makeLayout(layoutType, null);
					break;
				default:
					break;
				}
			}
			pcSupport.firePropertyChange("layoutType", oldLayoutType, getLayoutType());
		}

	}

}
