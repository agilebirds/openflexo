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
package org.openflexo.foundation.wkf;

/*
 * FlexoObservable.java
 * Project WorkflowEditor
 *
 * Created by benoit on Mar 3, 2004
 */

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.AttributeDataModification;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.RepresentableFlexoModelObject;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationModel;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;

/**
 * A WKFObject instance represents an object (model in MVC paradigm) of a process (or be the process itself). This is the root class for all
 * objects of WKF model.<br>
 * This class implements:
 * <ul>
 * <li>a levelled observer/observable scheme</li>
 * <li>link to parent process</li>
 * <li>a method allowing to retrieve all embedded WKFObject</li>
 * <li>methods used to perform validation</li>
 * </ul>
 * 
 * @author benoit, sylvain
 */
public abstract class WKFObject extends RepresentableFlexoModelObject implements Validable {

	private static final Logger logger = Logger.getLogger(WKFObject.class.getPackage().getName());

	private FlexoProcess _process;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Should only be used by FlexoProcess
	 */
	public WKFObject(FlexoProject project) {
		super(project);
	}

	/**
	 * Default constructor
	 */
	public WKFObject(FlexoProcess process) {
		super(process != null ? process.getProject() : null);
		setProcess(process);
	}

	// ==========================================================================
	// ============================= ACCESSORS ================================
	// ==========================================================================

	/**
	 * Returns reference to the main object in which this XML-serializable object is contained relating to storing scheme: here it's the
	 * process itself
	 * 
	 * @return the process this object is relating to
	 */
	@Override
	public XMLStorageResourceData getXMLResourceData() {
		return getProcess();
	}

	public FlexoWorkflow getWorkflow() {
		if (getProject() != null) {
			return getProject().getFlexoWorkflow();
		} else if (getProcess() != null && getProcess() != this) {
			return getProcess().getWorkflow();
		}
		return null;
	}

	public FlexoProcess getProcess() {
		return _process;
	}

	public void setProcess(FlexoProcess p) {
		_process = p;
	}

	/**
	 * Return a Vector of all embedded WKFObjects (Note must include itself in this vector)
	 * 
	 * @return a Vector of WKFObject instances
	 */
	public abstract Vector<? extends WKFObject> getAllEmbeddedWKFObjects();

	@Override
	public Collection<? extends WKFObject> getEmbeddedValidableObjects() {
		// TODO : use a non-recursive method instead (or implement getEmbeddedValidableObject in every subclass)
		return getAllEmbeddedWKFObjects();
	}

	public Vector<WKFObject> getAllRecursivelyEmbeddedWKFObjects() {
		HashSet<WKFObject> returned = new HashSet<WKFObject>();
		processToAdditionOfEmbedded(this, returned);
		return new Vector<WKFObject>(returned);
	}

