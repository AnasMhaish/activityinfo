package org.activityinfo.migrator.tables;

import org.activityinfo.migrator.ResourceMigrator;
import org.activityinfo.migrator.ResourceWriter;
import org.activityinfo.migrator.filter.MigrationContext;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.auth.UserPermission;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.ResourceId;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.activityinfo.model.legacy.CuidAdapter.*;

public class UserPermissionTable extends ResourceMigrator {

    private MigrationContext context;

    public UserPermissionTable(MigrationContext context) {
        this.context = context;
    }

    @Override
    public void getResources(Connection connection, ResourceWriter writer) throws Exception {

        String sql = "SELECT P.*, PID.DatabaseId " +
                     "FROM userpermission P " +
                     "INNER JOIN userdatabase DB ON (P.DatabaseId=DB.DatabaseId)";

        try(Statement statement = connection.createStatement()) {
            try(ResultSet rs = statement.executeQuery(sql)) {

                while(rs.next()) {
                    ResourceId id = context.getIdStrategy()
                        .partnerInstanceId(rs.getInt("DatabaseId"), rs.getInt("PartnerId"));
                    ResourceId classId = context.resourceId(DATABASE_DOMAIN, rs.getInt("DatabaseId"));

                    FormInstance instance = new FormInstance(id, classId);
                    instance.set(field(classId, NAME_FIELD), rs.getString("name"));
                    instance.set(field(classId, FULL_NAME_FIELD), rs.getString("fullName"));

                    writer.writeResource(0, instance.asResource(), null, null);

                    AuthenticatedUser user = new AuthenticatedUser(rs.getInt("userId"));
                    UserPermission up = new UserPermission(classId, user.getUserResourceId());
                    up.setView(rs.getBoolean("view"));
                    // TODO
                }
            }
        }
    }
}
