package org.activityinfo.ui.client.pageView;

import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.application.FolderClass;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.ui.client.style.Icons;

/**
 * Provides icon styles for different types of FormClasses.
 * Currently hard coded, but eventually should be flexible enough
 * to have good defaults as well as allow the user to choose an icon
 */
public class IconStyleProvider {

    public static String getIconStyleForFormClass(Cuid classId) {
        if(classId.equals(FolderClass.CLASS_ID)) {
            return Icons.INSTANCE.folder();

        } else if(classId.getDomain() == CuidAdapter.LOCATION_TYPE_DOMAIN) {
            return Icons.INSTANCE.location();

        } else {
            return Icons.INSTANCE.form();
        }
    }
}
