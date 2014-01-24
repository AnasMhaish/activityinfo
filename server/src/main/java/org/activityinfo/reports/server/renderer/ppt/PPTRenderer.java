package org.activityinfo.reports.server.renderer.ppt;

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

import com.google.inject.Inject;
import org.activityinfo.reports.server.renderer.Renderer;
import org.activityinfo.reports.shared.model.MapReportElement;
import org.activityinfo.reports.shared.model.PivotChartReportElement;
import org.activityinfo.reports.shared.model.Report;
import org.activityinfo.reports.shared.model.ReportElement;
import org.apache.poi.hslf.usermodel.SlideShow;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Alex Bertram
 */
public class PPTRenderer implements Renderer {

    private final PPTMapRenderer mapRenderer;
    private final PPTChartRenderer chartRenderer;

    @Inject
    public PPTRenderer(PPTMapRenderer mapRenderer,
                       PPTChartRenderer chartRenderer) {
        this.mapRenderer = mapRenderer;
        this.chartRenderer = chartRenderer;
    }

    @Override
    public void render(ReportElement element, OutputStream os)
            throws IOException {

        if (element instanceof Report) {
            SlideShow ppt = new SlideShow();
            Report report = (Report) element;
            for (ReportElement child : report.getElements()) {
                if (child instanceof MapReportElement) {
                    mapRenderer.render((MapReportElement) child, ppt);
                } else if (child instanceof PivotChartReportElement) {
                    chartRenderer.render((PivotChartReportElement) child, ppt);
                }
            }
            ppt.write(os);

        } else if (element instanceof MapReportElement) {
            mapRenderer.render((MapReportElement) element, os);
        } else if (element instanceof PivotChartReportElement) {
            chartRenderer.render((PivotChartReportElement) element, os);
        }
    }

    @Override
    public String getMimeType() {
        return "application/vnd.ms-powerpoint";
    }

    @Override
    public String getFileSuffix() {
        return ".ppt";
    }
}
