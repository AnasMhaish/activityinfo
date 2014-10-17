package org.activityinfo.client.resource;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import org.activityinfo.model.resource.ResourceNode;

import java.util.List;

public class ResourceNodeListParser implements Function<Response, List<ResourceNode>> {
    @Override
    public List<ResourceNode> apply(Response input) {
        try {
            GWT.log("Received node list");
            JsArray<ResourceNodeOverlay> array = JsonUtils.safeEval(input.getText());
            GWT.log("Received node list size = " + array.length());
            List<ResourceNode> nodes = Lists.newArrayList();
            for (int i = 0; i != array.length(); ++i) {
                nodes.add(ResourceTreeParser.parse(array.get(i)));
            }
            return nodes;
        } catch (Throwable e) {
            GWT.log("Failed to parse resource nodes.", e);
            return Lists.newArrayList();
        }
    }
}
