package org.openflexo.foundation.data.validator;

import org.openflexo.foundation.data.FlexoAttribute;
import org.openflexo.foundation.data.FlexoEntity;
import org.openflexo.toolbox.StringUtils;

/**
 * A simple class to check that the entity is in a correct state.
 */
public class FlexoEntityValidator {
    /**
     * Ensure entity is valid and ensure all entity attributes (if any) are valid.
     * @param entity  entity to validate.
     * @throws FlexoDataValidationException is this entity is not valid
     * (i.e. noName, null attributes list, invalid attribute, null attribute).
     */
    public static void validate(FlexoEntity entity){
        if(entity==null) {
            return;
        }
        if(StringUtils.isEmpty(entity.getName())){
            throw new FlexoDataValidationException("Entity name cannot be empty."+ entity, entity);
        }
        if(entity.getFlexoAttributes()==null){
            throw new FlexoDataValidationException("attributes cannot be null on entity "+entity.getName(), entity);
        }
        for(FlexoAttribute attribute:entity.getFlexoAttributes()){
            if(attribute==null){
                throw new FlexoDataValidationException("Entity "+entity.getName()+" contains a null attribute.", entity);
            }
            FlexoAttributeValidator.validate(attribute);
        }
    }
}
