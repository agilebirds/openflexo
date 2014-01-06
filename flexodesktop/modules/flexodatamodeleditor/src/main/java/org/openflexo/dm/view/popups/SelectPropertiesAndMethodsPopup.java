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
package org.openflexo.dm.view.popups;

import java.awt.FlowLayout;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.openflexo.components.MultipleObjectSelectorPopup;
import org.openflexo.components.browser.BrowserElementFactory;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.dm.DMBrowserElementFactory;
import org.openflexo.components.tabular.model.AbstractColumn;
import org.openflexo.components.tabular.model.DropDownColumn;
import org.openflexo.components.widget.MultipleObjectSelector.TabularBrowserConfiguration;
import org.openflexo.dm.view.controller.DMController;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMMethod;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.action.CreateComponentFromEntity.DMAccessorWidget;
import org.openflexo.foundation.ie.util.WidgetType;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.localization.FlexoLocalization;

public class SelectPropertiesAndMethodsPopup extends MultipleObjectSelectorPopup {

	static final Logger logger = Logger.getLogger(SelectPropertiesAndMethodsPopup.class.getPackage().getName());

	private Vector<DMObject> selectedObjects;
	private Vector<DMAccessorWidget> selectedAccessorWidgets;

	private SelectPropertiesPopupBrowserConfiguration configuration;

	public SelectPropertiesAndMethodsPopup(String label, String description, String unlocalizedValidateButtonLabel, DMEntity entity,
			FlexoProject project, DMController controller) {
		super(FlexoLocalization.localizedForKey("attribute_selection"), label, description, unlocalizedValidateButtonLabel,
				new SelectPropertiesPopupBrowserConfiguration(entity), project, controller.getFlexoFrame());
		configuration = (SelectPropertiesPopupBrowserConfiguration) getBrowserConfiguration();
		selectedObjects = new Vector<DMObject>();
		selectedAccessorWidgets = new Vector<DMAccessorWidget>();
		selectedObjects.addAll(entity.getOrderedProperties());
		choicePanel.setSelectedObjects(selectedObjects);
	}

	@Override
	protected String getPopupTitle() {
		if (super.getPopupTitle() == null) {
			return FlexoLocalization.localizedForKey("attribute_selection");
		} else {
			return super.getPopupTitle();
		}
	}

	protected JPanel _additionalPanel = null;
	protected Hashtable<String, Object> params;

	private JCheckBox _copyDescriptions;

	public Object getParam(String key) {
		return getParams().get(key);
	}

	public void setParam(String key, Object value) {
		getParams().put(key, value);
	}

	private Hashtable<String, Object> getParams() {
		if (params == null) {
			params = new Hashtable<String, Object>();
		}
		return params;
	}

	@Override
	public JPanel getAdditionalPanel() {
		if (_additionalPanel == null) {
			_additionalPanel = new JPanel();
			_additionalPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			_copyDescriptions = new JCheckBox();
			_copyDescriptions.setText(FlexoLocalization.localizedForKey("copy_descriptions", _copyDescriptions));
			_copyDescriptions.setToolTipText(FlexoLocalization.localizedForKey("copy_descriptions_tooltip"));
			_copyDescriptions.setSelected(true);
			_additionalPanel.add(_copyDescriptions);
			_additionalPanel.setBorder(BorderFactory.createEmptyBorder(10, 150, 10, 10));
		}
		return _additionalPanel;
	}

	protected static class SelectPropertiesPopupBrowserConfiguration implements TabularBrowserConfiguration {
		protected DMEntity entity;
		private FlexoProject _project;
		private DMBrowserElementFactory factory;
		private DropDownColumn<DMObject, WidgetType> dropdownColumn;
		protected Hashtable<DMObject, DMAccessorWidget> values;
		protected Vector<WidgetType> availableValues;
		protected Vector<WidgetType> booleanValues;
		protected Vector<WidgetType> textValues;
		protected Vector<WidgetType> stringValue;
		protected Vector<WidgetType> multipleValue;
		protected Vector<WidgetType> dateOrNumverValues;

