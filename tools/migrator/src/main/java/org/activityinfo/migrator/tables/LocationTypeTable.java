package org.activityinfo.migrator.tables;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.activityinfo.migrator.ResourceMigrator;
import org.activityinfo.migrator.ResourceWriter;
import org.activityinfo.migrator.filter.MigrationContext;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.system.ApplicationProperties;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.geo.GeoPointType;
import org.activityinfo.model.type.primitive.TextType;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.activityinfo.model.legacy.CuidAdapter.*;

public class LocationTypeTable extends ResourceMigrator {

    private MigrationContext context;

    public LocationTypeTable(MigrationContext context) {
        this.context = context;
    }

    @Override
    public void getResources(Connection connection, ResourceWriter writer) throws Exception {

        Multimap<Integer, ResourceId> adminLevelsByCountry = queryLevels(connection);

        // only select "real" location types - we will discard dummy location types
        // as part of the migration
        String sql = "SELECT * FROM locationtype LT WHERE boundadminlevelid IS NULL AND " +
            context.filter().locationTypeFilter("LT");

        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {
                while(rs.next()) {

                    ResourceId id = context.resourceId(LOCATION_TYPE_DOMAIN, rs.getInt("locationTypeId"));
                    int countryId = rs.getInt("countryId");
                    Preconditions.checkState(!rs.wasNull());

                    FormClass formClass = new FormClass(id)
                            .setOwnerId(owner(rs))
                            .setLabel(rs.getString("name"));

                    formClass.addField(field(id, NAME_FIELD))
                            .setLabel("Name")
                            .setRequired(true)
                            .setSuperProperty(ApplicationProperties.LABEL_PROPERTY)
                            .setType(TextType.INSTANCE);

                    formClass.addField(field(id, AXE_FIELD))
                            .setLabel("Alternate Name")
                            .setRequired(false)
                            .setType(TextType.INSTANCE);

                    if(adminLevelsByCountry.containsKey(countryId)) {
                        formClass.addField(field(id, ADMIN_FIELD))
                                .setLabel("Administrative Unit")
                                .setRequired(false)
                                .setType(ReferenceType.multiple(adminLevelsByCountry.get(countryId)));
                    }

                    formClass.addField(field(id, GEOMETRY_FIELD))
                            .setLabel("Geographic Position")
                            .setRequired(false)
                            .setType(GeoPointType.INSTANCE);

                    writer.writeResource(0, formClass.asResource(), null, null);
                }
            }
        }
    }

    private ResourceId owner(ResultSet rs) throws SQLException {
        int databaseId = rs.getInt("databaseid");
        if(rs.wasNull()) {
            return context.resourceId(COUNTRY_DOMAIN, rs.getInt("CountryId"));
        } else {
            return context.resourceId(DATABASE_DOMAIN, databaseId);
        }
    }

    private Multimap<Integer, ResourceId> queryLevels(Connection connection) throws SQLException {

        Multimap<Integer, ResourceId> map = HashMultimap.create();

        String sql = "SELECT * FROM adminlevel";

        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {
                while(rs.next()) {
                    int countryId = rs.getInt("CountryId");
                    ResourceId levelId = context.resourceId(ADMIN_LEVEL_DOMAIN, rs.getInt("AdminLevelId"));

                    map.put(countryId, levelId);
                }
            }
        }

        return map;
    }

}
