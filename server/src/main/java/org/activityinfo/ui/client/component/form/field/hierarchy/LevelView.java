package org.activityinfo.ui.client.component.form.field.hierarchy;

import com.google.common.base.Supplier;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.user.client.ui.HasValue;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.fp.client.Promise;

import java.util.List;


public interface LevelView extends HasSelectionHandlers<Projection> {


    void clearSelection();

    void setReadOnly(boolean readOnly);

    void setEnabled(boolean enabled);

    void setSelection(Projection selection);

    void setChoices(Supplier<Promise<List<Projection>>> choices);
}
