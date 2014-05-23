package org.activityinfo.server.forms;


import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.FormClass;

public interface FormClassProvider {

    FormClass getFormClass(Cuid id);

}