		protected SelectPropertiesPopupBrowserConfiguration(DMEntity entity) {
			_project = entity.getProject();
			this.entity = entity;
			values = new Hashtable<DMObject, DMAccessorWidget>();
			for (DMProperty p : entity.getProperties().values()) {
				DMAccessorWidget aw = new DMAccessorWidget();
				aw.property = p;
				values.put(p, aw);
			}
			for (DMMethod m : entity.getMethods().values()) {
				DMAccessorWidget aw = new DMAccessorWidget();
				aw.method = m;
				values.put(m, aw);
			}
			availableValues = new Vector<WidgetType>();
			availableValues.add(WidgetType.STRING);
			availableValues.add(WidgetType.TEXTFIELD);
			availableValues.add(WidgetType.TEXTAREA);
			availableValues.add(WidgetType.DROPDOWN);
			availableValues.add(WidgetType.BROWSER);
			availableValues.add(WidgetType.CHECKBOX);
			availableValues.add(WidgetType.RADIO);

			booleanValues = new Vector<WidgetType>();
			booleanValues.add(WidgetType.CHECKBOX);
			booleanValues.add(WidgetType.RADIO);
			booleanValues.add(WidgetType.STRING);

			textValues = new Vector<WidgetType>();
			textValues.add(WidgetType.TEXTFIELD);
			textValues.add(WidgetType.TEXTAREA);
			textValues.add(WidgetType.STRING);

			stringValue = new Vector<WidgetType>();
			stringValue.add(WidgetType.STRING);

			multipleValue = new Vector<WidgetType>();
			multipleValue.add(WidgetType.BROWSER);

			dateOrNumverValues = new Vector<WidgetType>();
			dateOrNumverValues.add(WidgetType.TEXTFIELD);
			dateOrNumverValues.add(WidgetType.STRING);

			factory = new DMBrowserElementFactory();
			dropdownColumn = new DropDownColumn<DMObject, WidgetType>(FlexoLocalization.localizedForKey("widget"), 150) {

				@Override
				protected Vector<WidgetType> getAvailableValues() {
					return availableValues;
				}

				/**
				 * Overrides getAvailableValues
				 * 
				 * @see org.openflexo.components.tabular.model.DropDownColumn#getAvailableValues(org.openflexo.foundation.FlexoModelObject)
				 */
				@Override
				protected Vector<WidgetType> getAvailableValues(DMObject object) {
					if (object instanceof DMMethod) {
						return stringValue;
					}
					if (object instanceof DMProperty) {
						DMProperty p = (DMProperty) object;
						if (p.getType() == null) {
							return availableValues;
						}
						if (p.getCardinality() != null && p.getCardinality().isMultiple()) {
							return multipleValue;
						}
						if (p.getType().isBoolean()) {
							return booleanValues;
						} else if (p.getType().isString() || p.getType().isChar()) {
							return textValues;
						} else if (p.getType().isInteger() || p.getType().isFloat() || p.getType().isDouble() || p.getType().isDate()) {
							return dateOrNumverValues;
						} else {
							return availableValues;
						}

					}
					return new Vector<WidgetType>();
				}

				@Override
				public WidgetType getValue(DMObject object) {
					if (values.get(object) != null) {
						WidgetType type = values.get(object).widget;
						if (type == null) {
							Vector<WidgetType> v = getAvailableValues();
							if (v.size() > 0) {
								values.get(object).widget = v.firstElement();
							}
							return values.get(object).widget;
						} else {
							return type;
						}
					}

					return null;
				}

				@Override
				protected String renderValue(WidgetType value) {
					if (value == null) {
						return FlexoLocalization.localizedForKey("none");
					}
					return value.toString();
				}

				@Override
				public void setValue(DMObject object, WidgetType value) {
					if (value == null) {
						return;
					}
					if (values.get(object) != null) {
						values.get(object).widget = value;
					}
				}

			};
		}

		@Override
		public FlexoProject getProject() {
			return _project;
		}

		@Override
		public void configure(ProjectBrowser browser) {
		}

		@Override
		public DMEntity getDefaultRootObject() {
			return entity;
		}

		@Override
		public BrowserElementFactory getBrowserElementFactory() {
			return factory;
		}

		/**
		 * Overrides getBrowsingColumnWidth
		 * 
		 * @see org.openflexo.components.widget.MultipleObjectSelector.TabularBrowserConfiguration#getBrowsingColumnWidth()
		 */
		@Override
		public int getBrowsingColumnWidth() {
			return 200;
		}

		/**
		 * Overrides getExtraColumnAt
		 * 
		 * @see org.openflexo.components.widget.MultipleObjectSelector.TabularBrowserConfiguration#getExtraColumnAt(int)
		 */
		@Override
		public AbstractColumn getExtraColumnAt(int index) {
			return dropdownColumn;
		}

		/**
		 * Overrides getExtraColumnCount
		 * 
		 * @see org.openflexo.components.widget.MultipleObjectSelector.TabularBrowserConfiguration#getExtraColumnCount()
		 */
		@Override
		public int getExtraColumnCount() {
			return 1;
		}

		/**
		 * Overrides isSelectable
		 * 
		 * @see org.openflexo.components.widget.MultipleObjectSelector.TabularBrowserConfiguration#isSelectable(org.openflexo.foundation.FlexoModelObject)
		 */
		@Override
		public boolean isSelectable(FlexoObject object) {
			return object instanceof DMProperty || object instanceof DMMethod && ((DMMethod) object).getReturnType() != null;
		}

	}

	@Override
	public void performConfirm() {
		super.performConfirm();
		for (FlexoObject o : choicePanel.getSelectedObjects()) {
			if (o instanceof DMProperty || o instanceof DMMethod) {
				selectedAccessorWidgets.add(configuration.values.get(o));
			}
		}
	}

	public Vector<DMAccessorWidget> getSelectedAccessorWidgets() {
		return selectedAccessorWidgets;
	}

	public boolean getCopyDescriptions() {
		if (_copyDescriptions != null) {
			return _copyDescriptions.isSelected();
		}
		return true;
	}

}
