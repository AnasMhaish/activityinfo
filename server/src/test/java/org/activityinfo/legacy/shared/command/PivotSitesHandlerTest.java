package org.activityinfo.legacy.shared.command;

/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.bedatadriven.rebar.time.calendar.LocalDate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.cube.*;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.core.shared.table.TableColumnData;
import org.activityinfo.core.shared.table.TableColumnDataBuilder;
import org.activityinfo.core.shared.table.TableModel;
import org.activityinfo.fixtures.InjectionSupport;
import org.activityinfo.fixtures.Modules;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.legacy.shared.adapter.ResourceLocatorAdaptor;
import org.activityinfo.legacy.shared.command.PivotSites.ValueType;
import org.activityinfo.legacy.shared.command.result.Bucket;
import org.activityinfo.legacy.shared.exception.CommandException;
import org.activityinfo.legacy.shared.reports.content.*;
import org.activityinfo.legacy.shared.reports.model.*;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.server.database.OnDataSet;
import org.activityinfo.server.database.TestDatabaseModule;
import org.activityinfo.server.report.util.DateUtilCalendarImpl;
import org.activityinfo.ui.client.component.table.FieldColumn;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.persistence.EntityManager;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Arrays.asList;
import static org.activityinfo.core.client.PromiseMatchers.assertResolves;
import static org.junit.Assert.assertEquals;

@RunWith(InjectionSupport.class)
@Modules({TestDatabaseModule.class})
@OnDataSet("/dbunit/sites-simple1.db.xml")
public class PivotSitesHandlerTest extends CommandTestCase2 {

    private Set<Dimension> dimensions;
    private Dimension indicatorDim;
    private Filter filter;
    private AdminDimension provinceDim;
    private AdminDimension territoireDim;
    private List<Bucket> buckets;
    private final Dimension projectDim = new Dimension(DimensionType.Project);
    private Dimension partnerDim;
    private ValueType valueType = ValueType.INDICATOR;
    private boolean pointsRequested;

    private static final int OWNER_USER_ID = 1;
    private static final int NB_BENEFICIARIES_ID = 1;

    @Inject
    private EntityManager em;

    @BeforeClass
    public static void setup() {
        Logger.getLogger("org.activityinfo").setLevel(Level.ALL);
        Logger.getLogger("com.bedatadriven.rebar").setLevel(Level.ALL);
    }

    @Before
    public void setUp() throws Exception {
        dimensions = new HashSet<Dimension>();
        filter = new Filter();

        provinceDim = new AdminDimension(OWNER_USER_ID);
        territoireDim = new AdminDimension(2);
    }

    @Test
    public void testNoIndicator() {
        withIndicatorAsDimension();
        filteringOnDatabases(1,2);

        execute();

        assertThat().forIndicator(1).thereIsOneBucketWithValue(15100);
    }

    @Test
    public void testBasic() {
        withIndicatorAsDimension();
        filter.addRestriction(DimensionType.Indicator, 1);

        execute();

        assertThat().forIndicator(1).thereIsOneBucketWithValue(15100);
    }

    @Test
    public void testTotalSiteCount() {
        forTotalSiteCounts();
        filteringOnDatabases(1,2,3,4,5);

        execute();

        assertThat().thereIsOneBucketWithValue(8);
    }


    @Test
    public void testYears() {
        forTotalSiteCounts();
        filteringOnDatabases(1, 2);
        dimensions.add(new DateDimension(DateUnit.YEAR));

        execute();

        assertThat().forYear(2008).thereIsOneBucketWithValue(1);
        assertThat().forYear(2009).thereIsOneBucketWithValue(4);
    }

    @Test
    public void pivotOnSites() {
        filter.addRestriction(DimensionType.Indicator, 1);
        dimensions.add(new Dimension(DimensionType.Site));

        execute();

        assertThat().thereAre(3).buckets();

        assertThat().forSite(1).thereIsOneBucketWithValue(1500);
        assertThat().forSite(2).thereIsOneBucketWithValue(3600);
        assertThat().forSite(3).thereIsOneBucketWithValue(10000);
    }

    @Test
    public void pivotByDate() {
        filter.addRestriction(DimensionType.Indicator, 5);
        dimensions.add(new DateDimension(DateUnit.DAY));

        execute();
    }

