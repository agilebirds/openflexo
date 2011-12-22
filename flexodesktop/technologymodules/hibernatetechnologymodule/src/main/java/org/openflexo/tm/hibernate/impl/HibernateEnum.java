/**
 *
 */
package org.openflexo.tm.hibernate.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.apache.commons.lang.StringUtils;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.dm.DMAttributeDataModification;
import org.openflexo.foundation.dm.dm.DMEntityNameChanged;
import org.openflexo.foundation.dm.dm.PropertyRegistered;
import org.openflexo.foundation.dm.dm.PropertyUnregistered;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.LinkableTechnologyModelObject;
import org.openflexo.foundation.sg.implmodel.event.SGAttributeModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectAddedToListModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectDeletedModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectRemovedFromListModification;
import org.openflexo.foundation.xml.ImplementationModelBuilder;
import org.openflexo.toolbox.JavaUtils;

/**
 * @author Nicolas Daniels
 */
public class HibernateEnum extends LinkableTechnologyModelObject<DMEntity> {

    protected static final Logger logger = Logger.getLogger(HibernateEnum.class.getPackage().getName());

    public static final String CLASS_NAME_KEY = "hibernate_enum";

    protected HibernateEnumContainer hibernateEnumContainer;
    protected Vector<HibernateEnumValue> hibernateEnumValues = new Vector<HibernateEnumValue>();

    // ================ //
    // = Constructors = //
    // ================ //

    /**
     * Build a new Hibernate enum for the specified implementation model builder.<br/>
     * This constructor is namely invoked during deserialization.
     *
     * @param builder the builder that will create this enum
     */
    public HibernateEnum(ImplementationModelBuilder builder) {
        this(builder.implementationModel);
        initializeDeserialization(builder);
    }

    /**
     * Build a new Hibernate enum for the specified implementation model.
     *
     * @param implementationModel the implementation model where to create this Hibernate enum
     */
    public HibernateEnum(ImplementationModel implementationModel) {
        super(implementationModel);
    }

    /**
     * @param implementationModel    the implementation model where to create this Hibernate enum
     * @param linkedFlexoModelObject Can be null
     */
    public HibernateEnum(ImplementationModel implementationModel, DMEntity linkedFlexoModelObject) {
        super(implementationModel, linkedFlexoModelObject);
        for (DMProperty enumValue : linkedFlexoModelObject.getProperties().values()) {
            HibernateEnumValue newValue = new HibernateEnumValue(getImplementationModel(), enumValue);
            addToHibernateEnumValues(newValue);
        }
    }

    // =========== //
    // = Methods = //
    // =========== //

