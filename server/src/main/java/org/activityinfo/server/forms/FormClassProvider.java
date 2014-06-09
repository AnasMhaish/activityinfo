package org.activityinfo.server.forms;


import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.datamodel.shared.Cuid;

public interface FormClassProvider {

    FormClass getFormClass(Cuid id);

}
