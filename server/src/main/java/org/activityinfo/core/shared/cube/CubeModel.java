package org.activityinfo.core.shared.cube;

import com.google.common.collect.Lists;
import org.activityinfo.core.shared.table.TableModel;

import java.util.List;

/**
 * Created by alex on 5/28/14.
 */
public class CubeModel {

    private TableModel tableModel;
    private List<DimensionModel> dimensions = Lists.newArrayList();
    private MeasureModel measure;

    public CubeModel(TableModel tableModel) {
        this.tableModel = tableModel;
    }

    public TableModel getTableModel() {
        return tableModel;
    }

    public List<DimensionModel> getDimensions() {
        return dimensions;
    }

    public MeasureModel getMeasure() {
        return measure;
    }

    public void setMeasure(MeasureModel measure) {
        this.measure = measure;
    }
}
