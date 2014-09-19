package org.activityinfo.migrator.tables;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.activityinfo.migrator.ResourceMigrator;
import org.activityinfo.migrator.ResourceWriter;
import org.activityinfo.migrator.filter.MigrationContext;
import org.activityinfo.migrator.filter.MigrationFilter;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormElement;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormSection;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.NarrativeType;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.enumerated.EnumValue;
import org.activityinfo.model.type.expr.CalculatedFieldType;
import org.activityinfo.model.type.number.Quantity;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.model.type.time.LocalDateIntervalType;
import org.activityinfo.model.type.time.MonthType;
import org.activityinfo.model.type.time.YearType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.activityinfo.model.legacy.CuidAdapter.*;

public class ActivityTable extends ResourceMigrator {

    private static final Logger LOGGER = Logger.getLogger(ActivityTable.class.getName());

    public static final int ONCE = 0;
    public static final int MONTHLY = 1;

    private final MigrationContext context;

    public ActivityTable(MigrationContext context) {
        this.context = context;
    }

    @Override
    public void getResources(Connection connection, ResourceWriter writer) throws Exception {

        String sql =
                "SELECT " +
                "A.ActivityId, " +
                "A.category, " +
                "A.Name, " +
                "A.ReportingFrequency, " +
                "A.DatabaseId, " +
                "A.LocationTypeId, " +
                "L.Name locationTypeName, " +
                "L.BoundAdminLevelId " +
                "FROM activity A " +
                "LEFT JOIN locationtype L on (A.locationtypeid=L.locationtypeid) " +
                "LEFT JOIN userdatabase d on (A.databaseId=d.DatabaseId) " +
                "WHERE d.dateDeleted is null and A.dateDeleted is null AND " + context.filter().activityFilter("A");

        Map<Integer, List<EnumValue>> attributes = queryAttributes(connection);
        Map<Integer, List<FormElement>> fields = queryFields(connection, attributes, context.filter());
        Set<Integer> databasesWithProjects = queryDatabasesWithProjects(connection);
        Set<ResourceId> categories = Sets.newHashSet();

        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {

                while(rs.next()) {

                    LOGGER.info("Migrating Activity " + rs.getObject("activityId"));

                    int databaseId = rs.getInt("databaseId");
                    ResourceId databaseResourceId = context.resourceId(DATABASE_DOMAIN, databaseId);

                    int activityId = rs.getInt("activityId");
                    String category = rs.getString("category");

                    ResourceId ownerId;
                    if(Strings.isNullOrEmpty(category)) {
                        ownerId = databaseResourceId;
                    } else {
                        ResourceId categoryId = context.getIdStrategy().activityCategoryId(databaseId, category);
                        ownerId = categoryId;
                        if(!categories.contains(categoryId)) {
                            writer.writeResource(categoryResource(databaseResourceId, categoryId, category), null, null);
                            categories.add(categoryId);
                        }
                    }

                    writeSiteForm(ownerId, rs, fields.get(activityId), databasesWithProjects, writer);

                    if(rs.getInt("reportingFrequency")==1) {
                        writePeriodForm(ownerId, rs, fields.get(activityId), writer);
                    }
                }
            }
        }
    }

    private Set<Integer> queryDatabasesWithProjects(Connection connection) throws SQLException {

        String sql = "SELECT distinct databaseId from project WHERE dateDeleted is null";

        Set<Integer> databases = Sets.newHashSet();

        try(Statement statement = connection.createStatement()) {
            try (ResultSet rs = statement.executeQuery(sql)) {
                while (rs.next()) {
                    databases.add(rs.getInt(1));
                }
            }
        }
        return databases;
    }

    private Resource categoryResource(ResourceId databaseId, ResourceId categoryId, String category) {
        return Resources.createResource()
                        .setId(categoryId)
                        .setOwnerId(databaseId)
                        .set(CLASS_FIELD, FolderClass.CLASS_ID)
                        .set(FolderClass.LABEL_FIELD_ID.asString(), category);
    }

    private Map<Integer, List<FormElement>> queryFields(
            Connection connection, Map<Integer, List<EnumValue>> attributes, MigrationFilter filter) throws SQLException {

        LOGGER.info("Querying fields...");


        String indicatorQuery = "(SELECT " +
                                        "ActivityId, " +
                                        "IndicatorId as Id, " +
                                        "Category, " +
                                        "Name, " +
                                        "Description, " +
                                        "Mandatory, " +
                                        "Type, " +
                                        "NULL as MultipleAllowed, " +
                                        "units, " +
                                        "SortOrder, " +
                                        "nameinexpression code, " +
                                        "calculatedautomatically ca, " +
                                        "Expression expr " +
                                    "FROM indicator " +
                                    "WHERE dateDeleted IS NULL AND " +
                                        filter.indicatorFilter("indicator") +
                                   " ) " +
                                "UNION ALL " +
                                "(SELECT " +
                                        "A.ActivityId, " +
                                        "G.attributeGroupId as Id, " +
                                        "NULL as Category, " +
                                        "Name, " +
                                        "NULL as Description, " +
                                        "Mandatory, " +
                                        "'ENUM' as Type, " +
                                        "multipleAllowed, " +
                                        "NULL as Units, " +
                                        "SortOrder, " +
                                        "NULL code, " +
                                        "NULL ca, " +
                                        "NULL expr " +
                                    "FROM attributegroup G " +
                                    "INNER JOIN attributegroupinactivity A on G.attributeGroupId = A.attributeGroupId " +
                                    "WHERE dateDeleted is null AND " +
                                        filter.attributeGroupFilter("A") +
                                    ") " +
                                "ORDER BY SortOrder";

        Map<Integer, List<FormElement>> activityMap = Maps.newHashMap();
        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(indicatorQuery)) {
                while(rs.next()) {

                    int activityId = rs.getInt("ActivityId");
                    List<FormElement> list = activityMap.get(activityId);
                    if(list == null) {
                        activityMap.put(activityId, list = Lists.newArrayList());
                    }

                    String category = rs.getString("Category");
                    if(Strings.isNullOrEmpty(category)) {
                        list.add(createField(rs, attributes));

                    } else {
                        FormSection categorySection = findCategorySection(list, category);
                        if(categorySection == null) {
                            categorySection = new FormSection(activityFormSection(activityId, category));
                            categorySection.setLabel(category);
                            list.add(categorySection);
                        }
                        categorySection.addElement(createField(rs, attributes));
                    }
                }
            }
        }
        return activityMap;
    }


    private Map<Integer, List<EnumValue>> queryAttributes(Connection connection) throws SQLException {

        String sql = "SELECT * " +
                     "FROM attribute A " +
                     "WHERE A.dateDeleted is null AND " +
                           context.filter().attributeFilter("A")  +
                     " ORDER BY sortOrder";

        Map<Integer, List<EnumValue>> groupMap = Maps.newHashMap();


        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {
                while(rs.next()) {
                    int attributeGroupId = rs.getInt("AttributeGroupId");

                    List<EnumValue> values = groupMap.get(attributeGroupId);
                    if(values == null) {
                        groupMap.put(attributeGroupId, values = Lists.newArrayList());
                    }

                    int attributeId = rs.getInt("attributeId");
                    String attributeName = rs.getString("name");

                    values.add(new EnumValue(context.resourceId(ATTRIBUTE_DOMAIN, attributeId), attributeName));
                }
            }
        }
        return groupMap;
    }


    private FormField createField(ResultSet rs, Map<Integer, List<EnumValue>> attributes) throws SQLException {

        ResourceId fieldId;
        if(rs.getString("Type").equals("ENUM")) {
            fieldId = CuidAdapter.attributeGroupField(rs.getInt("id"));
        } else {
            fieldId = CuidAdapter.indicatorField(rs.getInt("id"));
        }

        FormField field = new FormField(fieldId)
                .setLabel(rs.getString("Name"))
                .setRequired(getMandatory(rs))
                .setDescription(rs.getString("Description"))
                .setCode(rs.getString("Code"));

        if(rs.getBoolean("ca") && rs.getString("expr") != null) {
            field.setType(new CalculatedFieldType(rs.getString("expr")));

        } else {
            switch (rs.getString("Type")) {
                default:
                case "QUANTITY":
                    field.setType(new QuantityType().setUnits(rs.getString("units")));
                    if(rs.getInt("ActivityId") == 6240) {
                        field.setDefaultValue(new Quantity(0, rs.getString("units")));
                    }
                    break;
                case "FREE_TEXT":
                    field.setType(TextType.INSTANCE);
                    break;
                case "NARRATIVE":
                    field.setType(TextType.INSTANCE);
                    break;
                case "ENUM":
                    field.setType(createEnumType(rs, attributes));
                    break;
            }
            if(rs.getInt("ActivityId") == 6240) {
                if(rs.getString("Name").equals("Year of Expediture")) {
                    field.setType(YearType.INSTANCE);
                }
            }
        }


        return field;
    }

    private boolean getMandatory(ResultSet rs) throws SQLException {
        try {
            return rs.getBoolean("Mandatory");
        } catch(Exception e) {
            LOGGER.log(Level.SEVERE, "Exception while accessing mandatory flag (value = [" +
                toDebugString(rs) + "]");
            throw new RuntimeException(e);
        }
    }

    private Object toDebugString(ResultSet rs) throws SQLException {
        try {
            Object object = rs.getObject("Mandatory");
            if(object == null) {
                return "null";
            } else {
                return object.toString() + ", class = " + object.getClass().getName();
            }
        } catch(Exception e) {
            return "Exception: " + e.getMessage();
        }
    }

    private EnumType createEnumType(ResultSet rs, Map<Integer, List<EnumValue>> attributes) throws SQLException {

        Cardinality cardinality;
        if(rs.getBoolean("multipleAllowed")) {
            cardinality = Cardinality.MULTIPLE;
        } else {
            cardinality = Cardinality.SINGLE;
        }

        List<EnumValue> enumValues = attributes.get(rs.getInt("id"));
        if(enumValues == null) {
            enumValues = Lists.newArrayList();
        }
        return new EnumType(cardinality, enumValues);
    }

    private FormSection findCategorySection(List<FormElement> section, String category) {
        for(FormElement element : section) {
            if(element instanceof FormSection && element.getLabel().equals(category)) {
                return (FormSection) element;
            }
        }
        return null;
    }

    private void writeSiteForm(ResourceId ownerId,
                               ResultSet rs,
                               List<FormElement> fields,
                               Set<Integer> databasesWithProjects,
                               ResourceWriter writer) throws Exception {

        int reportingFrequency = rs.getInt("ReportingFrequency");

        int activityId = rs.getInt("activityId");
        int databaseId = rs.getInt("databaseId");
        ResourceId classId = context.resourceId(ACTIVITY_DOMAIN, activityId);

        FormClass siteForm = new FormClass(classId);
        siteForm.setOwnerId(ownerId);
        siteForm.setLabel(rs.getString("name"));
        siteForm.setParentId(ownerId);

        FormField partnerField = new FormField(field(classId, PARTNER_FIELD))
                .setLabel("Partner")
                .setType(ReferenceType.single(context.resourceId(PARTNER_FORM_CLASS_DOMAIN, databaseId)))
                .setRequired(true);
        siteForm.addElement(partnerField);

        if(databasesWithProjects.contains(databaseId)) {
            FormField projectField = new FormField(field(classId, PROJECT_FIELD))
                .setLabel("Project")
                .setType(ReferenceType.single(context.resourceId(PROJECT_DOMAIN, databaseId)));
            siteForm.addElement(projectField);
        }

        if(reportingFrequency == ONCE) {

            siteForm.addElement(
                new FormField(field(classId, START_DATE_FIELD))
                    .setLabel("Date")
                    .setType(LocalDateIntervalType.INSTANCE)
                    .setRequired(true));
        }

        FormField locationField = new FormField(field(classId, LOCATION_FIELD))
                .setLabel(rs.getString("locationTypeName"))
                .setType(ReferenceType.single(locationRange(rs)))
                .setRequired(true);
        siteForm.addElement(locationField);

        if(fields != null) {
            if(reportingFrequency == ONCE) {
                siteForm.getElements().addAll(fields);
            } else {
                // in AI2.0, attributes (=enum types) were associated
                // with sites, and indicators with reporting periods
                for(FormElement field : fields) {
                    if(isAttribute(field)) {
                        siteForm.addElement(field);
                    }
                }
            }
        }

        FormField commentsField = new FormField(field(classId, COMMENT_FIELD));
        commentsField.setType(NarrativeType.INSTANCE);
        commentsField.setLabel("Comments");
        siteForm.addElement(commentsField);

//        if(reportingFrequency == MONTHLY) {
//            siteForm.addElement(
//                new FormField(field(classId, REPORTING_PERIODS_FIELD))
//                    .setLabel("Monthly reports")
//                    .setType(ReferenceType.multiple(context.resourceId(MONTHLY_REPORT_CLASS_DOMAIN, activityId))));
//        }

        writer.writeResource(siteForm.asResource(), null, null);
    }


    private void writePeriodForm(ResourceId ownerId,
                                 ResultSet rs,
                                 List<FormElement> fields,
                                 ResourceWriter writer) throws Exception {


        int activityId = rs.getInt("activityId");
        ResourceId parentClassId = context.resourceId(ACTIVITY_DOMAIN, activityId);
        ResourceId classId = context.resourceId(MONTHLY_REPORT_CLASS_DOMAIN, activityId);

        FormClass periodForm = new FormClass(classId);
        periodForm.setOwnerId(parentClassId);
        periodForm.setLabel(rs.getString("name") + " - Monthly Reports");
        periodForm.setParentId(ownerId);


        periodForm.addElement(new FormField(field(classId, SITE_FIELD))
            .setLabel("Site")
            .setType(ReferenceType.single(parentClassId))
            .setRequired(true)
            .setPrimaryKey(true));

        periodForm.addElement(new FormField(field(classId, DATE_FIELD))
            .setLabel("Month")
            .setType(MonthType.INSTANCE)
            .setRequired(true)
            .setPrimaryKey(true));


        // temporary hack for LCCA anal
        periodForm.addElement(new FormField(field(classId, YEAR_FIELD))
            .setLabel("Year")
            .setType(TextType.INSTANCE)
            .setRequired(true)
            .setPrimaryKey(true));

        if(fields != null) {
            for (FormElement element : fields) {
                if (!isAttribute(element)) {
                    periodForm.addElement(element);
                }
            }
        }

        writer.writeResource(periodForm.asResource(), null, null);
    }

    private boolean isAttribute(FormElement field) {
        return field instanceof FormField && ((FormField) field).getType() instanceof EnumType;
    }

    private ResourceId locationRange(ResultSet rs) throws SQLException {

        int boundAdminLevelId = rs.getInt("BoundAdminLevelId");
        if(rs.wasNull()) {
            int locationTypeId = rs.getInt("LocationTypeId");
            return context.resourceId(LOCATION_TYPE_DOMAIN, locationTypeId);
        } else {
            return context.resourceId(ADMIN_LEVEL_DOMAIN, boundAdminLevelId);
        }
    }

}
