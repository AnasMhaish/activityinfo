package org.activityinfo.core.shared.importing.strategy;

import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.core.shared.form.FormField;

/**
 * Some fields can have multiple binding "sites" to which columns
 * can be bound. For example, a GeographicPoint can accept two columns -
 * a latitude and a longitude - as an import source.
 */
public class ImportTarget {

    private final FormField formField;
    private final TargetSiteId site;
    private final String label;
    private final Cuid formClassId;

    public ImportTarget(FormField formField, TargetSiteId site, String label, Cuid formClassId) {
        this.formField = formField;
        this.site = site;
        this.label = label;
        this.formClassId = formClassId;
    }

    public Cuid getFormClassId() {
        return formClassId;
    }

    public FormField getFormField() {
        return formField;
    }

    public TargetSiteId getSite() {
        return site;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label + "[" + site + "@" + formField.getId() + "]";
    }
}
