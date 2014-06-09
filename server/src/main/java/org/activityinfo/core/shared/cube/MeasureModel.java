package org.activityinfo.core.shared.cube;

import org.activityinfo.datamodel.shared.Cuid;

/**
 * Created by alex on 5/28/14.
 */
public class MeasureModel {
    private Cuid columnId;
    private AggregationType aggregationType;

    public MeasureModel(AggregationType aggregationType, Cuid columnId) {
        this.aggregationType = aggregationType;
        this.columnId = columnId;
    }

    public AggregationType getAggregationType() {
        return aggregationType;
    }

    public Cuid getColumnId() {
        return columnId;
    }
}
