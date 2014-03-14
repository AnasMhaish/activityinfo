package org.activityinfo.ui.client.util;


import com.google.gwt.user.client.Window;

public class FeatureSwitch {

    public static boolean isEnabled(String featureName) {
        return Boolean.valueOf(Window.Location.getParameter("feature." + featureName));
    }

    public static boolean isNewFormEnabled() {
        return isEnabled("newForm");
    }
}
