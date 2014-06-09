package org.activityinfo.core.shared.application;

import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.FormField;

/**
 * Defines a system-level FormClass of folders
 */
public class FolderClass {

    public static final Cuid CLASS_ID = Cuid.create("_folder");

    public static final Cuid LABEL_FIELD_ID = Cuid.create("_folder_label");

    public static final Cuid DESCRIPTION_FIELD_ID = Cuid.create("_folder_description");

    public static final FormClass get() {

        FormField labelField = new FormField(LABEL_FIELD_ID);
        labelField.setSuperProperty(ApplicationProperties.LABEL_PROPERTY);

        FormField descriptionField = new FormField(DESCRIPTION_FIELD_ID);
        descriptionField.setSuperProperty(ApplicationProperties.DESCRIPTION_PROPERTY);

        FormClass formClass = new FormClass(CLASS_ID);
        formClass.addElement(labelField);
        formClass.addElement(descriptionField);

        return formClass;
    }
}