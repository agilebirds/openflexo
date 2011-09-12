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
package org.openflexo.foundation;

import java.beans.PropertyChangeSupport;
import java.lang.ref.WeakReference;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.rm.ResourceStatusModification;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.InspectorObserver;
import org.openflexo.toolbox.HasPropertyChangeSupport;


/**
 * This class represents an observable object, or "data" in the model-view
 * paradigm. It can be subclassed to represent an object that the application
 * wants to have observed.
 * <p>
 * An observable object can have one or more observers. An observer may be any
 * object that implements interface <tt>FlexoObserver</tt>. After an
 * observable instance changes, an application calling the
 * <code>FlexoObservable</code>'s <code>notifyObservers</code> method
 * causes all of its observers to be notified of the change by a call to their
 * <code>update</code> method.
 * <p>
 * The order in which notifications will be delivered is unspecified. The
 * default implementation provided in the Observerable class will notify
 * Observers in the order in which they registered interest, but subclasses may
 * change this order, use no guaranteed order, deliver notifications on separate
 * threads, or may guarantee that their subclass follows this order, as they
 * choose.
 * <p>
 * Note that this notification mechanism is has nothing to do with threads and
 * is completely separate from the <tt>wait</tt> and <tt>notify</tt>
 * mechanism of class <tt>Object</tt>.
 * <p>
 * When an observable object is newly created, its set of observers is empty.
 * Two observers are considered the same if and only if the <tt>equals</tt>
 * method returns true for them.
 * <p>
 * Additionnaly, this class manages other observers than <tt>FlexoObserver</tt>
 * instances with a delegate <tt>Observable</tt> instance: this is because
 * FlexoInspector doesn't not use <tt>FlexoObserver</tt> class, as this is a
 * Flexo-external project.
 * <p>
 * Features allowing to totally enable or disable observing scheme (or some
 * classes) have also been included.
 * 
 * <br>
 * NB: this class has been rewrited from {@link java.util.Observable}, because
 * Java doesn't support multiple inheritance.
 * 
 * @author sguerin
 * @see FlexoObservable#notifyObservers()
 * @see FlexoObservable#notifyObservers(java.lang.Object)
 * @see FlexoObserver
 * @see FlexoObserver#update(java.util.Observable, java.lang.Object)
 * 
 * 
 */
public class FlexoObservable extends FlexoObject implements HasPropertyChangeSupport
{

    private static final Logger logger = Logger.getLogger(FlexoObservable.class.getPackage().getName());

    private boolean changed = false;

    private Vector<WeakReference<FlexoObserver>> _flexoObservers;
    private Vector<WeakReference<InspectorObserver>> _inspectorObservers;

    private PropertyChangeSupport _pcSupport;
    
    /**
     * This hastable stores for all classes encountered as observers for this
     * observable a property coded as a Boolean indicating if notifications
     * should be fired.
     */
    protected Hashtable<Class,Boolean> observerClasses;

    /**
     * This flag codes the necessity to fire notifications or not
     */
    protected boolean enableObserving = true;

    /**
     * Construct a FlexoObservable with zero observers.
     */
    public FlexoObservable()
    {
        super();
        _pcSupport = new PropertyChangeSupport(this);
        _flexoObservers = new Vector<WeakReference<FlexoObserver>>();
        _inspectorObservers = new Vector<WeakReference<InspectorObserver>>();
        observerClasses = new Hashtable<Class, Boolean>();
    }

    @Override
    public PropertyChangeSupport getPropertyChangeSupport() 
    {
    	return _pcSupport;
    }
    
