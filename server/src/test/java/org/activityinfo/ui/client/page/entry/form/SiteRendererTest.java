package org.activityinfo.ui.client.page.entry.form;

import com.teklabs.gwt.i18n.server.LocaleProxy;
import org.activityinfo.core.shared.form.FormFieldType;
import org.activityinfo.legacy.shared.model.ActivityDTO;
import org.activityinfo.legacy.shared.model.IndicatorDTO;
import org.activityinfo.legacy.shared.model.SiteDTO;
import org.activityinfo.server.endpoint.kml.JreIndicatorValueFormatter;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertTrue;

public class SiteRendererTest {

    private SiteRenderer siteRenderer;

    @Before
    public void setup() {
        LocaleProxy.setLocale(Locale.ENGLISH);

        siteRenderer = new SiteRenderer(new JreIndicatorValueFormatter());

    }

    @Test
    public void multipleGroupsRender() {


        IndicatorDTO indicator1 = new IndicatorDTO();
        indicator1.setId(1);
        indicator1.setAggregation(IndicatorDTO.AGGREGATE_SUM);
        indicator1.setName("First indicator");
        indicator1.setCategory("First group");

        IndicatorDTO indicator2 = new IndicatorDTO();
        indicator2.setAggregation(IndicatorDTO.AGGREGATE_SUM);
        indicator2.setId(2);
        indicator2.setName("Second indicator");
        indicator2.setCategory("Second group");


        IndicatorDTO indicator3 = new IndicatorDTO();
        indicator3.setAggregation(IndicatorDTO.AGGREGATE_SUM);
        indicator3.setType(FormFieldType.NARRATIVE);
        indicator3.setId(3);
        indicator3.setName("Third indicator");
        indicator3.setCategory("Second group");


        ActivityDTO activity = new ActivityDTO();
        activity.setId(1);
        activity.getIndicators().add(indicator1);
        activity.getIndicators().add(indicator2);
        activity.getIndicators().add(indicator3);

        SiteDTO site = new SiteDTO();
        site.setIndicatorValue(1, 1000d);
        site.setIndicatorValue(2, 2000d);
        site.setIndicatorValue(3, "Some help but not a huge amount");

        String html = siteRenderer.renderSite(site, activity, false, true);

        assertTrue(html.contains(indicator1.getName()));
        assertTrue(html.contains(indicator2.getName()));
        assertTrue(html.contains(indicator3.getName()));
    }

}
