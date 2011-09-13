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
package org.openflexo.foundation.utils;

import java.io.Serializable;
import java.util.EventListener;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.openflexo.foundation.FlexoObservable;


/**
 * Redefines SWING AbstractListModel to extend FlexoObservable class
 * 
 * @author sguerin
 * 
 */

public abstract class FlexoListModel extends FlexoObservable implements ListModel, Serializable
{
    protected EventListenerList listenerList = new EventListenerList();

    /**
     * Adds a listener to the list that's notified each time a change to the
     * data model occurs.
     * 
     * @param l
     *            the <code>ListDataListener</code> to be added
     */
    @Override
	public void addListDataListener(ListDataListener l)
    {
        listenerList.add(ListDataListener.class, l);
    }

    /**
     * Removes a listener from the list that's notified each time a change to
     * the data model occurs.
     * 
     * @param l
     *            the <code>ListDataListener</code> to be removed
     */
    @Override
	public void removeListDataListener(ListDataListener l)
    {
        listenerList.remove(ListDataListener.class, l);
    }

    /**
     * Returns an array of all the list data listeners registered on this
     * <code>AbstractListModel</code>.
     * 
     * @return all of this model's <code>ListDataListener</code>s, or an
     *         empty array if no list data listeners are currently registered
     * 
     * @see #addListDataListener
     * @see #removeListDataListener
     * 
     * @since 1.4
     */
    public ListDataListener[] getListDataListeners()
    {
        return listenerList.getListeners(ListDataListener.class);
    }

    /**
     * <code>AbstractListModel</code> subclasses must call this method
     * <b>after</b> one or more elements of the list change. The changed
     * elements are specified by the closed interval index0, index1 -- the
     * endpoints are included. Note that index0 need not be less than or equal
     * to index1.
     * 
     * @param source
     *            the <code>ListModel</code> that changed, typically "this"
     * @param index0
     *            one end of the new interval
     * @param index1
     *            the other end of the new interval
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireContentsChanged(Object source, int index0, int index1)
    {
        Object[] listeners = listenerList.getListenerList();
        ListDataEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (e == null) {
                    e = new ListDataEvent(source, ListDataEvent.CONTENTS_CHANGED, index0, index1);
                }
                ((ListDataListener) listeners[i + 1]).contentsChanged(e);
            }
        }
    }

    /**
     * <code>AbstractListModel</code> subclasses must call this method
     * <b>after</b> one or more elements are added to the model. The new
     * elements are specified by a closed interval index0, index1 -- the
     * enpoints are included. Note that index0 need not be less than or equal to
     * index1.
     * 
     * @param source
     *            the <code>ListModel</code> that changed, typically "this"
     * @param index0
     *            one end of the new interval
     * @param index1
     *            the other end of the new interval
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireIntervalAdded(Object source, int index0, int index1)
    {
        Object[] listeners = listenerList.getListenerList();
        ListDataEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (e == null) {
                    e = new ListDataEvent(source, ListDataEvent.INTERVAL_ADDED, index0, index1);
                }
                ((ListDataListener) listeners[i + 1]).intervalAdded(e);
            }
        }
    }

    /**
     * <code>AbstractListModel</code> subclasses must call this method
     * <b>after</b> one or more elements are removed from the model. The new
     * elements are specified by a closed interval index0, index1, i.e. the
     * range that includes both index0 and index1. Note that index0 need not be
     * less than or equal to index1.
     * 
     * @param source
     *            the ListModel that changed, typically "this"
     * @param index0
     *            one end of the new interval
     * @param index1
     *            the other end of the new interval
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireIntervalRemoved(Object source, int index0, int index1)
    {
        Object[] listeners = listenerList.getListenerList();
        ListDataEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListDataListener.class) {
                if (e == null) {
                    e = new ListDataEvent(source, ListDataEvent.INTERVAL_REMOVED, index0, index1);
                }
                ((ListDataListener) listeners[i + 1]).intervalRemoved(e);
            }
        }
    }

    /**
     * Returns an array of all the objects currently registered as
     * <code><em>Foo</em>Listener</code>s upon this model.
     * <code><em>Foo</em>Listener</code>s are registered using the
     * <code>add<em>Foo</em>Listener</code> method.
     * <p>
     * You can specify the <code>listenerType</code> argument with a class
     * literal, such as <code><em>Foo</em>Listener.class</code>. For
     * example, you can query a list model <code>m</code> for its list data
     * listeners with the following code:
     * 
     * <pre>
     * ListDataListener[] ldls = (ListDataListener[]) (m.getListeners(ListDataListener.class));
     * </pre>
     * 
     * If no such listeners exist, this method returns an empty array.
     * 
     * @param listenerType
     *            the type of listeners requested; this parameter should specify
     *            an interface that descends from
     *            <code>java.util.EventListener</code>
     * @return an array of all objects registered as
     *         <code><em>Foo</em>Listener</code>s on this model, or an
     *         empty array if no such listeners have been added
     * @exception ClassCastException
     *                if <code>listenerType</code> doesn't specify a class or
     *                interface that implements
     *                <code>java.util.EventListener</code>
     * 
     * @see #getListDataListeners
     * 
     * @since 1.3
     */
    public EventListener[] getListeners(Class listenerType)
    {
        return listenerList.getListeners(listenerType);
    }
}