    @Test
    public void testSiteCountOnQuarters() {
        forTotalSiteCounts();
        filteringOnDatabases(1,2);
        dimensions.add(new DateDimension(DateUnit.QUARTER));

        execute();

        assertThat().forQuarter(2008, 4).thereIsOneBucketWithValue(1);
        assertThat().forQuarter(2009, 1).thereIsOneBucketWithValue(4);
    }

    @Test
    public void testMonths() {
        forTotalSiteCounts();
        filteringOnDatabases(1, 2);
        dimensions.add(new DateDimension(DateUnit.MONTH));
        filter.setDateRange(new DateUtilCalendarImpl().yearRange(2009));

        execute();

        assertThat().thereAre(3).buckets();
    }

    @Test
    public void testIndicatorFilter() {
        withIndicatorAsDimension();
        filter.addRestriction(DimensionType.Database, 1);
        filter.addRestriction(DimensionType.Activity, 1);
        filter.addRestriction(DimensionType.Indicator, 1);
        filter.addRestriction(DimensionType.Partner, 2);

        execute();

        assertThat().thereIsOneBucketWithValue(10000);
    }

    @Test
    public void testAdminFilter() {
        withIndicatorAsDimension();
        filter.addRestriction(DimensionType.AdminLevel, 11);
        filter.addRestriction(DimensionType.Indicator, 1);

        execute();

        assertThat().thereIsOneBucketWithValue(3600);
    }

    @Test
    public void testPartnerPivot() {

        withIndicatorAsDimension();
        withPartnerAsDimension();
        filter.addRestriction(DimensionType.Indicator, 1);

        execute();

        assertThat().thereAre(2).buckets();
        assertThat().forPartner(1).thereIsOneBucketWithValue(5100)
                .andItsPartnerLabelIs("NRC");
        assertThat().forPartner(2).thereIsOneBucketWithValue(10000)
                .andItsPartnerLabelIs("Solidarites");


        PivotSites query = composeQuery();

        ResourceLocator resourceLocator = new ResourceLocatorAdaptor(getDispatcher());

        TableModel tableModel = new TableModel();
        tableModel.setFormClassId(CuidAdapter.activityFormClass(1));
        FieldPath path1 = new FieldPath(CuidAdapter.indicatorField(1));
        FieldPath path2 = new FieldPath(CuidAdapter.partnerField(1),
                CuidAdapter.field(CuidAdapter.partnerFormClass(1), CuidAdapter.NAME_FIELD));

        FieldColumn beneficiaries = new FieldColumn(path1, "Beneficiaries");
        FieldColumn partner = new FieldColumn(path2, "Partner");

        tableModel.setColumns(Arrays.asList(beneficiaries, partner));

        CubeModel cubeModel = new CubeModel(tableModel);
        cubeModel.getDimensions().add(new DimensionModel(partner.getId()));
        cubeModel.setMeasure(new MeasureModel(AggregationType.SUM, beneficiaries.getId()));

        TableColumnData tableData = assertResolves(new TableColumnDataBuilder(resourceLocator).build(tableModel));

        List<Bucket> buckets = buildCube(cubeModel, tableData);

        printBuckets(buckets);



//
//        int formClassId = 1;
//        int indicatorId = 1;
//        double[] indicatorColumn = buildIndicatorColumn(formClassId, indicatorId);
//
//        System.out.println(Arrays.toString(indicatorColumn));


    }

    private List<Bucket> buildCube(CubeModel cubeModel, TableColumnData tableData) {

        Map<BucketKey, Aggregator> aggregators = Maps.newHashMap();

        Object[] dimensions = new Object[cubeModel.getDimensions().size()];

        int numRows = tableData.getColumnIdToViewMap().values().iterator().next().numRows();

        for(int i=0;i!=numRows;++i) {

            for(int j=0;j!=cubeModel.getDimensions().size();++j) {
                DimensionModel dim = cubeModel.getDimensions().get(j);
                dimensions[j] = tableData.getColumnIdToViewMap().get(dim.getColumnId()).get(i);
            }
            BucketKey key = new BucketKey(dimensions);
            Aggregator aggregator = aggregators.get(key);
            if(aggregator == null) {
                aggregator = createAggregator(cubeModel);
                aggregators.put(key, aggregator);
            }

            aggregator.value(tableData.getColumnIdToViewMap().get(cubeModel.getMeasure().getColumnId())
                                      .getDouble(i));
        }

        List<Bucket> buckets = Lists.newArrayList();
        for(BucketKey key : aggregators.keySet()) {
            Bucket bucket = new Bucket(aggregators.get(key).compute());
            for(int j=0;j!=cubeModel.getDimensions().size();++j) {
                DimensionModel dim = cubeModel.getDimensions().get(j);
                bucket.setCategory(
                        toLegacyDimension(cubeModel, dim),
                        toLegacyDimensionCategory(cubeModel, dim, key.getCategory(j)));
            }
            buckets.add(bucket);
        }

        return buckets;
    }