    /**
     * Adds an observer to the set of observers for this object, provided that
     * it is not the same as some observer already in the set. The order in
     * which notifications will be delivered to multiple observers is not
     * specified. See the class comment.
     * 
     * @param o
     *            an observer to be added.
     * @throws NullPointerException
     *             if the parameter o is null.
     */
    public void addObserver(FlexoObserver o)
    {
        if (o == null)
            throw new NullPointerException();
        synchronized (_flexoObservers) {

			if (!isObservedBy(o)) {
				_flexoObservers.add(new WeakReference<FlexoObserver>(o));
				if (observerClasses.get(o.getClass()) == null) {
					// Add an entry for this kind of observer
					observerClasses.put(o.getClass(), new Boolean(true));
				}
			}
        }
    }

    /**
     * Deletes an observer from the set of observers of this object.
     * 
     * @param o
     *            the observer to be deleted.
     */
    public void deleteObserver(FlexoObserver o)
    {
		synchronized (_flexoObservers) {
			Iterator<WeakReference<FlexoObserver>> i = _flexoObservers.iterator();
			while (i.hasNext()) {
				WeakReference<FlexoObserver> reference = i.next();
				if (reference.get() == null)
					i.remove();
				else if (reference.get() == o) {
					i.remove();
					break;
				}
			}
		}
    }

    /**
     * Deletes an observer from the set of observers of this object.
     * 
     * @param o
     *            the observer to be deleted.
     */
    public void deleteInspectorObserver(InspectorObserver obs)
    {
    	Iterator<WeakReference<InspectorObserver>> i = _inspectorObservers.iterator();
        while (i.hasNext()) {
			WeakReference<InspectorObserver> reference = i.next();
			if (reference.get()==null)
				i.remove();
			else if (reference.get()==obs) {
				i.remove();
				break;
			}
		}
    }

    /**
     * Adds an observer to the set of observers for this object, provided that
     * it is not the same as some observer already in the set. The order in
     * which notifications will be delivered to multiple observers is not
     * specified. See the class comment.
     * 
     * @param o
     *            an observer to be added.
     * @throws NullPointerException
     *             if the parameter o is null.
     */
    public void addInspectorObserver(InspectorObserver obs)
    {
        if (obs == null)
            throw new NullPointerException();
        if (!isObservedBy(obs)) {
            _inspectorObservers.add(new WeakReference<InspectorObserver>(obs));
        }
    }

    /**
     * If this object has changed, as indicated by the <code>hasChanged</code>
     * method, then notify all of its observers and then call the
     * <code>clearChanged</code> method to indicate that this object has no
     * longer changed.
     * <p>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and <code>null</code>. In other
     * words, this method is equivalent to: <blockquote><tt>
     * notifyFlexoObservers(null)</tt></blockquote>
     * 
     * @see java.util.Observable#clearChanged()
     * @see java.util.Observable#hasChanged()
     * @see java.util.FlexoObserver#update(java.util.Observable,
     *      java.lang.Object)
     */
    public void notifyObservers()
    {
        notifyObservers(null);
    }

