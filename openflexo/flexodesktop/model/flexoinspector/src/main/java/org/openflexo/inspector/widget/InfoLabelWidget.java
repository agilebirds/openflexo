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

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.localization.FlexoLocalization;

/**
 * @author gpolet Created on 5 oct. 2005
 */
public class InfoLabelWidget extends DenaliWidget
{

	public static final String ROWS = "rows";
	public static final String COLUMNS = "columns";

    private JTextArea infoLabel;
    private static final int DEFAULT_ROWS = 3;
    private int rows;
    private static final int DEFAULT_COLUMNS = 40;
    private int columns;

    /**
     * @param model
     */
    public InfoLabelWidget(PropertyModel model, AbstractController controller)
    {
        super(model,controller);
        infoLabel = initInfoLabel();

        if (model.hasValueForParameter(ROWS)) {
        	rows = model.getIntValueForParameter(ROWS);
       }
        else {
        	rows = DEFAULT_ROWS;
        }
        if (model.hasValueForParameter(COLUMNS)) {
        	columns = model.getIntValueForParameter(COLUMNS);
       }
        else {
        	columns = DEFAULT_COLUMNS;
        }
        infoLabel.setColumns(columns);
        infoLabel.setRows(rows);
    }

    @Override
	public boolean defaultDisplayLabel()
    {
    	return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openflexo.inspector.widget.DenaliWidget#updateWidgetFromModel()
     */
    @Override
	public void updateWidgetFromModel()
    {
        widgetUpdating = true;
        infoLabel.setText(getStringValue());
        widgetUpdating = false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openflexo.inspector.widget.DenaliWidget#updateModelFromWidget()
     */
    @Override
	public void updateModelFromWidget()
    {
        // Empty block on purpose since this is read-only <-- Look at this
        // Master comment!
    }

    public JTextArea initInfoLabel()
    {
        if (infoLabel == null) {
            infoLabel = new JTextArea(rows, columns);
            infoLabel.setLineWrap(true);
            infoLabel.setWrapStyleWord(true);
            infoLabel.setFont(DEFAULT_MEDIUM_FONT);
            infoLabel.setEditable(false);
            infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 50, 20, 50));
        //	infoLabel = new JLabel(_propertyModel.label + " : ", SwingConstants.LEFT);
        	//String textLabel = "balbalbalblbaalblb";
        //	infoLabel.setText(textLabel);
        	//infoLabel.setText(FlexoLocalization.localizedForKey(_propertyModel.label, " : ", _label));
        //	infoLabel.setBackground(Color.WHITE);
        	//infoLabel.setFont(DEFAULT_LABEL_FONT);
        	//if (_propertyModel.help != null && !_propertyModel.help.equals(""))
         //       infoLabel.setToolTipText(_propertyModel.help);
        }
        return infoLabel;
    }

    @Override
	public JLabel getLabel()
    {
        if (_label == null) {
            _label = new JLabel("",SwingConstants.CENTER);
            if (_propertyModel.label!=null && _propertyModel.label.trim().length()>0)
                _label.setText(FlexoLocalization.localizedForKey(_propertyModel.label, _label));
            //_label.setBackground(InspectorCst.BACK_COLOR);
            _label.setFont(DEFAULT_LABEL_FONT);
            _label.setBorder(BorderFactory.createEmptyBorder(20, 50, 10, 50));
            if (_propertyModel.help != null && _propertyModel.help.trim().length()>0)
                _label.setToolTipText(_propertyModel.help);
        }
        return _label;
    }

    @Override
	public JComponent getDynamicComponent()
    {
        return infoLabel;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openflexo.inspector.widget.DenaliWidget#getDefaultType()
     */
    @Override
	public Class getDefaultType()
    {
        return String.class;
    }

    @Override
	public WidgetLayout getDefaultWidgetLayout()
    {
    	return WidgetLayout.LABEL_ABOVE_WIDGET_LAYOUT;
    }

    @Override
	public boolean defaultShouldExpandVertically()
    {
    	return true;
    }

}
