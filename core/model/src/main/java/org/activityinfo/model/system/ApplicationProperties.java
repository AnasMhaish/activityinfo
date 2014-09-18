package org.activityinfo.model.system;

import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.ResourceId;

/**
 * Defines Application-level properties.
 * <strong>Should not be used as a form field id, only as a super property</strong>
 */
public class ApplicationProperties {

    /**
     * Application-defined property that provides a human-readable name for
     * a given form instance.
     */
    public static final ResourceId LABEL_PROPERTY = ResourceId.valueOf("label");

    public static final ResourceId PARENT_PROPERTY = ResourceId.valueOf("_parent");

    /**
     * Application-defined property that provides an extended human-readable description
     * for a given form instance.
     */
    public static final ResourceId DESCRIPTION_PROPERTY = ResourceId.valueOf("_description");

    /**
     * Application-defined property that provides the class ids of an instance.
     */
    public static final ResourceId CLASS_PROPERTY = ResourceId.valueOf("_classOf");


    public static final ResourceId HIERARCHIAL = ResourceId.valueOf("_multiLevel");

    public static final ResourceId COUNTRY_CLASS = ResourceId.valueOf("_country");


    public static String getLabelPropertyName(String classId) {
        if (FormClass.CLASS_ID.asString().equals(classId)) {
            return FormClass.LABEL_FIELD_ID;

        } else if (FolderClass.CLASS_ID.asString().equals(classId)) {
            return FolderClass.LABEL_FIELD_ID.asString();

        } else if (PivotTableModel.CLASS_ID.asString().equals(classId)) {
            return PivotTableModel.LABEL_FIELD_ID;
        }
        return null;
    }

    /**
     *
     * @return {@code true} if resources with class {@code formClassId} should be
     * displayed within a folder
     */
    public static boolean isFolderItem(ResourceId formClassId) {
        return formClassId.equals(FormClass.CLASS_ID) ||
            formClassId.equals(FolderClass.CLASS_ID) ||
            formClassId.equals(PivotTableModel.CLASS_ID);
    }
}
