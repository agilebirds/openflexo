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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBDropDown;
import org.openflexo.localization.FlexoLocalization;

public class FIBDropDownWidget extends FIBMultipleValueWidget<FIBDropDown,JComboBox,Object>
{

	static final Logger logger = Logger.getLogger(FIBDropDownWidget.class.getPackage().getName());

	private final JButton _resetButton;

	private final JPanel _mySmallPanel;

	protected JComboBox _jComboBox;

	public FIBDropDownWidget(FIBDropDown model, FIBController controller)
	{
		super(model,controller);
		initJComboBox();
		_mySmallPanel = new JPanel(new BorderLayout());
		_resetButton = new JButton();
		_resetButton.setText(FlexoLocalization.localizedForKey("reset", _resetButton));
		_resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				_jComboBox.getModel().setSelectedItem(null);
				setValue(null);
			}
		});
		
		_mySmallPanel.add(_jComboBox, BorderLayout.CENTER);
		if (model.showReset) _mySmallPanel.add(_resetButton, BorderLayout.EAST);
		_mySmallPanel.setOpaque(true);
		_mySmallPanel.addFocusListener(this);

        updateFont();
	}

	
	protected void initJComboBox()
	{
		if (logger.isLoggable(Level.FINE))
			logger.fine("initJComboBox()");
		Dimension dimTemp = null;
		Point locTemp = null;
		Container parentTemp = null;
		if ((_jComboBox != null) && (_jComboBox.getParent() != null)) {
			dimTemp = _jComboBox.getSize();
			locTemp = _jComboBox.getLocation();
			parentTemp = _jComboBox.getParent();
			parentTemp.remove(_jComboBox);
			parentTemp.remove(_resetButton);
		}
		listModel=null;
		_jComboBox = new JComboBox(getListModel());
		/*if (getDataObject() == null) {
			Vector<Object> defaultValue = new Vector<Object>();
			defaultValue.add(FlexoLocalization.localizedForKey("no_selection"));
			_jComboBox = new JComboBox(defaultValue);
		} else {
			// TODO: Verify that there is no reason for this comboBoxModel to be cached.
			listModel=null;
			_jComboBox = new JComboBox(getListModel());
		}*/
		_jComboBox.setFont(getFont());
		_jComboBox.setRenderer(getListCellRenderer());
		_jComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (logger.isLoggable(Level.FINE))
					logger.fine("Action performed in " + this.getClass().getName());
				updateModelFromWidget();
			}
		});
		if (parentTemp != null) {
			//_jComboBox.setSize(dimTemp);
			_jComboBox.setLocation(locTemp);
			((JPanel) parentTemp).add(_jComboBox, BorderLayout.CENTER);
			if (getWidget().showReset)
				((JPanel) parentTemp).add(_resetButton, BorderLayout.EAST);
		}
		// Important: otherwise might be desynchronized
		_jComboBox.revalidate();
	}

	@Override
	public synchronized boolean updateWidgetFromModel()
	{
		if (notEquals(getValue(),_jComboBox.getSelectedItem()) || listModelRequireChange()) {

			if (logger.isLoggable(Level.FINE))
				logger.fine("updateWidgetFromModel()");
			widgetUpdating = true;
			initJComboBox();
			_jComboBox.setSelectedItem(getValue());
			widgetUpdating = false;
			return true;
		}
		return false;
	}


	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized boolean updateModelFromWidget()
	{
		if (notEquals(getValue(),_jComboBox.getSelectedItem())) {
			modelUpdating = true;
			if (logger.isLoggable(Level.FINE))
				logger.fine("updateModelFromWidget with " + _jComboBox.getSelectedItem());
			if ((_jComboBox.getSelectedItem() != null) && (!widgetUpdating)) {
				setValue(_jComboBox.getSelectedItem());
			}
			modelUpdating = false;
			return true;
		}
		return false;
	}

	@Override
	public MyComboBoxModel getListModel()
	{
		return (MyComboBoxModel)super.getListModel();
	}

	@Override
	protected MyComboBoxModel updateListModelWhenRequired()
	{
		if (listModel == null) {
			listModel = new MyComboBoxModel(getValue());
			if (_jComboBox != null) {
				_jComboBox.setModel(getListModel());
			}
		}
		return (MyComboBoxModel)listModel;
	}

	protected class MyComboBoxModel extends FIBMultipleValueModel implements ComboBoxModel
	{
		protected Object selectedItem = null;

		public MyComboBoxModel(Object selectedObject) {
			super();
			setSelectedItem(selectedObject);
		}
		
		@Override
		public void setSelectedItem(Object anItem)
		{
			selectedItem = anItem;
		}

		@Override
		public Object getSelectedItem()
		{
			return selectedItem;
		}

	}

	@Override
	public JPanel getJComponent() 
	{
		return _mySmallPanel;
	}

	@Override
	public JComboBox getDynamicJComponent()
	{
		return _jComboBox;
	}

	@Override
	public void updateFont()
	{
		super.updateFont();
		_jComboBox.setFont(getFont());
	}
	

}