	private void processToAdditionOfEmbedded(WKFObject object, Collection<WKFObject> queue) {
		if (!queue.contains(object)) {
			queue.add(object);
		}
		if (object.getAllEmbeddedWKFObjects() != null) {
			Enumeration<? extends WKFObject> en = object.getAllEmbeddedWKFObjects().elements();
			WKFObject candidate = null;
			while (en.hasMoreElements()) {
				candidate = en.nextElement();
				if (candidate == null) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Object of class " + object.getClass().getName()
								+ " returned IEObjects null in its method getEmbeddedIEObjects");
					}
					continue;
				}
				if (!queue.contains(candidate)) {
					queue.add(candidate);
					processToAdditionOfEmbedded(candidate, queue);
				}
			}
		}

	}

	@Override
	public Object objectForKey(String key) {
		return super.objectForKey(key);
	}

	/**
	 * Overrides the default KV-coding implementation by sending notifications about modified attribute
	 * 
	 * Overrides
	 * 
	 * @see org.openflexo.kvc.KeyValueCoding#setObjectForKey(java.lang.Object, java.lang.String)
	 * @see org.openflexo.kvc.KeyValueCoding#setObjectForKey(java.lang.Object, java.lang.String)
	 */
	@Override
	public void setObjectForKey(Object object, String key) {
		Object oldValue = objectForKey(key);
		super.setObjectForKey(object, key);
		if (oldValue != null && !oldValue.equals(object) || oldValue == null && object != null) {
			// logger.info("Obj: "+getClass().getName()+" property: "+key+" was "+oldValue+" is now "+object);
			notifyModification(key, oldValue, object);
		}
	}

	@Override
	public Class getTypeForKey(String key) {
		if (logger.isLoggable(Level.FINE)) {
			logger.finer("getTypeForKey for " + key);
		}
		try {
			return super.getTypeForKey(key);
		} catch (InvalidObjectSpecificationException e) {
			if (logger.isLoggable(Level.FINE)) {
				logger.finer("OK, let the inspector to determine type of dynamic atttribute !");
			}
			return null;
		}
	}

	protected void notifyModification(String key, Object oldValue, Object newValue) {
		setChanged();
		notifyObservers(new AttributeDataModification(key, oldValue, newValue));

	}

	@Override
	public boolean delete() {
		return super.delete();
	}

	// ==========================================================================
	// ========================== Embedding implementation =====================
	// ==========================================================================

	// TODO: we really need to rewrite all this contains/isContained implementation, since it's really
	// not well organized, and has a lot of inconsistency. One of the both method shoud be final, and all cases
	// handled in the other one

	public boolean isContainedIn(WKFObject obj) {
		// logger.warning
		// ("Contains not IMPLEMENTED for "+getClass().getName()+" and "+obj.getClass().getName()+": default implementation returns true only if supplied object is the owner process: override in sub-classes !");
		return obj == getProcess();
	}

	public boolean contains(WKFObject obj) {
		return obj.isContainedIn(this);
	}

	/**
	 * Purpose of this method is to compute a closed set embedding all supplied object using isContainedIn() / contains() methods
	 * 
	 * @return
	 */
	public static <T extends WKFObject> Vector<T> buildEnclosingSetOfObjects(Vector<T> someObjects) {
		Vector<T> returned = new Vector<T>();
		if (someObjects != null) {
			for (T obj : someObjects) {
				boolean alreadyContained = false;
				for (T temp : returned) {
					if (obj.isContainedIn(temp)) {
						alreadyContained = true;
					}
				}
				if (!alreadyContained) {
					// Not already contained, add it
					// Before to do it, look if some other are to be removed
					Vector<T> removeThose = new Vector<T>();
					for (T temp : returned) {
						if (temp.isContainedIn(obj)) {
							removeThose.add(temp);
						}
					}
					returned.removeAll(removeThose);
					returned.add(obj);
				}
			}
		}
		return returned;
	}

	// ==========================================================================
	// ========================== Observable implementation =====================
	// ==========================================================================

	public void notifyAttributeModification(String attributeName, Object oldValue, Object newValue) {
		setChanged();
		notifyObservers(new WKFAttributeDataModification(attributeName, oldValue, newValue));
	}

	public void forwardNotification(DataModification dataModification) {
		setChanged();
		notifyObservers(dataModification);
	}

	// ==========================================================================
	// ========================= Validable interface
	// ============================
	// ==========================================================================

	/**
	 * Return default validation model for this object
	 * 
	 * @return
	 */
	@Override
	public ValidationModel getDefaultValidationModel() {
		if (getProcess() != null) {
			if (getProcess().getProject() != null) {
				return getProcess().getProject().getWKFValidationModel();
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not access to project !");
				}
			}
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not access to process !");
			}
		}
		return null;
	}

	/**
	 * Returns a flag indicating if this object is valid according to default validation model
	 * 
	 * @return boolean
	 */
	@Override
	public boolean isValid() {
		return isValid(getDefaultValidationModel());
	}

	/**
	 * Returns a flag indicating if this object is valid according to specified validation model
	 * 
	 * @return boolean
	 */
	@Override
	public boolean isValid(ValidationModel validationModel) {
		return validationModel.isValid(this);
	}

	/**
	 * Validates this object by building new ValidationReport object Default validation model is used to perform this validation.
	 */
	@Override
	public ValidationReport validate() {
		return validate(getDefaultValidationModel());
	}

	/**
	 * Validates this object by building new ValidationReport object Supplied validation model is used to perform this validation.
	 */
	@Override
	public ValidationReport validate(ValidationModel validationModel) {
		return validationModel.validate(this);
	}

	/**
	 * Validates this object by appending eventual issues to supplied ValidationReport. Default validation model is used to perform this
	 * validation.
	 * 
	 * @param report
	 *            , a ValidationReport object on which found issues are appened
	 */
	@Override
	public void validate(ValidationReport report) {
		validate(report, getDefaultValidationModel());
	}

	/**
	 * Validates this object by appending eventual issues to supplied ValidationReport. Supplied validation model is used to perform this
	 * validation.
	 * 
	 * @param report
	 *            , a ValidationReport object on which found issues are appened
	 */
	@Override
	public void validate(ValidationReport report, ValidationModel validationModel) {
		validationModel.validate(this, report);
	}

	/**
	 * Return a vector of all embedded objects on which the validation will be performed
	 * 
	 * @return a Vector of Validable objects
	 */
	@Override
	public Vector<Validable> getAllEmbeddedValidableObjects() {
		return new Vector<Validable>(getAllEmbeddedWKFObjects());
	}

	/**
	 * Returns a vector of all objects that will be deleted if you call delete on this object. If no other object are deleted, then the
	 * method should return EMPTY_VECTOR
	 * 
	 * @return
	 */
	public abstract Vector<? extends WKFObject> getAllEmbeddedDeleted();

	// ==========================================================================
	// ============================= Validation
	// =================================
	// ==========================================================================

	public static class WKFObjectMustReferToAProcess extends ValidationRule {
		public WKFObjectMustReferToAProcess() {
			super(WKFObject.class, "object_must_refer_to_a_process");
		}

		@Override
		public ValidationIssue applyValidation(final Validable object) {
			final WKFObject wkfObject = (WKFObject) object;
			if (wkfObject.getProcess() == null) {
				return new ValidationError(this, object, "internal_consistency_error_object_does_not_refer_to_a_process");
			}
			return null;
		}
	}

}
