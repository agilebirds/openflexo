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

import java.awt.Component;

import javax.swing.JComponent;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.TabModelView;
import org.openflexo.inspector.model.InnerTabWidget;
import org.openflexo.inspector.widget.DenaliWidget.WidgetLayout;


/**
 * @author gpolet
 *
 */
public interface InnerTabWidgetView
{
    public JComponent getDynamicComponent();

    /**
     * @param view
     * @param i
     */
    public void setTabModelView(TabModelView view, int i);

    /**
     * @return
     */
    public boolean shouldExpandVertically();

    /**
     * @return
     */
    public boolean shouldExpandHorizontally();

    /**
     * @return
     */
    public boolean displayLabel();

    /**
     * @return
     */
    public WidgetLayout getWidgetLayout();

    /**
     * @return
     */
    public Component getLabel();
    
    public InnerTabWidget getXMLModel();

    /**
     * @return
     */
    public String getName();

    /**
     * @param newValue
     * @param observedPropertyName
     * @return
     */
    public boolean isStillVisible(Object newValue, String observedPropertyName);

    /**
     * @param widget
     * @return
     */
    public boolean dependsOfProperty(DenaliWidget widget);

    /**
     * @param newInspectable
     */
    public void switchObserved(InspectableObject newInspectable);

    /**
     * @param controller
     */
    public void setController(AbstractController controller);

    /**
     * 
     */
    public void updateWidgetFromModel();
}
