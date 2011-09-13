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
package org.openflexo.inspector.widget;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.localization.FlexoLocalization;

/**
 * Represents a widget able to select an object in a list of objects. The
 * represented object could be:
 * <ul>
 * <li> an instance of
 *
 * <pre>
 * ChoiceList
 * </pre>
 *
 * (in this case, no need to precise range of values)</li>
 * <li> a simple Object: in this case you must specify the range of values
 * represented value could take: it can be a static list, defined by
 *
 * <pre>
 * staticlist
 * </pre>
 *
 * tag (the object must be a String or a StringConvertable), or a dynamic list,
 * which is a key-value coded access to a Vector (defined by the tag
 *
 * <pre>
 * dynamiclist&lt;pre&gt; or a dynamic hashtable, which is a key-value coded access
 *  to a Hashtable (defined by the tag &lt;pre&gt;dynamichash&lt;pre&gt;)
 * </li>
 * </ul>
 *  To represent the object a key-value coded access defines the string to be displayed
 *  (see tag &lt;pre&gt;format&lt;/pre&gt;). If the object is a &lt;pre&gt;StringConvertable&lt;/pre&gt;, used
 *  String is the string representation given by the related converter.
 *
 *  @author sguerin
 *
 */
public class DropDownWidget extends MultipleValuesWidget<Object>
{

	static final Logger logger = Logger.getLogger(DropDownWidget.class.getPackage().getName());

	public static final String WIDTH_PARAM = "width";
	public static final String HEIGHT_PARAM = "height";
	private static final int DEFAULT_WIDTH = 150;
	private static final int DEFAULT_HEIGHT = 25;

	private JButton _resetButton;

	private JPanel _mySmallPanel;

	protected JComboBox _jComboBox;

	private DenaliComboBoxModel comboBoxModel = null;

	private Dimension _preferredSize;

	public DropDownWidget(PropertyModel model, AbstractController controller)
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
				/*
				 * initJComboBox(); _jComboBox.getParent().validate();
				 * _jComboBox.getParent().repaint();
				 */
				setObjectValue(null);
			}
		});

		_mySmallPanel.add(_jComboBox, BorderLayout.CENTER);
		if (!model.hasValueForParameter("showReset") || model.getBooleanValueForParameter("showReset"))
			_mySmallPanel.add(_resetButton, BorderLayout.EAST);
		//_mySmallPanel.setBackground(InspectorCst.BACK_COLOR);
		_mySmallPanel.setOpaque(true);
		_mySmallPanel.setMinimumSize(MINIMUM_SIZE);
		getDynamicComponent().addFocusListener(new WidgetFocusListener(this));
		_preferredSize = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		if (model.hasValueForParameter(WIDTH_PARAM)) {
			_preferredSize.width = model.getIntValueForParameter(WIDTH_PARAM);
		}
		if (model.hasValueForParameter(HEIGHT_PARAM)) {
			_preferredSize.height = model.getIntValueForParameter(HEIGHT_PARAM);
		}
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
		if (getModel() == null) {
			Vector<Object> defaultValue = new Vector<Object>();
			defaultValue.add(FlexoLocalization.localizedForKey("no_selection"));
			_jComboBox = new JComboBox(defaultValue);
		} else {
			// TODO: Verify that there is no reason for this comboBoxModel to be cached.
			comboBoxModel=null;
			_jComboBox = new JComboBox(getComboBoxModel());
		}
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
			_jComboBox.setSize(dimTemp);
			_jComboBox.setLocation(locTemp);
			((JPanel) parentTemp).add(_jComboBox, BorderLayout.CENTER);
			String s=_propertyModel.getValueForParameter("showReset");
			if (s==null || !s.toLowerCase().equals("false"))

				((JPanel) parentTemp).add(_resetButton, BorderLayout.EAST);
		}
		// Important: otherwise might be desynchronized
		_jComboBox.revalidate();
		_jComboBox.setMinimumSize(_preferredSize);
		_jComboBox.setPreferredSize(_preferredSize);
	}

	@Override
	protected void setModel(InspectableObject value)
	{
		super.setModel(value);
		comboBoxModel = new DenaliComboBoxModel();
	}

	@Override
	public JComponent getDynamicComponent()
	{
		return _mySmallPanel;
	}

	/*@Override
	public void performUpdate() {
		updateWidgetFromModel();
	}*/

	@Override
	public synchronized void updateWidgetFromModel()
	{
		if (logger.isLoggable(Level.FINE))
			logger.fine("updateWidgetFromModel()");
		widgetUpdating = true;
		initJComboBox();
		_jComboBox.setSelectedItem(getObjectValue());
		widgetUpdating = false;
	}

	/**
	 * Update the model given the actual state of the widget
	 */
	@Override
	public synchronized void updateModelFromWidget()
	{
		modelUpdating = true;
		if (logger.isLoggable(Level.FINE))
			logger.fine("updateModelFromWidget with " + _jComboBox.getSelectedItem());
		if ((_jComboBox.getSelectedItem() != null) && (!widgetUpdating)) {
			setObjectValue(_jComboBox.getSelectedItem());
		}
		modelUpdating = false;
	}

	public DenaliComboBoxModel getComboBoxModel()
	{
		if (comboBoxModel == null) {
			comboBoxModel = new DenaliComboBoxModel();
		}
		return comboBoxModel;
	}

	protected class DenaliComboBoxModel extends DenaliListModel implements ComboBoxModel
	{
		protected Object selectedItem = null;

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

	/*public void update(InspectableObject inspectable, InspectableModification modification)
    {
        logger.info("update in " + this.getClass().getName() + " with " + modification);
        super.update(inspectable,modification);
    }*/
}
