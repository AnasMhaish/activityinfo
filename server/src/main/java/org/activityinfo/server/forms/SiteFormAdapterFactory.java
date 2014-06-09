package org.activityinfo.server.forms;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import org.activityinfo.core.shared.form.*;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.legacy.shared.model.ActivityDTO;
import org.activityinfo.server.database.hibernate.entity.*;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.activityinfo.legacy.shared.adapter.CuidAdapter.activityCategoryFolderId;
import static org.activityinfo.legacy.shared.adapter.CuidAdapter.attributeGroupField;

public class SiteFormAdapterFactory {

    private static final Logger LOGGER = Logger.getLogger(SiteFormAdapterFactory.class.getName());

    private final EntityManager entityManager;
    private final MemcacheService memcacheService;

    @Inject
    public SiteFormAdapterFactory(EntityManager entityManager, MemcacheService memcacheService) {
        this.entityManager = entityManager;
        this.memcacheService = memcacheService;
    }

    public SiteFormAdapter create(int activityId) {

        Activity activity = entityManager.find(Activity.class, activityId);

        SiteFormAdapter cached = tryLoadFromCache(activity);
        if(cached != null) {
            return cached;
        }

        Cuid classId = CuidAdapter.activityFormClass(activityId);
        FormClass siteForm = new FormClass(classId);
        siteForm.setLabel(activity.getName());

        if (!Strings.isNullOrEmpty(activity.getCategory())) {
            siteForm.setParentId(activityCategoryFolderId(activity.getDatabase().getId(), activity.getCategory()));
        } else {
            siteForm.setParentId(CuidAdapter.databaseId(activity.getDatabase().getId()));
        }

        FormField partnerField = new FormField(CuidAdapter.field(classId, CuidAdapter.PARTNER_FIELD));
        partnerField.setLabel(I18N.CONSTANTS.partner());
        partnerField.setRange(CuidAdapter.partnerFormClass(activity.getDatabase().getId()));
        partnerField.setType(FormFieldType.REFERENCE);
        partnerField.setCardinality(FormFieldCardinality.SINGLE);
        partnerField.setRequired(true);
        siteForm.addElement(partnerField);

        FormField projectField = new FormField(CuidAdapter.field(classId, CuidAdapter.PROJECT_FIELD));
        projectField.setLabel(I18N.CONSTANTS.project());
        projectField.setRange(CuidAdapter.projectFormClass(activity.getDatabase().getId()));
        projectField.setType(FormFieldType.REFERENCE);
        projectField.setCardinality(FormFieldCardinality.SINGLE);
        siteForm.addElement(projectField);

        if(activity.getReportingFrequency() == ActivityDTO.REPORT_ONCE) {

            FormField startDateField = new FormField(CuidAdapter.field(classId, CuidAdapter.START_DATE_FIELD));
            startDateField.setLabel(I18N.CONSTANTS.startDate());
            startDateField.setType(FormFieldType.LOCAL_DATE);
            startDateField.setRequired(true);
            siteForm.addElement(startDateField);

            FormField endDateField = new FormField(CuidAdapter.field(classId, CuidAdapter.END_DATE_FIELD));
            endDateField.setLabel(I18N.CONSTANTS.endDate());
            endDateField.setType(FormFieldType.LOCAL_DATE);
            endDateField.setRequired(true);
            siteForm.addElement(endDateField);
        }

        FormField locationField = new FormField(CuidAdapter.locationField(activity.getId()));
        locationField.setLabel(activity.getLocationType().getName());
        locationField.setRange(locationClass(activity.getLocationType()));
        locationField.setType(FormFieldType.REFERENCE);
        locationField.setRequired(true);
        locationField.setCardinality(FormFieldCardinality.SINGLE);
        siteForm.addElement(locationField);

        Map<Integer, Integer> attributeGroupMap = new HashMap<>();
        for (AttributeGroup group : activity.getAttributeGroups()) {
            FormField attributeField = new FormField(attributeGroupField(activity.getId(), group.getId()));
            attributeField.setLabel(group.getName());
            attributeField.setRange(CuidAdapter.attributeGroupFormClass(group.getId()));
            attributeField.setType(FormFieldType.REFERENCE);
            attributeField.setRequired(group.isMandatory());
            if (group.isMultipleAllowed()) {
                attributeField.setCardinality(FormFieldCardinality.MULTIPLE);
            } else {
                attributeField.setCardinality(FormFieldCardinality.SINGLE);
            }
            for(Attribute attribute : group.getAttributes()) {
                attributeGroupMap.put(attribute.getId(), attribute.getGroup().getId());
            }
            siteForm.addElement(attributeField);
        }

        FormClass monthlyReport = null;
        if(activity.getReportingFrequency() == ActivityDTO.REPORT_ONCE) {
            addIndicators(siteForm, activity);
        } else {
            monthlyReport = new FormClass(CuidAdapter.monthlyReportFormClass(activityId));

            FormField siteField = new FormField(Cuid.create("site"));
            siteField.setType(FormFieldType.REFERENCE);
            siteField.setRequired(true);
            siteField.setLabel("site");
            monthlyReport.addElement(siteField);

            FormField monthField = new FormField(Cuid.create("month"));
            monthField.setType(FormFieldType.LOCAL_DATE);
            monthField.setLabel("Month");
            monthField.setRequired(true);
            monthlyReport.addElement(monthField);

            addIndicators(monthlyReport, activity);
        }

        FormField commentsField = new FormField(CuidAdapter.commentsField(activity.getId()));
        commentsField.setType(FormFieldType.NARRATIVE);
        commentsField.setLabel(I18N.CONSTANTS.comments());
        siteForm.addElement(commentsField);

        SiteFormAdapter adapter = new SiteFormAdapter(siteForm, monthlyReport, attributeGroupMap);

        memcacheService.put(cacheKey(activity), adapter);

        return adapter;
    }

    private SiteFormAdapter tryLoadFromCache(Activity activity) {
        try {
            return (SiteFormAdapter)memcacheService.get(cacheKey(activity));
        } catch(Exception caught) {
            LOGGER.log(Level.WARNING, "Exception while trying to fetch SiteFormAdapter from cache",
                    caught);
            return null;
        }
    }

    private String cacheKey(Activity activity) {
        return getClass().getName() + ":" + activity.getId() + "v" + activity.getSchemaVersion();
    }

    private void addIndicators(FormClass siteForm, Activity activity) {
        Map<String, FormSection> categoryMap = new HashMap<>();
        for (Indicator indicator : activity.getIndicators()) {
            String category = indicator.getCategory();
            if(category == null) {
                siteForm.addElement(createIndicatorField(indicator));
            } else {
                FormSection section = categoryMap.get(category);
                if(section == null) {
                    section = new FormSection(CuidAdapter.activityFormSection(activity.getId(), category));
                    categoryMap.put(category, section);
                    siteForm.addElement(section);
                }
                section.addElement(createIndicatorField(indicator));
            }
        }
    }

    private Cuid locationClass(LocationType locationType) {
        if (locationType.getBoundAdminLevel() != null) {
            return CuidAdapter.adminLevelFormClass(locationType.getBoundAdminLevel().getId());
        } else {
            return CuidAdapter.locationFormClass(locationType.getId());
        }
    }

    private FormField createIndicatorField(Indicator indicator) {
        FormField field = new FormField(CuidAdapter.indicatorField(indicator.getId()));
        field.setLabel(indicator.getName());
        field.setDescription(indicator.getDescription());
        field.setType(FormFieldType.QUANTITY);
        field.setUnit(indicator.getUnits());
        return field;
    }
}
