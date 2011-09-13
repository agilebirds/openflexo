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

import java.awt.ComponentOrientation;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.toolbox.ToolBox;


/**
 * Represents a widget able to edit an int or an Integer object
 * 
 * @author sguerin
 */
public class IntegerWidget extends DenaliWidget<Integer> {

    static final Logger logger = Logger.getLogger(IntegerWidget.class.getPackage().getName());

    boolean validateOnReturn;

    private static final String VALIDATE_ON_RETURN_PARAM = "validateOnReturn";
    
    public static final String MIN_VALUE_PARAM = "minimum";
    public static final String MAX_VALUE_PARAM = "maximum";
    public static final String INCREMENT_VALUE_PARAM = "increment";

    private static final int DEFAULT_MIN_VALUE = Integer.MIN_VALUE;
    private static final int DEFAULT_MAX_VALUE = Integer.MAX_VALUE;
    private static final int DEFAULT_INC_VALUE = 1;
    
    
    protected boolean ignoreTextfieldChanges=false;

    JSpinner valueChooser;
    
    /**
     * @param model
     */
    public IntegerWidget(PropertyModel model, AbstractController controller)
    {
        super(model,controller);
        if (model.hasValueForParameter(VALIDATE_ON_RETURN_PARAM)) {
            validateOnReturn = model.getBooleanValueForParameter(VALIDATE_ON_RETURN_PARAM);
        } else {
            validateOnReturn = false;
        }
        
        int min,max,inc;
        if (model.hasValueForParameter(MIN_VALUE_PARAM)) {
        	min = model.getIntValueForParameter(MIN_VALUE_PARAM);
        } else {
        	min = DEFAULT_MIN_VALUE;
        }
        if (model.hasValueForParameter(MAX_VALUE_PARAM)) {
        	max = model.getIntValueForParameter(MAX_VALUE_PARAM);
        } else {
        	max = DEFAULT_MAX_VALUE;
        }
        if (model.hasValueForParameter(INCREMENT_VALUE_PARAM)) {
        	inc = model.getIntValueForParameter(INCREMENT_VALUE_PARAM);
        } else {
        	inc = DEFAULT_INC_VALUE;
        }
         
        SpinnerNumberModel valueModel = new SpinnerNumberModel(min, min,max,inc);
        valueChooser = new JSpinner(valueModel);
        valueChooser.setEditor(new JSpinner.NumberEditor(valueChooser, "#"));
        valueChooser.setValue(1);
        valueChooser.addChangeListener(new ChangeListener() {
            @Override
			public void stateChanged(ChangeEvent e)
            {
                if (e.getSource() == valueChooser) {
                   	updateModelFromWidget();
                }                       
             }
        });
        valueChooser.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        JComponent editor = valueChooser.getEditor();
        if (editor instanceof DefaultEditor) {
            ((DefaultEditor)editor).getTextField().setHorizontalAlignment(SwingConstants.LEFT);
            if(ToolBox.getPLATFORM()!=ToolBox.MACOS)
            	((DefaultEditor)editor).getTextField().setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
        }
       /* int colNb = DEFAULT_COLUMNS;
        if (model.hasValueForParameter(COLUMNS_PARAM)) {
        	colNb = model.getIntValueForParameter(COLUMNS_PARAM);
        }
        valueChooser.setPreferredSize(new Dimension(colNb*10,valueChooser.getPreferredSize().height));
        valueChooser.setMinimumSize(valueChooser.getPreferredSize());*/
              

        if (isReadOnly()){
        	valueChooser.setEnabled(false);
        } 

        /*if (model.hasValueForParameter(COLUMNS_PARAM)) {
        int colNb = model.getIntValueForParameter(COLUMNS_PARAM);
        _textField.setColumns(colNb > 0 ? colNb : DEFAULT_COLUMNS);
    } else {
        _textField.setColumns(DEFAULT_COLUMNS);
    }*/
        
        //_textField = new JTextField();
        /*if (model.hasValueForParameter(COLUMNS_PARAM)) {
            int colNb = model.getIntValueForParameter(COLUMNS_PARAM);
            _textField.setColumns(colNb > 0 ? colNb : DEFAULT_COLUMNS);
        } else {
            _textField.setColumns(DEFAULT_COLUMNS);
        }*/
        /*if (model.hasValueForParameter(READONLY_TEXTFIELD) && model.getBooleanValueForParameter(READONLY_TEXTFIELD)){
        	_textField.setEditable(false);
        	_textField.setEnabled(true);
        	_textField.setSelectionColor(InspectorCst.SELECTED_TEXT_COLOR);
        	_textField.setSelectedTextColor(Color.WHITE);
        	_textField.addFocusListener(new FocusListener() {

                public void focusGained(FocusEvent e)
                {
                	_textField.selectAll();
                }

                public void focusLost(FocusEvent e)
                {

                }

            });
        }*/
        
        
        
        
      /*  _textField.addFocusListener(new FocusListener() {

            public void focusGained(FocusEvent e)
            {
                
            }

            public void focusLost(FocusEvent e)
            {
                if (!validateOnReturn && !ignoreTextfieldChanges) {
                    if (logger.isLoggable(Level.FINE))
                        logger.fine("changedUpdate()");
                    updateModelFromWidget();
                }                
            }
            
        });
        _textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                if (logger.isLoggable(Level.FINE))
                    logger.fine("actionPerformed()");
                updateModelFromWidget();
            }
        });
        _upButton = new JButton(PLUS_IMAGE);
        _downButton = new JButton(MINUS_IMAGE);
        _upButton.setBackground(InspectorCst.BACK_COLOR);
        _upButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                increase();
            }
        });
        _downButton.setBackground(InspectorCst.BACK_COLOR);
        _downButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                decrease();
            }
        });
        if (ToolBox.getPLATFORM().equals(ToolBox.MACOS)) {
            _upButton.setSize(12, 12);
            _upButton.setLocation(0, 0);
            _upButton.setHorizontalAlignment(SwingConstants.CENTER);
            _downButton.setSize(12, 12);
            _downButton.setLocation(0, 12);
            _downButton.setHorizontalAlignment(SwingConstants.CENTER);
        } else {
            _upButton.setSize(PLUS_IMAGE.getIconWidth() + 1, PLUS_IMAGE.getIconHeight());
            _upButton.setLocation(0, 1);
            _downButton.setSize(MINUS_IMAGE.getIconWidth() + 1, MINUS_IMAGE.getIconHeight());
            _downButton.setLocation(0, 13);
        }
*/
        /*_panel = new JPanel(null) {
              @Override
            public Dimension getMinimumSize()
            {
                return MINIMUM_SIZE;
            }
            
            @Override
            public Dimension getPreferredSize()
            {
                Dimension d = _textField.getPreferredSize();
                if (d.height<25)
                    d.height=25;
                d.width+=15;
                return d;
            }
        };
        _panel.setBackground(InspectorCst.BACK_COLOR);

        _textField.setSize(100, 24);
        _textField.setLocation(15, 0);
        _panel.add(_upButton);
        _panel.add(_downButton);
        _panel.add(_textField);
        */
        
        getDynamicComponent().addFocusListener(new WidgetFocusListener(this));

    }

