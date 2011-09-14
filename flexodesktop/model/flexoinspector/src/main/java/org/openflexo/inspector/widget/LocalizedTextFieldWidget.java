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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.LocalizedString;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.toolbox.ToolBox;

/**
 * Simple widget allowing to display/edit a String
 * 
 * @author bmangez
 */
public class LocalizedTextFieldWidget extends DenaliWidget<LocalizedString>
{

    private static final Logger logger = Logger.getLogger(LocalizedTextFieldWidget.class.getPackage()
            .getName());

    private JTextField _textField;
    private JComboBox _languageCB;
    private JPanel _panel;

    boolean validateOnReturn;

    // private boolean hasFocus = false;

    private static final int DEFAULT_COLUMNS = 5;

    public static final String COLUMNS_PARAM = "columns";
    public static final String PASSWORD_PARAM = "password";

    public static final String VALIDATE_ON_RETURN = "validateOnReturn";

    public static final String ADDITIONAL_LABEL = "additionalLabel";

    public LocalizedTextFieldWidget(PropertyModel model, AbstractController controller)
    {
        super(model,controller);
        if (model.hasValueForParameter(PASSWORD_PARAM) && model.getBooleanValueForParameter(PASSWORD_PARAM)) {
            _textField = new JPasswordField() {
                /**
                 * Overrides getMinimumSize
                 * @see javax.swing.JComponent#getMinimumSize()
                 */
                @Override
                public Dimension getMinimumSize()
                {
                    return MINIMUM_SIZE;
                }
            };
        } 
        else {
        	_textField = new JTextField() {
                /**
                 * Overrides getMinimumSize
                 * @see javax.swing.JComponent#getMinimumSize()
                 */
                @Override
                public Dimension getMinimumSize()
                {
                    return MINIMUM_SIZE;
                }
            };
        }
        
        _languageCB = new JComboBox(Language.availableValues());
        _languageCB.setSelectedItem(Language.ENGLISH);
        _languageCB.setRenderer(new DefaultListCellRenderer() {
        	@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
        	{
        		DefaultListCellRenderer label = (DefaultListCellRenderer) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
         		if (value instanceof Language) {
     				label.setText(((Language)value).getName());
        		} else if (value == null) {
        			label.setText(FlexoLocalization.localizedForKey("no_selection"));
        		}
        		label.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));
        		return label;
        	}
        });
        _languageCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (widgetUpdating) return;
				if (logger.isLoggable(Level.FINE))
					logger.fine("Action performed in " + this.getClass().getName());
				updateModelFromWidget();
			}
		});
        
        _panel = new JPanel(new BorderLayout());
        _panel.add(_textField,BorderLayout.CENTER);
        _panel.add(_languageCB,BorderLayout.EAST);
        
        
        if (model.hasValueForParameter(VALIDATE_ON_RETURN)) {
            validateOnReturn = model.getBooleanValueForParameter(VALIDATE_ON_RETURN);
        } else {
            validateOnReturn = false;
        }
        if (logger.isLoggable(Level.FINE))
            logger.fine("validateOnReturn=" + validateOnReturn);
        if (model.hasValueForParameter(COLUMNS_PARAM)) {
            int colNb = model.getIntValueForParameter(COLUMNS_PARAM);
            _textField.setColumns(colNb > 0 ? colNb : DEFAULT_COLUMNS);
        } else {
            _textField.setColumns(DEFAULT_COLUMNS);
        }
        _textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
			public void changedUpdate(DocumentEvent e)
            {
                // if (logger.isLoggable(Level.FINE)) logger.finer
                // ("changedUpdate() validateOnReturn="+validateOnReturn+"
                // widgetUpdating="+widgetUpdating);
                if ((!validateOnReturn) && (!widgetUpdating)) {
                    // if (logger.isLoggable(Level.FINE)) logger.fine
                    // ("changedUpdate()");e
                    updateModelFromWidget();
                }
            }

            @Override
			public void insertUpdate(DocumentEvent e)
            {
                // if (logger.isLoggable(Level.FINE)) logger.finer
                // ("insertUpdate() validateOnReturn="+validateOnReturn+"
                // widgetUpdating="+widgetUpdating);
                if ((!validateOnReturn) && (!widgetUpdating)) {
                    // if (logger.isLoggable(Level.FINE)) logger.fine
                    // ("insertUpdate()");
                	try {
						if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
							if (e.getLength() == 1) {
								char c = _textField.getText().charAt(
										e.getOffset());
								if (c == '´' || c == 'ˆ' || c=='˜'||c=='`'||c=='¨')
									return;
							}
						}
					} catch (RuntimeException e1) {
						e1.printStackTrace();
					}
                		
                    updateModelFromWidget();
                }
            }

            @Override
			public void removeUpdate(DocumentEvent e)
            {
                // if (logger.isLoggable(Level.FINE)) logger.finer
                // ("removeUpdate() validateOnReturn="+validateOnReturn+"
                // widgetUpdating="+widgetUpdating);
                if ((!validateOnReturn) && (!widgetUpdating)) {
                    // if (logger.isLoggable(Level.FINE)) logger.fine
                    // ("removeUpdate()");
                    updateModelFromWidget();
                }
            }
        });
        _textField.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e)
            {
                // if (logger.isLoggable(Level.FINE)) logger.fine
                // ("actionPerformed()");
                updateModelFromWidget();
            }
        });
        getDynamicComponent().addFocusListener(new WidgetFocusListener(this) {
            @Override
			public void focusGained(FocusEvent arg0)
            {
                super.focusGained(arg0);
                ((JTextField) getDynamicComponent()).selectAll();
            }
        });
    }

    @Override
	public Class getDefaultType()
    {
        return LocalizedString.class;
    }

    @Override
	public synchronized void updateWidgetFromModel()
    {
        // if (logger.isLoggable(Level.FINE)) logger.fine ("BEGIN
        // updateWidgetFromModel()");
        if (logger.isLoggable(Level.FINE)) logger.fine("updateWidgetFromModel() in TextFieldWidget");
        if (modelUpdating)
            return;
        widgetUpdating = true;
        int caret = _textField.getCaretPosition();
        _textField.setText(getObjectValue().string);
        if (caret>-1 && caret<_textField.getDocument().getLength())
        	_textField.setCaretPosition(caret);
        _languageCB.setSelectedItem(getObjectValue().language);
        widgetUpdating = false;
        // if (logger.isLoggable(Level.INFO)) logger.info ("END
        // updateWidgetFromModel()");
        
    }

    /**
     * Update the model given the actual state of the widget
     */
    @Override
	public synchronized void updateModelFromWidget()
    {
        // if (logger.isLoggable(Level.INFO)) logger.info ("BEGIN
        // updateModelFromWidget()");
        modelUpdating = true;
        if (logger.isLoggable(Level.FINE)) logger.fine("updateModelFromWidget() in TextFieldWidget");
        setObjectValue(new LocalizedString(_textField.getText(),(Language)_languageCB.getSelectedItem()));
        modelUpdating = false;
        // if (logger.isLoggable(Level.INFO)) logger.info ("END
        // updateModelFromWidget()");
    }

    @Override
	public JComponent getDynamicComponent()
    {
        return _panel;
    }

}
