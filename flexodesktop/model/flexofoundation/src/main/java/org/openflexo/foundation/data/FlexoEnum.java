package org.openflexo.foundation.data;

import java.util.List;

/**
 * Represent a static list of String values.
 */
public interface FlexoEnum extends FlexoDataObject {

    /**
     * @return the name of this FlexoEnum. Must be unique within a project and must also
     * be distinct from any FlexoEntity name.
     */
    public String getName();

    /**
     * Each value must be distinct. A value cannot be null.
     * @return list of values in this FlexoEnum
     */
    public List<String> getValues();

}
