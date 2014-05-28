package org.activityinfo.core.shared.cube;

import org.activityinfo.core.shared.Cuid;
import org.activityinfo.ui.client.component.table.FieldColumn;

/**
 * Created by alex on 5/28/14.
 */
public class DimensionModel {
    private Cuid columnId;
    // other stuff


    public DimensionModel(Cuid columnId) {
        this.columnId = columnId;
    }

    public Cuid getColumnId() {
        return columnId;
    }
}
