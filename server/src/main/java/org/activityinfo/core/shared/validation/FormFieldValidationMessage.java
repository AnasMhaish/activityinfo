package org.activityinfo.core.shared.validation;

import org.activityinfo.datamodel.shared.Cuid;

/**
 * Validation Error bound to form field.
 */
public class FormFieldValidationMessage extends ValidationMessage {

    private Cuid fieldId;

    public FormFieldValidationMessage(Cuid fieldId) {
        this.fieldId = fieldId;
    }

    public Cuid getFieldId() {
        return fieldId;
    }
}
