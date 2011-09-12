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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ie.dm.ListOfValuesHasChanged;
import org.openflexo.foundation.ie.dm.table.WidgetRemovedFromTable;
import org.openflexo.foundation.ie.widget.IEDropDownWidget;
import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.controller.IEController;


/**
 * @author bmangez
 *
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class IEDropDownWidgetView extends AbstractInnerTableWidgetView<IEDropDownWidget>
{

    // ==========================================================================
    // ============================= Variables
    // ==================================
    // ==========================================================================
    private transient JComboBox _jComboBox;

    private JPanel container;

    protected TransparentMouseListener tml;

    public static final String BINDING_ISCHECKED_NAME = "isChecked";

    public static final String ATTRIB_DEFAULTVALUE_NAME = "defaultValue";

    public static final String ATTRIB_EXAMPLELIST_NAME = "exampleList";

    public static final Font TEXTFIELD_FONT = new Font("SansSerif", Font.PLAIN, 10);

    public IEDropDownWidgetView(IEController ieController, IEDropDownWidget model, boolean addDnDSupport, IEWOComponentView view)
    {
        super(ieController, model, addDnDSupport,view);
        FlowLayout layout = new FlowLayout(FlowLayout.CENTER,0,0);
        setLayout(layout);
        _jComboBox = new JComboBox(new Vector<Object>(model.getValueList()));
        _jComboBox.setFont(TEXTFIELD_FONT);
        _jComboBox.setFocusable(false);
        tml = new TransparentMouseListener(_jComboBox, this);
        _jComboBox.addMouseListener(tml);
        _jComboBox.addMouseMotionListener(tml);
        container = new JPanel(new FlowLayout(FlowLayout.CENTER,4,4));
        container.setOpaque(false);
        add(container);
        container.add(_jComboBox);
        if (getDropDownModel().getDescription() != null) {
            _jComboBox.setToolTipText(getDropDownModel().getDescription());
        }
        if (getDropDownModel().getValue() != null) {
            _jComboBox.setSelectedItem(getDropDownModel().getValue());
        } else {
            _jComboBox.setSelectedIndex(0);
        }
        _jComboBox.addActionListener(new ActionListener(){

            @Override
			public void actionPerformed(ActionEvent e)
            {
                JComboBox cb = (JComboBox)e.getSource();
                (getModel()).setValue((String)cb.getSelectedItem());
            }
        });
        _jComboBox.setRenderer(new DefaultListCellRenderer() {
            /**
             * Overrides getListCellRendererComponent
             * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
             */
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
            {

                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                l.addMouseListener(tml);
                l.addMouseMotionListener(tml);
                return l;
            }
        });
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        container.setBackground(getBackgroundColor());
    }

    public IEDropDownWidget getDropDownModel()
    {
        return getModel();
    }

    @Override
    public Dimension getPreferredSize()
    {
    	if (getHoldsNextComputedPreferredSize()){
        	Dimension storedSize = storedPrefSize();
            if(storedSize!=null)return storedSize;
        }
		if(getModel().getParent() instanceof IETDWidget){
			Dimension d = container.getPreferredSize();
            d.width+=2;
            d.height+=2;
            if (getHoldsNextComputedPreferredSize())
                storePrefSize(d);
			return d;
		}
		Dimension d = super.getPreferredSize();
        if (getHoldsNextComputedPreferredSize())
            storePrefSize(d);
        return d;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(FlexoObservable arg0, DataModification modif)
    {
        if (modif.modificationType() == DataModification.ATTRIBUTE) {
            if (modif.propertyName().equals("value")) {
                if (getDropDownModel().getValue() != null) {
                    _jComboBox.setSelectedItem(getDropDownModel().getValue());
                } else {
                    _jComboBox.setSelectedIndex(0);
                }

            } else if (modif.propertyName().equals(ATTRIB_EXAMPLELIST_NAME) || modif.propertyName().equals("domain") || modif instanceof ListOfValuesHasChanged) {
            	container.remove(_jComboBox);
                _jComboBox = new JComboBox(new Vector<Object>(getModel().getValueList()));
                _jComboBox.setFont(TEXTFIELD_FONT);
                if (getDropDownModel().getValue() != null) {
                    _jComboBox.setSelectedItem(getDropDownModel().getValue());
                } else {
                    _jComboBox.setSelectedIndex(0);
                }
                _jComboBox.setFocusable(false);
                container.add(_jComboBox);
                validate();
                repaint();
            } else if (modif.propertyName().equals("colSpan") || modif.propertyName().equals("rowSpan")) {
                getParent().doLayout();
                ((JComponent) getParent()).repaint();
            }
        }
        if (modif instanceof WidgetRemovedFromTable && arg0 == getModel()) {
            delete();
        } else
            super.update(arg0, modif);
    }

}
