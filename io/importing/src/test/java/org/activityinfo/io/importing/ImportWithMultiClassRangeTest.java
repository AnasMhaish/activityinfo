package org.activityinfo.io.importing;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import com.teklabs.gwt.i18n.server.LocaleProxy;
import org.activityinfo.client.AsyncFormTreeBuilder;
import org.activityinfo.client.ResourceLocator;
import org.activityinfo.io.importing.data.PastedTable;
import org.activityinfo.io.importing.model.ImportModel;
import org.activityinfo.io.importing.strategy.FieldImportStrategies;
import org.activityinfo.io.importing.validation.ValidatedRowTable;
import org.activityinfo.model.formTree.FieldPath;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.formTree.FormTreePrettyPrinter;
import org.activityinfo.model.hierarchy.Hierarchy;
import org.activityinfo.model.hierarchy.HierarchyPrettyPrinter;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.legacy.InstanceQuery;
import org.activityinfo.model.legacy.Projection;
import org.activityinfo.model.legacy.criteria.ClassCriteria;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.converter.JvmConverterFactory;
import org.activityinfo.store.test.TestResourceStore;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.google.common.io.Resources.getResource;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

public class ImportWithMultiClassRangeTest extends AbstractImporterTest {

    public static final ResourceId NFI_DISTRIBUTION_FORM_CLASS = CuidAdapter.activityFormClass(33);

    public static final ResourceId SCHOOL_FORM_CLASS = CuidAdapter.locationFormClass(2);

    public static final ResourceId ADMIN_FIELD = CuidAdapter.getAdminFieldId(SCHOOL_FORM_CLASS);


    // admin levels
    public static final int PROVINCE_LEVEL = 1;
    public static final int DISTRICT_LEVEL = 2;
    public static final int TERRITOIRE_LEVEL = 3;
    public static final int SECTEUR_LEVEL = 4;
    public static final int GROUPEMENT_LEVEL = 5;
    public static final int ZONE_DE_SANTE = 7;
    public static final int AIRE_DE_SANTE = 8;

    // indicators
    public static final int NUMBER_MENAGES = 118;

    // attributes
    public static final int ECHO = 400;
    public static final int DEPLACEMENT = 63;

    public static final ResourceId PROVINCE_KATANGA = CuidAdapter.entity(141804);
    public static final ResourceId DISTRICT_TANGANIKA = CuidAdapter.entity(141845);
    public static final ResourceId TERRITOIRE_KALEMIE = CuidAdapter.entity(141979);
    public static final ResourceId SECTEUR_TUMBWE = CuidAdapter.entity(142803);
    public static final ResourceId GROUPEMENT_LAMBO_KATENGA = CuidAdapter.entity(148235);
    public static final ResourceId ZONE_SANTE_NYEMBA = CuidAdapter.entity(212931);

    private List<Projection> instances;


    private ResourceLocator resourceLocator;
    private AsyncFormTreeBuilder formTreeBuilder;

    @Before
    public void setUp() throws IOException {
        resourceLocator = TestResourceStore
                .createLocator("nfi-import.json");
        formTreeBuilder = new AsyncFormTreeBuilder(resourceLocator);

        LocaleProxy.initialize();
    }


    @Test
    public void testSimple() throws IOException {

        //setUser(3);

        FormTree formTree = assertResolves(formTreeBuilder.apply(NFI_DISTRIBUTION_FORM_CLASS));
        FormTreePrettyPrinter.print(formTree);

        FormTree.Node field = formTree.getNodeByPath(new FieldPath(CuidAdapter.locationField(33)));
        Hierarchy hierarchy = new Hierarchy(field.getRangeFormClasses());
        HierarchyPrettyPrinter.prettyPrint(hierarchy);

        importModel = new ImportModel(formTree);
        importer = new Importer(resourceLocator, formTree, FieldImportStrategies.get(JvmConverterFactory.get()));

        // Step 1: User pastes in data to import
        PastedTable source = new PastedTable(
                Resources.toString(getResource("nfi.csv"), Charsets.UTF_8));
        source.parseAllRows();

        importModel.setSource(source);

        dumpList("COLUMNS", source.getColumns());
        importModel.setColumnAction(columnIndex("Date1"), target("Start Date"));
        importModel.setColumnAction(columnIndex("Date2"), target("End Date"));
        importModel.setColumnAction(columnIndex("Partner"), target("Partners Name"));
        importModel.setColumnAction(columnIndex("Localité"), target("Localité Name"));
        importModel.setColumnAction(columnIndex("Province"), target("Province Name"));
        importModel.setColumnAction(columnIndex("District"), target("District Name"));
        importModel.setColumnAction(columnIndex("Territoire"), target("Territoire Name"));
        importModel.setColumnAction(columnIndex("Secteur"), target("Secteur Name"));
        importModel.setColumnAction(columnIndex("Groupement"), target("Groupement Name"));
        importModel.setColumnAction(columnIndex("Zone de Santé"), target("Zone de Santé Name"));
        importModel.setColumnAction(columnIndex("Nombre de ménages ayant reçu une assistance en NFI"),
                target("Nombre de ménages ayant reçu une assistance en NFI"));

        ValidatedRowTable validatedResult = assertResolves(importer.validateRows(importModel));
        showValidationGrid(validatedResult);

        assertResolves(importer.persist(importModel));
//
//        GetSites query = new GetSites(Filter.filter().onActivity(33));
//        query.setSortInfo(new SortInfo("date2", Style.SortDir.DESC));
//
//        SiteResult result = execute(query);
////        assertThat(result.getTotalLength(), equalTo(651));
//        assertThat(result.getTotalLength(), equalTo(313));
//
//        SiteDTO lastSite = result.getData().get(0);
//        assertThat(lastSite.getDate2(), equalTo(new LocalDate(2013,4,30)));
//        assertThat(lastSite.getLocationName(), equalTo("Kilimani Camp"));
//        assertThat(lastSite.getAdminEntity(PROVINCE_LEVEL).getName(), equalTo("Nord Kivu"));
//        assertThat(lastSite.getAdminEntity(DISTRICT_LEVEL).getName(), equalTo("Nord Kivu"));
//        assertThat(lastSite.getAdminEntity(TERRITOIRE_LEVEL).getName(), equalTo("Masisi"));
//        assertThat(lastSite.getAdminEntity(SECTEUR_LEVEL).getName(), equalTo("Masisi"));
//
//        assertThat((Double) lastSite.getIndicatorValue(NUMBER_MENAGES), equalTo(348.0));
//        assertThat(lastSite.getAttributeValue(ECHO), equalTo(false));
    }

