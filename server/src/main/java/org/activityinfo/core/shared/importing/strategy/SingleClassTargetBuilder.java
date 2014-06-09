package org.activityinfo.core.shared.importing.strategy;

import com.google.common.collect.Lists;
import org.activityinfo.datamodel.shared.Cuid;
import org.activityinfo.core.shared.criteria.FormClassSet;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.core.shared.form.tree.FormTree;

import java.util.List;
import java.util.Map;

/**
 * Created by alex on 4/4/14.
 */
public class SingleClassTargetBuilder {

    private final FormTree.Node rootField;
    private final TargetCollector targetCollector;

    public SingleClassTargetBuilder(FormTree.Node referenceField) {
        rootField = referenceField;
        targetCollector = new TargetCollector(referenceField);
    }

    public List<ImportTarget> getTargets() {
        return targetCollector.getTargets();
    }

    public SingleClassImporter newImporter(Map<TargetSiteId, ColumnAccessor> mappings) {
        List<ColumnAccessor> sourceColumns = Lists.newArrayList();
        Map<FieldPath, Integer> referenceValues = targetCollector.getPathMap(mappings, sourceColumns);
        List<FieldImporterColumn> fieldImporterColumns = targetCollector.fieldImporterColumns(mappings);

        Cuid rangeClassId = FormClassSet.of(rootField.getRange()).unique();

        return new SingleClassImporter(rangeClassId, sourceColumns, referenceValues, fieldImporterColumns, rootField.getFieldId());
    }
}
