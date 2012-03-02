package org.openflexo.foundation.data;

/**
 * A FlexoAttribute is a component of a FlexoEntity. The name of a FlexoAttribute is unique
 * within it's FlexoEntity. A FlexoAttribute is always typed.
 */
public interface FlexoAttribute extends FlexoDataObject {

    /**
     * @return the type of this FlexoAttribute. Cannot be null.
     */
    public FlexoAttributeType getAttributeType();

    /**
     * @return the name of this FlexoAttribute.
     * Must be unique within a FlexoEntity. Cannot be null or empty.
     */
    public String getName();

    /**
     * @return if this attributes contains multiple values
     */
    public boolean isMany();

    /**
     * @return the FlexoEntity this FlexoAttribute belongs to. Cannot be null.
     */
    public FlexoEntity getFlexoEntity();

}
