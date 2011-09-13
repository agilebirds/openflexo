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

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;


/**
 * Simple widget allowing to view a label (HTML allowed)
 * 
 * Usage:
 * 
 * <property name="xxx" label="a_label" widget="LABEL">
 * 	...
 * 	<param name="displayLabel" value="true"/>     		<!-- Label will be displayed -->
 * 	<param name="expandHorizontally" value="true"/>     <!-- Available horizontal space is "taken" by the dynamic component, when possible -->
 * 	<param name="expandVertically" value="true"/>     <!-- Available vertical space is "taken" by the dynamic component, when possible -->
 * 	<param name="widgetLayout" value="2COL"/>     <!-- 2COL means that label will be let left to the dynamic component, 1COL means that the label is laid above dynamic component -->
 *	<param name="align" value="CENTER"/>   <!-- horizontal alignment : LEFT / RIGHT / CENTER -->
 * </property>
 * 
 * @author sguerin
 */
public class LabelWidget extends DenaliWidget
{

    public static final String ALIGN = "align";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String LEFT = "left";
    public static final String RIGHT = "right";
    public static final String CENTER = "center";

    private static final int DEFAULT_WIDTH = 200;
    private static final int DEFAULT_HEIGHT = 20;
    
    private JLabel labelWidget;

     public LabelWidget(PropertyModel model, AbstractController controller)
    {
        super(model,controller);
        labelWidget = new JLabel();
        labelWidget.setHorizontalAlignment(SwingConstants.LEFT);
        if (model.hasValueForParameter(ALIGN)) {
        	if (model.getValueForParameter(ALIGN).equalsIgnoreCase(LEFT)) {
        		labelWidget.setHorizontalAlignment(SwingConstants.LEFT);
        	}
        	else if (model.getValueForParameter(ALIGN).equalsIgnoreCase(RIGHT)) {
        		labelWidget.setHorizontalAlignment(SwingConstants.RIGHT);
        	}
           	else if (model.getValueForParameter(ALIGN).equalsIgnoreCase(CENTER)) {
        		labelWidget.setHorizontalAlignment(SwingConstants.CENTER);
        	}
       }
        if (model.hasValueForParameter(WIDTH) || model.hasValueForParameter(HEIGHT)) {
        	Dimension preferredSize = new Dimension(DEFAULT_WIDTH,DEFAULT_HEIGHT);
        	if (model.hasValueForParameter(WIDTH)) {
        		preferredSize.width = model.getIntValueForParameter(WIDTH);
        	}
        	if (model.hasValueForParameter(HEIGHT)) {
        		preferredSize.height = model.getIntValueForParameter(HEIGHT);
        	}
        	labelWidget.setPreferredSize(preferredSize);
        }
    }

    @Override
	public Class getDefaultType()
    {
        return String.class;
    }

    @Override
	public synchronized void updateWidgetFromModel()
    {
        if (modelUpdating)
            return;
        widgetUpdating = true;
        labelWidget.setText(getStringValue());
        widgetUpdating = false;
     }

    /**
     * Update the model given the actual state of the widget
     */
    @Override
	public synchronized void updateModelFromWidget()
    {
    	// Read only component
    }

    @Override
	public JComponent getDynamicComponent()
    {
        return labelWidget;
    }

}