 /*   public void increase()
    {
        ignoreTextfieldChanges=true;
        _textField.setText(String.valueOf(_currentValue+1));
        updateModelFromWidget();
        updateWidgetFromModel();
        ignoreTextfieldChanges=false;
    }

    public void decrease()
    {
        ignoreTextfieldChanges=true;
        _textField.setText(String.valueOf(_currentValue-1));
        updateModelFromWidget();
        updateWidgetFromModel();
        ignoreTextfieldChanges=false;
    }*/

    @Override
	public synchronized void updateWidgetFromModel()
    {
    	widgetUpdating = true;
        Integer currentValue = null;

        if (getObjectValue() == null) {
            setObjectValue(new Integer(0));
        }
        currentValue = getObjectValue(); 
        
        
        /*if (getType() == Integer.class ) {
            if (getObjectValue() == null) {
                setObjectValue(new Integer(0));
            }
            currentValue = ((Integer) getObjectValue()).intValue();
        } else if (getType() == Integer.TYPE) {
            currentValue = getIntegerValue();
        } else if (getType() == String.class) {
            try {
                Object o = getObjectValue();
                if (o==null) {
                    currentValue = 0;
                    return;
                }
                currentValue = new Integer((String) o).intValue();
            } catch (NumberFormatException e) {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Property " + _propertyModel.name + " is supposed to represents an Integer but values '"
                            + (String) getObjectValue() + "'");
                currentValue = 1;
            }
        } else {
            if (logger.isLoggable(Level.WARNING))
                logger
                        .warning("Property " + _propertyModel.name + " is supposed to be a int or an Integer or a String, not a "
                                + getType());
        }*/
        ignoreTextfieldChanges=true;
        
        try {
        	valueChooser.setValue(currentValue);
        }
        catch (IllegalArgumentException e) {
        	e.printStackTrace();
        	logger.warning("Unexpected exception "+e.getMessage());
        }
        
       // _textField.setText("" + _currentValue);
        ignoreTextfieldChanges=false;
        widgetUpdating = false;
    }

    /**
     * Update the model given the actual state of the widget
     */
    @Override
	public synchronized void updateModelFromWidget()
    {
    	if (isReadOnly())
    		return;
        modelUpdating = true;
        int currentValue = (Integer)valueChooser.getValue();
        
        setObjectValue(currentValue);
        
        /*if (getType() == Integer.class) {
            setObjectValue(currentValue);
        } else if (getType() == Integer.TYPE) {
            setIntegerValue(currentValue);
        } else if (getType() == String.class) {
            setObjectValue((new Integer(currentValue)).toString());
        }  else {
            if (logger.isLoggable(Level.WARNING))
                logger
                        .warning("Property " + _propertyModel.name + " is supposed to be a int or an Integer, not a "
                                + getType());
        }*/
        modelUpdating = false;
    }

    @Override
	public JComponent getDynamicComponent()
    {
        return valueChooser;
    }

    @Override
	public Class getDefaultType()
    {
        return Integer.class;
    }

}