    private DimensionCategory toLegacyDimensionCategory(CubeModel cubeModel, DimensionModel dim, Object category) {
        return new SimpleCategory("" + category);

    }

    private Dimension toLegacyDimension(CubeModel cubeModel, DimensionModel dim) {
        return new Dimension(DimensionType.Partner);
    }

    private Aggregator createAggregator(CubeModel cubeModel) {

        if(cubeModel.getMeasure().getAggregationType() == AggregationType.SUM) {
            return new SumAggregator();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private double[] buildIndicatorColumn(int formClassId, int indicatorId) {
        List resultList = em.createNativeQuery("select fieldValues.value from site s left join " +
                                               "(select p.siteid, iv.value from reportingperiod p " +
                                               "left join indicatorvalue iv on (p.reportingPeriodId = iv" +
                                               ".ReportingPeriodId) " +
                                               "where iv.indicatorId = " + indicatorId +
                                               ") fieldValues on (s.siteid=fieldValues.siteid) " +
                                               "where s.activityId=" + formClassId + " order by s.siteid")
                            .getResultList();

        double[] values = new double[resultList.size()];
        int row = 0;
        for(Object result : resultList) {
            Number value = (Number) result;
            if(value == null) {
                values[row++] = Double.NaN;
            } else {
                values[row++] = value.doubleValue();
            }
        }
        return values;
    }

    @Test
    @OnDataSet("/dbunit/sites-simple-target.db.xml")
    public void testTargetPivot() {

        withIndicatorAsDimension();
        dimensions.add(new DateDimension(DateUnit.YEAR));
        dimensions.add(new Dimension(DimensionType.Target));
        filter.addRestriction(DimensionType.Indicator, 1);
        filter.setDateRange(new DateRange(new LocalDate(2008, 1, 1),
                new LocalDate(2008, 12, 31)));
        execute();

        assertThat().thereAre(2).buckets();
    }

    @Test
    public void testFractions() {
        filter.addRestriction(DimensionType.Indicator, 5);

        execute();

        assertThat().thereIsOneBucketWithValue(0.26666666);
    }

    @Test
    public void testAttributePivot() {
        withIndicatorAsDimension();
        withAttributeGroupDim(1);

        filter.addRestriction(DimensionType.Indicator, 1);

        execute();

        assertThat().thereAre(3).buckets();

        assertThat().forAttributeGroupLabeled(1, "Deplacement")
                .thereIsOneBucketWithValue(3600);

        assertThat().forAttributeGroupLabeled(1, "Catastrophe Naturelle")
                .thereIsOneBucketWithValue(10000);
    }

    @Test
    public void testAdminPivot() {
        withIndicatorAsDimension();
        withAdminDimension(provinceDim);
        withAdminDimension(territoireDim);
        filter.addRestriction(DimensionType.Indicator, 1);

        execute();

        assertThat().thereAre(3).buckets();
        assertThat().forProvince(2).thereAre(2).buckets();
        assertThat().forProvince(2).forTerritoire(11)
                .thereIsOneBucketWithValue(3600).with(provinceDim)
                .label("Sud Kivu").with(territoireDim).label("Walungu");
        assertThat().forProvince(4).thereIsOneBucketWithValue(10000);
    }

    @Test
    public void testSiteCount() {

        withIndicatorAsDimension();
        filter.addRestriction(DimensionType.Indicator, 103);

        execute();

        int expectedCount = 1;
        assertBucketCount(expectedCount);
        assertEquals(3, (int) buckets.get(0).doubleValue());

    }

    @Test
    public void projects() {

        withIndicatorAsDimension();
        withProjectAsDimension();
        filter.addRestriction(DimensionType.Database, 1);
        filter.addRestriction(DimensionType.Indicator, 1);

        execute();

        assertBucketCount(2);
        assertThat().forProject(1).thereIsOneBucketWithValue(5100);
        assertThat().forProject(2).thereIsOneBucketWithValue(10000);

    }

    @Test
    public void projectFilters() {

        withIndicatorAsDimension();
        withProjectAsDimension();

        filter.addRestriction(DimensionType.Database, 1);
        filter.addRestriction(DimensionType.Project, 1);
        filter
                .addRestriction(DimensionType.Indicator, asList(1, 2, 103));

        execute();

        assertBucketCount(3);

        assertThat().forIndicator(1).thereIsOneBucketWithValue(5100);
        assertThat().forIndicator(2).thereIsOneBucketWithValue(1700);
        assertThat().forIndicator(103).thereIsOneBucketWithValue(2);
    }

    private void assertBucketCount(int expectedCount) {
        assertEquals(expectedCount, buckets.size());
    }

    @Test
    public void siteCountWithIndicatorFilter() {

        // PivotSites [dimensions=[Partner], filter=Indicator={ 275 274 278 277
        // 276 129 4736 119 118 125 124 123 122 121 },
        // valueType=TOTAL_SITES]
        withPartnerAsDimension();
        forTotalSiteCounts();
        filter.addRestriction(DimensionType.Indicator, Lists.newArrayList(1, 2, 3));

        execute();
    }

    @Test
    public void targetFilter() {
        // Pivoting: PivotSites [dimensions=[Date, Partner, Date, Target,
        // Activity, Indicator],
        // filter=AdminLevel={ 141801 }, Partner={ 130 },
        // Indicator={ 747 746 745 744 749 748 739 738 743 740 119 118 3661 125
        // 124 123 122 121 }, valueType=INDICATOR]

        withPartnerAsDimension();
        dimensions.add(new DateDimension(DateUnit.YEAR));
        dimensions.add(new Dimension(DimensionType.Target));
        dimensions.add(new Dimension(DimensionType.Activity));
        dimensions.add(new Dimension(DimensionType.Indicator));

        filter.addRestriction(DimensionType.AdminLevel, 141801);
        filter.addRestriction(DimensionType.Partner, 130);
        filter.addRestriction(DimensionType.Indicator, 1);

        execute();

    }

    @Test
    public void testIndicatorOrder() {

        withIndicatorAsDimension();

        filter.addRestriction(DimensionType.Indicator, 1);
        filter.addRestriction(DimensionType.Indicator, 2);

        execute();

        assertEquals(2, buckets.size());

        Bucket indicator1 = findBucketsByCategory(buckets, indicatorDim,
                new EntityCategory(1)).get(0);
        Bucket indicator2 = findBucketsByCategory(buckets, indicatorDim,
                new EntityCategory(2)).get(0);

        EntityCategory cat1 = (EntityCategory) indicator1
                .getCategory(indicatorDim);
        EntityCategory cat2 = (EntityCategory) indicator2
                .getCategory(indicatorDim);

        assertEquals(2, cat1.getSortOrder().intValue());
        assertEquals(OWNER_USER_ID, cat2.getSortOrder().intValue());

    }

    @Test
    @OnDataSet("/dbunit/sites-deleted.db.xml")
    public void testDeletedNotIncluded() {

        withIndicatorAsDimension();
        filter.addRestriction(DimensionType.Indicator, 1);

        execute();

        assertEquals(1, buckets.size());
        assertEquals(13600, (int) buckets.get(0).doubleValue());
    }

    @Test
    @OnDataSet("/dbunit/sites-deleted.db.xml")
    public void testDeletedNotLinked() {

        withIndicatorAsDimension();
        filter.addRestriction(DimensionType.Indicator, asList(400, 401));

        execute();

        assertEquals(1, buckets.size());
        assertEquals(13600, (int) buckets.get(0).doubleValue());
    }

    @Test
    @OnDataSet("/dbunit/sites-zeros.db.xml")
    public void testZerosExcluded() {

        withIndicatorAsDimension();
        filter.addRestriction(DimensionType.Indicator, 5);

        execute();

        assertEquals(1, buckets.size());
        assertEquals(0, (int) buckets.get(0).doubleValue());
        assertEquals(5,
                ((EntityCategory) buckets.get(0).getCategory(this.indicatorDim))
                        .getId());
    }

    @Test
    @OnDataSet("/dbunit/sites-weeks.db.xml")
    public void testWeeks() {

        final Dimension weekDim = new DateDimension(DateUnit.WEEK_MON);
        dimensions.add(weekDim);

        filter.addRestriction(DimensionType.Indicator, 1);

        execute();

        assertEquals(3, buckets.size());
        assertEquals(3600, (int) findBucketByWeek(buckets, 2011, 52)
                .doubleValue());
        assertEquals(1500, (int) findBucketByWeek(buckets, 2012, 1)
                .doubleValue());
        assertEquals(4142, (int) findBucketByWeek(buckets, 2012, 13)
                .doubleValue());

    }

    @Test
    @OnDataSet("/dbunit/sites-quarters.db.xml")
    public void testQuarters() {

        final Dimension quarterDim = new DateDimension(DateUnit.QUARTER);
        dimensions.add(quarterDim);

        filter.addRestriction(DimensionType.Indicator, 1);

        execute();

        assertEquals(3, buckets.size());
        assertEquals(1500, (int) findBucketByQuarter(buckets, 2009, 1).doubleValue());
        assertEquals(3600, (int) findBucketByQuarter(buckets, 2009, 2).doubleValue());
        assertEquals(10000, (int) findBucketByQuarter(buckets, 2008, 4).doubleValue());
    }

    @Test
    @OnDataSet("/dbunit/sites-linked.db.xml")
    public void testLinked() {
        withIndicatorAsDimension();
        filter.addRestriction(DimensionType.Indicator, 1);
        execute();
        assertThat().forIndicator(1).thereIsOneBucketWithValue(1900);
    }

    @Test
    @OnDataSet("/dbunit/sites-linked.db.xml")
    public void testLinkedValuesAreDuplicated() {
        withIndicatorAsDimension();
        filter.addRestriction(DimensionType.Indicator, asList(1, 3));
        execute();
        assertThat().forIndicator(1).thereIsOneBucketWithValue(1900);
        assertThat().forIndicator(3).thereIsOneBucketWithValue(1500);
    }

    @Test
    @OnDataSet("/dbunit/sites-linked.db.xml")
    public void testLinkedPartnerSiteCount() {
        withPartnerAsDimension();
        forTotalSiteCounts();
        filteringOnDatabases(1, 2);
        execute();
        assertThat().thereAre(2).buckets();
        assertThat().forPartner(1).thereIsOneBucketWithValue(2).andItsPartnerLabelIs("NRC");
        assertThat().forPartner(2).thereIsOneBucketWithValue(1).andItsPartnerLabelIs("NRC2");
    }

    @Test
    @OnDataSet("/dbunit/sites-linked.db.xml")
    public void testLinkedPartnerFilterData() {
        withPartnerAsDimension();
        filter.addRestriction(DimensionType.Database, asList(1, 2));
        forFilterData();
        execute();

        assertThat().thereAre(2).buckets();
        assertThat().forPartner(1).thereIsOneBucketWithValue(0).andItsPartnerLabelIs("NRC");
        assertThat().forPartner(2).thereIsOneBucketWithValue(0).andItsPartnerLabelIs("NRC2");
    }

    @Test
    @OnDataSet("/dbunit/sites-linked.db.xml")
    public void testLinkedAttributegroupFilterData() {
        withAttributeGroupDim();
        forFilterData();
        filteringOnDatabases(1, 2);
        execute();

        assertThat().thereAre(2).buckets();

        Dimension dim = new Dimension(DimensionType.AttributeGroup);
        Bucket causeBucket = findBucketsByCategory(buckets, dim, new EntityCategory(1)).get(0);
        assertEquals("cause", causeBucket.getCategory(dim).getLabel());
        assertEquals(0, (int) causeBucket.doubleValue());

        Bucket contenuBucket = findBucketsByCategory(buckets, dim, new EntityCategory(2)).get(0);
        assertEquals("contenu du kit", contenuBucket.getCategory(dim).getLabel());
        assertEquals(0, (int) contenuBucket.doubleValue());
    }

    @Test
    @OnDataSet("/dbunit/sites-linked.db.xml")
    public void testLinkedPartnerFilterDataForIndicators() {
        withPartnerAsDimension();
        forFilterData();

        // empty
        filter.addRestriction(DimensionType.Indicator, 100);
        execute();
        assertThat().thereAre(0).buckets();

        // NRC, NRC2
        filter = new Filter();
        filter.addRestriction(DimensionType.Indicator, 1);
        execute();
        assertThat().thereAre(2).buckets();
        assertThat().forPartner(1).thereIsOneBucketWithValue(0).andItsPartnerLabelIs("NRC");
        assertThat().forPartner(2).thereIsOneBucketWithValue(0).andItsPartnerLabelIs("NRC2");

        // NRC
        filter = new Filter();
        filter.addRestriction(DimensionType.Indicator, 2);
        execute();
        assertThat().thereAre(1).buckets();
        assertThat().forPartner(1).thereIsOneBucketWithValue(0).andItsPartnerLabelIs("NRC");

        // NRC, NRC2
        filter = new Filter();
        filter.addRestriction(DimensionType.Indicator, asList(1, 2, 100));
        execute();
        assertThat().thereAre(2).buckets();
        assertThat().forPartner(1).thereIsOneBucketWithValue(0).andItsPartnerLabelIs("NRC");
        assertThat().forPartner(2).thereIsOneBucketWithValue(0).andItsPartnerLabelIs("NRC2");
    }

    @Test
    @OnDataSet("/dbunit/sites-linked.db.xml")
    public void testLinkedAttributegroupFilterDataForIndicator() {
        withAttributeGroupDim();
        forFilterData();
        Dimension dim = new Dimension(DimensionType.AttributeGroup);

        // empty
        filter.addRestriction(DimensionType.Indicator, 100);
        execute();
        assertThat().thereAre(0).buckets();

        // cause, contenu du kit
        filter = new Filter();
        filter.addRestriction(DimensionType.Indicator, 1);
        execute();
        assertThat().thereAre(2).buckets();
        Bucket bucket1 = findBucketsByCategory(buckets, dim, new EntityCategory(1)).get(0);
        assertEquals("cause", bucket1.getCategory(dim).getLabel());
        assertEquals(0, (int) bucket1.doubleValue());
        Bucket bucket2 = findBucketsByCategory(buckets, dim, new EntityCategory(2)).get(0);
        assertEquals("contenu du kit", bucket2.getCategory(dim).getLabel());
        assertEquals(0, (int) bucket2.doubleValue());

        // cause
        filter = new Filter();
        filter.addRestriction(DimensionType.Indicator, 2);
        execute();
        assertThat().thereAre(1).buckets();
        bucket1 = findBucketsByCategory(buckets, dim, new EntityCategory(1)).get(0);
        assertEquals("cause", bucket1.getCategory(dim).getLabel());
        assertEquals(0, (int) bucket1.doubleValue());

        // cause, contenu du kit
        filter = new Filter();
        filter.addRestriction(DimensionType.Indicator, asList(1, 2, 100));
        execute();
        assertThat().thereAre(2).buckets();
        bucket1 = findBucketsByCategory(buckets, dim, new EntityCategory(1)).get(0);
        assertEquals("cause", bucket1.getCategory(dim).getLabel());
        assertEquals(0, (int) bucket1.doubleValue());
        bucket2 = findBucketsByCategory(buckets, dim, new EntityCategory(2)).get(0);
        assertEquals("contenu du kit", bucket2.getCategory(dim).getLabel());
        assertEquals(0, (int) bucket2.doubleValue());
    }

    @Test
    @OnDataSet("/dbunit/attrib-merge.db.xml")
    public void testAttributesAreMergedAcrossDbByName() {
        withIndicatorAsDimension();
        withAttributeGroupDim(1);
        execute();
    }

    @Test
    @OnDataSet("/dbunit/sites-points.db.xml")
    public void testPointsInferred() {
        dimensions.add(new Dimension(DimensionType.Location));
        withPoints();
        filteringOnDatabases(1);
        execute();

        // should be calculated from the territory's MBR
        assertThat().forLocation(1).thereIsOneBucketWithValue(1500).at(
                (26.8106418 + 28.37725848) / 2.0,
                (-4.022388142 + -1.991221064) / 2.0);

        // should be taken right from the location
        assertThat().forLocation(2).thereIsOneBucketWithValue(3600).at(27.328491, -2.712609);

        // should be calculated from RDC's MBR
        assertThat().forLocation(4).thereIsOneBucketWithValue(44).at((12.18794184 + 31.306) / 2,
                (-13.45599996 + 5.386098154) / 2);
    }

    @Test
    @OnDataSet("/dbunit/sites-points.db.xml")
    public void testPointsAdmin() {
        dimensions.add(new AdminDimension(2));
        filteringOnDatabases(1);
        withPoints();
        execute();

        assertThat().forTerritoire(10).thereIsOneBucketWithValue(10000).at(
                (28.30146624 + 29.0339514) / 2.0,
                (-2.998746978 + -2.494392989) / 2.0);

        assertThat().forTerritoire(12).thereIsOneBucketWithValue(5100).at((26.8106418 + 28.37725848) / 2.0,
                (-4.022388142 + -1.991221064) / 2.0);
    }

    private void filteringOnDatabases(Integer... databaseIds) {
        filter.addRestriction(DimensionType.Database, asList(databaseIds));
    }

    private List<Bucket> findBucketsByCategory(List<Bucket> buckets,
                                               Dimension dim, DimensionCategory cat) {
        List<Bucket> matching = new ArrayList<Bucket>();
        for (Bucket bucket : buckets) {
            if (bucket.getCategory(dim).equals(cat)) {
                matching.add(bucket);
            }
        }
        return matching;
    }

    private Bucket findBucketByQuarter(List<Bucket> buckets, int year,
                                       int quarter) {
        for (Bucket bucket : buckets) {
            QuarterCategory category = (QuarterCategory) bucket
                    .getCategory(new DateDimension(DateUnit.QUARTER));
            if (category.getYear() == year && category.getQuarter() == quarter) {
                return bucket;
            }
        }
        throw new AssertionError("No bucket for " + year + "q" + quarter);
    }

    private Bucket findBucketByWeek(List<Bucket> buckets, int year, int week) {
        for (Bucket bucket : buckets) {
            WeekCategory category = (WeekCategory) bucket
                    .getCategory(new DateDimension(DateUnit.WEEK_MON));
            if (category != null && category.getYear() == year
                    && category.getWeek() == week) {
                return bucket;
            }
        }
        throw new AssertionError("No bucket for " + year + " W " + week);
    }

    private void forTotalSiteCounts() {
        valueType = valueType.TOTAL_SITES;
    }

    private void forFilterData() {
        valueType = valueType.DIMENSION;
    }

    private void withIndicatorAsDimension() {
        indicatorDim = new Dimension(DimensionType.Indicator);
        dimensions.add(indicatorDim);
    }

    private void withProjectAsDimension() {
        dimensions.add(projectDim);
    }

    private void withAdminDimension(AdminDimension adminDimension) {
        dimensions.add(adminDimension);
    }

    private void withPartnerAsDimension() {
        partnerDim = new Dimension(DimensionType.Partner);
        dimensions.add(partnerDim);
    }

    private void withPoints() {
        pointsRequested = true;
    }

    private void withAttributeGroupDim() {
        dimensions.add(new Dimension(DimensionType.AttributeGroup));
    }

    private void withAttributeGroupDim(int groupId) {
        dimensions.add(new AttributeGroupDimension(groupId));
    }

    private void execute() {

        setUser(OWNER_USER_ID);
        try {
            PivotSites pivot = composeQuery();
            pivot.setValueType(valueType);
            pivot.setPointRequested(pointsRequested);
            buckets = execute(pivot).getBuckets();
        } catch (CommandException e) {
            throw new RuntimeException(e);
        }

        printBuckets(buckets);
    }

    private void printBuckets(List<Bucket> buckets1) {
        System.out.println("Buckets = [");
        for (Bucket bucket : buckets1) {
            System.out.print("  { Value: " + bucket.doubleValue());
            for (Dimension dim : bucket.dimensions()) {
                DimensionCategory cat = bucket.getCategory(dim);
                System.out.print("\n    " + dim.toString() + ": ");
                System.out.print(cat.toString());
            }
            System.out.println("\n  }");
        }
        System.out.print("]\n");
    }

    private PivotSites composeQuery() {
        return new PivotSites(dimensions, filter);
    }

    public AssertionBuilder assertThat() {
        return new AssertionBuilder();
    }

    private class AssertionBuilder {
        List<Bucket> matchingBuckets = new ArrayList<Bucket>(buckets);
        StringBuilder criteria = new StringBuilder();

        Object predicate;

        public AssertionBuilder forIndicator(int indicatorId) {
            criteria.append(" with indicator ").append(indicatorId);
            filter(indicatorDim, indicatorId);
            return this;
        }

        public AssertionBuilder forYear(int year) {
            criteria.append(" in year ").append(year);
            filter(new DateDimension(DateUnit.YEAR), Integer.toString(year));
            return this;
        }

        public AssertionBuilder forSite(int siteId) {
            criteria.append(" for site id ").append(siteId);

            filter(new Dimension(DimensionType.Site), siteId);
            return this;
        }

        public AssertionBuilder forQuarter(int year, int quarter) {
            criteria.append(" in quarter ").append(year)
                    .append("Q").append(quarter).append(" ");
            filter(new DateDimension(DateUnit.QUARTER), year + "Q" + quarter);
            return this;
        }

        public AssertionBuilder forProject(int projectId) {
            criteria.append(" with project ").append(projectId);
            filter(projectDim, projectId);
            return this;
        }

        public AssertionBuilder forPartner(int partnerId) {
            criteria.append(" with partner ").append(partnerId);
            filter(partnerDim, partnerId);
            return this;
        }

        public AssertionBuilder forProvince(int provinceId) {
            criteria.append(" with province ").append(provinceId);
            filter(provinceDim, provinceId);
            return this;
        }

        public AssertionBuilder forTerritoire(int territoireId) {
            criteria.append(" with territoire ").append(territoireId);
            filter(territoireDim, territoireId);
            return this;
        }

        public AssertionBuilder forLocation(int locationId) {
            criteria.append(" with location id=").append(locationId);
            filter(new Dimension(DimensionType.Location), locationId);
            return this;
        }

        public AssertionBuilder forAttributeGroupLabeled(int groupId,
                                                         String label) {
            criteria.append(" with a dimension labeled '").append(label)
                    .append("'");
            filter(new AttributeGroupDimension(groupId), label);
            return this;
        }

        private void filter(Dimension dim, String label) {
            ListIterator<Bucket> it = matchingBuckets.listIterator();
            while (it.hasNext()) {
                Bucket bucket = it.next();
                DimensionCategory category = bucket.getCategory(dim);
                if (category == null || !category.getLabel().equals(label)) {
                    it.remove();
                }
            }
        }

        private void filter(Dimension dim, int id) {
            ListIterator<Bucket> it = matchingBuckets.listIterator();
            while (it.hasNext()) {
                Bucket bucket = it.next();
                DimensionCategory category = bucket.getCategory(dim);
                if (!(category instanceof EntityCategory) ||
                        ((EntityCategory) category).getId() != id) {

                    it.remove();

                }
            }
        }

        private String description(String assertion) {
            String s = assertion + " " + criteria.toString();
            return s.trim();
        }

        public AssertionBuilder thereAre(int predicate) {
            this.predicate = predicate;
            return this;
        }

        public AssertionBuilder with(Dimension predicate) {
            this.predicate = predicate;
            return this;
        }

        public AssertionBuilder buckets() {
            bucketCountIs((Integer) predicate);
            return this;
        }

        public AssertionBuilder label(String label) {
            Dimension dim = (Dimension) predicate;
            assertEquals(description(dim.toString() + " label of only bucket"),
                    label,
                    ((EntityCategory) matchingBuckets.get(0).getCategory(dim)).getLabel());
            return this;
        }

        public AssertionBuilder bucketCountIs(int expectedCount) {
            assertEquals(description("count of buckets"), expectedCount,
                    matchingBuckets.size());
            return this;
        }

        public AssertionBuilder thereIsOneBucketWithValue(double expectedValue) {
            bucketCountIs(OWNER_USER_ID);
            assertEquals(description("value of only bucket"), expectedValue,
                    matchingBuckets.get(0).doubleValue(), 0.001);
            return this;
        }

        public AssertionBuilder at(double x, double y) {
            if (matchingBuckets.get(0).getPoint() == null) {
                throw new AssertionError(description("non-null point for "));
            }
            assertEquals(description("x"), x, matchingBuckets.get(0).getPoint().getLng(), 0.001);
            assertEquals(description("y"), y, matchingBuckets.get(0).getPoint().getLat(), 0.001);

            return this;
        }

        public AssertionBuilder andItsPartnerLabelIs(String label) {
            bucketCountIs(OWNER_USER_ID);
            with(partnerDim).label(label);
            return this;
        }
    }

}
