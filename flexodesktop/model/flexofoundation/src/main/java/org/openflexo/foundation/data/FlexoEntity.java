package org.openflexo.foundation.data;

import java.util.Set;

/**
 * A FlexoEntity is a uniquely named concept holding a set of FlexoAttribute.
 */
public interface FlexoEntity  extends FlexoDataObject {

    /**
     * @return the name of this FlexoEntity. Must be unique within a project and must also
     * be distinct from any FlexoEnum name. Cannot be null or empty.
     */
    public String getName();

    /**
     * @return all attributes in this FlexoEntity. Cannot be null.
     */
    public Set<FlexoAttribute> getFlexoAttributes();

}
