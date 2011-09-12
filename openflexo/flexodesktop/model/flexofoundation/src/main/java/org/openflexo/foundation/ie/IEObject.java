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
package org.openflexo.foundation.ie;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Observable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DataFlexoObserver;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.action.IECopy;
import org.openflexo.foundation.ie.action.IECut;
import org.openflexo.foundation.ie.action.IEDelete;
import org.openflexo.foundation.ie.action.IEPaste;
import org.openflexo.foundation.ie.widget.IEBrowserWidget;
import org.openflexo.foundation.ie.widget.IEButtonWidget;
import org.openflexo.foundation.ie.widget.IECheckBoxWidget;
import org.openflexo.foundation.ie.widget.IEDropDownWidget;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.foundation.ie.widget.IERadioButtonWidget;
import org.openflexo.foundation.ie.widget.IEStringWidget;
import org.openflexo.foundation.ie.widget.IETextAreaWidget;
import org.openflexo.foundation.ie.widget.IETextFieldWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.toolbox.EmptyVector;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;
import org.openflexo.xmlcode.XMLMapping;


/**
 * Superclass for all model classes. Holds all attributes for a model class.
 * Notify observers when some attribute change (i.e. when put is called), giving
 * the modified key as notification argument. TODO: currently implements
 * Serializable: don't know why: remove this interface ???
 *
 * @author bmangez
 *
 */
public abstract class IEObject extends FlexoModelObject implements DataFlexoObserver, IObject
{

    private static final Logger logger = Logger.getLogger(IEObject.class.getPackage().getName());

    protected static final Vector<IObject> EMPTY_IOBJECT_VECTOR = EmptyVector.EMPTY_VECTOR(IObject.class);
    
    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================
    /**
     * This consrtuctor should never be called anywhre by anyone
     */
    public IEObject(FlexoProject project)
    {
        super(project);
    }

    // ==========================================================================
    // ========================= XML Serialization ============================
    // ==========================================================================

    @Override
    public XMLMapping getXMLMapping()
    {
        return getProject().getXmlMappings().getIEMapping();
    }

    // ==========================================================================
    // ============================= Instance Methods
    // ===========================
    // ==========================================================================

    @Override
	public void update(Observable observable, Object obj)
    {
        // Do nothing, since Observer interface is no more used
        // See FlexoObserver
    }

    @Override
	public void update(FlexoObservable observable, DataModification obj)
    {
        // Ignored at this level: implements it in sub-classes
    }

    @Override
    protected Vector<FlexoActionType> getSpecificActionListForThatClass()
    {
         Vector<FlexoActionType> returned = super.getSpecificActionListForThatClass();
         returned.add(IECut.actionType);
         returned.add(IECopy.actionType);
         returned.add(IEPaste.actionType);
         returned.add(IEDelete.actionType);
         return returned;
    }

    // ==========================================================================
    // ============================== KeyValueCoding
    // ============================
    // ==========================================================================

    protected void notifyModification(String key, Object oldValue, Object newValue)
    {
        notifyModification(key, oldValue, newValue, false);
    }

    protected void notifyModification(String key, Object oldValue, Object newValue, boolean isReentrant)
    {
    	setChanged();
    	int modifType = DataModification.ATTRIBUTE;
    	DataModification dataModification = new DataModification(modifType, key, oldValue, newValue);
    	if (isReentrant)
    		dataModification.setReentrant(isReentrant);
		notifyObservers(dataModification);
    }
    
    @Override
    public Class getTypeForKey(String key)
    {
        if (logger.isLoggable(Level.FINE))
            logger.finer("getTypeForKey for " + key);
        try {
            return super.getTypeForKey(key);
        } catch (InvalidObjectSpecificationException e) {
            if (logger.isLoggable(Level.FINE))
                logger.finer("OK, lets use the dynamic attributes !");
            return String.class;
        }
    }

    @Override
	public ValidationModel getDefaultValidationModel()
    {
        if (getProject() != null) {
            return getProject().getIEValidationModel();
        } else {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Could not access to project !");
        }
        return null;
    }

    private void processToAdditionOfEmbedded(IEObject object, Collection<IObject> queue)
    {
        queue.add(object);
        
        if (object.getEmbeddedIEObjects() != null) {
            Enumeration en = object.getEmbeddedIEObjects().elements();
            Object candidate = null;
            while (en.hasMoreElements()) {
                candidate = en.nextElement();
                if (candidate==null){
                    if (logger.isLoggable(Level.WARNING))
                        logger.warning("Object of class "+object.getClass().getName()+" returned IEObjects null in its method getEmbeddedIEObjects");
                    continue;
                }
                if (!queue.contains(candidate)) {
                	if(candidate instanceof IObject){
                		processToAdditionOfEmbedded((IEObject) candidate, queue);
                	}else{
                		if (logger.isLoggable(Level.SEVERE))
                            logger.severe("Object of class "+object.getClass().getName()+" returned non IEObjects in its method getEmbeddedIEObjects");
                	}
                }
            }
        }

    }

