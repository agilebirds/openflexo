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
package org.openflexo.ie.view.widget;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ie.dm.ButtonRemoved;
import org.openflexo.foundation.ie.dm.CustomButtonValueChanged;
import org.openflexo.foundation.ie.widget.IECustomButtonWidget;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.ie.view.listener.DoubleClickResponder;


public class IECustomButtonWidgetView extends IEWidgetView<IECustomButtonWidget> implements DoubleClickResponder, LabeledWidget
{
    private static final int ROUNDED_BORDER_SIZE = 1;

    private static final Logger logger = Logger.getLogger(IECustomButtonWidgetView.class.getPackage().getName());

    protected JLabel _label;

    JTextField _jLabelTextField;

    private boolean labelEditing = false;

    /**
     * @param model
     */
    public IECustomButtonWidgetView(IEController ieController, IECustomButtonWidget model, boolean addDnDSupport,
    		IEWOComponentView componentView)
    {
        super(ieController, model, addDnDSupport, componentView);
        setOpaque(false);
        // File imageIconFile = getImageIconPath(model);
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        createViewForButtonWithName();
    }

    private void createViewForButtonWithName()
    {

        _label = new JLabel() {
            /**
             * Overrides paint
             * 
             * @see javax.swing.JComponent#paint(java.awt.Graphics)
             */
            @Override
            public void paint(Graphics g)
            {
                super.paint(g);
                Rectangle bounds = getBounds();
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, ROUNDED_BORDER_SIZE, ROUNDED_BORDER_SIZE);
                g.fillRect(0, bounds.height - ROUNDED_BORDER_SIZE, ROUNDED_BORDER_SIZE, ROUNDED_BORDER_SIZE);
                g.fillRect(bounds.width - ROUNDED_BORDER_SIZE, 0, ROUNDED_BORDER_SIZE, ROUNDED_BORDER_SIZE);
                g.fillRect(bounds.width - ROUNDED_BORDER_SIZE, bounds.height - ROUNDED_BORDER_SIZE, ROUNDED_BORDER_SIZE,
                        ROUNDED_BORDER_SIZE);
            }

            /**
             * Overrides getPreferredSize
             * 
             * @see javax.swing.JComponent#getPreferredSize()
             */
            @Override
            public Dimension getPreferredSize()
            {
                String s = getCustomButtonModel().getCustomButtonValue();
                if (s == null)
                    return new Dimension(30, 15);
                else {
                    return new Dimension(
                            (int) (_label.getFontMetrics(_label.getFont()).getStringBounds(s, _label.getGraphics()).getWidth() + 32), 15);
                }
            }
        };
        Color color = getFlexoCSS().getButtonColor();

