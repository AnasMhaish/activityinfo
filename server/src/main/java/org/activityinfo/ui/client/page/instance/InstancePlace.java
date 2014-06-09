package org.activityinfo.ui.client.page.instance;

import com.google.common.collect.Lists;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.ui.client.page.PageId;
import org.activityinfo.ui.client.page.PageState;
import org.activityinfo.ui.client.page.PageStateParser;
import org.activityinfo.ui.client.page.app.Section;

import java.util.List;

/**
 * Place corresponding to the view of a instance.
 */
public class InstancePlace implements PageState {

    private Cuid instanceId;
    private String view;

    public InstancePlace(Cuid instanceId) {
        this.instanceId = instanceId;
    }

    public InstancePlace(Cuid cuid, String part) {
        this.instanceId = cuid;
        this.view = part;
    }

    @Override
    public String serializeAsHistoryToken() {
        StringBuilder token = new StringBuilder(instanceId.asString());
        if(view != null) {
            token.append("/").append(view);
        }
        return token.toString();
    }

    @Override
    public PageId getPageId() {
        return InstancePage.PAGE_ID;
    }

    public Cuid getInstanceId() {
        return instanceId;
    }

    @Override
    public List<PageId> getEnclosingFrames() {
        return Lists.newArrayList(InstancePage.PAGE_ID);
    }

    @Override
    public Section getSection() {
        return null;
    }

    public String getView() {
        return view;
    }

    public static class Parser implements PageStateParser {

        @Override
        public PageState parse(String token) {
            String parts[] = token.split("/");
            if(parts.length == 1) {
                return new InstancePlace(Cuid.create(parts[0]));
            } else {
                return new InstancePlace(Cuid.create(parts[0]), parts[1]);
            }
        }
    }


    public static SafeUri safeUri(Cuid instanceId) {
        return UriUtils.fromTrustedString("#i/" + instanceId.asString());
    }
}
