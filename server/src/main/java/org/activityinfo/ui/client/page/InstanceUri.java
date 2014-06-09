package org.activityinfo.ui.client.page;

import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.core.shared.form.FormInstance;

public class InstanceUri {

    public static SafeUri of(FormInstance instance) {
        return of(instance.getId());
    }

    private static SafeUri of(Cuid id) {
        return UriUtils.fromTrustedString("#i/" + id.asString());
    }
}