    /**
     * If this object has changed, as indicated by the <code>hasChanged</code>
     * method, then notify all of its observers and then call the
     * <code>clearChanged</code> method to indicate that this object has no
     * longer changed.
     * <p>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and the <code>arg</code> argument.
     * 
     * @param arg
     *            any object.
     * @see java.util.Observable#clearChanged()
     * @see java.util.Observable#hasChanged()
     * @see java.util.FlexoObserver#update(java.util.Observable,
     *      java.lang.Object)
     */
    public void notifyObservers(DataModification arg)
    {

        if (enableObserving) {
            /*
             * a temporary array buffer, used as a snapshot of the state of
             * current FlexoObservers.
             */
            WeakReference<FlexoObserver>[] arrLocal1 = new WeakReference[_flexoObservers.size()];
            WeakReference<InspectorObserver>[] arrLocal2 = new WeakReference[_inspectorObservers.size()];

            synchronized (this) {
                /*
                 * We don't want the FlexoObserver doing callbacks into
                 * arbitrary code while holding its own Monitor. The code where
                 * we extract each Observable from the Vector and store the
                 * state of the FlexoObserver needs synchronization, but
                 * notifying observers does not (should not). The worst result
                 * of any potential race-condition here is that: 1) a
                 * newly-added FlexoObserver will miss a notification in
                 * progress 2) a recently unregistered FlexoObserver will be
                 * wrongly notified when it doesn't care
                 */
                if (!changed && !(arg instanceof ResourceStatusModification))
                    return;
                arrLocal1 = _flexoObservers.toArray(arrLocal1);
                arrLocal2 = _inspectorObservers.toArray(arrLocal2);
                clearChanged();
            }

            // Notify all Flexo observers of observing for this kind of observer
            // is enabled
            for (int i = arrLocal1.length - 1; i >= 0; i--) {
            	WeakReference<FlexoObserver> weakRef = arrLocal1[i];
            	if(weakRef!=null){
            		FlexoObserver o = arrLocal1[i].get();
            		if (o==null){
            			_flexoObservers.remove(arrLocal1[i]);
            			continue;
            		}
            		if ((observerClasses.get(o.getClass())).booleanValue()) {
            			o.update(this, arg);
            		}
            	}
            }

            // Notify all Inspector observers
            for (int i = arrLocal2.length - 1; i >= 0; i--) {
            	WeakReference<InspectorObserver> weakRef = arrLocal2[i];
            	if(weakRef!=null){
            		InspectorObserver o = arrLocal2[i].get();
            	
            		if (o==null){
            			_inspectorObservers.remove(arrLocal2[i]);
            			continue;
            		}
            		o.update((InspectableObject)this, arg);
            	}
            }
            
            if (arg == null) {
            	_pcSupport.firePropertyChange(null,null,null);
            }
            else {
            	_pcSupport.firePropertyChange(arg.propertyName(), arg.oldValue(), arg.newValue());
            }
        }

    }

    /**
     * Build and return a Vector of all current observers, as a snapshot of the
     * state of current FlexoObservers and Inspector observers. Be careful with method such
     * as indexOf, contains, etc... which usually rely on equals() method. They have been overriden
     * to use explicitly the == operator.
     */
    public Vector getAllObservers()
    {
        Vector returned = new Vector(){
        	
        	@Override
        	public synchronized int indexOf(Object o, int index) {
        		for (int i = index; i < size(); i++) {
					if (elementData[i]==o)
						return i;
				}
        		return -1;
        	}
        	
        };
        Iterator<WeakReference<FlexoObserver>> i = _flexoObservers.iterator();
        while (i.hasNext()) {
			WeakReference<FlexoObserver> reference = i.next();
			if (reference.get()==null)
				i.remove();
			else
				returned.add(reference.get());
		}
        Iterator<WeakReference<InspectorObserver>> i2 = _inspectorObservers.iterator();
        while (i2.hasNext()) {
			WeakReference<InspectorObserver> reference = i2.next();
			if (reference.get()==null)
				i2.remove();
			else
				returned.add(reference.get());
		}
        return returned;
    }

    /**
     * Prints array of all current observers, as a snapshot of the state of
     * current FlexoObservers.
     */
    public void printObservers()
    {
        Enumeration e = getAllObservers().elements();
        if (logger.isLoggable(Level.INFO))
            logger.info("Observers of: " + getClass().getName() + " / " + this);
        int i = 0;
        while (e.hasMoreElements()) {
            Object object = e.nextElement();
            if (object instanceof FlexoObserver) {
                FlexoObserver o = (FlexoObserver) object;
                if (logger.isLoggable(Level.INFO))
                    logger.info(" * " + i + " hash= "+Integer.toHexString(o.hashCode())+ " FlexoObserver: " + o.getClass().getName() + " / " + o);
            }
            if (object instanceof InspectorObserver) {
                InspectorObserver o = (InspectorObserver) object;
                if (logger.isLoggable(Level.INFO))
                    logger.info(" * " + i + " hash= "+Integer.toHexString(o.hashCode()) + " InspectorObserver: " + o.getClass().getName() + " / " + o);
            }
            i++;
        }
    }

