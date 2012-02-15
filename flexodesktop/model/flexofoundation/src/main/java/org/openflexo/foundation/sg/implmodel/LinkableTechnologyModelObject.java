package org.openflexo.foundation.sg.implmodel;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.dm.DMObjectDeleted;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.sg.implmodel.event.SGAttributeModification;
import org.openflexo.foundation.sg.implmodel.metamodel.DerivedAttribute;
import org.openflexo.foundation.sg.implmodel.metamodel.MetaModelHelper;
import org.openflexo.foundation.utils.FlexoModelObjectReference;

/**
 * Represent a Technology Model Object which can be linked to (synchronized with) a Flexo model Object.
 *
 * @param <T> The Flexo model object type this object can be liked to
 * @author Nicolas Daniels
 */
abstract public class LinkableTechnologyModelObject<T extends DMObject> extends TechnologyModelObject implements FlexoObserver {

    protected static final Logger logger = Logger.getLogger(LinkableTechnologyModelObject.class.getPackage().getName());

    private T linkedFlexoModelObject;
    private boolean wasLinkedAtLastDeserialization;
    private Map<String, Boolean> derivedAttributeSynchronizationStatus = new HashMap<String, Boolean>();

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
        initAllSynchronizedAttributes();
    }

    /**
     * by default all attributes must be synchronized. This is called by the constructor.
     */
    private final void initAllSynchronizedAttributes(){
        for(String attributeName:MetaModelHelper.getAllDerivableAttributes(this)){
            setDerivedAttributeSynchronizationStatusForKey(true,attributeName);
        }
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
        } else if (observable == getLinkedFlexoModelObject()) {
            synchronizeWithLinkedFlexoModelObject();
        }
    }

    public String getDerivedName() {
        return getLinkedFlexoModelObject() != null ? escapeName(getLinkedFlexoModelObject().getName()) : null;
    }

    public String getDerivedDescription() {
        return getLinkedFlexoModelObject() != null ? getLinkedFlexoModelObject().getDescription() : null;
    }

    /* =============== */
    /* === Methods === */
    /* =============== */

    protected void checkSynchronizationStatus(String attributeName, Object value) {
        if (isDeserializing) return;
        if (MetaModelHelper.isDerivedAttribute(this,attributeName)) {
            Object derivedValue = MetaModelHelper.invokeTransformation(this, attributeName);
            if (derivedValue == null) {
                setDerivedAttributeSynchronizationStatusForKey(value == null, attributeName);
            } else {
                setDerivedAttributeSynchronizationStatusForKey(derivedValue.equals(value), attributeName);
            }
        }
    }

    /**
     * Update everything which can be retrieved from the linked Flexo Model Object. If the linked flexo model object is not set, nothing is performed.
     */
    public void synchronizeWithLinkedFlexoModelObject() {
        // Update name is not performed here because some parent class could not want to use the same name as the linked flexo object.
        // If needed, it must be performed in the parent class.

        for (String attributeName : MetaModelHelper.getAllDerivableAttributes(this)) {
            if (isStillSynchronized(attributeName)) {
                Object derivedValue = MetaModelHelper.invokeTransformation(this, attributeName);
                MetaModelHelper.invokeSetter(this, attributeName, derivedValue);
            }
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

    public boolean isStillSynchronized(String attributeName) {
        Boolean b = derivedAttributeSynchronizationStatus.get(attributeName);
        return b!=null && b.booleanValue();
    }

    /* ===================== */
    /* == Getter / Setter == */
    /* ===================== */

    /**
     * {@inheritDoc}
     */
    @DerivedAttribute
    @Override
    public void setName(String name) {
        super.setName(name);
        checkSynchronizationStatus("name", name);
    }
    
    /**
     * {@inheritDoc}
     */
    @DerivedAttribute
    @Override
    public void setDescription(String description) {
        super.setDescription(description);
        checkSynchronizationStatus("description", description);
    }

    public T getLinkedFlexoModelObject() {
        return linkedFlexoModelObject;
    }

    public final void setLinkedFlexoModelObject(T linkedFlexoModelObject) {
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

    public boolean getWasLinkedAtLastDeserialization() {
        return wasLinkedAtLastDeserialization;
    }


    public Map<String, Boolean> getDerivedAttributeSynchronizationStatus() {
        return derivedAttributeSynchronizationStatus;
    }

    public void setDerivedAttributeSynchronizationStatus(Map<String, Boolean> derivedAttributeSynchronizationStatus) {
        this.derivedAttributeSynchronizationStatus = new TreeMap<String, Boolean>(derivedAttributeSynchronizationStatus);
    }

    public Boolean getDerivedAttributeSynchronizationStatusForKey(String key) {
        return derivedAttributeSynchronizationStatus.get(key);
    }

    public void setDerivedAttributeSynchronizationStatusForKey(String status, String key) {
        setDerivedAttributeSynchronizationStatusForKey("true".equals(status), key);
    }

    public void setDerivedAttributeSynchronizationStatusForKey(Boolean status, String key) {
        derivedAttributeSynchronizationStatus.put(key, status);
    }

    public void removeDerivedAttributeSynchronizationStatusWithKey(String key) {
        derivedAttributeSynchronizationStatus.remove(key);
    }

}
