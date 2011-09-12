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
package org.openflexo.inspector;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public interface InspectorObserver
{

    /**
     * This method is called whenever the observed object is changed. An
     * application calls an <tt>InspectableObject</tt> object's
     * <code>notifyObservers</code> method to have all the object's observers
     * notified of the change.
     * 
     * @param o
     *            the inspectable object.
     * @param arg
     *            an argument passed to the <code>notifyObservers</code>
     *            method.
     */
    public void update(InspectableObject inspectable, InspectableModification modification);
}