        if (color == null)
            color = Color.BLACK;
        _label.setMinimumSize(new Dimension(30, 15));
        _label.setBackground(color);
        // setBackground(color);
        _label.setForeground(Color.WHITE);
        _label.setText(getModel().getCustomButtonValue());
        if (getCustomButtonModel().getCustomButtonValue() == null || getCustomButtonModel().getCustomButtonValue().length() == 0) {
            _label.setText("   ");

        }
        _label.setFont(getButtonFont());
        _label.setOpaque(true);
        _label.setBorder(BorderFactory.createMatteBorder(0, 15, 1, 15, color));
        _label.setVerticalTextPosition(SwingConstants.CENTER);
        _label.setHorizontalAlignment(SwingConstants.CENTER);
        add(_label);
        validate();
        repaint();
    }

    private static Font getButtonFont()
    {
        return IEHyperlinkWidget.getButtonFont();
    }

    protected IECustomButtonWidget getCustomButtonModel()
    {
        return getModel();
    }

    @Override
	public void editLabel()
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("Edit ie custom button");
        labelEditing = true;
        _jLabelTextField = new JTextField(getModel().getCustomButtonValue()) {
            /**
             * Overrides paint
             * 
             * @see javax.swing.JComponent#paint(java.awt.Graphics)
             */
            @Override
            public void paint(Graphics g)
            {
                super.paint(g);
                Rectangle bounds = getBounds();
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, ROUNDED_BORDER_SIZE, ROUNDED_BORDER_SIZE);
                g.fillRect(0, bounds.height - ROUNDED_BORDER_SIZE, ROUNDED_BORDER_SIZE, ROUNDED_BORDER_SIZE);
                g.fillRect(bounds.width - ROUNDED_BORDER_SIZE, 0, ROUNDED_BORDER_SIZE, ROUNDED_BORDER_SIZE);
                g.fillRect(bounds.width - ROUNDED_BORDER_SIZE, bounds.height - ROUNDED_BORDER_SIZE, ROUNDED_BORDER_SIZE,
                        ROUNDED_BORDER_SIZE);
            }

            /**
             * Overrides getPreferredSize
             * 
             * @see javax.swing.JComponent#getPreferredSize()
             */
            @Override
            public Dimension getPreferredSize()
            {
                String s = getCustomButtonModel().getCustomButtonValue();
                if (s == null)
                    return new Dimension(30, 15);
                else {
                    return new Dimension(
                            (int) (_label.getFontMetrics(_label.getFont()).getStringBounds(s, _label.getGraphics()).getWidth() + 32), 15);
                }
            }
        };
        _jLabelTextField.setBackground(_label.getBackground());
        _jLabelTextField.setForeground(_label.getForeground());
        _jLabelTextField.setMinimumSize(new Dimension(30, 15));
        _jLabelTextField.setFont(getButtonFont());
        _jLabelTextField.setMargin(new Insets(0, 15, 1, 15));
        _jLabelTextField.setBorder(BorderFactory.createMatteBorder(0, 15, 1, 15, _label.getBackground()));
        _jLabelTextField.setHorizontalAlignment(SwingConstants.CENTER);
        _jLabelTextField.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent event)
            {
                finalizeEditButton();
            }
        });

        _jLabelTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
			public void insertUpdate(DocumentEvent event)
            {
                updateSize();
            }

            @Override
			public void removeUpdate(DocumentEvent event)
            {
                updateSize();
            }

            @Override
			public void changedUpdate(DocumentEvent event)
            {
                updateSize();
            }

            public void updateSize()
            {
                revalidate();
                repaint();
            }
        });
        _jLabelTextField.addFocusListener(new FocusAdapter() {
            @Override
			public void focusLost(FocusEvent arg0)
            {
                finalizeEditButton();
            }
        });
        remove(_label);
        add(_jLabelTextField);
        _jLabelTextField.requestFocus();
        _jLabelTextField.selectAll();
        _jLabelTextField.revalidate();
        _jLabelTextField.repaint();
        revalidate();
        repaint();
    }
    /**
     * Overrides getPreferredSize
     * @see javax.swing.JComponent#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize()
    {
        String s = _label.getText();
        if (s==null)
            return new Dimension(30,17);
        else {
            return new Dimension((int) _label.getPreferredSize().getWidth()+2,17);
        }
    }

    public void finalizeEditButton()
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("Finalize edit ie custombutton");
        _label.setText(getCustomButtonModel().getCustomButtonValue());
        if (getCustomButtonModel().getCustomButtonValue() == null || getCustomButtonModel().getCustomButtonValue().length() == 0) {
            _label.setText("   ");
        }
        if (labelEditing)
            getCustomButtonModel().setCustomButtonValue(_jLabelTextField.getText());
        labelEditing = false;
        remove(_jLabelTextField);
        add(_label);
        // layoutComponent();
        revalidate();
        repaint();
        // getIEController().clearEditedButton();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */

    @Override
	public void update(FlexoObservable arg0, DataModification arg1)
    {
        if (getModel() == arg0 && arg1 instanceof ButtonRemoved) {
            JComponent parent = (JComponent) getParent();
            delete();
            if (parent != null) {
                parent.validate();
                parent.repaint();
            }
        }
        if (getModel() == arg0 && arg1 instanceof CustomButtonValueChanged) {
            _label.setText(getCustomButtonModel().getCustomButtonValue());
            if (getCustomButtonModel().getCustomButtonValue() == null || getCustomButtonModel().getCustomButtonValue().length() == 0) {
                _label.setText("   ");
            }
            doLayout();
            repaint();
        } else
            super.update(arg0, arg1);
    }

    /**
     * Overrides performDoubleClick
     * 
     * @see org.openflexo.ie.view.listener.DoubleClickResponder#performDoubleClick(javax.swing.JComponent,
     *      java.awt.Point, boolean)
     */
    @Override
	public void performDoubleClick(JComponent clickedContainer, Point clickedPoint, boolean isShiftDown)
    {
    	editLabel();
    }
}