    /**
     * @Override
     */
    @Override
    public String getClassNameKey() {
        return CLASS_NAME_KEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getHasInspector() {
        return true;
    }

    /**
     * @Override
     */
    @Override
    public String getFullyQualifiedName() {
        return getHibernateEnumContainer().getFullyQualifiedName() + "." + getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void synchronizeWithLinkedFlexoModelObject() {
        updateNameIfNecessary();
        if (getLinkedFlexoModelObject() != null) {
            List<LinkableTechnologyModelObject<?>> deletedChildren = new ArrayList<LinkableTechnologyModelObject<?>>();

            Map<DMProperty, LinkableTechnologyModelObject<?>> alreadyCreatedChildren = new HashMap<DMProperty, LinkableTechnologyModelObject<?>>();

            //first : filling maps with deleted FlexoModelObject and already linked FlexoModelObject
            for (HibernateEnumValue hibernateEnumValue : this.hibernateEnumValues) {
                if (hibernateEnumValue.getLinkedFlexoModelObject() != null) {
                    alreadyCreatedChildren.put(hibernateEnumValue.getLinkedFlexoModelObject(), hibernateEnumValue);
                } else if (hibernateEnumValue.getWasLinkedAtLastDeserialization()) {
                    deletedChildren.add(hibernateEnumValue);
                }
            }

            List<LinkableTechnologyModelObject<?>> createdEntities = new ArrayList<LinkableTechnologyModelObject<?>>();
            // Create missing enumValues and update existing ones
            for (DMProperty property : getLinkedFlexoModelObject().getProperties().values()) {
                if (!alreadyCreatedChildren.containsKey(property)) {
                    createdEntities.add(createHibernateObjectBasedOnDMProperty(property, false));
                } else {
                    alreadyCreatedChildren.get(property).synchronizeWithLinkedFlexoModelObject();
                }
            }

            // Remove deleted entities & enums
            for (LinkableTechnologyModelObject<?> hibernateObj : deletedChildren) {
                hibernateObj.delete();
            }

            // Synchronize created entities with their linked Flexo object. Performed now to be sure all objects are created
            for (LinkableTechnologyModelObject<?> entity : createdEntities) {
                entity.synchronizeWithLinkedFlexoModelObject();
            }
        }
    }


    /**
     * Add a new HibernateEnumValue to this Enum. The newly created hibernate object is based and linked to the specified
     * DMProperty. If an Hibernate object was already existing for this DMProperty, nothing is performed.
     *
     * @param property                    the DMProperty the newly created Hibernate object should be linked to.
     * @param synchronizeWithLinkedObject specify if the synchronization with the linked object should occur directly or not (useful if multiple object are created
     *                                    which can have link to each others)
     * @return the created object if any, null otherwise.
     */
    public LinkableTechnologyModelObject<?> createHibernateObjectBasedOnDMProperty(DMProperty property, boolean synchronizeWithLinkedObject) {
        HibernateEnumValue hibernateEnumValue = new HibernateEnumValue(getImplementationModel(), property);
        addToHibernateEnumValues(hibernateEnumValue);
        if (synchronizeWithLinkedObject) {
            hibernateEnumValue.synchronizeWithLinkedFlexoModelObject();
        }
        return hibernateEnumValue;
    }

    private HibernateEnumValue getHibernateEnumValue(DMProperty p) {
        for (HibernateEnumValue v : getHibernateEnumValues()) {
            if (p.equals(v.getLinkedFlexoModelObject())) {
                return v;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String escapeName(String name) {
        return JavaUtils.getClassName(name);
    }

    /**
     * Update the is enum with the linked Flexo Model Object one if necessary.
     */
    public void updateIsEnumIfNecessary() {
        if (getLinkedFlexoModelObject() != null) {
            DMEntity dmEntity = getLinkedFlexoModelObject();
            if (!dmEntity.getIsEnumeration()) {
                throw new IllegalStateException("Linked entity is not an Enum. This shouldn't append.");
            }

        }
    }

    /* ===================== */
    /* ====== Actions ====== */
    /* ===================== */

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete() {

        if (getHibernateEnumContainer() != null) {
            getHibernateEnumContainer().removeFromHibernateEnums(this);
        }

        setChanged();
        notifyObservers(new SGObjectDeletedModification<HibernateEnum>(this));
        super.delete();
        deleteObservers();
    }

    /* ============== */
    /* == Observer == */
    /* ============== */

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(FlexoObservable observable, DataModification dataModification) {
        super.update(observable, dataModification);
        if (observable == getLinkedFlexoModelObject()) {
            if (dataModification instanceof DMEntityNameChanged) {
                updateNameIfNecessary();
            } else if (dataModification instanceof PropertyRegistered) {
                DMProperty newProperty = (DMProperty) dataModification.newValue();
                HibernateEnumValue newEnumValue = new HibernateEnumValue(getImplementationModel(), newProperty);
                addToHibernateEnumValues(newEnumValue);
            }
        }
    }

    /* ===================== */
    /* == Getter / Setter == */
    /* ===================== */

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLinkedFlexoModelObject(DMEntity linkedFlexoModelObject) {
        if (linkedFlexoModelObject == null || linkedFlexoModelObject.getIsEnumeration()) {
            super.setLinkedFlexoModelObject(linkedFlexoModelObject);
        } else {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "Cannot set linked object to Hibernate Enum with a non enumeration DM Entity. DM Entity: "
                        + linkedFlexoModelObject.getName());
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(String name) {
        name = escapeName(name);

        if (StringUtils.isEmpty(name)) {
            name = getDefaultName();
        }

        super.setName(name);
    }

    /**
     * Return the hibernateEnumValues stored in this model. HibernateEnumValues are sorted by name.
     *
     * @return sorted hibernateEnumValues stored in this model.
     */
    public Vector<HibernateEnumValue> getHibernateEnumValues() {
        return hibernateEnumValues;
    }

    public void setHibernateEnumValues(Vector<HibernateEnumValue> hibernateEnumValues) {
        if (requireChange(this.hibernateEnumValues, hibernateEnumValues)) {
            Vector<HibernateEnumValue> oldValue = this.hibernateEnumValues;
            this.hibernateEnumValues = hibernateEnumValues;
            setChanged();
            notifyObservers(new SGAttributeModification("hibernateEnumValues", oldValue, hibernateEnumValues));
        }
    }

    public void addToHibernateEnumValues(HibernateEnumValue hibernateEnumValue) {
        hibernateEnumValue.setHibernateEnum(this);
        hibernateEnumValues.add(hibernateEnumValue);

        setChanged();
        notifyObservers(new SGObjectAddedToListModification<HibernateEnumValue>("hibernateEnumValues", hibernateEnumValue));
    }

    public void removeFromHibernateEnumValues(HibernateEnumValue hibernateEnumValue) {
        if (hibernateEnumValues.remove(hibernateEnumValue)) {
            setChanged();
            notifyObservers(new SGObjectRemovedFromListModification<HibernateEnumValue>("hibernateEnumValues", hibernateEnumValue));
        }
    }

    public HibernateEnumContainer getHibernateEnumContainer() {
        return hibernateEnumContainer;
    }

    /**
     * Called only from HibernateEnumContainer at deserialisation or at entity creation
     *
     * @param hibernateEnumContainer
     */
    protected void setHibernateEnumContainer(HibernateEnumContainer hibernateEnumContainer) {
        this.hibernateEnumContainer = hibernateEnumContainer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HibernateImplementation getTechnologyModuleImplementation() {
        return HibernateImplementation.getHibernateImplementation(getImplementationModel());
    }

}
