package org.activityinfo.ui.client.component.form.model;

import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.core.shared.form.tree.FormTree;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates all the information need to build the layout for a form
 */
public class FormViewModel {

    FormTree formTree;
    FormInstance instance;
    Map<Cuid, FieldViewModel> fields = new HashMap<>();

    FormViewModel() {
    }

    public FormTree getFormTree() {
        return formTree;
    }

    public FormInstance getInstance() {
        return instance;
    }

    public FieldViewModel getFieldViewModel(Cuid fieldId) {
        return fields.get(fieldId);
    }

    public FormClass getFormClass() {
        return formTree.getRootFormClasses().values().iterator().next();
    }
}
