package org.activityinfo.model.legacy;

import com.google.common.base.Strings;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;

/**
 * Provides an adapter between legacy ids, which are either random or sequential 32-bit integers but only
 * guaranteed to be unique within a table, and Collision Resistant Universal Ids (CUIDs) which
 * will serve as the identifiers for all user-created objects.
 */
public class CuidAdapter {

    public static final char COUNTRY_DOMAIN = 'c';

    public static final char SITE_DOMAIN = 's';

    public static final char ACTIVITY_DOMAIN = 'a';

    public static final char LOCATION_DOMAIN = 'g'; // avoid lower case l !

    public static final char LOCATION_TYPE_DOMAIN = 'L'; // avoid lower case l !

    public static final char PARTNER_DOMAIN = 'p';

    public static final char PARTNER_FORM_CLASS_DOMAIN = 'P';

    public static final char INDICATOR_DOMAIN = 'I';

    public static final char ATTRIBUTE_GROUP_DOMAIN = 'A';

    public static final char ATTRIBUTE_GROUP_FIELD_DOMAIN = 'Q';

    public static final char MONTHLY_REPORT_CLASS_DOMAIN = 'm';


    public static final char MONTHLY_REPORT_INSTANCE_DOMAIN = 'M';

    public static final char ATTRIBUTE_DOMAIN = 't';

    public static final char DATABASE_DOMAIN = 'd';

    public static final char ADMIN_LEVEL_DOMAIN = 'E';

    public static final char ADMIN_ENTITY_DOMAIN = 'z';

    public static final char PROJECT_CLASS_DOMAIN = 'R';

    public static final char PROJECT_DOMAIN = 'r';

    public static final char ACTIVITY_CATEGORY_DOMAIN = 'C';

    public static final char USER_DOMAIN = 'U';


    public static final int NAME_FIELD = 1;
    public static final int ADMIN_PARENT_FIELD = 2;
    public static final int CODE_FIELD = 3;
    public static final int AXE_FIELD = 4;
    public static final int GEOMETRY_FIELD = 5;
    public static final int ADMIN_FIELD = 6;
    public static final int PARTNER_FIELD = 7;
    public static final int PROJECT_FIELD = 8;
    public static final int DATE_FIELD = 9;
    public static final int FULL_NAME_FIELD = 10;
    public static final int LOCATION_FIELD = 11;
    public static final int START_DATE_FIELD = 12;
    public static final int END_DATE_FIELD = 13;
    public static final int COMMENT_FIELD = 14;

    public static final int YEAR_FIELD = 15;

    public static final int REPORTING_PERIODS_FIELD = 15;

    public static final int SITE_FIELD = 16;

    public static final int BLOCK_SIZE = 10;
    public static final String CLASS_FIELD = "_class";



    /**
     * Avoid instance creation.
     */
    private CuidAdapter() {
    }

    public static final ResourceId resourceId(char domain, int id) {
        assert id != 0 : "resourceId for domain " + domain + " has id = 0";
        return ResourceId.valueOf(domain + Integer.toString(id));
    }

    public static ResourceId partnerInstanceId(int databaseId, int partnerId) {
        return ResourceId.valueOf(String.valueOf(DATABASE_DOMAIN) + databaseId +
                                  String.valueOf(PARTNER_DOMAIN) + partnerId);
    }

    /**
     * @return the {@code FormField}  ResourceId for the Location field of a given Activity {@code FormClass}
     */
    public static ResourceId locationField(int activityId) {
        return field(activityFormClass(activityId), LOCATION_FIELD);
    }

    /**
     * @return the {@code FormClass} ResourceId for a given LocationType
     */
    public static ResourceId locationFormClass(int locationTypeId) {
        return resourceId(LOCATION_TYPE_DOMAIN, locationTypeId);
    }

    public static ResourceId locationInstanceId(int locationId) {
        return resourceId(LOCATION_DOMAIN, locationId);
    }

    public static ResourceId adminLevelFormClass(int adminLevelId) {
        return resourceId(ADMIN_LEVEL_DOMAIN, adminLevelId);
    }


    public static ResourceId entity(int adminEntityId) {
        return resourceId(ADMIN_ENTITY_DOMAIN, adminEntityId);

    }

