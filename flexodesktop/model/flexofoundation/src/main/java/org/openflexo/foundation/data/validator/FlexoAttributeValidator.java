package org.openflexo.foundation.data.validator;

import org.openflexo.foundation.data.FlexoAttribute;
import org.openflexo.toolbox.StringUtils;

/**
 * A simple class to check that the attribute is in a correct state.
 */
public class FlexoAttributeValidator {

    /**
     * Ensure attribute is valid.
     * @param attribute  attribute to validate. (can be null)
     * @throws FlexoDataValidationException is this attribute is not valid
     * (i.e. noName, no entity or no type).
     */
    public static void validate(FlexoAttribute attribute){
        if(attribute==null){
            return;
        }
        if(StringUtils.isEmpty(attribute.getName())){
            throw new FlexoDataValidationException("Attribute name cannot be empty."+ attribute, attribute);
        }
        if(attribute.getFlexoEntity()==null){
            throw new FlexoDataValidationException("FlexoEntity cannot be null on attribute "+attribute.getName(), attribute);
        }
        if(attribute.getAttributeType()==null){
            throw new FlexoDataValidationException("Attribute type cannot be null on attribute "
                    +attribute.getFlexoEntity().getName()+"."+attribute.getName(), attribute);
        }
    }
}
