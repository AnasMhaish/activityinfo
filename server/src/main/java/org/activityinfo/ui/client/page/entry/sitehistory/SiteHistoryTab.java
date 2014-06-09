package org.activityinfo.ui.client.page.entry.sitehistory;

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

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.shared.command.GetSiteHistory;
import org.activityinfo.legacy.shared.command.result.HtmlResult;
import org.activityinfo.legacy.shared.model.SiteDTO;


public class SiteHistoryTab extends TabItem {

    private final Html content;
    private final Dispatcher dispatcher;
    private SiteDTO selectedSite;
    private boolean deferredLoad;

    public SiteHistoryTab(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;

        this.setScrollMode(Scroll.AUTO);

        setText(I18N.CONSTANTS.history());

        content = new Html();
        content.setStyleName("details");
        add(content);

        addListener(Events.Select, new Listener<ComponentEvent>() {

            @Override
            public void handleEvent(ComponentEvent componentEvent) {
                if(deferredLoad) {
                    maybeLoadHistory();
                }
            }
        });
    }

    public void setSelectedSite(final SiteDTO selectedSite) {
        this.selectedSite = selectedSite;
        renderLoading();
        maybeLoadHistory();
    }

    private void maybeLoadHistory() {
        if(getTabPanel().getSelectedItem() == this) {
            loadHistory();
        } else {
            deferredLoad = true;
        }
    }

    private void loadHistory() {
        deferredLoad = false;
        dispatcher.execute(new GetSiteHistory(selectedSite.getId()), new AsyncCallback<HtmlResult>() {
            @Override
            public void onFailure(Throwable caught) {
                content.setHtml("Site history not available");
            }

            @Override
            public void onSuccess(final HtmlResult historyResult) {
                content.setHtml(historyResult.getHtml());
            }
        });
    }


    private void renderLoading() {
        content.setHtml(I18N.CONSTANTS.loading());
    }
}
