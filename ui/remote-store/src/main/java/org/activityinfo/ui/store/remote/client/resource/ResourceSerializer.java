package org.activityinfo.ui.store.remote.client.resource;

import com.google.gwt.json.client.JSONObject;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.resource.Resource;

import java.util.Map;

public class ResourceSerializer {

    public static String toJson(Resource resource) {
        JsPropertyBag jso = JsPropertyBag.create();
        jso.setString("@id", resource.getId().asString());
        jso.setString("@owner", resource.getOwnerId().asString());
        jso.setString("@version", Long.toString(resource.getVersion()));
        jso.setString("@class", resource.getValue().getClassId().asString());

        for (Map.Entry<String, Object> entry : resource.getValue().asMap().entrySet()) {
            jso.set(entry.getKey(), entry.getValue());
        }

        return new JSONObject(jso).toString();
    }

    public static String toJson(Record record) {
        JsPropertyBag jso = JsPropertyBag.create();
        for (Map.Entry<String, Object> entry : record.asMap().entrySet()) {
            jso.set(entry.getKey(), entry.getValue());
        }
        return new JSONObject(jso).toString();
    }
}
