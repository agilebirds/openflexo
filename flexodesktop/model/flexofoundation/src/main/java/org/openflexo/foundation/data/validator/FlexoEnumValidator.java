package org.openflexo.foundation.data.validator;

import org.openflexo.foundation.data.FlexoEnum;
import org.openflexo.foundation.data.FlexoEnumValue;
import org.openflexo.toolbox.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * A simple class to check that a FlexoEnum is in a correct state.
 */
public class FlexoEnumValidator {

    /**
     * Ensure flexoEnum is valid.
     *
     * @param flexoEnum flexoEnum to validate.
     * @throws FlexoDataValidationException is this flexoEnum is not valid
     *                               (i.e. noName, duplicate values, null value).
     */
    public static void validate(FlexoEnum flexoEnum) {
        if (StringUtils.isEmpty(flexoEnum.getName())) {
            throw new FlexoDataValidationException("FlexoEnum name cannot be empty." + flexoEnum, flexoEnum);
        }
        if (flexoEnum.getValues() == null) {
            throw new FlexoDataValidationException("values cannot be null on flexoEnum " + flexoEnum.getName(), flexoEnum);
        }
        Set<String> existingValues = new HashSet<String>();
        for (FlexoEnumValue value : flexoEnum.getValues()) {
            if(value==null){
                throw new FlexoDataValidationException("value in a FlexoEnum cannot be null.", flexoEnum);
            }
            if(existingValues.contains(value.getName())){
                throw new FlexoDataValidationException("FlexoEnum contains a duplicate value :"+value, flexoEnum);
            }
            existingValues.add(value.getName());
        }
    }
}
