package org.activityinfo.core.shared.application;

import org.activityinfo.datamodel.shared.Cuid;

/**
 * Defines Application-level properties.
 * <strong>Should not be used as a form field id, only as a super property</strong>
 */
public class ApplicationProperties {

    /**
     * Application-defined property that provides a human-readable name for
     * a given form instance.
     */
    public static final Cuid LABEL_PROPERTY = Cuid.create("_label");

    public static final Cuid PARENT_PROPERTY = Cuid.create("_parent");

    /**
     * Application-defined property that provides an extended human-readable description
     * for a given form instance.
     */
    public static final Cuid DESCRIPTION_PROPERTY = Cuid.create("_description");

    /**
     * Application-defined property that provides the class ids of an instance.
     */
    public static final Cuid CLASS_PROPERTY = Cuid.create("_classOf");


    public static final Cuid HIERARCHIAL = Cuid.create("_multiLevel");

    public static final Cuid COUNTRY_CLASS = Cuid.create("_country");

    public static final Cuid COUNTRY_NAME_FIELD = Cuid.create("_country_name");

    public static final Cuid GEOREF_PROPERTY = Cuid.create("_georef");
}
