package org.activityinfo.core.shared.cube;

import org.activityinfo.datamodel.shared.Cuid;

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
