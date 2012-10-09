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
package org.openflexo.fib.view.widget;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBDropDown;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ToolBox;

public class FIBDropDownWidget extends FIBMultipleValueWidget<FIBDropDown, JComboBox, Object> {

	static final Logger logger = Logger.getLogger(FIBDropDownWidget.class.getPackage().getName());

	private final JButton resetButton;

	private final JPanel dropdownPanel;

	protected JComboBox jComboBox;

	public FIBDropDownWidget(FIBDropDown model, FIBController controller) {
		super(model, controller);
		initJComboBox();
		dropdownPanel = new JPanel(new BorderLayout());
		resetButton = new JButton();
		resetButton.setText(FlexoLocalization.localizedForKey(FIBModelObject.LOCALIZATION, "reset", resetButton));
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jComboBox.getModel().setSelectedItem(null);
				setValue(null);
			}
		});
		if (!ToolBox.isMacOSLaf()) {
			dropdownPanel.setBorder(BorderFactory.createEmptyBorder(TOP_COMPENSATING_BORDER, LEFT_COMPENSATING_BORDER,
					BOTTOM_COMPENSATING_BORDER, RIGHT_COMPENSATING_BORDER));
		}

		dropdownPanel.add(jComboBox, BorderLayout.CENTER);
		if (model.showReset) {
			dropdownPanel.add(resetButton, BorderLayout.EAST);
		}
		dropdownPanel.setOpaque(false);
		dropdownPanel.addFocusListener(this);

		updateFont();

	}

	protected void initJComboBox() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("initJComboBox()");
		}
		Point locTemp = null;
		Container parentTemp = null;
		if (jComboBox != null && jComboBox.getParent() != null) {
			locTemp = jComboBox.getLocation();
			parentTemp = jComboBox.getParent();
			parentTemp.remove(jComboBox);
			parentTemp.remove(resetButton);
		}
		listModel = null;
		jComboBox = new JComboBox(getListModel()) {
			// This override is for fixing bug OPENFLEXO-205
			// The problem comes when the model does not contain the null value
			// but the selected value is null (ie, selectedIndex==-1).
			// Currently, this is the only fix I found.
			private JList list;

			private JList getList() {
				if (list == null) {
					list = new JList();
					list.setFont(getFont());
					list.setForeground(getForeground());
					list.setBackground(getBackground());
					list.setSelectionForeground(UIManager.getColor("ComboBox.selectionForeground"));
					list.setSelectionBackground(UIManager.getColor("ComboBox.selectionBackground"));
					list.setBorder(null);
					list.setCellRenderer(getRenderer());
					list.setFocusable(false);
					list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				}
				return list;
			}

			private JButton getArrowButton() {
				for (Component c : getComponents()) {
					if (c instanceof JButton) {
						return (JButton) c;
					}
				}
				return null;
			}

			@Override
			public Dimension getPreferredSize() {
				Dimension preferredSize = super.getPreferredSize();
				if (getSelectedItem() == null && renderer != null) {
					Component renderer = getRenderer().getListCellRendererComponent(getList(), null, -1, false, false);
					Dimension nullDimension = renderer.getPreferredSize();
					JButton button = getArrowButton();
					int width = 0;
					if (button != null) {
						Insets arrowInsets = button.getInsets();
						width = button.getPreferredSize().width + arrowInsets.left + arrowInsets.right;
					} else {
						width = nullDimension.height; // Let's assume we have an arrow button which is square
					}
					nullDimension.width += width; // Take into account arrow button
					String name = UIManager.getLookAndFeel().getName().toLowerCase();
					if (name.contains("metal")) {
						nullDimension.width += 5; // Needs a bit more. Let's not be greedy and give 5 more pixels.
					} else if (name.contains("nimbus")) {
						// nimbus seems to be quite ok (it may even be a bit too big)
					} else {
						if (name.contains("windows")) {
							nullDimension.width += 5;// Windows requires 5 more pixels than expected.
						} else if (name.contains("aqua")) {
							nullDimension.width += 17;// Aqua requires 17 more pixels than expected.
						} else {
							nullDimension.width += 5; // Let's randomly put 5 more pixels. This can't really hurt
						}
					}
					Insets i = getInsets();
					Insets ri;
					if (renderer instanceof JComponent) {
						ri = ((JComponent) renderer).getInsets();
					} else {
						ri = new Insets(0, 0, 0, 0);
					}
					nullDimension.width += i.left + i.right + ri.left + ri.right;
					nullDimension.height += i.top + i.bottom + ri.top + ri.right;
					// System.err.println(nullDimension + " " + preferredSize);
					preferredSize.width = Math.max(preferredSize.width, nullDimension.width);
					preferredSize.height = Math.max(preferredSize.height, nullDimension.height);
				}
				return preferredSize;
			}
		};
		/*if (getDataObject() == null) {
			Vector<Object> defaultValue = new Vector<Object>();
			defaultValue.add(FlexoLocalization.localizedForKey("no_selection"));
			_jComboBox = new JComboBox(defaultValue);
		} else {
			// TODO: Verify that there is no reason for this comboBoxModel to be cached.
			listModel=null;
			_jComboBox = new JComboBox(getListModel());
		}*/
		jComboBox.setFont(getFont());
		jComboBox.setRenderer(getListCellRenderer());
		jComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Action performed in " + this.getClass().getName());
				}
				updateModelFromWidget();
			}
		});
		if (parentTemp != null) {
			// _jComboBox.setSize(dimTemp);
			jComboBox.setLocation(locTemp);
			((JPanel) parentTemp).add(jComboBox, BorderLayout.CENTER);
			if (getWidget().showReset) {
				((JPanel) parentTemp).add(resetButton, BorderLayout.EAST);
			}
		}
		// Important: otherwise might be desynchronized
		jComboBox.revalidate();

		if ((getWidget().getData() == null || !getWidget().getData().isValid()) && getWidget().getAutoSelectFirstRow()
				&& getListModel().getSize() > 0) {
			jComboBox.setSelectedIndex(0);
		}
		jComboBox.setEnabled(isComponentEnabled());
	}

	@Override
	public synchronized boolean updateWidgetFromModel() {
		if (notEquals(getValue(), jComboBox.getSelectedItem()) || listModelRequireChange()) {

			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateWidgetFromModel()");
			}
			widgetUpdating = true;
			initJComboBox();
			jComboBox.setSelectedItem(getValue());

			widgetUpdating = false;

			if (getValue() == null && getWidget().getAutoSelectFirstRow() && getListModel().getSize() > 0) {
				jComboBox.setSelectedIndex(0);
			}

			return true;
		}
		return false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget() {
		if (widgetUpdating) {
			return false;
		}
		if (notEquals(getValue(), jComboBox.getSelectedItem())) {
			modelUpdating = true;
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("updateModelFromWidget with " + jComboBox.getSelectedItem());
			}
			if (jComboBox.getSelectedItem() != null && !widgetUpdating) {
				setValue(jComboBox.getSelectedItem());
			}
			modelUpdating = false;
			return true;
		}
		return false;
	}

	@Override
	public MyComboBoxModel getListModel() {
		return (MyComboBoxModel) super.getListModel();
	}

	@Override
	protected MyComboBoxModel updateListModelWhenRequired() {
		if (listModel == null) {
			listModel = new MyComboBoxModel(getValue());
			if (jComboBox != null) {
				jComboBox.setModel((MyComboBoxModel) listModel);
			}
		} else {
			MyComboBoxModel aNewMyComboBoxModel = new MyComboBoxModel(getValue());
			if (!aNewMyComboBoxModel.equals(listModel)) {
				listModel = aNewMyComboBoxModel;
				jComboBox.setModel((MyComboBoxModel) listModel);
				// This next line should trigger the invalidation of the cached
				// preferred size of the combo box
				jComboBox.setPrototypeDisplayValue(null);
			}
		}
		return (MyComboBoxModel) listModel;
	}

	protected class MyComboBoxModel extends FIBMultipleValueModel implements ComboBoxModel {
		protected Object selectedItem = null;

		public MyComboBoxModel(Object selectedObject) {
			super();
		}

		@Override
		public void setSelectedItem(Object anItem) {
			if (selectedItem != anItem) {
				widgetUpdating = true;
				selectedItem = anItem;
				// logger.info("setSelectedItem() with " + anItem + " widgetUpdating=" + widgetUpdating + " modelUpdating=" +
				// modelUpdating);
				getDynamicModel().selected = anItem;
				getDynamicModel().selectedIndex = indexOf(anItem);
				if (!widgetUpdating && !modelUpdating) {
					notifyDynamicModelChanged();
				}
				widgetUpdating = false;
			}
		}

		@Override
		public Object getSelectedItem() {
			return selectedItem;
		}

		@Override
		public int hashCode() {
			return selectedItem == null ? 0 : selectedItem.hashCode();
		}

		@Override
		public boolean equals(Object object) {
			if (object instanceof MyComboBoxModel) {
				if (selectedItem != ((MyComboBoxModel) object).selectedItem) {
					return false;
				}
			}
			return super.equals(object);
		}

	}

	@Override
	public JPanel getJComponent() {
		return dropdownPanel;
	}

	@Override
	public JComboBox getDynamicJComponent() {
		return jComboBox;
	}

	@Override
	public void updateFont() {
		super.updateFont();
		jComboBox.setFont(getFont());
	}

}
