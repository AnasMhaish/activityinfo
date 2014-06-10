package org.activityinfo.core.shared.cube;

import com.google.common.collect.Lists;
import org.activityinfo.datamodel.shared.table.DefaultTableModel;

import java.util.List;

/**
 * Created by alex on 5/28/14.
 */
public class CubeModel {

    private DefaultTableModel tableModel;
    private List<DimensionModel> dimensions = Lists.newArrayList();
    private MeasureModel measure;

    public CubeModel(DefaultTableModel tableModel) {
        this.tableModel = tableModel;
    }

    public DefaultTableModel getTableModel() {
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