    /**
     * Generates a CUID for a FormField in a given previously-built-in FormClass using
     * the FormClass's CUID and a field index.
     *
     * @param fieldIndex
     * @return
     */
    public static ResourceId field(ResourceId classId, int fieldIndex) {
        return ResourceId.valueOf(classId.asString() + "f" + fieldIndex);
    }

    /**
     * @return the {@code FormClass} ResourceId for a given Activity
     */
    public static ResourceId activityFormClass(int activityId) {
        return resourceId(ACTIVITY_DOMAIN, activityId);
    }


    /**
     * @return the {@code FormClass} ResourceId for a given Activity
     */
    public static ResourceId commentsField(int activityId) {
        //        return new ResourceId(ACTIVITY_DOMAIN + block(activityId) + "C");
        return field(activityFormClass(activityId), COMMENT_FIELD);
    }

    /**
     * @return the {@code FormField} ResourceId for the indicator field within a given
     * Activity {@code FormClass}
     */
    public static ResourceId indicatorField(int indicatorId) {
        return resourceId(INDICATOR_DOMAIN, indicatorId);
    }

    /**
     * @return the {@code FormField} ResourceId for the field of a given Activity {@code FormClass} that
     * references the given AttributeGroup FormClass
     */
    public static ResourceId attributeGroupField(int attributeGroupId) {
        return resourceId(ATTRIBUTE_GROUP_FIELD_DOMAIN, attributeGroupId);
    }

    public static ResourceId activityCategoryFolderId(int dbId, String category) {
        return ResourceId.valueOf(ACTIVITY_CATEGORY_DOMAIN + Integer.toHexString(dbId) + "c" +
                                  Integer.toHexString(Math.abs(category.hashCode())));
    }

    public static ResourceId userId(int userId) {
        return resourceId(USER_DOMAIN, userId);
    }

    public static ResourceId attributeId(int attributeId) {
        return resourceId(ATTRIBUTE_DOMAIN, attributeId);
    }

    /**
     * @param databaseId the id of the user database
     * @return the {@code FormClass} ResourceId for a given database's list of partners.
     */
    public static ResourceId partnerFormClass(int databaseId) {
        return resourceId(PARTNER_FORM_CLASS_DOMAIN, databaseId);
    }

    /**
     * @param databaseId the id of the user database
     * @return the {@code FormClass} ResourceId for a given database's list of projects.
     */
    public static ResourceId projectFormClass(int databaseId) {
        return resourceId(PROJECT_CLASS_DOMAIN, databaseId);
    }

    /**
     * @return the {@code FormSection} ResourceId for a given indicator category within an
     * Activity {@code FormClass}
     */
    public static ResourceId activityFormSection(int id, String name) {
        return ResourceId.valueOf(ACTIVITY_DOMAIN + block(id) + block(name.hashCode()));
    }

    private static String block(int id) {
        return Strings.padStart(Integer.toString(id, Resources.RADIX), BLOCK_SIZE, '0');
    }

    public static int getBlock(ResourceId resourceId, int blockIndex) {
        int startIndex = 1 + (blockIndex * BLOCK_SIZE);
        String block = resourceId.asString().substring(startIndex, startIndex + BLOCK_SIZE);
        return Integer.parseInt(block, Resources.RADIX);
    }

    public static ResourceId databaseId(int databaseId) {
        return resourceId(DATABASE_DOMAIN, databaseId);
    }

    public static ResourceId getAxeFieldId(ResourceId classId) {
        return field(classId, AXE_FIELD);
    }

    public static ResourceId getAdminFieldId(ResourceId classId) {
        return field(classId, ADMIN_FIELD);
    }

    public static ResourceId getPointFieldId(ResourceId classId) {
        return field(classId, GEOMETRY_FIELD);
    }

    public static ResourceId getNameFieldId(ResourceId classId) {
        return field(classId, NAME_FIELD);
    }

    public static ResourceId cuid(char domain, int id) {
        return resourceId(domain, id);
    }

    public static int getLegacyId(ResourceId id) {
        return Integer.parseInt(id.asString().substring(1));
    }
}
