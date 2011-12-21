package org.openflexo.foundation.data.validator;

import org.openflexo.foundation.data.FlexoEntity;
import org.openflexo.foundation.data.FlexoEnum;

import java.util.HashSet;
import java.util.Set;

/**
 * A simple class to check that a set of FlexoEntity and FlexoEnum are consistent.
 */
public class FlexoDataValidator {

    /**
     * Ensure there is no name clash between entities and enums.
     * Ensure that all entities and enums are valid.
     *
     * @param flexoEntities entities to validate
     * @param flexoEnums    enums to validate
     * @throws IllegalStateException if data is not valid.
     */
    public void validate(Set<FlexoEntity> flexoEntities, Set<FlexoEnum> flexoEnums) {
        Set<String> existingValues = new HashSet<String>();
        if (flexoEntities != null) {
            for (FlexoEntity flexoEntity : flexoEntities) {
                FlexoEntityValidator.validate(flexoEntity);
                if (existingValues.contains(flexoEntity.getName())) {
                    throw new IllegalStateException("Found 2 entities with same name: " + flexoEntity.getName());
                }
                existingValues.add(flexoEntity.getName());
            }
        }
        if (flexoEnums != null) {
            for (FlexoEnum flexoEnum : flexoEnums) {
                FlexoEnumValidator.validate(flexoEnum);
                if (existingValues.contains(flexoEnum.getName())) {
                    throw new IllegalStateException("Found flexoEnum name clash with either another enum, " +
                            "either another entity: " + flexoEnum.getName());
                }
                existingValues.add(flexoEnum.getName());
            }
        }
    }

}
