package org.activityinfo.ui.client.importer;


import com.bedatadriven.rebar.time.calendar.LocalDate;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.activityinfo.core.server.type.converter.JvmConverterFactory;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.core.shared.form.tree.FormTree;
import org.activityinfo.core.shared.form.tree.FormTreePrettyPrinter;
import org.activityinfo.core.shared.importing.match.ColumnMappingGuesser;
import org.activityinfo.core.shared.importing.model.ImportModel;
import org.activityinfo.core.shared.importing.model.MapExistingAction;
import org.activityinfo.core.shared.importing.source.SourceColumn;
import org.activityinfo.core.shared.importing.strategy.FieldImportStrategies;
import org.activityinfo.core.shared.importing.validation.ValidatedRowTable;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.legacy.shared.command.DimensionType;
import org.activityinfo.legacy.shared.command.Filter;
import org.activityinfo.legacy.shared.command.GetSites;
import org.activityinfo.legacy.shared.command.result.SiteResult;
import org.activityinfo.legacy.shared.model.SiteDTO;
import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.ui.client.component.importDialog.Importer;
import org.activityinfo.ui.client.component.importDialog.data.PastedTable;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.google.common.io.Resources.getResource;
import static org.activityinfo.core.client.PromiseMatchers.assertResolves;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

//@SuppressWarnings("GwtClientClassFromNonInheritedModule")
@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/brac-import.db.xml")
public class ImportSimpleTest extends AbstractImporterTest {

    private static final Cuid HOUSEHOLD_SURVEY_FORM_CLASS = CuidAdapter.activityFormClass(1);

    private static final Cuid TRAINING_PROGRAM_CLASS = CuidAdapter.activityFormClass(2);


    private static final Cuid BRAC_PARTNER_CUID = CuidAdapter.partnerInstanceId(1);

    public static final int MODHUPUR = 24;

    @Test
    public void test() throws IOException {

        FormTree formTree = assertResolves(formTreeBuilder.apply(HOUSEHOLD_SURVEY_FORM_CLASS));
        FormTreePrettyPrinter.print(formTree);


        importModel = new ImportModel(formTree);


        // Step 1: User pastes in data to import
        PastedTable source = new PastedTable(
                Resources.toString(getResource("org/activityinfo/core/shared/importing/qis.csv"), Charsets.UTF_8));
        source.parseAllRows();

        assertThat(source.getRows().size(), equalTo(63));

        importModel.setSource(source);
        importer = new Importer(resourceLocator, formTree, FieldImportStrategies.get(JvmConverterFactory.get()));

        dumpList("COLUMNS", source.getColumns());

        // Step 2: User maps imported columns to FormFields
        dumpList("FIELDS", importer.getImportTargets());
        importModel.setColumnAction(columnIndex("MEMBER_NO_ADULT_FEMALE"), target("NumAdultMale"));
        importModel.setColumnAction(columnIndex("MEMBER_NO_ADULT_FEMALE"), target("NumAdultFemale"));
        importModel.setColumnAction(columnIndex("_CREATION_DATE"), target("Start Date"));
        importModel.setColumnAction(columnIndex("_SUBMISSION_DATE"), target("End Date"));
        importModel.setColumnAction(columnIndex("district"), target("District Name"));
        importModel.setColumnAction(columnIndex("upazila"), target("Upzilla Name"));
        importModel.setColumnAction(columnIndex("Partner"), target("Partner Name"));


        // Step 3: Validate for user
        ValidatedRowTable validatedResult = assertResolves(importer.validateRows(importModel));
        showValidationGrid(validatedResult);

        assertResolves(importer.persist(importModel));

        // AND... verify
        Filter filter = new Filter();
        filter.addRestriction(DimensionType.AdminLevel, MODHUPUR);

        SiteResult sites = execute(new GetSites(filter));
        assertThat(sites.getTotalLength(), equalTo(1));

        SiteDTO site = sites.getData().get(0);
        assertThat(site.getDate1(), equalTo(new LocalDate(2012,12,19)));
        assertThat(site.getDate2(), equalTo(new LocalDate(2012,12,19)));
    }

    @Test
    public void testExceptionHandling() throws IOException {


        FormTree formTree = assertResolves(formTreeBuilder.apply(HOUSEHOLD_SURVEY_FORM_CLASS));
        importModel = new ImportModel(formTree);

        // Step 1: User pastes in data to import
        PastedTable source = new PastedTable(
                Resources.toString(getResource("org/activityinfo/core/shared/importing/qis.csv"), Charsets.UTF_8));
        source.parseAllRows();

        importModel.setSource(source);
        importer = new Importer(resourceLocator, formTree, FieldImportStrategies.get(JvmConverterFactory.get()));
        importModel.setColumnAction(columnIndex("MEMBER_NO_ADULT_FEMALE"), target("NumAdultMale"));
        importModel.setColumnAction(columnIndex("MEMBER_NO_ADULT_FEMALE"), target("NumAdultFemale"));
        importModel.setColumnAction(columnIndex("_CREATION_DATE"), target("Start Date"));
        importModel.setColumnAction(columnIndex("_SUBMISSION_DATE"), target("End Date"));
        importModel.setColumnAction(columnIndex("district"), target("District Name"));
        importModel.setColumnAction(columnIndex("upazila"), target("Upzilla Name"));
       // importModel.setColumnAction(columnIndex("Partner"), target("Partner Name"));

        Promise<Void> result = importer.persist(importModel);
        assertThat(result.getState(), equalTo(Promise.State.REJECTED));
    }

    @Test
    public void columnMappingGuesser() throws IOException {
        FormTree formTree = assertResolves(formTreeBuilder.apply(HOUSEHOLD_SURVEY_FORM_CLASS));
        FormTreePrettyPrinter.print(formTree);

        importModel = new ImportModel(formTree);

        // Step 1: User pastes in data to import
        PastedTable source = new PastedTable(
                Resources.toString(getResource("org/activityinfo/core/shared/importing/qis.csv"), Charsets.UTF_8));

        importModel.setSource(source);
        importer = new Importer(resourceLocator, formTree, FieldImportStrategies.get(JvmConverterFactory.get()));

        dumpList("COLUMNS", source.getColumns());
        dumpList("FIELDS", importer.getImportTargets());

        // Step 2: Guesser guess mapping
        final ColumnMappingGuesser guesser = new ColumnMappingGuesser(importModel, importer.getImportTargets());
        guesser.guess();

        assertMapping("Partner", "Partner Name");
        assertMapping("district", "District Name");
        //assertMapping("upazila", "Upzilla Name");
    }

    private void assertMapping(String sourceColumnLabel, String targetColumnLabel) {
        final SourceColumn sourceColumn = importModel.getSourceColumn(columnIndex(sourceColumnLabel));
        assertNotNull(sourceColumn);

        final MapExistingAction columnAction = (MapExistingAction) importModel.getColumnAction(sourceColumn);
        assertTrue(columnAction.getTarget().getLabel().equals(targetColumnLabel));
    }

}
