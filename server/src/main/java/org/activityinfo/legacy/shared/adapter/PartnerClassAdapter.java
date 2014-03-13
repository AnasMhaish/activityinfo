package org.activityinfo.legacy.shared.adapter;

import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.LocalizedString;
import org.activityinfo.api2.shared.form.FormClass;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.FormFieldType;
import org.activityinfo.i18n.shared.I18N;


public class PartnerClassAdapter {

    public static Cuid getNameField(Cuid classId) {
        return CuidAdapter.field(classId, CuidAdapter.NAME_FIELD);
    }

    public static Cuid getFullNameField(Cuid classId) {
        return CuidAdapter.field(classId, CuidAdapter.FULL_NAME_FIELD);
    }


    /**
     * Partner was a builtin object type in api1. However, we need a different
     * FormClass for each legacy UserDatabase.
     *
     */
    public static FormClass create(int databaseId) {

        Cuid classId = CuidAdapter.partnerFormClass(databaseId);
        FormClass formClass = new FormClass(classId);
        formClass.setLabel(new LocalizedString(I18N.CONSTANTS.partner()));

        // add the partner's name
        FormField nameField = new FormField(getNameField(classId));
        nameField.setLabel(new LocalizedString(I18N.CONSTANTS.name()));
        nameField.setType(FormFieldType.FREE_TEXT);
        nameField.setRequired(true);
        formClass.addElement(nameField);

        // partner full name
        FormField fullNameField = new FormField(getFullNameField(classId));
        fullNameField.setLabel(new LocalizedString(I18N.CONSTANTS.fullName()));
        fullNameField.setType(FormFieldType.FREE_TEXT);
        formClass.addElement(fullNameField);

        return formClass;
    }


}