    public boolean checkWidgetDoesNotEmbedWOComponent(IEReusableComponent wo) {
		Vector<IObject> v=getAllEmbeddedIEObjects();
		for (IObject o:v){
			if (o instanceof PartialComponentInstance){
				if (((PartialComponentInstance)o).getComponentDefinition()==wo.getComponentDefinition())
					return true;
				else {
					boolean res =((PartialComponentInstance)o).getComponentDefinition().getWOComponent().checkWidgetDoesNotEmbedWOComponent(wo);
					if (res)
						return true;
				}
			}
		}
		return false;
	}
    
    public abstract IEObject getParent();
    
    /**
     * Return a Vector of all embedded IEObjects: recursive method (Note must
     * include itself in this vector)
     *
     * @return a Vector of IEObject instances
     */
    @Override
	public Vector<IObject> getAllEmbeddedIEObjects()
    {
    	return getAllEmbeddedIEObjects(false);
    }

    /**
	 * Return a Vector of all embedded IEObjects: recursive method (Note must include itself in this vector)
	 * 
	 * @param maintainNaturalOrder
	 *            wheter the natural order (e.g., in a table from left to right and top to bottom) should be maintained or not. This impacts
	 *            directly the performance.
	 * @return a Vector of IEObject instances
	 */
    public Vector<IObject> getAllEmbeddedIEObjects(boolean maintainNaturalOrder) {
        Collection<IObject> returned;
        if (maintainNaturalOrder)
        	returned = new Vector<IObject>();
        else
        	returned = new HashSet<IObject>();
        processToAdditionOfEmbedded(this, returned);
        return new Vector<IObject>(returned);
    }
    
    /**
     * Return a Vector of all embedded IEWidget matching the classe specified: recursive method (Note must include itself in this vector)
     * 
     * @param classeToMatch
     * @return a Vector of all embedded IEWidget matching the classe specified: recursive method (Note must include itself in this vector)
     */
    public Vector<IEWidget> getAllEmbeddedIEWidgets(Class<? extends IEWidget> classeToMatch){
    	Vector<IEWidget> reply = new Vector<IEWidget>();
    	Enumeration en = getAllEmbeddedIEObjects(true).elements();
    	while(en.hasMoreElements()){
    		IEObject widget = (IEObject)en.nextElement();
    		if(classeToMatch.isAssignableFrom(widget.getClass()))
    			reply.add((IEWidget) widget);
    	}
    	return reply;
    }
      
    /**
     * Return a Vector of embedded IEObjects at this level. NOTE that this is
     * NOT a recursive method
     *
     * @return a Vector of IEObject instances
     */
    @Override
	public abstract Vector<IObject> getEmbeddedIEObjects();

    @Override
	public Vector<Validable> getAllEmbeddedValidableObjects()
    {
        return new Vector<Validable>(getAllEmbeddedIEObjects());
    }

    /**
     * Returns all the strings ({@link IEStringWidget}) of this object.
     * You should not use directly this method but instead implement a cache
     * mechanism and use the benefits of the methods notifyWidgetAdded and
     * notifyWidgetRemoved. This method is intended to be used to preserve the
     * order of this object when presenting this to a user.
     *
     * @return all the strings contained in this component.
     */
    public Vector<IEStringWidget> getStrings()
    {
        Vector<IEStringWidget> v = new Vector<IEStringWidget>();
        Enumeration<IObject> en = getAllEmbeddedIEObjects(true).elements();
        while (en.hasMoreElements()) {
            IObject o = en.nextElement();
            if (o instanceof IEStringWidget)
                v.add((IEStringWidget) o);
        }
        return v;
    }

    /**
     * Returns all the textfields ({@link IETextFieldWidget}) of this object.
     * You should not use directly this method but instead implement a cache
     * mechanism and use the benefits of the methods notifyWidgetAdded and
     * notifyWidgetRemoved. This method is intended to be used to preserve the
     * order of this object when presenting this to a user.
     *
     * @return all the textfields contained in this component.
     */
    public Vector<IETextFieldWidget> getTextfields()
    {
        Vector<IETextFieldWidget> v = new Vector<IETextFieldWidget>();
        Enumeration<IObject> en = getAllEmbeddedIEObjects(true).elements();
        while (en.hasMoreElements()) {
            IObject o = en.nextElement();
            if (o instanceof IETextFieldWidget)
                v.add((IETextFieldWidget) o);
        }
        return v;
    }

    /**
     * Returns all the textareas ({@link IETextAreaWidget}) of this object.
     * You should not use directly this method but instead implement a cache
     * mechanism and use the benefits of the methods notifyWidgetAdded and
     * notifyWidgetRemoved. This method is intended to be used to preserve the
     * order of this object when presenting this to a user.
     *
     * @return all the textareas contained in this component.
     */
    public Vector<IETextAreaWidget> getTextareas()
    {
        Vector<IETextAreaWidget> v = new Vector<IETextAreaWidget>();
        Enumeration<IObject> en = getAllEmbeddedIEObjects(true).elements();
        while (en.hasMoreElements()) {
            IObject o = en.nextElement();
            if (o instanceof IETextAreaWidget)
                v.add((IETextAreaWidget) o);
        }
        return v;
    }

