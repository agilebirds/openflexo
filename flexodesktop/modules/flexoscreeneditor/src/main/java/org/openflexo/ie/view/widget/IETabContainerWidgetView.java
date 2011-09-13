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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Enumeration;

import javax.swing.BorderFactory;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.ie.dm.SubsequenceInserted;
import org.openflexo.foundation.ie.dm.TabInserted;
import org.openflexo.foundation.ie.dm.TabRemoved;
import org.openflexo.foundation.ie.dm.TabReordered;
import org.openflexo.foundation.ie.dm.TabSelectionChanged;
import org.openflexo.foundation.ie.widget.IESequence;
import org.openflexo.foundation.ie.widget.IESequenceTab;
import org.openflexo.foundation.ie.widget.IETabWidget;
import org.openflexo.foundation.ie.widget.ITabWidget;
import org.openflexo.foundation.ie.widget.OperatorChanged;
import org.openflexo.foundation.ie.widget.SubsequenceRemoved;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.controller.IEController;


/**
 * @author bmangez
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class IETabContainerWidgetView extends IEWidgetView<IESequenceTab>
{

    private GrabTabZone grabTabZone;

    private ButtonPanel _buttonPanel;

    private DropTabZone _dropTabZone;

    public static final Font BLOC_TITLE_FONT = new Font("SansSerif", Font.BOLD, 10);

    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    public IETabContainerWidgetView(IEController ieController, IESequenceTab model, boolean addDnDSupport, IEWOComponentView componentView)
    {
        super(ieController, model, addDnDSupport, componentView);
        setLayout(new BorderLayout());
        setDefaultBorder();
        setOpaque(false);
        model.getWOComponent().addObserver(this);
        //setBackground(Color.WHITE);
        _buttonPanel = new ButtonPanel(getIEController(), model, componentView);
        add(_buttonPanel, BorderLayout.SOUTH);
        grabTabZone = new GrabTabZone(model);
        add(grabTabZone, BorderLayout.NORTH);
        _dropTabZone = new DropTabZone(ieController, this);
        add(_dropTabZone, BorderLayout.CENTER);
        Enumeration en = model.elements();
        while (en.hasMoreElements()) {
            updateTabInsertion((ITabWidget) en.nextElement());
        }
    }

    public int getSelectedIndex() {
    	return _dropTabZone.getSelectedIndex();
    }
    
    @Override
    public void setDefaultBorder()
    {
        setBorder(BorderFactory.createLineBorder(getMainColor()));
    }

    @Override
    public void delete()
    {
    	deleteObserversOnModel(getModel());
        if (_dropTabZone!=null) {
            _dropTabZone.delete();
        }
        _dropTabZone = null;
        getModel().getWOComponent().deleteObserver(this);
        super.delete();
    }

    private void deleteObserversOnModel(ITabWidget w) {
    	w.deleteObserver(this);
    	if (w instanceof IESequence)
    	for (ITabWidget tab : ((IESequence<ITabWidget>)w).getInnerWidgets()) {
			deleteObserversOnModel(tab);
		}
	}

	public IESequenceTab getTabContainerWidget()
    {
        return getModel();
    }

    /**
     * Overrides propagateResize
     *
     * @see org.openflexo.ie.view.widget.IEWidgetView#propagateResize()
     */
    @Override
    public void propagateResize()
    {
        super.propagateResize();
        _dropTabZone.propagateResize();
        _buttonPanel.propagateResize();
    }

    // ==========================================================================
    // ============================= Observer
    // ===================================
    // ==========================================================================

    @Override
    public void update(FlexoObservable arg0, DataModification modif)
    {
        if (modif.modificationType() == DataModification.BLOC_BG_CLOR_CHANGE) {
            setBorder(BorderFactory.createLineBorder(getMainColor()));
        } else if (modif instanceof TabInserted) {
            updateTabInsertion((IETabWidget) modif.newValue());
        } else if (modif instanceof TabReordered) {
            updateTabReodering();
        } else if (modif instanceof TabRemoved) {
            IETabWidgetView view = (IETabWidgetView) _dropTabZone.findViewForModel((IETabWidget) modif.oldValue());
            if (view==null)
            	return; // In case of double notification
            _dropTabZone.remove(view);
            ((IETabWidget) modif.oldValue()).deleteObserver(this);
            view.delete();
        } else if (modif instanceof TabSelectionChanged) {
            Component c = findTabForModel((IETabWidget) modif.newValue());
            if (c != null)
                _dropTabZone.setSelectedComponent(c);
        } else if (modif instanceof SubsequenceInserted) {
        	((IESequence) ((SubsequenceInserted) modif).newValue()).addObserver(this);
            updateTabInsertion((ITabWidget) ((SubsequenceInserted) modif).newValue());
        } else if (modif instanceof SubsequenceRemoved) {
            ((IESequence) ((SubsequenceRemoved) modif).oldValue()).deleteObserver(this);
            _dropTabZone.updateConditionalIcons();
        } else if (modif instanceof OperatorChanged) {
            _dropTabZone.updateConditionalIcons();
        } else
            super.update(arg0, modif);
    }

    private Component findTabForModel(IETabWidget widgetModel)
    {
        for (int i = 0; i < _dropTabZone.getComponentCount(); i++) {
            Component c = _dropTabZone.getComponent(i);
            if (c instanceof IETabWidgetView) {
                if (((IETabWidgetView) c).getModel().equals(widgetModel))
                    return c;
            }
        }
        return null;
    }

    private void updateTabInsertion(ITabWidget newTab)
    {
        if (newTab instanceof IETabWidget) {
            int i = 0;
            if (((IESequenceTab) ((IETabWidget) newTab).getParent()).isSubsequence())
                i = ((IESequenceTab) ((IETabWidget) newTab).getParent()).getIndex();
            else
                i = ((IETabWidget) newTab).getIndex();
            _dropTabZone.updateTabInsertion(((IETabWidget) newTab), i);
        } else if (newTab instanceof IESequenceTab) {
            Enumeration<ITabWidget> en = ((IESequenceTab) newTab).elements();
            while (en.hasMoreElements()) {
                updateTabInsertion(en.nextElement());
            }
        }
        newTab.addObserver(this);
    }

    private void updateTabReodering()
    {
        _dropTabZone.updateTabReodering();
    }

    @Override
    public Dimension getPreferredSize()
    {
    	if (getHoldsNextComputedPreferredSize()){
        	Dimension storedSize = storedPrefSize();
            if(storedSize!=null)return storedSize;
        }
        IESequenceWidgetWidgetView parentSequenceView = null;
        if (getParent() instanceof IESequenceWidgetWidgetView) {
            parentSequenceView = (IESequenceWidgetWidgetView) getParent();
        }
        Dimension d = super.getPreferredSize();
        if (parentSequenceView != null) {
            d.width = parentSequenceView.getAvailableWidth();
            if (d.height < 20)
                d.height = 20;
        }
        if (getHoldsNextComputedPreferredSize())
            storePrefSize(d);
        return d;
    }

}