    @Ignore("Todo: fix reference to cuids")
    @Test
    public void testMulti() throws IOException {

        //setUser(3);

        FormTree formTree = assertResolves(formTreeBuilder.apply(SCHOOL_FORM_CLASS));
        FormTreePrettyPrinter.print(formTree);

        importModel = new ImportModel(formTree);
        importer = new Importer(resourceLocator, formTree, FieldImportStrategies.get(JvmConverterFactory.get()));


        // Step 1: User pastes in data to import
        PastedTable source = new PastedTable(
                Resources.toString(getResource("school-import.csv"), Charsets.UTF_8));
        importModel.setSource(source);

        dumpList("COLUMNS", source.getColumns());

        importModel.setColumnAction(columnIndex("School"), target("Name"));

        // Province is at the root of both hierarchies
        importModel.setColumnAction(columnIndex("Province"), target("Province Name"));

        // Admin hierarchy
        importModel.setColumnAction(columnIndex("District"), target("District Name"));
        importModel.setColumnAction(columnIndex("Territoire"), target("Territoire Name"));
        importModel.setColumnAction(columnIndex("Secteur"), target("Secteur Name"));
        importModel.setColumnAction(columnIndex("Groupement"), target("Groupement Name"));

        // health ministry hierarchy
        importModel.setColumnAction(columnIndex("Zone de Santé"), target("Zone de Santé Name"));

        ValidatedRowTable validatedResult = assertResolves(importer.validateRows(importModel));
        showValidationGrid(validatedResult);


        assertResolves(importer.persist(importModel));

        instances = assertResolves(resourceLocator.queryProjection(
                new InstanceQuery(Arrays.asList(new FieldPath(ADMIN_FIELD)), new ClassCriteria(SCHOOL_FORM_CLASS))))
                .getProjections();
        assertThat(instances.size(), equalTo(8)); // we have 8 rows in school-import.csv

        assertThat(adminFieldOfSchool("P"), contains(PROVINCE_KATANGA));
        assertThat(adminFieldOfSchool("D"), contains(DISTRICT_TANGANIKA));
        assertThat(adminFieldOfSchool("T"), contains(TERRITOIRE_KALEMIE));
        assertThat(adminFieldOfSchool("S"), contains(SECTEUR_TUMBWE));
        assertThat(adminFieldOfSchool("G"), contains(GROUPEMENT_LAMBO_KATENGA));
        assertThat(adminFieldOfSchool("GZ"), contains(GROUPEMENT_LAMBO_KATENGA, ZONE_SANTE_NYEMBA));
        assertThat(adminFieldOfSchool("TZ"), contains(TERRITOIRE_KALEMIE, ZONE_SANTE_NYEMBA));
    }

    private Set<ResourceId> adminFieldOfSchool(String name) {
        for(Projection instance : instances) {
            if(name.equals(instance.getStringValue(CuidAdapter.getNameFieldId(SCHOOL_FORM_CLASS)))) {
                Set<ResourceId> references = instance.getReferenceValue(ADMIN_FIELD);
                System.out.println(name +", references: " + references);
                return references;
            }
        }
        throw new AssertionError("No instance with name " + name);
    }

    public static Set<ResourceId> set(ResourceId... resourceIds) {
        return Sets.newHashSet(resourceIds);
    }


}
