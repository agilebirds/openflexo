package org.openflexo.foundation.data;

import org.openflexo.foundation.FlexoObserver;

public interface FlexoDataObject extends FlexoObserver {

    /**
     * @return a business description of this object.
     */
    public String getDescription();

    /**
     * @return the name of this FlexoDataObject.
     */
    public String getName();
}
