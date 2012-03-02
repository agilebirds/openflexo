package org.openflexo.foundation.data;

public interface FlexoEnumValue extends FlexoDataObject{

    /**
     * @return the name of this FlexoEnumValue. Must be unique within a FlexoEnum.
     */
    public String getName();

    /**
     * @return the FlexoEnum this FlexoEnumValue belongs to. Cannot be null.
     */
    public FlexoEnum getFlexoEnum();
}