    /**
     * Clears the observer list so that this object no longer has any observers.
     */
    public synchronized void deleteObservers()
    {
    	synchronized (_flexoObservers) {
    		_flexoObservers.clear();
		}
        _inspectorObservers.clear();
    }

    /**
     * Marks this <tt>Observable</tt> object as having been changed; the
     * <tt>hasChanged</tt> method will now return <tt>true</tt>.
     */
    protected synchronized void setChanged()
    {
        changed = true;
    }

    /**
     * Indicates that this object has no longer changed, or that it has already
     * notified all of its observers of its most recent change, so that the
     * <tt>hasChanged</tt> method will now return <tt>false</tt>. This
     * method is called automatically by the <code>notifyFlexoObservers</code>
     * methods.
     * 
     * @see java.util.Observable#notifyFlexoObservers()
     * @see java.util.Observable#notifyFlexoObservers(java.lang.Object)
     */
    protected synchronized void clearChanged()
    {
        changed = false;
    }

    /**
     * Tests if this object has changed.
     * 
     * @return <code>true</code> if and only if the <code>setChanged</code>
     *         method has been called more recently than the
     *         <code>clearChanged</code> method on this object;
     *         <code>false</code> otherwise.
     * @see java.util.Observable#clearChanged()
     * @see java.util.Observable#setChanged()
     */
    public boolean hasChanged()
    {
        return changed;
    }

    /**
     * Returns the number of observers of this <tt>Observable</tt> object.
     * 
     * @return the number of observers of this object.
     */
    public int countObservers()
    {
        return _flexoObservers.size() + _inspectorObservers.size();
    }

    /**
     * Enable observing. Does not affect disabled observing classes
     */
    public synchronized void enableObserving()
    {
        enableObserving = true;
    }

    /**
     * Disable observing.
     */
    public synchronized void disableObserving()
    {
        enableObserving = false;
    }

    /**
     * Enable observing for all observers of class 'observerClass' and all
     * related subclasses
     */
    public synchronized void enableObserving(Class observerClass)
    {
        for (Enumeration en = observerClasses.keys(); en.hasMoreElements();) {
            Class temp = (Class) en.nextElement();
            if (observerClass.isAssignableFrom(temp)) {
                if (logger.isLoggable(Level.FINE))
                    logger.fine("Enable observing for " + temp.getName());
                observerClasses.put(temp, new Boolean(true));
            }
        }
    }

    /**
     * Disable observing for all observers of class 'observerClass' and all
     * related subclasses
     */
    public synchronized void disableObserving(Class observerClass)
    {
        for (Enumeration en = observerClasses.keys(); en.hasMoreElements();) {
            Class temp = (Class) en.nextElement();
            if (observerClass.isAssignableFrom(temp)) {
                if (logger.isLoggable(Level.FINE))
                    logger.fine("Disable observing for " + temp.getName());
                observerClasses.put(temp, new Boolean(false));
            }
        }
    }

    public static boolean areSameValue(Object o1,Object o2){
    	if(o1==null)return o2==null;
    	return o1.equals(o2);
    }

    public boolean isObservedBy(FlexoObserver observer) {
		synchronized (_flexoObservers) {

			Iterator<WeakReference<FlexoObserver>> i = _flexoObservers.iterator();
			while (i.hasNext()) {
				WeakReference<FlexoObserver> reference = i.next();
				if (reference.get() == null)
					i.remove();
				else if (reference.get() == observer)
					return true;
			}
			return false;
		}
	}
    
    public synchronized boolean isObservedBy(InspectorObserver observer) {
    	Iterator<WeakReference<InspectorObserver>> i = _inspectorObservers.iterator();
        while (i.hasNext()) {
			WeakReference<InspectorObserver> reference = i.next();
			if (reference.get()==null)
				i.remove();
			else if (reference.get()==observer)
				return true;
		}
    	return false;
    }
}
