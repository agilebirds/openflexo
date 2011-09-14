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
package org.openflexo.foundation.ie.widget;

import java.util.Enumeration;
import java.util.Vector;

import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.utils.FlexoCSS;


/**
 * @author gpolet
 *
 */
public interface ButtonedWidgetInterface extends IWidget
{
    public Enumeration<IEHyperlinkWidget> buttonWidgets();
    
    public void insertButtonAtIndex(IEHyperlinkWidget button, int index);
    
    public void removeButton(IEHyperlinkWidget button);
    
    public void addObserver(FlexoObserver observer);
    
    public void deleteObserver(FlexoObserver observer);
    
    public FlexoCSS getFlexoCSS();
    
    public IESequenceWidget getSequenceWidget();
    
    public Vector<IEHyperlinkWidget> getAllButtons();
}