    /**
     * Returns all the dropdowns ({@link IEDropDownWidget}) of this object.
     * You should not use directly this method but instead implement a cache
     * mechanism and use the benefits of the methods notifyWidgetAdded and
     * notifyWidgetRemoved. This method is intended to be used to preserve the
     * order of this object when presenting this to a user.
     *
     * @return all the dropdowns contained in this component.
     */
    public Vector<IEDropDownWidget> getDropdowns()
    {
        Vector<IEDropDownWidget> v = new Vector<IEDropDownWidget>();
        Enumeration<IObject> en = getAllEmbeddedIEObjects(true).elements();
        while (en.hasMoreElements()) {
            IObject o = en.nextElement();
            if (o instanceof IEDropDownWidget)
                v.add((IEDropDownWidget) o);
        }
        return v;
    }

    /**
     * Returns all the browsers ({@link IEBrowserWidget}) of this object.
     * You should not use directly this method but instead implement a cache
     * mechanism and use the benefits of the methods notifyWidgetAdded and
     * notifyWidgetRemoved. This method is intended to be used to preserve the
     * order of this object when presenting this to a user.
     *
     * @return all the browsers contained in this component.
     */
    public Vector<IEBrowserWidget> getBrowsers()
    {
        Vector<IEBrowserWidget> v = new Vector<IEBrowserWidget>();
        Enumeration<IObject> en = getAllEmbeddedIEObjects(true).elements();
        while (en.hasMoreElements()) {
            IObject o = en.nextElement();
            if (o instanceof IEBrowserWidget)
                v.add((IEBrowserWidget) o);
        }
        return v;
    }

    /**
     * Returns all the checkboxes ({@link IECheckBoxWidget}) of this object.
     * You should not use directly this method but instead implement a cache
     * mechanism and use the benefits of the methods notifyWidgetAdded and
     * notifyWidgetRemoved. This method is intended to be used to preserve the
     * order of this object when presenting this to a user.
     *
     * @return all the checkboxes contained in this component.
     */
    public Vector<IECheckBoxWidget> getCheckboxes()
    {
        Vector<IECheckBoxWidget> v = new Vector<IECheckBoxWidget>();
        Enumeration<IObject> en = getAllEmbeddedIEObjects(true).elements();
        while (en.hasMoreElements()) {
            IObject o = en.nextElement();
            if (o instanceof IECheckBoxWidget)
                v.add((IECheckBoxWidget) o);
        }
        return v;
    }

    /**
     * Returns all the radio buttons ({@link IERadioButtonWidget}) of this object.
     * You should not use directly this method but instead implement a cache
     * mechanism and use the benefits of the methods notifyWidgetAdded and
     * notifyWidgetRemoved. This method is intended to be used to preserve the
     * order of this object when presenting this to a user.
     *
     * @return all the radio buttons contained in this component.
     */
    public Vector<IERadioButtonWidget> getRadios()
    {
        Vector<IERadioButtonWidget> v = new Vector<IERadioButtonWidget>();
        Enumeration<IObject> en = getAllEmbeddedIEObjects(true).elements();
        while (en.hasMoreElements()) {
            IObject o = en.nextElement();
            if (o instanceof IERadioButtonWidget)
                v.add((IERadioButtonWidget) o);
        }
        return v;
    }

    /**
     * Returns all the buttons ({@link IEButtonWidget}) of this object.
     * You should not use directly this method but instead implement a cache
     * mechanism and use the benefits of the methods notifyWidgetAdded and
     * notifyWidgetRemoved. This method is intended to be used to preserve the
     * order of this object when presenting this to a user.
     *
     * @return all the buttons contained in this component.
     */
    public Vector<IEButtonWidget> getIEButtons()
    {
        Vector<IEButtonWidget> v = new Vector<IEButtonWidget>();
        Enumeration<IObject> en = getAllEmbeddedIEObjects(true).elements();
        while (en.hasMoreElements()) {
            IObject o = en.nextElement();
            if (o.getClass()==IEButtonWidget.class)
                v.add((IEButtonWidget) o);
        }
        return v;
    }

    /**
     * Returns all the hyperlinks ({@link IEHyperlinkWidget}) of this object.
     * You should not use directly this method but instead implement a cache
     * mechanism and use the benefits of the methods notifyWidgetAdded and
     * notifyWidgetRemoved. This method is intended to be used to preserve the
     * order of this object when presenting this to a user.
     *
     * @return all the hyperlinks contained in this component.
     */
    public Vector<IEHyperlinkWidget> getHyperlinks()
    {
        Vector<IEHyperlinkWidget> v = new Vector<IEHyperlinkWidget>();
        Enumeration<IObject> en = getAllEmbeddedIEObjects(true).elements();
        while (en.hasMoreElements()) {
            IObject o = en.nextElement();
            if (o instanceof IEHyperlinkWidget)
                v.add((IEHyperlinkWidget) o);
        }
        return v;
    }

}
