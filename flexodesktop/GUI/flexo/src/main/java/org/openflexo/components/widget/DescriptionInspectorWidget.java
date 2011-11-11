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
package org.openflexo.components.widget;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.FocusManager;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.InspectableModification;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.inspector.widget.WysiwygLightWidget;
import org.openflexo.kvc.KVCObject;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.view.controller.FlexoDocInspectorController;
import org.openflexo.wysiwyg.FlexoWysiwygLight;
import org.openflexo.wysiwyg.FlexoWysiwygUltraLight;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.DocType;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.cg.DocTypeAdded;
import org.openflexo.foundation.cg.DocTypeRemoved;
import org.openflexo.foundation.rm.FlexoProject;

/**
 * @author gpolet
 * 
 */
public class DescriptionInspectorWidget extends CustomInspectorWidget<FlexoModelObject> {
	private static final Logger logger = FlexoLogger.getLogger(DescriptionInspectorWidget.class.getPackage().getName());

	protected class DescriptionPanel extends JPanel implements FlexoObserver {

		private JPanel top;

		protected FlexoWysiwygLight wysiwyg;

		private JComboBox docTypes;

		protected JCheckBox checkBox;

		private ActionListener cbListener;

		/**
         *
         */
		public DescriptionPanel() {
			super(new BorderLayout());
			setOpaque(false);
			checkBox = new JCheckBox();
			checkBox.setOpaque(false);
			boolean showViewSourceButtonInWysiwyg = getController().getConfiguration() != null
					&& getController().getConfiguration().showViewSourceButtonInWysiwyg();
			if (useUltraLightWysiwyg()) {
				wysiwyg = new FlexoWysiwygUltraLight(showViewSourceButtonInWysiwyg) {
					@Override
					public void notifyTextChanged() {
						updateModelFromWidget();
					}
				};
			} else {
				// Cannot set css as the modelObject is not set yet
				// TODO -> load the CSS
				wysiwyg = new FlexoWysiwygLight(null, null, showViewSourceButtonInWysiwyg) {
					@Override
					public void notifyTextChanged() {
						updateModelFromWidget();
					}
				};
			}
			wysiwyg.addBorderAroundToolbar();
			wysiwyg.setPreferredSize(new Dimension(250, 150));
			checkBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					updateModelFromWidget();
					updateWidgetFromModel();
				}

			});
			docTypes = new JComboBox();
			docTypes.setRenderer(new DefaultListCellRenderer());
			docTypes.addActionListener(cbListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					updateWidgetFromModel();
				}
			});

			top = new JPanel(new BorderLayout());
			top.setOpaque(false);
			top.add(checkBox, BorderLayout.WEST);
			top.add(docTypes);
			top.validate();
			top.doLayout();
			add(top, BorderLayout.NORTH);
			add(wysiwyg, BorderLayout.CENTER);
			validate();
			doLayout();
		}

		public void updateModelFromWidget() {
			if (isUpdatingWidget || isUpdatingModel || getObject() == null)
				return;
			isUpdatingModel = true;
			try {
				if (checkBox.isSelected()) {
					for (DocType dt : getObject().getProject().getDocTypes()) {
						if (getObject().getSpecificDescriptions().get(dt.getName()) == null)
							getObject().setSpecificDescriptionsForKey("", dt.getName());
					}
					getObject().setSpecificDescriptionsForKey(wysiwyg.getBodyContent(), ((DocType) docTypes.getSelectedItem()).getName());
					getObject().setHasSpecificDescriptions(true);
					wysiwyg.setEnabled(true);
					wysiwyg.setVisible(true);
					// docTypes.setEditable(true);
					docTypes.setEnabled(true);
				} else {
					wysiwyg.setEnabled(false);
					wysiwyg.setVisible(false);
					docTypes.setEnabled(false);
					getObject().setHasSpecificDescriptions(false);
				}

			} finally {
				isUpdatingModel = false;
			}
		}

		public void updateWidgetFromModel() {
			if (isUpdatingModel || getObject() == null || getObject().getProject() == null)
				return;
			isUpdatingWidget = true;
			try {
				checkBox.setSelected(getObject().getHasSpecificDescriptions());
				updateDocTypes();
				if (checkBox.isSelected()) {
					wysiwyg.setEnabled(true);
					wysiwyg.setVisible(true);
					// docTypes.setEditable(true);
					docTypes.setEnabled(true);
					DocType dt = (DocType) docTypes.getSelectedItem();
					wysiwyg.setActivated(false);
					try {
						if (getObject().getSpecificDescriptions().get(dt.getName()) != null)
							wysiwyg.setContent(getObject().getSpecificDescriptions().get(dt.getName()));
						else {
							boolean b = isUpdatingModel;
							try {
								isUpdatingModel = true;
								getObject().setSpecificDescriptionsForKey("", dt.getName());
							} finally {
								isUpdatingModel = b;
							}
							wysiwyg.setContent("");
						}
					} finally {
						wysiwyg.setActivated(true);
					}
				} else {
					wysiwyg.setEnabled(false);
					wysiwyg.setVisible(false);
					wysiwyg.setContent("");
					docTypes.setEnabled(false);
				}
				updateSplitPane();
				revalidate();
				doLayout();
			} finally {
				isUpdatingWidget = false;
			}
		}

		private int lastDividerLocation = -1;
		private boolean bothShowing = false;

		private void updateSplitPane() {
			if (checkBox.isSelected()) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (!bothShowing) {
							splitPane.setResizeWeight(0.5);// Extra space is given equally to top and bottom component
							if (lastDividerLocation == -1)
								splitPane.setDividerLocation(0.5);
							else {
								splitPane.setDividerLocation(lastDividerLocation);
							}
							bothShowing = true;
						}
					}
				});
			} else {
				if (bothShowing) {
					lastDividerLocation = splitPane.getDividerLocation();
					splitPane.setResizeWeight(1.0);// Extra space is given to the top component
					bothShowing = false;
				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						snapBottomPanelToMinimalSize();
					}
				});

			}
		}

		private void snapBottomPanelToMinimalSize() {
			if (splitPane.getHeight() > 0)
				splitPane.setDividerLocation(splitPane.getHeight() - DescriptionPanel.this.getPreferredSize().height
						- splitPane.getDividerSize());
		}

		private FlexoProject observedProject;
		private boolean updatingDocTypes = false;

		/**
		 *
		 */
		private void updateDocTypes() {
			if (updatingDocTypes)
				return;
			updatingDocTypes = true;
			try {
				if (observedProject != null) {
					observedProject.deleteObserver(this);
					observedProject = null;
				}
				if (getObject() != null) {
					DocType dt = (DocType) docTypes.getSelectedItem();
					docTypes.removeActionListener(cbListener);
					docTypes.setSelectedItem(null);
					docTypes.setModel(new DefaultComboBoxModel(getObject().getProject().getDocTypes()));
					if (dt != null && getObject().getProject().getDocTypes().indexOf(dt) > -1)
						docTypes.setSelectedItem(dt);
					else
						docTypes.setSelectedIndex(0);
					docTypes.addActionListener(cbListener);

					updateWidgetFromModel();
					getObject().getProject().addObserver(this);
					observedProject = getObject().getProject();
				}
			} finally {
				updatingDocTypes = false;
			}
		}

		@Override
		public void update(FlexoObservable observable, DataModification dataModification) {
			if (dataModification instanceof DocTypeRemoved || dataModification instanceof DocTypeAdded) {
				updateDocTypes();
			}
		}

	}

	private DescriptionPanel panel;

	protected boolean isUpdatingModel = false;

	protected boolean isUpdatingWidget = false;

	/**
     *
     *
     */
	public FlexoModelObject getObject() {
		KVCObject o = (KVCObject) super.getModel();
		if (getPropertyModel().name.indexOf('.') > -1) {
			String[] s = getPropertyModel().name.split(".");
			for (int i = 0; i < s.length - 1; i++) {
				String string = s[i];
				Object returned = o.objectForKey(string);
				if (!(returned instanceof KVCObject)) {
					if (logger.isLoggable(Level.SEVERE))
						logger.severe("Someting is wrong with an inspector. Inspected property path contains object that are not KVCObject "
								+ getPropertyModel().name);
					return null;
				} else
					o = (KVCObject) returned;
			}
		}
		if (o instanceof FlexoModelObject)
			return (FlexoModelObject) o;
		else if (o != null && logger.isLoggable(Level.SEVERE))
			logger.severe("Object at the end of property path is not a FlexoModelObject: " + getPropertyModel().name + " this is a " + o
					+ " of " + o.getClass().getSimpleName());
		return null;
	}

	/**
	 * Overrides setModel
	 * 
	 * @see org.openflexo.components.widget.CustomInspectorWidget#setModel(org.openflexo.inspector.InspectableObject)
	 */
	@Override
	public void setModel(InspectableObject value) {
		if (FocusManager.getCurrentManager().getFocusOwner() != null && getDynamicComponent() != null
				&& SwingUtilities.isDescendingFrom(FocusManager.getCurrentManager().getFocusOwner(), getDynamicComponent())) {
			// If we have the focus and we switch the observed object, we first set the last values on the previous object (so that data
			// does not get lost)
			updateModelFromWidget();
		}
		super.setModel(value);
		if (value != null) {
			if (value instanceof FlexoModelObject && ((FlexoModelObject) value).getProject() != null) {
				defaultWidget.getDynamicComponent().addSupportForInsertedObjects(
						((FlexoModelObject) value).getProject().getImportedImagesDir());
				panel.wysiwyg.addSupportForInsertedObjects(((FlexoModelObject) value).getProject().getImportedImagesDir());
			}
			updateWidgetFromModel();
		}
	}

	@Override
	public synchronized void switchObserved(InspectableObject inspectable) {
		super.switchObserved(inspectable);
		defaultWidget.switchObserved(inspectable);
	}

	protected JSplitPane splitPane;
	private WysiwygLightWidget defaultWidget;

	/**
	 * @param model
	 */
	public DescriptionInspectorWidget(PropertyModel model, AbstractController controller) {
		super(model, controller);
		PropertyModel descModel = new PropertyModel();
		descModel.parameters.putAll(model.parameters);
		descModel._tabModelName = model._tabModelName;
		descModel.setTabModel(model.getTabModel());
		descModel.label = "description";
		descModel.name = "description";
		descModel.setValueForParameter("readOnly", "!isDocEditable");
		descModel.setValueForParameter("widgetLayout", "1COL");
		descModel.setValueForParameter("align", "CENTER");
		defaultWidget = new WysiwygLightWidget(descModel, controller, useUltraLightWysiwyg());
		panel = new DescriptionPanel();
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, defaultWidget.getDynamicComponent(), panel);
		/*splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				System.err.println(splitPane.getResizeWeight()+" "+evt.getOldValue()+" --> "+evt.getNewValue());
				Thread.dumpStack();
			}
		});*/
	}

	@Override
	public boolean widgetHasFocus() {
		return super.widgetHasFocus() || panel.wysiwyg.hasFocus() || defaultWidget.hasFocus();
	}

	public boolean isInMainFrame() {
		return getController() instanceof FlexoDocInspectorController;
	}

	/**
	 * Overrides getDefaultType
	 * 
	 * @see org.openflexo.inspector.widget.DenaliWidget#getDefaultType()
	 */
	@Override
	public Class getDefaultType() {
		return FlexoModelObject.class;
	}

	/**
	 * Overrides getDynamicComponent
	 * 
	 * @see org.openflexo.inspector.widget.DenaliWidget#getDynamicComponent()
	 */
	@Override
	public JComponent getDynamicComponent() {
		return splitPane;
	}

	/**
	 * Overrides updateModelFromWidget
	 * 
	 * @see org.openflexo.inspector.widget.DenaliWidget#updateModelFromWidget()
	 */
	@Override
	public void updateModelFromWidget() {
		if (isUpdatingWidget)
			return;
		panel.updateModelFromWidget();
		super.updateModelFromWidget();
	}

	/**
	 * Overrides updateWidgetFromModel
	 * 
	 * @see org.openflexo.inspector.widget.DenaliWidget#updateWidgetFromModel()
	 */
	@Override
	public void updateWidgetFromModel() {
		if (isUpdatingModel)
			return;
		panel.updateWidgetFromModel();
	}

	@Override
	public boolean defaultShouldExpandVertically() {
		/*if (getObject()!=null)
		    return getObject().getHasSpecificDescriptions();*/
		return true;
	}

	@Override
	public void update(final InspectableObject inspectable, final InspectableModification modification) {
		if (modification != null && modification.propertyName() != null && modification.propertyName().equals("hasSpecificDescriptions")) {
			updateWidgetFromModel();
			return;
		}
		super.update(inspectable, modification);
	}

	protected boolean useUltraLightWysiwyg() {
		return getPropertyModel().hasValueForParameter("useUltraLightWysiwyg")
				&& getPropertyModel().getBooleanValueForParameter("useUltraLightWysiwyg");
	}

}