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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ie.dm.table.SpanChanged;
import org.openflexo.foundation.ie.dm.table.WidgetRemovedFromTable;
import org.openflexo.foundation.ie.widget.IECheckBoxWidget;
import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.controller.IEController;


/**
 * @author bmangez
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class IECheckBoxWidgetView extends AbstractInnerTableWidgetView<IECheckBoxWidget>
{

    // ==========================================================================
    // ============================= Variables
    // ==================================
    // ==========================================================================
    private transient JCheckBox _jCheckBox;
    
    private JPanel container;

    protected boolean isUpdatingModel=false;
    
    public static final Font TEXTFIELD_FONT = new Font("SansSerif", Font.PLAIN, 10);


    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    public IECheckBoxWidgetView(IEController ieController, IECheckBoxWidget model, boolean addDnDSupport, IEWOComponentView view)
    {
        super(ieController, model, addDnDSupport,view);
        FlowLayout layout = new FlowLayout(FlowLayout.LEFT,0,0);
        setLayout(layout);
        _jCheckBox = new JCheckBox();
        _jCheckBox.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                isUpdatingModel=true;
                getCheckBoxModel().setValue(((JCheckBox)e.getSource()).isSelected());
                isUpdatingModel=false;
            }}
        );
        _jCheckBox.setFont(TEXTFIELD_FONT);
        container = new JPanel(new FlowLayout(FlowLayout.CENTER,4,4));
        container.setOpaque(false);
        container.add(_jCheckBox);
        container.validate();
        container.doLayout();
        add(container);
        
        _jCheckBox.setEnabled(true);
        _jCheckBox.setFocusable(false);
        _jCheckBox.setOpaque(false);
        if (getCheckBoxModel().getDescription() != null) {
            _jCheckBox.setToolTipText(getCheckBoxModel().getDescription());
        }
        TransparentMouseListener tml = new TransparentMouseListener(_jCheckBox, this);
        _jCheckBox.addMouseListener(tml);
        _jCheckBox.addMouseMotionListener(tml);
        _jCheckBox.setSelected(getCheckBoxModel().getValue());
        _jCheckBox.setBackground(getBackgroundColor());
        setBackground(getBackgroundColor());
    }

    public IECheckBoxWidget getCheckBoxModel()
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
    // ==========================================================================
    // ============================= Observer
    // ===================================
    // ==========================================================================
    
    /*
     * (non-Javadoc)
     * 
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
	public void update(FlexoObservable arg0, DataModification modif)
    {
        if (modif.modificationType() == DataModification.ATTRIBUTE) {
            if (modif.propertyName().equals(IECheckBoxWidget.ATTRIB_DEFAULTVALUE_NAME)) {
                if (!isUpdatingModel)
                    _jCheckBox.setSelected(getCheckBoxModel().getValue());
            } else if (modif.propertyName().equals("colSpan") || modif.propertyName().equals("rowSpan")) {
                if (getParent() != null) {
                    getParent().doLayout();
                    ((JComponent) getParent()).repaint();
                }

            }
        }
        if (modif instanceof SpanChanged) {
            if (getParent() != null) {
                getParent().doLayout();
                ((JComponent) getParent()).repaint();
            }
        } else if (modif instanceof WidgetRemovedFromTable && arg0 == getModel()) {
            delete();
        } else
            super.update(arg0, modif);
    }

}
