package org.openflexo.foundation.data.validator;

public class FlexoDataValidationException extends IllegalStateException {

    private Object invalidObject;

    public FlexoDataValidationException(String s, Object invalidObject) {
        super(s);
        this.invalidObject = invalidObject;
    }
}
