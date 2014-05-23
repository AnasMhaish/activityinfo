package org.activityinfo.ui.client.importer;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;


@SuppressWarnings({"NonJREEmulationClassesInClientCode", "AppEngineForbiddenCode"})
public class ExtractDbUnit {

    /**
     * Utility to create a dbunit xml file from a local mysql database
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Connection jdbcConnection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/activityinfo?zeroDateTimeBehavior=convertToNull", "root", "root");
        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

        // partial database export
        QueryDataSet partialDataSet = new QueryDataSet(connection);
//
//        partialDataSet.addTable("userlogin", "select * from userlogin where userid in " +
//                "(select owneruserid from userdatabase where databaseid=1100)");

        partialDataSet.addTable("country", "select * from country where countryid=360");
//        partialDataSet.addTable("adminlevel", "select * from adminlevel where countryid=360");
//        partialDataSet.addTable("adminentity", "select AdminEntityId, AdminEntityParentId, AdminLevelId, Name" +
//                " from adminentity where adminlevelid in " +
//                "(select adminlevelid from adminlevel where CountryId=360)");
//        partialDataSet.addTable("locationtype", "select * from locationtype" +
//                " where countryid = 360 and BoundAdminLevelId is null");


        partialDataSet.addTable("userdatabase", "select * from userdatabase where databaseid in (4)");
        partialDataSet.addTable("activity", "select * from activity where activityid in (33)");
        partialDataSet.addTable("indicator", "select * from indicator where activityid in (33) ");
        partialDataSet.addTable("attributegroupinactivity", "select * from attributegroupinactivity where activityid in (33)");
        partialDataSet.addTable("attributegroup", "select * from attributegroup where attributeGroupid in " +
                                                  " (select AttributeGroupId from attributegroupinactivity where activityid in (33))");
        partialDataSet.addTable("attribute", "select * from attribute where attributeGroupid in " +
                                                  " (select AttributeGroupId from attributegroupinactivity where activityid in (33))");
        partialDataSet.addTable("site", "select * from site where siteid in (3882, 1297074580)");
        partialDataSet.addTable("locationtype", "select * from locationtype where locationtypeid in (1)");
        //partialDataSet.addTable("location", "select * from location where locationid in ()");
        partialDataSet.addTable("partner", "select * from partner where partnerid in " +
                                           "(select partnerid from site where siteid in (3882, 1297074580))");
        partialDataSet.addTable("partnerindatabase", "select * from partnerindatabase where databaseid in (4) and " +
                                         "partnerid in (select partnerid from site where siteid in (3882, 1297074580))");

        partialDataSet.addTable("sitehistory", "select * from sitehistory where siteid in (3882, 1297074580)");
        partialDataSet.addTable("userlogin", "select * from userlogin where userid in " +
                                             "(select userid from sitehistory where siteid in (3882, 1297074580)) or " +
                                             "userid in (select owneruserid from userdatabase where databaseid in (4))");


        FlatXmlDataSet.write(partialDataSet, new FileOutputStream("src/test/resources/dbunit/site-history.db.xml"));

    }
}
