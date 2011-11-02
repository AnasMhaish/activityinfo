/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.report.generator;

import static org.easymock.EasyMock.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.sigmah.server.command.DispatcherSync;
import org.sigmah.server.database.hibernate.dao.IndicatorDAO;
import org.sigmah.server.database.hibernate.dao.SiteOrder;
import org.sigmah.server.database.hibernate.dao.SiteProjectionBinder;
import org.sigmah.server.database.hibernate.dao.SiteTableColumn;
import org.sigmah.server.database.hibernate.dao.SiteTableDAO;
import org.sigmah.server.database.hibernate.entity.User;
import org.sigmah.server.report.generator.map.MockBaseMapDAO;
import org.sigmah.shared.command.Filter;
import org.sigmah.shared.command.GetSites;
import org.sigmah.shared.command.result.SiteResult;
import org.sigmah.shared.dto.SiteDTO;
import org.sigmah.shared.report.content.BubbleMapMarker;
import org.sigmah.shared.report.content.MapContent;
import org.sigmah.shared.report.content.TableData;
import org.sigmah.shared.report.model.MapReportElement;
import org.sigmah.shared.report.model.TableColumn;
import org.sigmah.shared.report.model.TableElement;
import org.sigmah.shared.report.model.labeling.ArabicNumberSequence;
import org.sigmah.shared.report.model.layers.BubbleMapLayer;
import org.sigmah.shared.report.model.layers.CircledMapLayer;

/**
 * @author Alex Bertram
 */
public class TableGeneratorTest {
    private User user;

    @Before
    public void setUp() {
        user = new User();
        user.setName("Alex");
        user.setEmail("akbertra@mgail.com");
        user.setLocale("fr");
    }

    @Test
    public void testTableGenerator() {

        TableElement table = new TableElement();
        table.addColumn(new TableColumn("Location", "location.name"));

        TableGenerator gtor = new TableGenerator(createDispatcher(), new MockSiteTableDAO(), createIndicator(), null);
        gtor.generate(user, table, null, null);

        Assert.assertNotNull("content is set", table.getContent());

        TableData data = table.getContent().getData();
        List<TableData.Row> rows = data.getRows();
        Assert.assertEquals("row count", 1, rows.size());

        TableData.Row row = rows.get(0);
        Assert.assertEquals("column data", "tampa bay", row.values[0]);
    }


    @Test
    public void testMap() {

        TableElement table = new TableElement();
        table.addColumn(new TableColumn("Index", "map"));
        table.addColumn(new TableColumn("Site", "location.name"));

        MapReportElement map = new MapReportElement();
        map.setBaseMapId("map1");
        CircledMapLayer layer = new BubbleMapLayer();
        layer.setLabelSequence(new ArabicNumberSequence());
        map.addLayer(layer);
        table.setMap(map);
        
        DispatcherSync dispatcher = createMock(DispatcherSync.class);
        expect(dispatcher.execute(isA(GetSites.class)))
        	.andReturn(new SiteResult(dummySite()))
        	.anyTimes();
       
        replay(dispatcher);

        TableGenerator gtor = new TableGenerator(dispatcher, new MockSiteTableDAO(), createIndicator(),
                new MapGenerator(dispatcher, new MockBaseMapDAO(), new MockIndicatorDAO()));
        gtor.generate(user, table, null, null);

        MapContent mapContent = map.getContent();
        Assert.assertNotNull("map content", mapContent);
        Assert.assertEquals("marker count", 1, mapContent.getMarkers().size());
        Assert.assertEquals("label on marker", "1", ((BubbleMapMarker) mapContent.getMarkers().get(0)).getLabel());

        Map<Integer, String> siteLabels = mapContent.siteLabelMap();
        Assert.assertEquals("site id in map", "1", siteLabels.get(1));

        TableData.Row row = table.getContent().getData().getRows().get(0);
        Assert.assertEquals("label on row", "1", row.values[0]);
    }

    private IndicatorDAO createIndicator() {
        IndicatorDAO indicatorDAO = createNiceMock(IndicatorDAO.class);
        replay(indicatorDAO);
        return indicatorDAO;
    }

    private DispatcherSync createDispatcher() {
        DispatcherSync dispatcher = createNiceMock(DispatcherSync.class);
        replay(dispatcher);
        return dispatcher;
    }

    public SiteDTO dummySite() {
    	SiteDTO site = new SiteDTO();
    	site.setId(1);
    	site.setLocationName("tampa bay");
    	site.setX(28.4);
    	site.setY(1.2);
    	return site;
    }
    
    private class MockSiteTableDAO implements SiteTableDAO {
        @Override
        public <RowT> List<RowT> query(User user, Filter filter, List<SiteOrder> orderings, SiteProjectionBinder<RowT> binder, int retrieve, int offset, int limit)  {
            try {
                final ResultSet rs = createNiceMock(ResultSet.class);
                expect(rs.getInt(SiteTableColumn.id.index())).andReturn(1);
                expect(rs.getObject(SiteTableColumn.id.index())).andReturn(1);
                expect(rs.getObject(SiteTableColumn.location_name.index())).andReturn("tampa bay");
                expect(rs.getDouble(SiteTableColumn.x.index())).andReturn(28.4);
                expect(rs.getObject(SiteTableColumn.x.index())).andReturn(28.4);
                expect(rs.getDouble(SiteTableColumn.y.index())).andReturn(1.2);
                expect(rs.getObject(SiteTableColumn.y.index())).andReturn(1.2);
                expect(rs.wasNull()).andReturn(false).anyTimes();
                replay(rs);

                return Collections.singletonList(binder.newInstance(new String[0], rs));
            } catch (SQLException e) {
                throw new AssertionError(e);
            }
        }

        @Override
        public int queryCount(User user, Filter filter) {
            return 0;
        }

        @Override
        public int queryPageNumber(User user, Filter filter, List<SiteOrder> orderings, int pageSize, int siteId) {
            return 0;
        }
    }
}
