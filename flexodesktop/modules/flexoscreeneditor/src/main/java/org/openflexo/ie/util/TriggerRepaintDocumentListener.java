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
package org.openflexo.ie.util;

import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * A DocumentListener triggering revalidate() and repaint() (in that order) on targetComponent
 * whenever the Document content change.
 */
public class TriggerRepaintDocumentListener implements DocumentListener {

    private JComponent targetComponent;

    public TriggerRepaintDocumentListener(JComponent targetComponent) {
        super();
        if(targetComponent==null){
            throw new IllegalArgumentException("targetComponent cannot be null.");
        }
        this.targetComponent = targetComponent;
    }

    @Override
    public void insertUpdate(DocumentEvent documentEvent) {
        validateRepaint();
    }

    @Override
    public void removeUpdate(DocumentEvent documentEvent) {
        validateRepaint();
    }

    @Override
    public void changedUpdate(DocumentEvent documentEvent) {
        validateRepaint();
    }

    private void validateRepaint() {
        targetComponent.revalidate();
        targetComponent.repaint();
    }
}
