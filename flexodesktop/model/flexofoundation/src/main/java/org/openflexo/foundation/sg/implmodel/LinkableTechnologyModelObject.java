package org.openflexo.foundation.sg.implmodel;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.apache.commons.lang.StringUtils;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.dm.DMObjectDeleted;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.sg.implmodel.event.SGAttributeModification;
import org.openflexo.foundation.utils.FlexoModelObjectReference;

/**
 * Represent a Technology Model Object which can be linked to (synchronized with) a Flexo model Object.
 * 
 * @author Nicolas Daniels
 * 
 * @param <T> The Flexo model object type this object can be liked to
 */
abstract public class LinkableTechnologyModelObject<T extends DMObject> extends TechnologyModelObject implements FlexoObserver {

	protected static final Logger logger = Logger.getLogger(LinkableTechnologyModelObject.class.getPackage().getName());

	private T linkedFlexoModelObject;
	private boolean wasLinkedAtLastDeserialization;
	private boolean isNameSynchronizedWithLinkedObject;

	public LinkableTechnologyModelObject(FlexoProject project) {
		super(project);
	}

	public LinkableTechnologyModelObject(ImplementationModel implementationModel) {
		super(implementationModel);
	}

	/**
	 * @param implementationModel
	 * @param linkedFlexoModelObject Can be null
	 */
	public LinkableTechnologyModelObject(ImplementationModel implementationModel, T linkedFlexoModelObject) {
		super(implementationModel);
		setLinkedFlexoModelObject(linkedFlexoModelObject);
		if (linkedFlexoModelObject != null)
			isNameSynchronizedWithLinkedObject = true;
	}

	/* ============== */
	/* == Observer == */
	/* ============== */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getLinkedFlexoModelObject() && dataModification instanceof DMObjectDeleted) {
			delete();
		}
	}

	/* =============== */
	/* === Methods === */
	/* =============== */

	/**
	 * Update everything which can be retrieved from the linked Flexo Model Object. If the linked flexo model object is not set, nothing is performed.
	 */
	public void synchronizeWithLinkedFlexoModelObject() {
		// Update name is not performed here because some parent class could not want to use the same name as the linked flexo object.
		// If needed, it must be performed in the parent class.
	}

	/**
	 * Update the name with the linked flexo model object name if necessary.
	 */
	public void updateNameIfNecessary() {
		if (getLinkedFlexoModelObject() != null) {
			if (getIsNameSynchronizedWithLinkedObject()) {
				try {
					setName(getDefaultName());
				} catch (Exception e) {
					if (logger.isLoggable(Level.WARNING))
						logger.warning("Cannot propagate name from linked Flexo Model Object to " + this.getClass().getName() + ". " + e.getMessage());
				}
			} else
				updateIsNameSynchronizedWithLinkedObject(); // Update this anyway in case the name of the linked object is set to the same name as this object. In this case synchonization is set back
															// to true.
		}
	}

	/**
	 * Escapes the name before using it as name for this Object. By default it does nothing. <br>
	 * Override to use the correct implementation depending on your object.
	 * 
	 * @param name
	 * @return the name escaped to be used as name for this object.
	 */
	protected String escapeName(String name) {
		return name;
	}

	/**
	 * Retrieve the default name to use based on its linked Flexo Model object. If no Flexo Model object is linked, null is returned.
	 * 
	 * @return the default name to use.
	 */
	public String getDefaultName() {
		return getLinkedFlexoModelObject() != null ? escapeName(getLinkedFlexoModelObject().getName()) : null;
	}

	public void updateIsNameSynchronizedWithLinkedObject() {
		if (!isDeserializing) {
			String defaultName = getDefaultName();
			setIsNameSynchronizedWithLinkedObject(defaultName != null && (StringUtils.isEmpty(getName()) || StringUtils.equals(defaultName, getName())));
		}
	}

	/* ===================== */
	/* == Getter / Setter == */
	/* ===================== */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName(String name) {
		super.setName(name);
		updateIsNameSynchronizedWithLinkedObject();
	}

	public T getLinkedFlexoModelObject() {
		return linkedFlexoModelObject;
	}

	public void setLinkedFlexoModelObject(T linkedFlexoModelObject) {
		if (requireChange(this.linkedFlexoModelObject, linkedFlexoModelObject)) {
			T oldValue = this.linkedFlexoModelObject;

			if (oldValue != null)
				oldValue.deleteObserver(this);

			this.linkedFlexoModelObject = linkedFlexoModelObject;

			if (this.linkedFlexoModelObject != null)
				this.linkedFlexoModelObject.addObserver(this);

			setChanged();
			notifyObservers(new SGAttributeModification("linkedObject", oldValue, linkedFlexoModelObject));
		}
	}

	public FlexoModelObjectReference<T> getLinkedFlexoModelObjectReference() {
		if (getLinkedFlexoModelObject() != null)
			return new FlexoModelObjectReference<T>(getProject(), getLinkedFlexoModelObject());
		return null;
	}

	public void setLinkedFlexoModelObjectReference(FlexoModelObjectReference<T> linkedFlexoModelObjectReferenceReference) {
		setLinkedFlexoModelObject(linkedFlexoModelObjectReferenceReference == null ? null : linkedFlexoModelObjectReferenceReference.getObject());
		this.wasLinkedAtLastDeserialization = linkedFlexoModelObjectReferenceReference != null;
	}

	public boolean getIsNameSynchronizedWithLinkedObject() {
		return isNameSynchronizedWithLinkedObject;
	}

	public void setIsNameSynchronizedWithLinkedObject(boolean isNameSynchronizedWithLinkedObject) {
		if (requireChange(this.isNameSynchronizedWithLinkedObject, isNameSynchronizedWithLinkedObject)) {
			boolean oldValue = this.isNameSynchronizedWithLinkedObject;
			this.isNameSynchronizedWithLinkedObject = isNameSynchronizedWithLinkedObject;
			setChanged();
			notifyObservers(new SGAttributeModification("isNameSynchronizedWithLinkedObject", oldValue, isNameSynchronizedWithLinkedObject));
		}
	}

	public boolean getWasLinkedAtLastDeserialization() {
		return wasLinkedAtLastDeserialization;
	}
}
